export CLEARDB_DATABASE_URL=<insert the jdbc url for which you want to create the tables>
sbt "run-main tools.imports.DBMigration"
