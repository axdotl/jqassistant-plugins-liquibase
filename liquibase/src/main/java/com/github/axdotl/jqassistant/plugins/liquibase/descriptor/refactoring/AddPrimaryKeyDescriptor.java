package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for an addPrimaryKey change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/add_primary_key.html">http://www.liquibase.org/documentation/changes/add_primary_key.html</a>
 */
@Label("AddPrimaryKey")
public interface AddPrimaryKeyDescriptor extends RefactoringDescriptor, TableDescriptor, ConstraintDescriptor {
}
