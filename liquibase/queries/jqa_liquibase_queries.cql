//-- Top 10 authors
MATCH (set:ChangeSet)
RETURN set.author, COUNT(set.author) as number
ORDER BY number DESC
LIMIT 10;

//-- Number of refactorings per type
MATCH (ref:Refactoring)
RETURN ref.name, COUNT(ref.name)
ORDER BY ref.name;

//-- All AddPrimaryKey changes w/o a constraint name or constraint name not like '<table_name>_PK'
MATCH (log:ChangeLog)-[:HAS_CHANGESET]->(set:ChangeSet)-[:HAS_REFACTORING]->(addPk:AddPrimaryKey)
WHERE addPk.constraintName <> UPPER(addPk.tableName)+"_PK"
  OR addPk.constraintName IS NULL
RETURN log.fileName, set.id,  set.author, addPk.tableName, addPk.columnNames, addPk.constraintName
LIMIT 100;

//-- Find createTable changes which have an AddPrimaryKey change as successor
MATCH (createTable:CreateTable)-[:NEXT_REFACTORING*]->(addPk:AddPrimaryKey)
WHERE createTable.tableName = addPk.tableName
RETURN createTable, addPk;
