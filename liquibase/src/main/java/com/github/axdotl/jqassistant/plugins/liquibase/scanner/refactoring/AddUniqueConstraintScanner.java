package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.apache.commons.lang.BooleanUtils;
import org.liquibase.xml.ns.dbchangelog.AddUniqueConstraint;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.AddUniqueConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link AddUniqueConstraint}
 * 
 * @author Axel Koehler
 */
public class AddUniqueConstraintScanner implements LiquibaseElementScanner<AddUniqueConstraint, AddUniqueConstraintDescriptor> {

    @Override
    public AddUniqueConstraintDescriptor scanElement(AddUniqueConstraint element, Scanner scanner) {

        AddUniqueConstraintDescriptor descriptor = scanner.getContext().getStore().create(AddUniqueConstraintDescriptor.class);

        descriptor.setTableName(element.getTableName());
        descriptor.setSchemaName(element.getSchemaName());
        descriptor.setConstraintName(element.getConstraintName());
        descriptor.setColumnNames(element.getColumnNames());
        descriptor.setDisabled(BooleanUtils.toBoolean(element.getDisabled()));
        descriptor.setDeferrable(BooleanUtils.toBoolean(element.getDeferrable()));
        descriptor.setInitiallyDeferred(BooleanUtils.toBoolean(element.getInitiallyDeferred()));

        return descriptor;
    }

}
