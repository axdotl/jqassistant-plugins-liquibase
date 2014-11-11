package com.github.axdotl.jqassistant.plugins.liquibase.scanner.precondition;

import org.liquibase.xml.ns.dbchangelog.TableExists;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.TableExistsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

public class TableExistScanner implements LiquibaseElementScanner<TableExists, TableExistsDescriptor> {

    @Override
    public TableExistsDescriptor scanElement(TableExists element, Scanner scanner) {

        TableExistsDescriptor descriptor = scanner.getContext().getStore().create(TableExistsDescriptor.class);

        descriptor.setSchemaName(element.getSchemaName());
        descriptor.setTableName(element.getTableName());

        return descriptor;
    }

}
