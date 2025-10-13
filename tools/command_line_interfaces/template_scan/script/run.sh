# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
        echo "Il Batch richiede Java Runtime Environment (JRE) 11 (https://jdk.java.net/archive/)"
        echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
        exit 1;
fi

if [ -z $1 ]
then
	echo "Usage error: ./NOME_SCRIPT <regex-to-search>"
	exit 2;
fi

TOOL_CLASSPATH=lib
TOOL_CONFIG=properties
TOOL_JDBC=jdbc

COMMAND="${JAVA_HOME}/bin/java"

${COMMAND} -cp "${TOOL_CLASSPATH}/*":"${TOOL_CONFIG}":"${TOOL_JDBC}/*" org.openspcoop2.pdd.template_scan.cli.TemplateScan $1
