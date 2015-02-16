package com.github.axdotl.jqassistant.plugins.liquibase.descriptor;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Descriptor for an include.
 * 
 * @author Axel Koehler
 * @see <a href="http://www.liquibase.org/documentation/include.html">http://www.liquibase.org/documentation/include.html</a>
 */
@Label("Include")
public interface IncludeDescriptor extends FileDescriptor, LiquibaseDescriptor {

    boolean isIncludeAll();

    void setIncludeAll(boolean includeAll);

    boolean isRelativeToChangelogFile();

    void setRelativeToChangelogFile(boolean relativeToChangelogFile);

    String getFile();

    void setFile(String file);
}
