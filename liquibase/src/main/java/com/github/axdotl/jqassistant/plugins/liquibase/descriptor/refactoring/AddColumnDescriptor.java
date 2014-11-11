package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for an addColumn change.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/changes/add_column.html">http://www.liquibase.org/documentation/changes/add_column.html</a>
 */
@Label("AddColumn")
public interface AddColumnDescriptor extends RefactoringDescriptor, TableDescriptor {

    @Relation("HAS_COLUMN")
    List<ColumnDescriptor> getColumns();
}
