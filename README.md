## jQAssistant-plugins-liquibase

Liquibase plugin for [jQAssistant](https://github.com/buschmais/jqassistant)

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
