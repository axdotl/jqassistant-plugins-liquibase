package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.rollback;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Descriptor for the rollback element of a change set.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/rollback.html">http://www.liquibase.org/documentation/rollback.html</a>
 */
@Label("Rollback")
public interface RollbackDescriptor extends LiquibaseDescriptor {

    String getChangeSetId();

    void setChangeSetId(String changeSetId);

    String getChangeSetAuthor();

    void setChangeSetAuthor(String changeSetAuthor);

    String getChangeSetPath();

    void setChangeSetPath(String changeSetPath);

    String getComment();

    void setComment(String comment);

    @Relation("HAS_ROLLBACK_REFACTORING")
    List<RollbackRefactoringDescriptor> getRollbackRefactorings();

    void setRollbackRefactorings(List<RollbackRefactoringDescriptor> rollbackRefactorings);
}
