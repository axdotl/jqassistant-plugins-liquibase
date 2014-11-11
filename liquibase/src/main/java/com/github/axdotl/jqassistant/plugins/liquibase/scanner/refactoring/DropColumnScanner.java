package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.DropColumn;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.DropColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link DropColumn}
 * 
 * @author Axel Koehler
 */
public class DropColumnScanner implements LiquibaseElementScanner<DropColumn, DropColumnDescriptor> {

    @Override
    public DropColumnDescriptor scanElement(DropColumn element, Scanner scanner) {

        DropColumnDescriptor descriptor = scanner.getContext().getStore().create(DropColumnDescriptor.class);

        descriptor.setColumnName(element.getColumnName());
        descriptor.setSchemaName(element.getSchemaName());
        descriptor.setTableName(element.getTableName());

        return descriptor;
    }

}
