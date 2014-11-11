package com.github.axdotl.jqassistant.plugins.liquibase.descriptor;

import java.util.List;

import com.buschmais.jqassistant.core.store.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Descriptor for a database change log.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/databasechangelog.html">http://www.liquibase.org/documentation/databasechangelog.html</a>
 */
@Label("ChangeLog")
public interface ChangeLogDescriptor extends FileDescriptor, LiquibaseDescriptor {

    @Relation("HAS_CHANGESET")
    List<ChangeSetDescriptor> getChangeSets();

    @Relation("HAS_INCLUDE")
    List<IncludeDescriptor> getIncludes();
}
