//-- Top 10 authors
MATCH (c:ChangeSet)
RETURN c.author, COUNT(c.author)
ORDER BY count(c.author) DESC
LIMIT 10;

//-- Number of changes per author
MATCH (c:ChangeSet)
RETURN c.author, COUNT(c.author)
ORDER BY c.author;

//-- Number of refactorings per type
MATCH (r:Refactoring)
RETURN r.name, COUNT(r.name)
ORDER BY r.name;

//-- All AddPrimaryKey changes w/o a constraint name or constraint name not like '<table_name>_PK'
MATCH (changeLog:ChangeLog)-[:HAS_CHANGESET]->(changeSet:ChangeSet)-[:HAS_REFACTORING]->(pkChange:AddPrimaryKey)
WHERE pkChange.constraintName <> UPPER(pkChange.tableName)+"_PK"
  OR pkChange.constraintName IS NULL
RETURN changeLog.fileName, changeSet.id,  changeSet.author, pkChange.tableName,pkChange.columnNames, pkChange.constraintName
LIMIT 100;

//-- Find createTable changes which have an AddPrimaryKey change as successor
MATCH (createTable:CreateTable)-[:NEXT_REFACTORING*]->(addPk:AddPrimaryKey)
WHERE createTable.tableName = addPk.tableName
RETURN createTable, addPk;
