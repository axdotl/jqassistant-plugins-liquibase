package com.github.axdotl.jqassistant.plugins.liquibase.scanner.refactoring;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.liquibase.xml.ns.dbchangelog.CreateSequence;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.refactoring.CreateSequenceDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.scanner.LiquibaseElementScanner;

/**
 * Scanner for {@link CreateSequence}.
 * 
 * @author Axel Koehler
 */
public class CreateSequenceScanner implements LiquibaseElementScanner<CreateSequence, CreateSequenceDescriptor> {

    @Override
    public CreateSequenceDescriptor scanElement(CreateSequence element, Scanner scanner) {

        CreateSequenceDescriptor createSequenceDescriptor = scanner.getContext().getStore().create(CreateSequenceDescriptor.class);
        createSequenceDescriptor.setCycle(BooleanUtils.toBoolean(element.getCycle()));
        createSequenceDescriptor.setIncrementBy(NumberUtils.toInt(element.getIncrementBy()));
        createSequenceDescriptor.setMaxValue(NumberUtils.toInt(element.getMaxValue()));
        createSequenceDescriptor.setMinValue(NumberUtils.toInt(element.getMinValue()));
        createSequenceDescriptor.setOrdered(BooleanUtils.toBoolean(element.getOrdered()));
        createSequenceDescriptor.setSchemaName(element.getSchemaName());
        createSequenceDescriptor.setSequenceName(element.getSequenceName());
        createSequenceDescriptor.setStartValue(NumberUtils.toInt(element.getStartValue()));

        return createSequenceDescriptor;
    }
}
