package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.common;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;

/**
 * Descriptor for all table related elements (e.g. createTable, addColumn, ...)
 * 
 * @author Axel Koehler
 */
public interface TableDescriptor extends Descriptor {

    String getTableName();

    void setTableName(String tableName);

    String getSchemaName();

    void setSchemaName(String schemaName);
}
