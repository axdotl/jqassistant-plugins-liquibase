package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.Column;
import org.liquibase.xml.ns.dbchangelog.CreateTable;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.CreateTableDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link CreateTable}.
 * 
 * @author Axel Koehler
 */
public class CreateTableScanner implements LiquibaseElementScanner<CreateTable, CreateTableDescriptor> {

    @Override
    public CreateTableDescriptor scanElement(CreateTable element, Scanner scanner) {

        CreateTableDescriptor createTableDescriptor = scanner.getContext().getStore().create(CreateTableDescriptor.class);
        createTableDescriptor.setTableName(element.getTableName());

        for (Column column : element.getColumn()) {
            ColumnDescriptor columnDescriptor = scanner.getContext().getStore().create(ColumnDescriptor.class);
            columnDescriptor.setColumnName(column.getName());
            columnDescriptor.setType(column.getType());

            createTableDescriptor.getColumns().add(columnDescriptor);
        }
        return createTableDescriptor;
    }
}
