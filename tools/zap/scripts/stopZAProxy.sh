ZAP_DEBUG=$1
if [ -z "${ZAP_DEBUG}" ]
then
	ZAP_DEBUG="true"
fi

ZAP_PID_GREP=$(ps axww | grep -v " grep " | grep org.zaproxy.zap.ZAP)
ZAP_PID_GREP_TRIM=$(echo "${ZAP_PID_GREP}" | xargs)
ZAP_PID=$(echo "${ZAP_PID_GREP_TRIM}" | cut -d ' ' -f 1)
if [ -z "${ZAP_PID}" ]
then
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Processo non attivo ?"
	fi
else
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Fermo processo con pid: ${ZAP_PID} ..."
	fi
	kill -15 ${ZAP_PID}
	# Attende che il processo ZAP termini (max 30 secondi)
	count=0
	while kill -0 ${ZAP_PID} 2>/dev/null && [ $count -lt 30 ]; do
		sleep 1
		count=$((count+1))
	done
	if kill -0 ${ZAP_PID} 2>/dev/null; then
		if [ "${ZAP_DEBUG}" == "true" ]
		then
			echo "Processo ZAP non terminato dopo 30 secondi, invio SIGKILL ..."
		fi
		kill -9 ${ZAP_PID} 2>/dev/null
	fi
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Processo terminato"
	fi
fi

# Chiude eventuali processi Firefox headless orfani lanciati da ZAP
FIREFOX_PIDS=$(pgrep -f "firefox.*marionette.*headless" 2>/dev/null)
if [ -n "${FIREFOX_PIDS}" ]
then
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Chiusura processi Firefox headless orfani ..."
	fi
	pkill -f "firefox.*marionette.*headless" 2>/dev/null
	if [ "${ZAP_DEBUG}" == "true" ]
	then
		echo "Processi Firefox headless terminati"
	fi
fi
