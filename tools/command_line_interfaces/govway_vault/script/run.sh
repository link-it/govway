# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
        echo "Il Batch richiede Java Runtime Environment (JRE) 11 (https://jdk.java.net/archive/)"
        echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
        exit 1;
fi

VAULT_CLASSPATH=lib
VAULT_CONFIG=properties
VAULT_JDBC=jdbc

COMMAND="${JAVA_HOME}/bin/java"
# per consentire il setEnv via govway.map.properties
JAVA_OPTS="$JAVA_OPTS --add-opens=java.base/java.util=ALL-UNNAMED"

${COMMAND} ${JAVA_OPTS} -cp "${VAULT_CLASSPATH}/*":"${VAULT_CONFIG}":"${VAULT_JDBC}/*" org.openspcoop2.pdd.config.vault.cli.VaultTools TIPO_OPERAZIONE $*
