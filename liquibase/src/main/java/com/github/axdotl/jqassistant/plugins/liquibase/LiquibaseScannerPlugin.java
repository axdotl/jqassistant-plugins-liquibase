package com.github.axdotl.jqassistant.plugins.liquibase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.PreconditionsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddPrimaryKeyDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddUniqueConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.CreateTableDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.DropTableDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.SqlDescriptor;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseScannerPlugin.class);
    private JAXBContext jaxbContext;
    XMLInputFactory factory;

    @Override
    protected void initialize() {
        try {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        factory = XMLInputFactory.newInstance();
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

        List<Object> changesets = changeLog.getChangeSetOrIncludeOrIncludeAll();

        for (Object o : changesets) {

            if (o instanceof ChangeSet) {
                ChangeSet changeSet = (ChangeSet) o;
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
                for (Object child : changeSetChildren) {

                    // Looking for the changeset comment
                    if (child instanceof JAXBElement) {
                        @SuppressWarnings("unchecked")
                        String comment = ((JAXBElement<String>) child).getValue();
                        changeSetDescriptor.setComment(comment);
                        continue;
                    }
                    // determine more detailed information about refactoring
                    changeSetDescriptor.getRefactorings().add(scanRefactoring(child, scanner));
                }

                changeLogDescriptor.getChangeSets().add(changeSetDescriptor);
            }

            else if (o instanceof Include) {
                Include include = (Include) o;
                IncludeDescriptor includeDescriptor = scanner.getContext().getStore().create(IncludeDescriptor.class);

                includeDescriptor.setIncludeAll(false);
                includeDescriptor.setFileName(include.getFile());
                includeDescriptor.setRelativeToChangelogFile(BooleanUtils.toBoolean(include.getRelativeToChangelogFile()));

                changeLogDescriptor.getIncludes().add(includeDescriptor);
            }

            else if (o instanceof IncludeAll) {
                IncludeAll includeAll = (IncludeAll) o;
                IncludeDescriptor includeDescriptor = scanner.getContext().getStore().create(IncludeDescriptor.class);

                includeDescriptor.setIncludeAll(true);
                includeDescriptor.setFileName(includeAll.getPath());
                includeDescriptor.setRelativeToChangelogFile(BooleanUtils.toBoolean(includeAll.getRelativeToChangelogFile()));

                changeLogDescriptor.getIncludes().add(includeDescriptor);
            }
        }

        return changeLogDescriptor;
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
    private RefactoringDescriptor scanRefactoring(Object refactoring, Scanner scanner) {

        LOGGER.debug("Scan refactoring: " + refactoring);

        RefactoringDescriptor descriptor = null;

        if (refactoring instanceof CreateTable) {
            CreateTable createTable = (CreateTable) refactoring;
            CreateTableDescriptor createTableDescriptor = new CreateTableScanner().scanElement(createTable, scanner);
            descriptor = createTableDescriptor;

        } else if (refactoring instanceof DropTable) {
            DropTable dropTable = (DropTable) refactoring;
            DropTableDescriptor dropTableDescriptor = new DropTableScanner().scanElement(dropTable, scanner);
            descriptor = dropTableDescriptor;

        } else if (refactoring instanceof Sql) {
            Sql sql = (Sql) refactoring;
            SqlDescriptor sqlDescriptor = new SqlScanner().scanElement(sql, scanner);
            descriptor = sqlDescriptor;

        } else if (refactoring instanceof AddColumn) {
            AddColumn addColumn = (AddColumn) refactoring;
            AddColumnDescriptor addColumnDescriptor = new AddColumnScanner().scanElement(addColumn, scanner);
            descriptor = addColumnDescriptor;

        } else if (refactoring instanceof AddPrimaryKey) {
            AddPrimaryKey addPrimaryKey = (AddPrimaryKey) refactoring;
            AddPrimaryKeyDescriptor addPrimaryKeyDescriptor = new AddPrimaryKeyScanner().scanElement(addPrimaryKey, scanner);
            descriptor = addPrimaryKeyDescriptor;

        } else if (refactoring instanceof AddUniqueConstraint) {
            AddUniqueConstraint addUniqueConstraint = (AddUniqueConstraint) refactoring;
            AddUniqueConstraintDescriptor addUniqueConstraintDescriptor = new AddUniqueConstraintScanner().scanElement(addUniqueConstraint, scanner);
            descriptor = addUniqueConstraintDescriptor;

        } else {
            // default
            descriptor = scanner.getContext().getStore().create(RefactoringDescriptor.class);
        }
        descriptor.setRefactoringTypeName(refactoring.getClass().getSimpleName());

        return descriptor;
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
}
