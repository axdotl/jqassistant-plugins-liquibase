package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.Column;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for a {@link Column}
 * 
 * @author Axel Koehler
 */
public class ColumnScanner implements LiquibaseElementScanner<Column, ColumnDescriptor> {

    @Override
    public ColumnDescriptor scanElement(Column element, Scanner scanner) {

        ColumnDescriptor columnDescriptor = scanner.getContext().getStore().create(ColumnDescriptor.class);
        columnDescriptor.setColumnName(element.getName());
        columnDescriptor.setType(element.getType());
        columnDescriptor.setRefactoringTypeName(element.getClass().getSimpleName());

        return columnDescriptor;
    }
}
