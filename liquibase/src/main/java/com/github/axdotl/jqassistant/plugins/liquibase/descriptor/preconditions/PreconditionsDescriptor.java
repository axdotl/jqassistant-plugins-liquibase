package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions;

import java.util.List;

import org.liquibase.xml.ns.dbchangelog.OnChangeLogPreconditionOnSqlOutput;
import org.liquibase.xml.ns.dbchangelog.OnChangeSetPreconditionErrorOrFail;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Descriptor for preconditions.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/preconditions.html">http://www.liquibase.org/documentation/preconditions.html</a>
 */
@Label("Preconditions")
public interface PreconditionsDescriptor extends LiquibaseDescriptor {

    OnChangeSetPreconditionErrorOrFail getOnFail();

    void setOnFail(OnChangeSetPreconditionErrorOrFail value);

    OnChangeSetPreconditionErrorOrFail getOnError();

    void setOnError(OnChangeSetPreconditionErrorOrFail value);

    OnChangeLogPreconditionOnSqlOutput getOnUpdateSql();

    void setOnUpdateSql(OnChangeLogPreconditionOnSqlOutput value);

    String getOnFailMessage();

    void setOnFailMessage(String message);

    String getOnErrorMessage();

    void setOnErrorMessage(String message);

    @Relation("HAS_NESTED_PRECONDITION")
    List<PreconditionDescriptor> getNestedPreconditions();
}
