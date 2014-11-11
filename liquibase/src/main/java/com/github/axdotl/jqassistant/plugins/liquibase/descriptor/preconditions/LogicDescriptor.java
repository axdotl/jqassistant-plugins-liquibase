package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Descriptor for precondition logic elements (AND/OR/NOT).
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/preconditions.html">http://www.liquibase.org/documentation/preconditions.html</a>
 */
public interface LogicDescriptor extends PreconditionDescriptor {

    @Relation("HAS_NESTED_PRECONDITION")
    List<PreconditionDescriptor> getNestedPreconditions();
}
