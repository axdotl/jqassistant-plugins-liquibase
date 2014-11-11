package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions;

import com.buschmais.jqassistant.core.store.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Base interface for acutal preconditions (e.g. dbms, tableExist, ...)
 * 
 * @author Axel Koehler
 */
@Label("Precondition")
public interface PreconditionDescriptor extends LiquibaseDescriptor, NamedDescriptor {
}
