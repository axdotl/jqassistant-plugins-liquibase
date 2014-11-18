package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for an addForeignKeyConstraint change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/add_foreign_key_constraint.html">http://www.liquibase.org/documentation/changes/add_foreign_key_constraint.html</a>
 */
@Label("AddForeignKey")
public interface AddForeignKeyDescriptor extends RefactoringDescriptor {
    
    String getBaseColumnNames();
    
    void setBaseColumnNames(String baseColumnNames);
    
    String getBaseTableName();
    
    void setBaseTableName(String baseTableName);
    
    String getConstraintName();
    
    void setConstraintName(String constraintName);
    
    String getReferencedColumnNames();
    
    void setReferencedColumnNames(String referencedColumnNames);
    
    String getReferencedTableName();
    
    void setReferencedTableName(String referencedTableName);
}
