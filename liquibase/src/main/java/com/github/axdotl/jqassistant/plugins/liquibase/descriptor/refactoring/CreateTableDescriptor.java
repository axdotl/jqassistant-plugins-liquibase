package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ColumnDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for a createTable change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/create_table.html">http://www.liquibase.org/documentation/changes/create_table.html</a>
 */
@Label("CreateTable")
public interface CreateTableDescriptor extends RefactoringDescriptor, TableDescriptor {

    @Relation("HAS_COLUMN")
    List<ColumnDescriptor> getColumns();
}
