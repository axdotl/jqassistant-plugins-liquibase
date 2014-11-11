package com.github.axdotl.jqassistant.plugins.liquibase.scanner.precondition;

import org.liquibase.xml.ns.dbchangelog.And;
import org.liquibase.xml.ns.dbchangelog.ColumnExists;
import org.liquibase.xml.ns.dbchangelog.Not;
import org.liquibase.xml.ns.dbchangelog.Or;
import org.liquibase.xml.ns.dbchangelog.TableExists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.ColumnExistsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.LogicDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.PreconditionDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.TableExistsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for preconditions. If current element is a logic (AND/OR/NOT) element, also nested elements will be scanned.
 * 
 * @author Axel Koehler
 */
public class PreconditionScanner implements LiquibaseElementScanner<Object, PreconditionDescriptor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreconditionScanner.class);

    @Override
    public PreconditionDescriptor scanElement(Object element, Scanner scanner) {

        LOGGER.debug("Scan precondition: " + element);

        PreconditionDescriptor descriptor = null;

        // ////////////////////////////////////////////////////////////////////
        // LOGIC operators
        if (element instanceof Not) {
            Not not = (Not) element;
            LogicDescriptor logicDescriptor = scanner.getContext().getStore().create(LogicDescriptor.class);
            for (Object o : not.getPreConditionChildren()) {
                logicDescriptor.getNestedPreconditions().add(scanElement(o, scanner));
            }
            descriptor = logicDescriptor;

        } else if (element instanceof And) {
            And and = (And) element;
            LogicDescriptor logicDescriptor = scanner.getContext().getStore().create(LogicDescriptor.class);
            for (Object o : and.getPreConditionChildren()) {
                logicDescriptor.getNestedPreconditions().add(scanElement(o, scanner));
            }
            descriptor = logicDescriptor;

        } else if (element instanceof Or) {
            Or or = (Or) element;
            LogicDescriptor logicDescriptor = scanner.getContext().getStore().create(LogicDescriptor.class);
            for (Object o : or.getPreConditionChildren()) {
                logicDescriptor.getNestedPreconditions().add(scanElement(o, scanner));
            }
            descriptor = logicDescriptor;

        }
        // ////////////////////////////////////////////////////////////////////
        // Preconditions
        else if (element instanceof TableExists) {
            TableExists tableExists = (TableExists) element;
            TableExistsDescriptor tableExistsDescriptor = new TableExistScanner().scanElement(tableExists, scanner);
            descriptor = tableExistsDescriptor;

        } else if (element instanceof ColumnExists) {
            ColumnExists columnExists = (ColumnExists) element;
            ColumnExistsDescriptor columnExistsDescriptor = scanner.getContext().getStore().create(ColumnExistsDescriptor.class);
            columnExistsDescriptor.setSchemaName(columnExists.getSchemaName());
            columnExistsDescriptor.setTableName(columnExists.getTableName());
            columnExistsDescriptor.setColumnName(columnExists.getColumnName());
            descriptor = columnExistsDescriptor;

        } else {
            descriptor = scanner.getContext().getStore().create(PreconditionDescriptor.class);
        }

        descriptor.setName(element.getClass().getSimpleName());

        return descriptor;
    }

}
