package com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for a createSequence change.
 * 
 * @author Axel Koehler
 * @see <a
 *      href="http://www.liquibase.org/documentation/changes/create_sequence.html">http://www.liquibase.org/documentation/changes/create_sequence.html</a>
 */
@Label("CreateSequence")
public interface CreateSequenceDescriptor extends RefactoringDescriptor {

    boolean isCycle();

    void setCycle(boolean cycle);

    int getIncrementBy();

    void setIncrementBy(int incrementBy);

    int getMaxValue();

    void setMaxValue(int maxValue);

    int getMinValue();

    void setMinValue(int minValue);

    boolean isOrdered();

    void setOrdered(boolean ordered);

    String getSchemaName();

    void setSchemaName(String schemaName);

    String getSequenceName();

    void setSequenceName(String sequenceName);

    int getStartValue();

    void setStartValue(int startValue);
}
