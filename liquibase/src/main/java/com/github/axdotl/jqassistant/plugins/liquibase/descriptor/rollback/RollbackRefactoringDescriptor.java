package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.rollback;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.RefactoringDescriptor;

/**
 * Marker interface to identify rollback refactoring.
 * 
 * @author Axel Koehler
 */
@Label("RollbackRefactoring")
public interface RollbackRefactoringDescriptor extends RefactoringDescriptor {

}
