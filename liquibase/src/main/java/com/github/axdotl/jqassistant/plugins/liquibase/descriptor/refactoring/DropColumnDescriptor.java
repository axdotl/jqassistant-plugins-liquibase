package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for a dropColumn change.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/changes/drop_column.html">http://www.liquibase.org/documentation/changes/drop_column.html</a>
 */
@Label("DropColumn")
public interface DropColumnDescriptor extends RefactoringDescriptor, TableDescriptor {

    /**
     * Name of the column to drop.
     * 
     * @return column name
     */
    String getColumnName();

    /**
     * Sets the name of the column to drop.
     * 
     * @param columnName
     *            column name
     */
    void setColumnName(String columnName);
}
