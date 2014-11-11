package com.github.axdotl.jqassistant.plugins.liquibase.descriptor;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
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
}
