ZAP_HOME=$1
if [ -z "${ZAP_HOME}" ]
then
	echo "ZAP Home non definita"
	exit 1
fi

ZAP_PORT=$2
if [ -z "${ZAP_PORT}" ]
then
	echo "Porta non definita"
	exit 1
fi
ZAP_HOST=$3
if [ -z "${ZAP_HOST}" ]
then
	echo "Host non definito"
	exit 1
fi

ZAP_COMMAND="${JAVA_HOME}/bin/java -classpath "${ZAP_HOME}/*":"${ZAP_HOME}/lib/*" org.zaproxy.zap.ZAP -cmd -addonupdate -port ${ZAP_PORT} -host ${ZAP_HOST}"

echo "Execute: ${ZAP_COMMAND}"
${ZAP_COMMAND}

