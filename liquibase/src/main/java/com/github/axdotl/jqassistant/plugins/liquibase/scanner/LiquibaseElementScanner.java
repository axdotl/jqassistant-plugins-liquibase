package com.github.axdotl.jqassistant.plugins.liquibase.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Intention is, that the given element {@code <I>} will be scanned and related values will be extracted an set to an appropriate descriptor
 * {@code <D>}.
 * 
 * @author Axel Koehler
 */
public interface LiquibaseElementScanner<I, D extends LiquibaseDescriptor> {

    /**
     * Scans given element and set values to result descriptor.
     * 
     * @param element
     *            Scan this element
     * @param scanner
     *            Use it to create {@link Descriptor}(s)
     * @return Well filled descriptor.
     */
    D scanElement(I element, Scanner scanner);
}
