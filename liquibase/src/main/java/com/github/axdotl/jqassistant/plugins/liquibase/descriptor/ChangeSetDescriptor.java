package com.github.axdotl.jqassistant.plugins.liquibase.descriptor;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.preconditions.PreconditionsDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;

/**
 * Descriptor for a database change set.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/changeset.html">http://www.liquibase.org/documentation/changeset.html</a>
 */
@Label("ChangeSet")
public interface ChangeSetDescriptor extends LiquibaseDescriptor {

    String getAuthor();

    void setAuthor(String author);

    String getId();

    void setId(String id);

    String getComment();

    void setComment(String comment);

    @Relation("HAS_REFACTORING")
    List<RefactoringDescriptor> getRefactorings();

    @Relation("HAS_PRECONDITION")
    List<PreconditionsDescriptor> getPreconditions();

    @Outgoing
    @NextChangeSet
    ChangeSetDescriptor getNextChangeSet();

    void setNextChangeSet(ChangeSetDescriptor changeSetDescriptor);

    @Incoming
    @NextChangeSet
    ChangeSetDescriptor getPreviousChangeSet();

    void setPreviousChangeSet(ChangeSetDescriptor changeSetDescriptor);

    @Relation("NEXT_CHANGESET")
    @Retention(RUNTIME)
    public @interface NextChangeSet {
    }
}
