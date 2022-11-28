ZAP_HOME=$1
if [ -z "${ZAP_HOME}" ]
then
	echo "ZAP Home non definita"
	exit 1
fi
ZAP_SESSION=$2
if [ -z "${ZAP_SESSION}" ]
then
	echo "Identificativo sessione non definito"
	exit 1
fi
ZAP_API_KEY=$3
if [ -z "${ZAP_API_KEY}" ]
then
	echo "API Key non definita"
	exit 1
fi
ZAP_PORT=$4
if [ -z "${ZAP_PORT}" ]
then
	echo "Porta non definita"
	exit 1
fi
ZAP_HOST=$5
if [ -z "${ZAP_HOST}" ]
then
	echo "Host non definito"
	exit 1
fi

ZAP_COMMAND="${JAVA_HOME}/bin/java -classpath "${ZAP_HOME}/*":"${ZAP_HOME}/lib/*" org.zaproxy.zap.ZAP -daemon -session ${ZAP_SESSION} -config api.key=${ZAP_API_KEY} -config port=${ZAP_PORT} -config host=${ZAP_HOST}"

echo "Execute: ${ZAP_COMMAND}"
${ZAP_COMMAND}
