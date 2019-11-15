# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
        echo "Il Batch richiede l'installazione di java http://java.sun.com 1.8 o superiore"
        echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
        exit 1;
fi


BATCH_CLASSPATH=lib
BATCH_CONFIG=properties
BATCH_JDBC=jdbc

COMMAND="${JAVA_HOME}/bin/java"

${COMMAND} -cp "${BATCH_CLASSPATH}/*":"${BATCH_CONFIG}":"${BATCH_JDBC}/*" org.openspcoop2.core.statistiche.batch.Generator TIPOSTATISTICA
