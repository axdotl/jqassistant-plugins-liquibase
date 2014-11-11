package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for a dropTable change.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/changes/drop_table.html">http://www.liquibase.org/documentation/changes/drop_table.html</a>
 */
@Label("DropTable")
public interface DropTableDescriptor extends RefactoringDescriptor, TableDescriptor {

}
