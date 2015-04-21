package com.github.axdotl.jqassistant.plugins.liquibase;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.buschmais.jqassistant.plugin.xml.api.model.XmlFileDescriptor;
import com.buschmais.jqassistant.plugin.xml.api.scanner.JAXBUnmarshaller;
import com.buschmais.jqassistant.plugin.xml.api.scanner.XmlScope;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeLogDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeSetDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.IncludeDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.PreconditionsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.rollback.RollbackDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.rollback.RollbackRefactoringDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.exception.BlankStringRefactoringException;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.precondition.PreconditionScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.*;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.liquibase.xml.ns.dbchangelog.*;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.ChangeSet;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.ChangeSet.PreConditions;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.Include;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.IncludeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scanner for Liquibase database change log files.
 * 
 * @version <b>0.1:</b> Supports <a href=
 *          "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd</a>
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/">http://www.liquibase.org/documentation/</a>
 */
public class LiquibaseScannerPlugin extends AbstractScannerPlugin<FileResource, ChangeLogDescriptor> {

    /** Cypher query to find an existing include node by file name */
    private static final String QUERY_FIND_INCLUDE_BY_FILE_NAME = "MATCH(inc:Include) WHERE inc.fileName=\"%s\" RETURN inc";
    /** Cypher query to find an existing changeLog node by file name */
    private static final String QUERY_FIND_CHANGELOG_BY_FILE_NAME = "MATCH(log:ChangeLog) WHERE log.fileName=\"%s\" RETURN log";
    /** It's the logger my friend. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseScannerPlugin.class);

    /** Mapping of refactoring elements to related scanner instance. */
    @SuppressWarnings("rawtypes")
    private final Map<Class, LiquibaseElementScanner> scannerMap = new HashMap<Class, LiquibaseElementScanner>();

    /** Used to unmarshal changelog, will be initialized once to improve performance. */
    private JAXBUnmarshaller<FileResource, DatabaseChangeLog> unmarshaller;

    /** Used to check whether the given file has <code>databaseChangeLog</code> as root element -&gt; it's a liquibase changelog file. */
    private XMLInputFactory factory;

    @Override
    public void initialize() {
        factory = XMLInputFactory.newInstance();
        unmarshaller = new JAXBUnmarshaller<>(DatabaseChangeLog.class);
        // Register scanner instances for certain refactoring types
        scannerMap.put(AddColumn.class, new AddColumnScanner());
        scannerMap.put(AddForeignKeyConstraint.class, new AddForeignKeyScanner());
        scannerMap.put(AddNotNullConstraint.class, new AddNotNullConstraintScanner());
        scannerMap.put(AddPrimaryKey.class, new AddPrimaryKeyScanner());
        scannerMap.put(AddUniqueConstraint.class, new AddUniqueConstraintScanner());
        scannerMap.put(CreateSequence.class, new CreateSequenceScanner());
        scannerMap.put(CreateTable.class, new CreateTableScanner());
        scannerMap.put(DropColumn.class, new DropColumnScanner());
        scannerMap.put(DropForeignKeyConstraint.class, new DropConstraintScanner());
        scannerMap.put(DropPrimaryKey.class, new DropConstraintScanner());
        scannerMap.put(DropUniqueConstraint.class, new DropConstraintScanner());
        scannerMap.put(DropTable.class, new DropTableScanner());
        scannerMap.put(Sql.class, new SqlScanner());
    }

    @Override
    public boolean accepts(FileResource item, String path, Scope scope) throws IOException {

        // Accept only XML files
        if (XmlScope.DOCUMENT.equals(scope) || !path.toLowerCase().endsWith(".xml")) {
            return false;
        }

        // Ensure that it is a liquibase changelog file
        String localName = null;
        try (InputStream stream = item.createStream()) {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            if (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    localName = reader.getLocalName();
                }
            }

        } catch (XMLStreamException e) {
            String msg = "Fail to read XML file. Path=[%s], Cause=[%s]";
            LOGGER.error(String.format(msg, path, e.getLocalizedMessage()));
            return false;
        }

