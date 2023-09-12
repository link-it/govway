#!/bin/bash

#sleep 30
echo ""
echo "**********************************************"
echo " Verifica Accesso Console Monitoraggio       "
echo "                                 "

curl -v -s -X GET -H 'Referer: http://127.0.0.1:8080/govwayMonitor/public/login.jsf' "http://127.0.0.1:8080/govwayMonitor/public/login.jsf" > /tmp/log_access_monitor 2>&1
COOKIE=$(grep Set-Cookie /tmp/log_access_monitor)
if [ -z "${COOKIE}" ]
then
        echo " ERROR: Cookie non trovato"
        echo ""
        echo "**********************************************"
        cat /tmp/log_access_monitor
        exit 2
fi
COOKIE=$(echo $COOKIE | cut -d ':' -f 2)
COOKIE=$(echo $COOKIE | cut -d ';' -f 1)
#echo "Cookie: ${COOKIE}"

PAYLOAD='AJAXREQUEST=_viewRoot&j_id44=j_id44&username=operatore&password=123456&javax.faces.ViewState=j_id1&submitBtn=submitBtn'
#echo "Payload: ${PAYLOAD}"

curl -v -s -X POST -H 'Origin: http://127.0.0.1:8080' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Referer: http://127.0.0.1:8080/govwayMonitor/public/login.jsf' -H "Cookie: ${COOKIE}" -d ${PAYLOAD} "http://127.0.0.1:8080/govwayMonitor/public/login.jsf"  > /tmp/log_access_monitor_cookie 2>&1
SUCCESSO_STATS=$(grep "/govwayMonitor/commons/pages/welcome.jsf?usaSVG=true" /tmp/log_access_monitor_cookie)
SUCCESSO_TRANSAZIONI=$(grep "/govwayMonitor/transazioni/pages/form/transazioni.jsf" /tmp/log_access_monitor_cookie)
ERROR=$(grep "error" /tmp/log_access_monitor_cookie | grep -v "id\=\"errorsPlaceHolder\"")
#echo "Trovato: ${SUCCESSO_STATS}"
#echo "Trovato: ${SUCCESSO_TRANSAZIONI}"
if [ ! -z "${ERROR}" ]
then
        echo " ERROR: Autenticazione fallita"
        echo ""
        echo "**********************************************"
        cat /tmp/log_access_monitor_cookie
        exit 2
fi
if [ -z "${SUCCESSO_STATS}" -a -z "${SUCCESSO_TRANSAZIONI}" ]
then
        echo " ERROR: Autenticazione fallita (pagina attesa non trovata)"
        echo ""
        echo "**********************************************"
        cat /tmp/log_access_monitor_cookie
        exit 2
fi
echo " Accesso GovWay : Login effettuato con successo"
echo "**********************************************"
