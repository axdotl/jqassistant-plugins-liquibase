package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import org.liquibase.xml.ns.dbchangelog.AddNotNullConstraint;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common.TableDescriptor;

/**
 * Descriptor for an {@link AddNotNullConstraint} change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/add_not_null_constraint.html">http://www.liquibase.org/documentation/changes/add_not_null_constraint.html</a>
 */
@Label("AddNotNull")
public interface AddNotNullConstraintDescriptor extends RefactoringDescriptor, TableDescriptor {

    String getDefaultNullValue();

    void setDefaultNullValue(String defaultNullValue);

    String getColumnName();

    void setColumnName(String columnName);

}