        // databaseChangeLog is the root element
        return StringUtils.equals("databaseChangeLog", localName);
    }

    @Override
    public ChangeLogDescriptor scan(FileResource item, String path, Scope scope, Scanner scanner) throws IOException {
        ChangeLogDescriptor changeLogDescriptor = createChangeLogDescriptor(item, path, scanner);
        DatabaseChangeLog changeLog = unmarshaller.unmarshal(item);
        List<Object> changeLogChildren = changeLog.getChangeSetOrIncludeOrIncludeAll();

        ChangeSetDescriptor lastChangeSetOfChangeLog = null;
        RefactoringDescriptor lastRefactoringOfPreviousChangeSet = null;
        for (Object o : changeLogChildren) {

            if (o instanceof ChangeSet) {
                ChangeSetDescriptor setDescriptor = scanChangeSet(scanner, (ChangeSet) o, lastRefactoringOfPreviousChangeSet);
                // Memorize last refactoring of last changeset to link it with refacotring of next changeset
                lastRefactoringOfPreviousChangeSet = setDescriptor.getLastRefactoring();

                if (lastChangeSetOfChangeLog != null) {
                    // Link changesets
                    LOGGER.debug("Setting next changeset '{}'-->'{}'", lastChangeSetOfChangeLog, setDescriptor);
                    lastChangeSetOfChangeLog.setNextChangeSet(setDescriptor);
                }
                lastChangeSetOfChangeLog = setDescriptor;
                changeLogDescriptor.getChangeSets().add(setDescriptor);
            }

            else if (o instanceof Include) {

                String changeLogParent = StringUtils.substringBeforeLast(changeLogDescriptor.getFileName(), "/");

                IncludeDescriptor includeDescriptor = scanInclude(scanner, (Include) o, changeLogParent);
                changeLogDescriptor.getIncludes().add(includeDescriptor);
            }

            else if (o instanceof IncludeAll) {
                IncludeDescriptor includeDescriptor = scanIncludeAll(scanner, (IncludeAll) o);
                changeLogDescriptor.getIncludes().add(includeDescriptor);
            }
        }

        return changeLogDescriptor;
    }

    /**
     * Scans a single changeset, creates a node and connect it
     * 
     * @param scanner
     *            To create {@link Descriptor}s
     * @param changeSet
     *            Current changeSet
     * @param lastRefactoringOfPreviousSet
     *            refactoring of the last scanned changeset, can be <code>null</code>.
     * @return The {@link ChangeSetDescriptor}
     */
    private ChangeSetDescriptor scanChangeSet(Scanner scanner, ChangeSet changeSet, RefactoringDescriptor lastRefactoringOfPreviousSet) {

        LOGGER.debug("Scan ChangeSet id=[{}]", changeSet.getId());

        ChangeSetDescriptor changeSetDescriptor = scanner.getContext().getStore().create(ChangeSetDescriptor.class);
        changeSetDescriptor.setAuthor(changeSet.getAuthor());
        changeSetDescriptor.setId(changeSet.getId());

        // Scan preconditions
        PreConditions preConditions = changeSet.getPreConditions();
        if (preConditions != null) {
            PreconditionsDescriptor preconditionsDescriptor = scanPrecondition(preConditions, scanner);
            changeSetDescriptor.getPreconditions().add(preconditionsDescriptor);
        }

        List<Object> changeSetChildren = changeSet.getChangeSetChildren();

        RefactoringDescriptor lastRefactoringOfChangeSet = lastRefactoringOfPreviousSet;
        for (Object child : changeSetChildren) {

            // Looking for the changeset comment
            if (child instanceof JAXBElement) {
                @SuppressWarnings("unchecked")
                String comment = ((JAXBElement<String>) child).getValue();
                changeSetDescriptor.setComment(comment);
                continue;

            }
            // It's a Rollback
            else if (child instanceof Rollback) {
                Rollback rollback = (Rollback) child;
                RollbackDescriptor rollbackDescriptor = scanRollback(rollback, scanner);
                changeSetDescriptor.setRollback(rollbackDescriptor);
                continue;

            }

            // determine more detailed information about refactoring
            RefactoringDescriptor refactoringDescriptor;
            try {
                refactoringDescriptor = scanRefactoring(child, scanner);
            } catch (BlankStringRefactoringException e) {
                LOGGER.debug("Skip element, because it is a blank string.");
                continue;
            }
            if (lastRefactoringOfChangeSet != null) {
                // Link refactorings
                LOGGER.debug("Setting next refactoring '{}'-->'{}'", lastRefactoringOfChangeSet, refactoringDescriptor);
                lastRefactoringOfChangeSet.setNextRefactoring(refactoringDescriptor);
            }
            changeSetDescriptor.getRefactorings().add(refactoringDescriptor);
            lastRefactoringOfChangeSet = refactoringDescriptor;
            // update last refactoring on set
            changeSetDescriptor.setLastRefactoring(lastRefactoringOfChangeSet);
        }

        return changeSetDescriptor;
    }

    /**
     * Scans an include. Checks whether a changelog-node already exists, then reuse this node.
     * 
     * @param scanner
     *            To create {@link Descriptor}s
     * @param include
     *            Current include
     * @param changeLogFolderPath
     *            Path to folder where changelog is located
     * @return Creates {@link IncludeDescriptor}
     */
    private IncludeDescriptor scanInclude(Scanner scanner, Include include, String changeLogFolderPath) {

        String file = include.getFile();
        LOGGER.debug("Scan include. File=[{}]", file);
        IncludeDescriptor includeDescriptor;
        StringBuilder pathBuilder = new StringBuilder();

        // Include can be specified relative to changelog, so create absolute path
        if (BooleanUtils.toBoolean(include.getRelativeToChangelogFile())) {
            LOGGER.debug("Include file attribute is relative to changelog. ChangeLogFolderPath:[{}], File=[{}]", changeLogFolderPath, file);
            pathBuilder.append(changeLogFolderPath);
            pathBuilder.append("/");
            pathBuilder.append(include.getFile());
            file = pathBuilder.toString();
        }

        Result<CompositeRowObject> query = scanner.getContext().getStore().executeQuery(String.format(QUERY_FIND_CHANGELOG_BY_FILE_NAME, file));
        if (query.hasResult()) {
            LOGGER.debug("ChangeLog for path '{}' already exists. Use this i.s.o. creating new one.", file);
            CompositeRowObject queryResult = query.getSingleResult();
            // includeDescriptor = queryResult.get("log", ChangeLogDescriptor.class);
            ChangeLogDescriptor changeLogDescriptor = queryResult.get("log", ChangeLogDescriptor.class);
            includeDescriptor = scanner.getContext().getStore().addDescriptorType(changeLogDescriptor, IncludeDescriptor.class);
        } else {
            LOGGER.debug("No ChangeLog for path '{}' exists yet. New include will be created.", file);
            includeDescriptor = scanner.getContext().getStore().create(IncludeDescriptor.class);
        }

        // Apply values to descriptor
        includeDescriptor.setIncludeAll(false);
        includeDescriptor.setRelativeToChangelogFile(BooleanUtils.toBoolean(include.getRelativeToChangelogFile()));
        includeDescriptor.setFile(include.getFile());
        includeDescriptor.setFileName(file);

        return includeDescriptor;
    }

    private IncludeDescriptor scanIncludeAll(Scanner scanner, IncludeAll includeAll) {

        IncludeDescriptor includeDescriptor = scanner.getContext().getStore().create(IncludeDescriptor.class);

        includeDescriptor.setIncludeAll(true);
        includeDescriptor.setFileName(includeAll.getPath());
        includeDescriptor.setRelativeToChangelogFile(BooleanUtils.toBoolean(includeAll.getRelativeToChangelogFile()));

        return includeDescriptor;
    }

    /**
     * Scans preconditions.
     * 
     * @param preConditions
     *            precondition root element
     * @param scanner
     *            to create elements
     * @return created descriptor
     */
    private PreconditionsDescriptor scanPrecondition(PreConditions preConditions, Scanner scanner) {

        PreconditionsDescriptor descriptor = scanner.getContext().getStore().create(PreconditionsDescriptor.class);
        descriptor.setOnError(preConditions.getOnError());
        descriptor.setOnErrorMessage(preConditions.getOnErrorMessage());
        descriptor.setOnFail(preConditions.getOnFail());
        descriptor.setOnFailMessage(preConditions.getOnFailMessage());
        descriptor.setOnUpdateSql(preConditions.getOnSqlOutput());

        for (Object o : preConditions.getPreConditionChildren()) {
            descriptor.getNestedPreconditions().add(new PreconditionScanner().scanElement(o, scanner));
        }

        return descriptor;
    }

    /**
     * Scans a single refactoring.<br />
     * 
     * @param refactoring
     *            current element
     * @param scanner
     *            scanner to create elements
     * @return Created {@link RefactoringDescriptor}
     * 
     * @throws BlankStringRefactoringException
     *             If refacotoring is a blank String for instance.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private RefactoringDescriptor scanRefactoring(Object refactoring, Scanner scanner) throws BlankStringRefactoringException {

        LOGGER.debug("Scan refactoring. Type=[{}]", refactoring.getClass().getSimpleName());

        if (refactoring instanceof String && StringUtils.isBlank(refactoring.toString())) {
            throw new BlankStringRefactoringException();
        }

        LiquibaseDescriptor descriptor = null;
        LiquibaseElementScanner liquibaseElementScanner = scannerMap.get(refactoring.getClass());
        if (liquibaseElementScanner != null) {
            descriptor = liquibaseElementScanner.scanElement(refactoring, scanner);
        } else {
            descriptor = scanner.getContext().getStore().create(RefactoringDescriptor.class);
        }
        RefactoringDescriptor result = (RefactoringDescriptor) descriptor;
        result.setRefactoringTypeName(refactoring.getClass().getSimpleName());

        return result;
    }

    /**
     * Scans a {@link Rollback} element and analyze nested refactorings.
     * 
     * @param rollback
     *            Analyze it
     * @param scanner
     *            scanner to create elements
     * @return created {@link RollbackDescriptor}
     */
    private RollbackDescriptor scanRollback(Rollback rollback, Scanner scanner) {

        LOGGER.debug("Scan rollback. '{}'", rollback);

        RollbackDescriptor rollbackDescriptor = scanner.getContext().getStore().create(RollbackDescriptor.class);
        rollbackDescriptor.setChangeSetAuthor(rollback.getChangeSetAuthor());
        rollbackDescriptor.setChangeSetId(rollback.getChangeSetId());
        rollbackDescriptor.setChangeSetPath(rollback.getChangeSetPath());

        List<Object> rollbackStatements = rollback.getContent();
        if (rollbackStatements.isEmpty()) {
            // No rollback statements, so we're done.
            return rollbackDescriptor;
        }

        RollbackRefactoringDescriptor lastRefactoringOfRollback = null;
        for (Object refactoring : rollbackStatements) {

            RefactoringDescriptor refactoringDescriptor;
            try {
                refactoringDescriptor = scanRefactoring(refactoring, scanner);
            } catch (BlankStringRefactoringException e) {
                LOGGER.debug("Skip element, because it is a blank string.");
                continue;
            }

            // Maybe there is a rollback comment
            if (refactoring instanceof JAXBElement) {
                @SuppressWarnings("unchecked")
                String comment = ((JAXBElement<String>) refactoring).getValue();
                rollbackDescriptor.setComment(comment);
                continue;

            }

            // Mark refactoring as rollback-refactoring
            RollbackRefactoringDescriptor rollbackRefactoringDescriptor = scanner.getContext().getStore()
                    .addDescriptorType(refactoringDescriptor, RollbackRefactoringDescriptor.class);

            rollbackDescriptor.getRollbackRefactorings().add(rollbackRefactoringDescriptor);
            if (lastRefactoringOfRollback != null) {
                // link the rollback statements
                lastRefactoringOfRollback.setNextRefactoring(rollbackRefactoringDescriptor);
            }
            lastRefactoringOfRollback = rollbackRefactoringDescriptor;
        }

        return rollbackDescriptor;
    }

    /**
     * Creates a new node for the current changeLog. Check whether an include-node for this changelog already exists, than reuse values from this.
     * 
     * @param path
     *            patch of current file
     * @param scanner
     *            scanner to create {@link Descriptor}
     * @return created {@link ChangeLogDescriptor}
     */
    private ChangeLogDescriptor createChangeLogDescriptor(FileResource item, String path, Scanner scanner) {

        Result<CompositeRowObject> query = scanner.getContext().getStore().executeQuery(String.format(QUERY_FIND_INCLUDE_BY_FILE_NAME, path));
        ChangeLogDescriptor changeLogDescriptor;

        if (query.hasResult()) {
            LOGGER.debug("Found Include for path=[{}]. Apply values to ChangeLog.", path);
            CompositeRowObject queryResult = query.getSingleResult();
            IncludeDescriptor incDesc = queryResult.get("inc", IncludeDescriptor.class);
            changeLogDescriptor = scanner.getContext().getStore().migrate(incDesc, ChangeLogDescriptor.class);

        } else {
            LOGGER.debug("No Include found for path=[{}].", path);
            XmlFileDescriptor xmlFileDescriptor = scanner.scan(item, path, XmlScope.DOCUMENT);
            changeLogDescriptor = scanner.getContext().getStore().addDescriptorType(xmlFileDescriptor, ChangeLogDescriptor.class);
        }

        // Root element - changeLog
        changeLogDescriptor.setFileName(path);

        return changeLogDescriptor;
    }
}
