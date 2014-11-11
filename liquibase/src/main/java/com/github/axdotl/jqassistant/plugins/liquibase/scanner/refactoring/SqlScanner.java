package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.liquibase.xml.ns.dbchangelog.Sql;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.SqlDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link Sql}.
 * 
 * @author Axel Koehler
 */
public class SqlScanner implements LiquibaseElementScanner<Sql, SqlDescriptor> {

    @Override
    public SqlDescriptor scanElement(Sql element, Scanner scanner) {

        SqlDescriptor descriptor = scanner.getContext().getStore().create(SqlDescriptor.class);
        descriptor.setSplitStatements(BooleanUtils.toBoolean(element.getSplitStatements()));
        descriptor.setEndDelimiter(element.getEndDelimiter());

        if (CollectionUtils.isNotEmpty(element.getContent())) {
            descriptor.setStatement(element.getContent().get(0).toString());
        }

        return descriptor;
    }

}
