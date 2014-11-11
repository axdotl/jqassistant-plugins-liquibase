package com.github.axdotl.jqassistant.plugins.liquibase.descriptor;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Base interface for all descriptors of this JQA Liquibase plugin.
 * 
 * @author Axel Koehler
 */
@Label("Liquibase")
public interface LiquibaseDescriptor extends Descriptor {

}
