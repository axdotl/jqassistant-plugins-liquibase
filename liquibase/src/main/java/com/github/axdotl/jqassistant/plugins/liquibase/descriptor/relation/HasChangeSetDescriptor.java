package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.relation;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeLogDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeSetDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.IndexDescriptor;

@Relation("HAS_CHANGESET")
public interface HasChangeSetDescriptor extends Descriptor, IndexDescriptor {

    @Outgoing
    ChangeLogDescriptor getChangeLog();

    @Incoming
    ChangeSetDescriptor getChangeSet();

}
