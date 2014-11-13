package com.github.axdotl.jqassistant.plugins.liquibase;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.liquibase.xml.ns.dbchangelog.AddColumn;
import org.liquibase.xml.ns.dbchangelog.AddPrimaryKey;
import org.liquibase.xml.ns.dbchangelog.AddUniqueConstraint;
import org.liquibase.xml.ns.dbchangelog.CreateTable;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.ChangeSet;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.ChangeSet.PreConditions;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.Include;
import org.liquibase.xml.ns.dbchangelog.DatabaseChangeLog.IncludeAll;
import org.liquibase.xml.ns.dbchangelog.DropTable;
import org.liquibase.xml.ns.dbchangelog.ObjectFactory;
import org.liquibase.xml.ns.dbchangelog.Sql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeLogDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeSetDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.IncludeDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.PreconditionsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.precondition.PreconditionScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.AddColumnScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.AddPrimaryKeyScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.AddUniqueConstraintScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.CreateTableScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.DropTableScanner;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring.SqlScanner;

/**
 * Scanner for Liquibase database change log files.
 * 
 * @version <b>0.1:</b> Supports <a href=
 *          "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd</a>
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/">http://www.liquibase.org/documentation/</a>
 */
public class LiquibaseScannerPlugin extends AbstractScannerPlugin<FileResource, ChangeLogDescriptor> {

    /** It's the logger my friend. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseScannerPlugin.class);
    /** Mapping of refactoring elements to related scanner instance. */
    @SuppressWarnings("rawtypes")
    private final Map<Class, LiquibaseElementScanner> scannerMap = new HashMap<Class, LiquibaseElementScanner>();
    /** Used to unmarshal changelog, will be initialized once to improve performance. */
    private JAXBContext jaxbContext;
    /** Used to check whether the given file has <code>databaseChangeLog</code> as root element -&gt; it's a liquibase changelog file. */
    private XMLInputFactory factory;

    @Override
    protected void initialize() {
        try {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        factory = XMLInputFactory.newInstance();

        // Register scanner instances for certain refactoring types
        scannerMap.put(CreateTable.class, new CreateTableScanner());
        scannerMap.put(DropTable.class, new DropTableScanner());
        scannerMap.put(Sql.class, new SqlScanner());
        scannerMap.put(AddColumn.class, new AddColumnScanner());
        scannerMap.put(AddPrimaryKey.class, new AddPrimaryKeyScanner());
        scannerMap.put(AddUniqueConstraint.class, new AddUniqueConstraintScanner());
    }

    @Override
    public boolean accepts(FileResource item, String path, Scope scope) throws IOException {

        // Accept only XML files
        if (!path.toLowerCase().endsWith(".xml")) {
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

        // Root element - changeLog
        ChangeLogDescriptor changeLogDescriptor = scanner.getContext().getStore().create(ChangeLogDescriptor.class);

        // Unmarshal DatabaseChangeLog
        DatabaseChangeLog changeLog;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            changeLog = unmarshaller.unmarshal(new StreamSource(item.createStream()), DatabaseChangeLog.class).getValue();

        } catch (JAXBException e) {
            LOGGER.error("Fail to unmarschal DatabaseChangeLog.", e);
            return null;
        }

        List<Object> changeLogChildren = changeLog.getChangeSetOrIncludeOrIncludeAll();

        ChangeSetDescriptor lastChangeSetOfChangeLog = null;
        for (Object o : changeLogChildren) {

            if (o instanceof ChangeSet) {
                ChangeSetDescriptor setDescriptor = scanChangeSet(scanner, (ChangeSet) o);

                if (lastChangeSetOfChangeLog != null) {
                    lastChangeSetOfChangeLog.setNextChangeSet(setDescriptor);
                }
                lastChangeSetOfChangeLog = setDescriptor;
                changeLogDescriptor.getChangeSets().add(setDescriptor);
            }

            else if (o instanceof Include) {
                IncludeDescriptor includeDescriptor = scanInclude(scanner, (Include) o);
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
     * @param changeSet
     * @return
     */
    private ChangeSetDescriptor scanChangeSet(Scanner scanner, ChangeSet changeSet) {
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

        RefactoringDescriptor lastRefactoringOfChangeSet = null;
        for (Object child : changeSetChildren) {

            // Looking for the changeset comment
            if (child instanceof JAXBElement) {
                @SuppressWarnings("unchecked")
                String comment = ((JAXBElement<String>) child).getValue();
                changeSetDescriptor.setComment(comment);
                continue;
            }
            // determine more detailed information about refactoring
            RefactoringDescriptor refactoringDescriptor = scanRefactoring(child, scanner);
            if (lastRefactoringOfChangeSet != null) {
                lastRefactoringOfChangeSet.setNextRefactoring(refactoringDescriptor);
            }
            lastRefactoringOfChangeSet = refactoringDescriptor;
            changeSetDescriptor.getRefactorings().add(refactoringDescriptor);
        }
        return changeSetDescriptor;
    }

    private IncludeDescriptor scanInclude(Scanner scanner, Include include) {
        IncludeDescriptor includeDescriptor = scanner.getContext().getStore().create(IncludeDescriptor.class);

        includeDescriptor.setIncludeAll(false);
        includeDescriptor.setFileName(include.getFile());
        includeDescriptor.setRelativeToChangelogFile(BooleanUtils.toBoolean(include.getRelativeToChangelogFile()));

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
     * Scans a single refactoring.
     * 
     * @param refactoring
     *            current element
     * @param scanner
     *            scanner to create elements
     * @return created descriptor
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private RefactoringDescriptor scanRefactoring(Object refactoring, Scanner scanner) {

        LOGGER.debug("Scan refactoring: " + refactoring);

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
}
