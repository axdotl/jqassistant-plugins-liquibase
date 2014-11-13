package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Base interface for all refactoring related descriptors.
 * 
 * @author Axel Koehler
 */
@Label("Refactoring")
public interface RefactoringDescriptor extends LiquibaseDescriptor {

    @Outgoing
    @NextRefactoring
    RefactoringDescriptor getNextRefactoring();

    void setNextRefactoring(RefactoringDescriptor refactoringDescriptor);

    @Incoming
    @NextRefactoring
    RefactoringDescriptor getPreviousRefactoring();

    void setPreviousRefactoring(RefactoringDescriptor refactoringDescriptor);

    String getRefactoringTypeName();

    void setRefactoringTypeName(String refactoringTypeName);

    @Relation("NEXT_REFACTORING")
    @Retention(RUNTIME)
    public @interface NextRefactoring {
    }
}
