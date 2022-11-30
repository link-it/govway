ZAP_PORT=$1
if [ -z "${ZAP_PORT}" ]
then
	echo "Porta non definita"
	exit 1
fi
ZAP_HOST=$2
if [ -z "${ZAP_HOST}" ]
then
	echo "Host non definito"
	exit 1
fi

sleep 5s

ZAP_PID_GREP=$(ps axww | grep -v " grep " | grep org.zaproxy.zap.ZAP)
ZAP_PID_GREP_TRIM=$(echo "${ZAP_PID_GREP}" | xargs)
ZAP_PID=$(echo "${ZAP_PID_GREP_TRIM}" | cut -d ' ' -f 1)
if [ -z "${ZAP_PID}" ]
then
	echo "Processo non attivo ?"
	exit 1
fi

ACTIVE=$(netstat -natp | grep LISTEN | grep "${ZAP_PID}/java" | grep "${ZAP_HOST}:${ZAP_PORT}")
#echo "ACTIVE [${ACTIVE}]"
X=1
while [ $X -le 30 -a -z "${ACTIVE}" ]
do
   #echo $X
   X=`expr $X + 1`
   sleep 2s
   ACTIVE=$(netstat -natp | grep LISTEN | grep "${ZAP_PID}/java" | grep "${ZAP_HOST}:${ZAP_PORT}")
done

if [ -z "${ACTIVE}" ]
then
	echo "Non è stato rilevato uno ZAP Proxy aviato sul contesto ${ZAP_HOST}:${ZAP_PORT}"
	exit 1
else
	echo "Rilevato ZAP Proxy avviato: ${ACTIVE}"
fi
