ZAP_DEBUG=$1
if [ -z "${ZAP_DEBUG}" ]
then
	ZAP_DEBUG="true"
fi

ZAP_PID_GREP=$(ps w | grep -v " grep " | grep org.zaproxy.zap.ZAP)
ZAP_PID_GREP_TRIM=$(echo "${ZAP_PID_GREP}" | xargs)
ZAP_PID=$(echo "${ZAP_PID_GREP_TRIM}" | cut -d ' ' -f 1)
if [ -z "${ZAP_PID}" ]
then
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Processo non attivo ?"
	fi
	exit 1
fi
if [ "${ZAP_DEBUG}" == "true" ]
then
	echo "Fermo processo con pid: ${ZAP_PID} ..."
fi
kill -15 ${ZAP_PID}
if [ "${ZAP_DEBUG}" == "true" ]
then
	echo "Processo terminato"
fi
