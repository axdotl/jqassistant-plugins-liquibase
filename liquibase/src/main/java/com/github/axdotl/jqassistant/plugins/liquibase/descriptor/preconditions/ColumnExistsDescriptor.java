package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for precondition ColumnExists.
 * 
 * @author Axel Koehler
 */
@Label("ColumnExists")
public interface ColumnExistsDescriptor extends PreconditionDescriptor, TableDescriptor {

    String getColumnName();

    void setColumnName(String columnName);
}
