# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
        echo "Il Batch richiede Java Runtime Environment (JRE) 11 (https://jdk.java.net/archive/)"
        echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
        exit 1;
fi

BATCH_DIR=DIRGESTORE
BATCH_CLASSPATH=${BATCH_DIR}/lib
BATCH_CONFIG=${BATCH_DIR}/properties
BATCH_JDBC=${BATCH_DIR}/jdbc

COMMAND="${JAVA_HOME}/bin/java"

${COMMAND} -cp "${BATCH_CLASSPATH}/*":"${BATCH_CONFIG}":"${BATCH_JDBC}/*" org.openspcoop2.pdd.core.batch.Generator TIPOGESTORE
