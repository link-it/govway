#!/bin/bash

#sleep 30
echo ""
echo "**********************************************"
echo " Verifica Accesso Console        "
echo "                                 "
CHK=$(curl -s -X POST -d "login=amministratore&password=123456" "http://127.0.0.1:8080/govwayConsole/login.do"  | grep messages-title-text | awk -F">" '{print $2}' | awk -F"<" '{print " Accesso GovWay : " $1}')
if [ "X$CHK" == "X" ];
   then
       echo " Problema di connessione al WildFly"
       echo ""
       echo "**********************************************"
       exit 1
   else
      echo "$CHK" 
fi 
echo ""
echo "**********************************************"

