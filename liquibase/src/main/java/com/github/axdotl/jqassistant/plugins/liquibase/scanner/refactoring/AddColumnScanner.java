package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.AddColumn;
import org.liquibase.xml.ns.dbchangelog.Column;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link AddColumn}.
 * 
 * @author Axel Koehler
 */
public class AddColumnScanner implements LiquibaseElementScanner<AddColumn, AddColumnDescriptor> {

    @Override
    public AddColumnDescriptor scanElement(AddColumn element, Scanner scanner) {

        AddColumnDescriptor addColumnDescriptor = scanner.getContext().getStore().create(AddColumnDescriptor.class);
        addColumnDescriptor.setTableName(element.getTableName());

        for (Column column : element.getColumn()) {
            ColumnDescriptor columnDescriptor = new ColumnScanner().scanElement(column, scanner);
            addColumnDescriptor.getColumns().add(columnDescriptor);
        }

        return addColumnDescriptor;
    }

}
