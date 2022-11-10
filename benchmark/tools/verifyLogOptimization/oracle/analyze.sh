IDENTITA="USERNAME"
PASSWORD="PASSWORD"
echo "!! NOTA: Analisi nel database '${IDENTITA}'"

DIR=${PWD}

rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select DIAGNOSTICI from transazioni WHERE DIAGNOSTICI NOT LIKE 'R%' AND ROWNUM<=1 order by data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevati diagnostici non simulati: ${RESULT}"
	exit 2
fi



rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select DIAGNOSTICI_EXT from transazioni WHERE DIAGNOSTICI_EXT is not null AND ROWNUM<=1 order by data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevati diagnostici non simulati e serializzati in ext: ${RESULT}"
	exit 2
fi



rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select transazioni.id from transazioni, msgdiagnostici WHERE transazioni.id=msgdiagnostici.id_transazione AND ROWNUM<=1 order by transazioni.data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevati diagnostici non simulati e scritti nella tabella dei messaggi diagnostici: ${RESULT}"
	exit 2
fi



rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select TRACCIA_RICHIESTA from transazioni WHERE TRACCIA_RICHIESTA IS NOT NULL AND NOT TRACCIA_RICHIESTA LIKE 'R%' AND ROWNUM<=1 order by data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevata traccia di richiesta non simulata: ${RESULT}"
	exit 2
fi



rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select TRACCIA_RISPOSTA from transazioni WHERE TRACCIA_RISPOSTA IS NOT NULL AND NOT TRACCIA_RICHIESTA LIKE 'R%' AND ROWNUM<=1 order by data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevata traccia di risposta non simulata: ${RESULT}"
	exit 2
fi




rm -f /tmp/out.csv
echo "spool /tmp/out.csv" > /tmp/COMMANDO.sql
echo "SET heading OFF; " >> /tmp/COMMANDO.sql
echo "select transazioni.id from transazioni, tracce WHERE transazioni.id=tracce.id_transazione AND ROWNUM<=1 order by transazioni.data_ingresso_richiesta DESC;" >> /tmp/COMMANDO.sql
echo "spool off" >> /tmp/COMMANDO.sql
sqlplus ${IDENTITA}/${PASSWORD} < /tmp/COMMANDO.sql
#rm /tmp/COMMANDO.sql

RESULT=$(cat /tmp/out.csv)
RESULT_GREP=$(grep "no rows selected" /tmp/out.csv)
if [ -z "${RESULT_GREP}" ]
then
	echo "Rilevate tracce non simulate e scritte nella tabella apposita: ${RESULT}"
	exit 2
fi


