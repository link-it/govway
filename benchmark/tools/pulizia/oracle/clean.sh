IDENTITA="username"
PASSWORD="password"
echo "!! NOTA: Pulizia nel database '${IDENTITA}'"

sqlplus ${IDENTITA}/${PASSWORD} < CleanDatabase_oracle.sql
