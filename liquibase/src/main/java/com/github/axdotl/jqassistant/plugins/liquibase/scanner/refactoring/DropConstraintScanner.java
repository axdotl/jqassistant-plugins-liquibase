package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.DropForeignKeyConstraint;
import org.liquibase.xml.ns.dbchangelog.DropPrimaryKey;
import org.liquibase.xml.ns.dbchangelog.DropUniqueConstraint;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.DropConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for
 * <ul>
 * <li>{@link DropPrimaryKey}</li>
 * <li>{@link DropForeignKeyConstraint}</li>
 * <li>{@link DropUniqueConstraint}</li>
 * </ul>
 * 
 * @author Axel Koehler
 */
public class DropConstraintScanner implements LiquibaseElementScanner<Object, DropConstraintDescriptor> {

    @Override
    public DropConstraintDescriptor scanElement(Object element, Scanner scanner) {
        DropConstraintDescriptor descriptor = scanner.getContext().getStore().create(DropConstraintDescriptor.class);

        String schemaName = "";
        String tableName = "";
        String constraintName = "";

        if (element instanceof DropPrimaryKey) {
            DropPrimaryKey dropPrimaryKey = (DropPrimaryKey) element;
            schemaName = dropPrimaryKey.getSchemaName();
            tableName = dropPrimaryKey.getTableName();
            constraintName = dropPrimaryKey.getConstraintName();

        } else if (element instanceof DropForeignKeyConstraint) {
            DropForeignKeyConstraint dropForeignKey = (DropForeignKeyConstraint) element;
            schemaName = dropForeignKey.getBaseTableName();
            tableName = dropForeignKey.getBaseTableName();
            constraintName = dropForeignKey.getConstraintName();

        } else if (element instanceof DropUniqueConstraint) {
            DropUniqueConstraint dropUniqueKey = (DropUniqueConstraint) element;
            schemaName = dropUniqueKey.getSchemaName();
            tableName = dropUniqueKey.getTableName();
            constraintName = dropUniqueKey.getConstraintName();

        } else {
            throw new IllegalArgumentException("Unsupported dropConstraint");
        }

        descriptor.setSchemaName(schemaName);
        descriptor.setTableName(tableName);
        descriptor.setConstraintName(constraintName);

        return descriptor;
    }

}
