package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for precondition TableExists.
 * 
 * @author Axel Koehler
 */
@Label("TableExists")
public interface TableExistsDescriptor extends PreconditionDescriptor, TableDescriptor {
}
