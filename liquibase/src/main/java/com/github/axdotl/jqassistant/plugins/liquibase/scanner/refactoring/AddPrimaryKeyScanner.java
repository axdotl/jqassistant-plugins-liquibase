package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.AddPrimaryKey;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddPrimaryKeyDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link AddPrimaryKey}
 * 
 * @author Axel Koehler
 */
public class AddPrimaryKeyScanner implements LiquibaseElementScanner<AddPrimaryKey, AddPrimaryKeyDescriptor> {

    @Override
    public AddPrimaryKeyDescriptor scanElement(AddPrimaryKey element, Scanner scanner) {

        AddPrimaryKeyDescriptor addPrimaryKeyDescriptor = scanner.getContext().getStore().create(AddPrimaryKeyDescriptor.class);
        addPrimaryKeyDescriptor.setTableName(element.getTableName());
        addPrimaryKeyDescriptor.setSchemaName(element.getSchemaName());
        addPrimaryKeyDescriptor.setConstraintName(element.getConstraintName());
        addPrimaryKeyDescriptor.setColumnNames(element.getColumnNames());

        return addPrimaryKeyDescriptor;
    }

}
