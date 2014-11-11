package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;

/**
 * Constraint related descriptor.
 * 
 * @author Axel Koehler
 */
public interface ConstraintDescriptor extends Descriptor {

    String getConstraintName();

    void setConstraintName(String constraintName);

    String getColumnNames();

    void setColumnNames(String columnNames);
}
