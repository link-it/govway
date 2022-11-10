IDENTITA="username"
PASSWORD="password"
DATABASE="govwaydb"
echo "!! NOTA: Analisi nel database '${DATABASE}' con user '${IDENTITA}'"

DIR=${PWD}

RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select DIAGNOSTICI from transazioni WHERE DIAGNOSTICI NOT LIKE 'R%' order by data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevati diagnostici non simulati: ${RESULT}"
	exit 2
fi

RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select DIAGNOSTICI_EXT from transazioni WHERE DIAGNOSTICI_EXT is not null order by data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevati diagnostici non simulati e serializzati in ext: ${RESULT}"
	exit 2
fi


RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select transazioni.id from transazioni, msgdiagnostici WHERE transazioni.id=msgdiagnostici.id_transazione order by transazioni.data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevati diagnostici non simulati e scritti nella tabella dei messaggi diagnostici: ${RESULT}"
	exit 2
fi


RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select TRACCIA_RICHIESTA from transazioni WHERE TRACCIA_RICHIESTA IS NOT NULL AND NOT TRACCIA_RICHIESTA LIKE 'R%' order by data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevata traccia di richiesta non simulata: ${RESULT}"
	exit 2
fi


RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select TRACCIA_RISPOSTA from transazioni WHERE TRACCIA_RISPOSTA IS NOT NULL AND NOT TRACCIA_RICHIESTA LIKE 'R%' order by data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevata traccia di risposta non simulata: ${RESULT}"
	exit 2
fi


RESULT=$(PGPASSWORD=${PASSWORD} psql ${DATABASE} ${IDENTITA} -t -c "select transazioni.id from transazioni, tracce WHERE transazioni.id=tracce.id_transazione order by transazioni.data_ingresso_richiesta DESC LIMIT 1;")
if [ ! -z "${RESULT}" ]
then
	echo "Rilevate tracce non simulate e scritte nella tabella apposita: ${RESULT}"
	exit 2
fi



