package com.github.axdotl.jqassistant.plugins.liquibase;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeLogDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.ChangeSetDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.IncludeDescriptor;
import com.github.axdotl.jqassistant.plugins.liquibase.descriptor.LiquibaseDescriptor;

/**
 * Test class for the {@link LiquibaseScannerPlugin}.
 * 
 * @author Axel Koehler
 */
public class LiquibaseScannerShould extends AbstractPluginIT {

    private final static String DIR_NON_CHANGELOG = "nonChangeLog";
    private final static String DIR_CHANGELOG = "changeLog";
    private final static String FILE_MASTER = "master.xml";
    private final static String FILE_VALID_CHANGELOG = "validChangeLog.xml";
    private final static String FILE_INCLUDED_BY_MASTER = "includedByMaster.xml";
    private final static String SET_ATTR_ID_COMPLETE = "complete";

    private Scanner scanner;
    private File testClassesDir;

    /**
     * Reset scanner and get classes directory.
     */
    @Before
    public void prepare() {

        scanner = getScanner();
        testClassesDir = getClassesDirectory(LiquibaseScannerShould.class);
    }

    /**
     * Test whether non-changelog-files will be rejected (not accepted).
     */
    @Test
    public void acceptOnlyDatabaseChangeLogFiles() {

        File nonChangeLogDir = new File(testClassesDir, DIR_NON_CHANGELOG);
        File[] files = nonChangeLogDir.listFiles();
        store.beginTransaction();

        for (File f : files) {

            Descriptor descriptor = scanner.scan(f, "/" + f.getName(), null);
            if (descriptor.getClass().isAssignableFrom(LiquibaseDescriptor.class)) {
                Assert.fail("File [" + f.getName() + "] has to be rejected.");
            }
        }
        store.commitTransaction();
    }

    /**
     * Test whether includes will be detected.
     */
    @Test
    public void detectIncludes() {

        File masterFile = new File(testClassesDir, DIR_CHANGELOG + "/" + FILE_MASTER);
        store.beginTransaction();

        ChangeLogDescriptor changeLogDescriptor = scanner.scan(masterFile, "/" + DIR_CHANGELOG + "/" + FILE_MASTER, null);
        List<IncludeDescriptor> includes = changeLogDescriptor.getIncludes();
        Assert.assertNotNull("Includes expected.", includes);
        Assert.assertEquals("Includes expected.", 2, includes.size());

        store.commitTransaction();
    }

    /**
     * Test whether the available sub-tags <i>comment, preConditions, &lt;refactorings&gt;, rollback</i> will be applied.
     */
    @Test
    public void applyChangeSetSubTags() {

        File changeLogFile = new File(testClassesDir, DIR_CHANGELOG + "/" + FILE_VALID_CHANGELOG);
        store.beginTransaction();

        ChangeLogDescriptor changeLogDescriptor = scanner.scan(changeLogFile, "/" + DIR_CHANGELOG + "/" + FILE_VALID_CHANGELOG, null);

        List<ChangeSetDescriptor> changeSets = changeLogDescriptor.getChangeSets();
        Assert.assertNotNull("ChangeSets expected.", changeSets);
        Assert.assertEquals("ChangeSets expected.", 2, changeSets.size());

        for (ChangeSetDescriptor changeSetDescriptor : changeSets) {
            if (SET_ATTR_ID_COMPLETE.equals(changeSetDescriptor.getId())) {
                Assert.assertNotNull("ChangeSet attribute author is missing.", changeSetDescriptor.getAuthor());
                Assert.assertNotNull("ChangeSet atrribute 'comment' is missing.", changeSetDescriptor.getComment());
                Assert.assertNotNull("ChangeSet atrribute 'preCondition' is missing.", changeSetDescriptor.getPreconditions());
                Assert.assertNotNull("ChangeSet atrribute 'rollback' is missing.", changeSetDescriptor.getRollback());
            }
        }

        store.commitTransaction();
    }

    /**
     * Test whether an existing Include-node is reused when scanning a ChangeLog i.s.o. creating a new/further node. Attributes has to be applied.
     */
    @Test
    public void reuseIncludeNodeWhenScanningChangeLog() {

        File changeLogDir = new File(testClassesDir, DIR_CHANGELOG);
        File[] changeLogFiles = changeLogDir.listFiles();
        store.beginTransaction();

        // scan all files in folder
        for (File file : changeLogFiles) {
            scanner.scan(file, "/" + DIR_CHANGELOG + "/" + file.getName(), null);
        }

        // Verify
        List<ChangeLogDescriptor> changeLogs = query("MATCH (inc:ChangeLog) RETURN inc").getColumn("inc");
        for (ChangeLogDescriptor changeLog : changeLogs) {
            String fileName = changeLog.getFileName();
            if (StringUtils.endsWith(fileName, FILE_INCLUDED_BY_MASTER)) {
                Assert.assertNotNull("ChangeLog is included in master.xml, so Include#file has to be set.", changeLog.getFile());
                Assert.assertEquals("Include#file is an absolut path. So Include#file and File#fileName has to be equal.", changeLog.getFile(),
                        fileName);
            } else {
                Assert.assertNull("ChangeLog is nowhere included, so Include#file has to be null.", changeLog.getFile());
            }
        }
        store.commitTransaction();
    }
}
