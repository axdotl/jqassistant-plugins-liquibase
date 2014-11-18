package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.AddNotNullConstraint;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddNotNullConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link AddNotNullConstraint}
 * 
 * @author Axel Koehler
 */
public class AddNotNullConstraintScanner implements LiquibaseElementScanner<AddNotNullConstraint, AddNotNullConstraintDescriptor> {

    @Override
    public AddNotNullConstraintDescriptor scanElement(AddNotNullConstraint element, Scanner scanner) {

        AddNotNullConstraintDescriptor descriptor = scanner.getContext().getStore().create(AddNotNullConstraintDescriptor.class);
        descriptor.setTableName(element.getTableName());
        descriptor.setSchemaName(element.getSchemaName());
        descriptor.setColumnName(element.getColumnName());
        descriptor.setDefaultNullValue(element.getDefaultNullValue());

        return descriptor;
    }

}
