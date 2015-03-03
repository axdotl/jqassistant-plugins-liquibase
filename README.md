## jQAssistant-plugins-liquibase

Liquibase plugin for [jQAssistant](https://jqassistant.org)

[![Build Status](https://travis-ci.org/axdotl/jqassistant-plugins-liquibase.svg)](https://travis-ci.org/axdotl/jqassistant-plugins-liquibase)

[ ![Download](https://api.bintray.com/packages/axdotl/maven/jqassistant-plugins-liquibase/images/download.svg) ](https://bintray.com/axdotl/maven/jqassistant-plugins-liquibase/_latestVersion)

### Screen
![Latest](https://github.com/axdotl/jqassistant-plugins-liquibase/blob/master/liquibase/screens/latest.png)

### Available constraints
* ChangeSet
  * w/o ID
  * w/o author
  * w/o comment
* SQL refactoring w/o rollback
* DROP refactorings w/o preCondition


### Example queries
```
// Top 10 authors
MATCH (set:ChangeSet)
RETURN set.author, COUNT(set.author) as number
ORDER BY number DESC
LIMIT 10;

// All AddPrimaryKey changes w/o a constraint name or constraint name not like '<table_name>_PK'
MATCH (log:ChangeLog)-[:HAS_CHANGESET]->(set:ChangeSet)-[:HAS_REFACTORING]->(addPk:AddPrimaryKey)
WHERE addPk.constraintName <> UPPER(addPk.tableName)+"_PK"
  OR addPk.constraintName IS NULL
RETURN log.fileName, set.id,  set.author, addPk.tableName, addPk.columnNames, addPk.constraintName
LIMIT 100;
```

### Getting started using Maven

* Add the dependency to the jQAssistant Maven plugin

```xml
<build>
	<plugins>
		<plugin>
			<groupId>com.buschmais.jqassistant.scm</groupId>
			<artifactId>jqassistant-maven-plugin</artifactId>
			<version>1.0.0-RC1</version>
			<executions>
				<execution>
					<goals>
						<goal>scan</goal>
						<goal>analyze</goal>
					</goals>
				</execution>
			</executions>
			<dependencies>
				<dependency>
					<groupId>com.github.axdotl</groupId>
					<artifactId>jqassistant-plugins-liquibase</artifactId>
					<version>0.0.4</version>
				</dependency>
			</dependencies>
		</plugin>
	</plugins>
</build>
```

* Check availability of the rules

```
mvn jqassistant:available-rules
```

The output will contain the following constraints:

```
"liquibase:ChangeSetWithoutComment" - All change sets has to specify a comment.
"liquibase:ChangeSetWithoutId" - All change sets has to specify the id attribute.
"liquibase:SqlRefactoringWithoutRollback" - A change with SQL refactoring must provide a rollback element.
```

* Scan and analyze

Note: your liquibase changelog descriptors must be classpath resources (e.g. located in "src/main/resources")

```
mvn jqassistant:scan
mvn jqassistant:analyze -Djqassistant.constraints=liquibase:ChangeSetWithoutComment,liquibase:ChangeSetWithoutId,liquibase:SqlRefactoringWithoutRollback
```
