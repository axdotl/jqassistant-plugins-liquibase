package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.AddForeignKeyConstraint;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddForeignKeyDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link AddForeignKeyConstraint}
 * 
 * @author Axel Koehler
 */
public class AddForeignKeyScanner implements LiquibaseElementScanner<AddForeignKeyConstraint, AddForeignKeyDescriptor> {

    @Override
    public AddForeignKeyDescriptor scanElement(AddForeignKeyConstraint element, Scanner scanner) {

        AddForeignKeyDescriptor addForeignKeyDescriptor = scanner.getContext().getStore().create(AddForeignKeyDescriptor.class);
        addForeignKeyDescriptor.setBaseColumnNames(element.getBaseColumnNames());
        addForeignKeyDescriptor.setBaseTableName(element.getBaseTableName());
        addForeignKeyDescriptor.setReferencedColumnNames(element.getReferencedColumnNames());
        addForeignKeyDescriptor.setReferencedTableName(element.getReferencedTableName());
        addForeignKeyDescriptor.setConstraintName(element.getConstraintName());

        return addForeignKeyDescriptor;
    }

}
