package com.github.axdotl.jqassistant.plugins.liquibase;

import java.io.File;
import java.util.List;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeLogDescriptor;

public class ChangeLogTest extends AbstractPluginIT {

    public void scanChangeLogAttributes() {

        Scanner scanner = getScanner();
        File testClassesDir = getClassesDirectory(ChangeLogTest.class);
        File clFile = new File(testClassesDir, "0.1.11.0.xml");
        store.beginTransaction();
        scanner.scan(clFile, "/0.1.11.0.xml", null);
        List<ChangeLogDescriptor> testSuiteDescriptors = query("MATCH (log:ChangeLog:File) RETURN log").getColumn("log");
        store.commitTransaction();
    }
}
