package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.ConstraintDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for a dropPrimaryKey change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/drop_primary_key.html">http://www.liquibase.org/documentation/changes/drop_primary_key.html</a>
 */
@Label("DropPrimaryKey")
public interface DropPrimaryKeyDescriptor extends RefactoringDescriptor, TableDescriptor, ConstraintDescriptor {

}
