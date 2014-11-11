package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a SQL change.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/changes/sql.html">http://www.liquibase.org/documentation/changes/sql.html</a>
 */
@Label("Sql")
public interface SqlDescriptor extends RefactoringDescriptor {

    String getStatement();

    void setStatement(String statement);

    boolean isSplitStatements();

    void setSplitStatements(boolean splitStatements);

    String getEndDelimiter();

    void setEndDelimiter(String delimiter);
}
