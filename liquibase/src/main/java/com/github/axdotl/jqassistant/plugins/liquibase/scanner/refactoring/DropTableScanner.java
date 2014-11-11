package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.liquibase.xml.ns.dbchangelog.DropTable;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.DropTableDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link DropTable}
 * 
 * @author Axel Koehler
 */
public class DropTableScanner implements LiquibaseElementScanner<DropTable, DropTableDescriptor> {

    @Override
    public DropTableDescriptor scanElement(DropTable element, Scanner scanner) {
        DropTableDescriptor descriptor = scanner.getContext().getStore().create(DropTableDescriptor.class);
        descriptor.setSchemaName(element.getSchemaName());
        descriptor.setTableName(element.getTableName());
        return descriptor;
    }

}
