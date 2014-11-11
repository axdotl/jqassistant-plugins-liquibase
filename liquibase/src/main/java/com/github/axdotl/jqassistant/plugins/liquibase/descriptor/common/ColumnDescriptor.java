package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;

/**
 * Descriptor for a column.
 * 
 * @author Axel Koehler
 */
@Label("Column")
public interface ColumnDescriptor extends RefactoringDescriptor {

    String getColumnName();

    void setColumnName(String columnName);

    String getType();

    void setType(String type);

}
