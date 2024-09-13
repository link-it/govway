# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
        echo "Il Batch richiede Java Runtime Environment (JRE) 11 (https://jdk.java.net/archive/)"
        echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
        exit 1;
fi

if [ -z $1 ]
then
	echo "Usage error: ./NOME_SCRIPT <archivePath>"
	exit 2;
fi

BATCH_CLASSPATH=lib
BATCH_CONFIG=properties
BATCH_JDBC=jdbc

COMMAND="${JAVA_HOME}/bin/java"
# per consentire il setEnv via govway.map.properties
JAVA_OPTS="$JAVA_OPTS --add-opens=java.base/java.util=ALL-UNNAMED"

${COMMAND} ${JAVA_OPTS} -cp "${BATCH_CLASSPATH}/*":"${BATCH_CONFIG}":"${BATCH_JDBC}/*" org.openspcoop2.pdd.config.loader.cli.Loader TIPO_OPERAZIONE $1
