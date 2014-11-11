package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Base interface for all refactoring related descriptors.
 * 
 * @author Axel Koehler
 */
@Label("Refactoring")
public interface RefactoringDescriptor extends LiquibaseDescriptor {

    String getRefactoringTypeName();

    void setRefactoringTypeName(String refactoringTypeName);
}
