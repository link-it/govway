IDENTITA="username"
PASSWORD="password"
DATABASE="govwaydb"
echo "!! NOTA: Pulizia nel database '${DATABASE}' con user '${IDENTITA}'"

PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -f CleanDatabase_postgresql.sql
