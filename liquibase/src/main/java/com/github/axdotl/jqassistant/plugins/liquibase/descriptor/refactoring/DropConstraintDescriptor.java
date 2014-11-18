package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for a drop constraint changes.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/drop_primary_key.html">http://www.liquibase.org/documentation/changes/drop_primary_key.html</a>
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/drop_foreign_key_constraint.html">http://www.liquibase.org/documentation/changes/drop_foreign_key_constraint.html</a>
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/drop_unique_constraint.html">http://www.liquibase.org/documentation/changes/drop_unique_constraint.html</a>
 */
@Label("DropConstraint")
public interface DropConstraintDescriptor extends RefactoringDescriptor, TableDescriptor, ConstraintDescriptor {

}
