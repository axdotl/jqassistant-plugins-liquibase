package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for an addUniqueConstraint change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/add_unique_constraint.html">http://www.liquibase.org/documentation/changes/add_unique_constraint.html</a>
 */
@Label("AddUnique")
public interface AddUniqueConstraintDescriptor extends RefactoringDescriptor, TableDescriptor, ConstraintDescriptor {

    boolean isDisabled();

    void setDisabled(boolean disabled);

    boolean isDeferrable();

    void setDeferrable(boolean deferrable);

    boolean isInitiallyDeferred();

    void setInitiallyDeferred(boolean initiallyDeferred);
}
