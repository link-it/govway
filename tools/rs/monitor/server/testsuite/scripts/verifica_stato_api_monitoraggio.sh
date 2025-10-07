#!/bin/bash

echo ""
echo "**********************************************"
echo " Verifica Stato API Monitoraggio            "
echo "                                 		  "

# Scarico openapi
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/openapi.json" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:200" /tmp/api_monitor.log|wc -l`

PS2=`grep "GovWay Monitor API" /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ! ${PS} -eq 1 -o ! ${PS2} -eq 1 ]
then
	echo " ERROR:  il servizio non è partito correttamente"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

# accesso report
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/reportistica/configurazione-api/riepilogo" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:200" /tmp/api_monitor.log|wc -l`

PS2=`grep "soggetti_dominio_esterno" /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 ]
then
	echo " Stato API : il servizio è correttamente in esecuzione"
	echo "**********************************************"
else
        echo " ERROR:  il servizio non è partito correttamente"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
        echo ""
        echo "**********************************************"
        exit 2
fi


echo ""
echo "**********************************************"
echo " Verifica Autenticazione tramite status       "
echo "                                 		  "


# Verifica endpoint status con credenziali corrette (OK)
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:200" /tmp/api_monitor.log|wc -l`
PS2=`grep '"type".*"https://httpstatuses.com/200"' /tmp/api_monitor.log|wc -l`
PS3=`grep '"title".*"OK"' /tmp/api_monitor.log|wc -l`
PS4=`grep '"status".*200' /tmp/api_monitor.log|wc -l`
PS5=`grep '"detail".*"Il servizio funziona correttamente"' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ! ${PS} -eq 1 -o ! ${PS2} -eq 1 -o ! ${PS3} -eq 1 -o ! ${PS4} -eq 1 -o ! ${PS5} -eq 1 ]
then
	echo " ERROR:  endpoint /status non risponde correttamente"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
        echo "         PS3:${PS3}"
        echo "         PS4:${PS4}"
        echo "         PS5:${PS5}"	
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

# Verifica endpoint status con credenziali errate 'password' (KO - deve restituire 401)
/usr/bin/curl --user operatore:passwordErrata --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:401" /tmp/api_monitor.log|wc -l`
PS2=`grep '"type".*"https://httpstatuses.com/401"' /tmp/api_monitor.log|wc -l`
PS3=`grep '"title".*"Unauthorized"' /tmp/api_monitor.log|wc -l`
PS4=`grep '"status".*401' /tmp/api_monitor.log|wc -l`
PS5=`grep '"detail".*"Bad credentials"' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 -a ${PS3} -eq 1 -a ${PS4} -eq 1 -a ${PS5} -eq 1 ]
then
	echo "" > /dev/null
else
        echo " ERROR:  l'autenticazione non funziona correttamente (passwordErrata)"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
        echo "         PS3:${PS3}"
        echo "         PS4:${PS4}"
        echo "         PS5:${PS5}"        
	echo "${MESSAGGIO}"
        echo ""
        echo "**********************************************"
        exit 2
fi

# Verifica endpoint status con credenziali errate 'username' (KO - deve restituire 401)
/usr/bin/curl --user utenteNonEsistente:passwordErrata --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:401" /tmp/api_monitor.log|wc -l`
PS2=`grep '"type".*"https://httpstatuses.com/401"' /tmp/api_monitor.log|wc -l`
PS3=`grep '"title".*"Unauthorized"' /tmp/api_monitor.log|wc -l`
PS4=`grep '"status".*401' /tmp/api_monitor.log|wc -l`
PS5=`grep '"detail".*"Bad credentials"' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 -a ${PS3} -eq 1 -a ${PS4} -eq 1 -a ${PS5} -eq 1 ]
then
	echo "" > /dev/null
else
        echo " ERROR:  l'autenticazione non funziona correttamente (utente non esistente)"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
        echo "         PS3:${PS3}"
        echo "         PS4:${PS4}"
        echo "         PS5:${PS5}"        
	echo "${MESSAGGIO}"
        echo ""
        echo "**********************************************"
        exit 2
fi

# Verifica endpoint status con credenziali ok e utente non autorizzato (KO - deve restituire 401)
/usr/bin/curl --user amministratore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:403" /tmp/api_monitor.log|wc -l`
PS2=`grep '"type".*"https://httpstatuses.com/403"' /tmp/api_monitor.log|wc -l`
PS3=`grep '"title".*"Forbidden"' /tmp/api_monitor.log|wc -l`
PS4=`grep '"status".*403' /tmp/api_monitor.log|wc -l`
read -r -d '' EXPECT_DETAIL <<'EOF'
L'utente 'amministratore' non è autorizzato ad invocare l'operazione 'getStatus': Acl rule 'aclStatus' not satisfied
EOF
PS5=$(grep -F -c "$EXPECT_DETAIL" /tmp/api_monitor.log)

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 -a ${PS3} -eq 1 -a ${PS4} -eq 1 -a ${PS5} -eq 1 ]
then
	echo " Autenticazione: controllo credenziali funziona correttamente"
	echo "**********************************************"
else
        echo " ERROR:  l'autenticazione non funziona correttamente (utente amministratore non deve essere autorizzato)"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
        echo "         PS3:${PS3}"
        echo "         PS4:${PS4}"
        echo "         PS5:${PS5}"
	echo "${MESSAGGIO}"
        echo ""
        echo "**********************************************"
        exit 2
fi


echo ""
echo "**********************************************"
echo " Verifica Brute Force - CWE-307"
echo ""

# Test 1: 4 tentativi falliti + 1 corretto (deve essere bloccato)
echo "     Test 1: Blocco dopo 4 tentativi falliti"

# Esegue 4 tentativi con password errata
for i in {1..4}; do
	echo "       Tentativo fallito $i/4..."
	/usr/bin/curl --user operatore:passworderrata --noproxy 127.0.0.1 "http://127.0.0.1:8080/govwayAPIMonitor/status" >/dev/null 2>&1
done

# Quinto tentativo con password corretta (deve essere bloccato e restituire 401)
echo "       Tentativo 5/5 con password corretta (dovrebbe essere bloccato)..."
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:401" /tmp/api_monitor.log|wc -l`
PS2=`grep '"status".*401' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 ]
then
	echo "     OK: Utenza correttamente bloccata (401 con password corretta)"
else
	echo "     ERRORE: Utenza NON bloccata dopo 4 tentativi falliti"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

# Verifica che continui ad essere bloccato
echo "     Verifica che l'utenza rimanga bloccata..."
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:401" /tmp/api_monitor.log|wc -l`
PS2=`grep '"status".*401' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 ]
then
	echo "     OK: Utenza ancora bloccata"
else
	echo "     ERRORE: Utenza NON più bloccata (dovrebbe rimanere bloccata)"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

# Attende 16 secondi per lo sblocco automatico
echo "     Attesa di 16 secondi per sblocco automatico..."
sleep 16

# Verifica che ora il login funzioni
echo "     Tentativo di accesso dopo attesa..."
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:200" /tmp/api_monitor.log|wc -l`
PS2=`grep '"detail".*"Il servizio funziona correttamente"' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 ]
then
	echo "     OK: Accesso ripristinato dopo sblocco automatico"
else
	echo "     ERRORE: Accesso non ripristinato dopo attesa"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

echo ""
echo "     Test 2: 3 tentativi falliti + 1 corretto (deve funzionare)"

# Attendo un attimo per sicurezza
sleep 2

# Esegue 3 tentativi con password errata
for i in {1..3}; do
	echo "       Tentativo fallito $i/3..."
	/usr/bin/curl --user operatore:passworderrata --noproxy 127.0.0.1 "http://127.0.0.1:8080/govwayAPIMonitor/status" >/dev/null 2>&1
done

# Quarto tentativo con password corretta (deve funzionare)
echo "       Tentativo 4/4 con password corretta (dovrebbe funzionare)..."
/usr/bin/curl --user operatore:123456 --noproxy 127.0.0.1 -w "\n\nRETURNCODE:%{http_code}" "http://127.0.0.1:8080/govwayAPIMonitor/status" >/tmp/api_monitor.log 2>&1

PS=`grep "RETURNCODE:200" /tmp/api_monitor.log|wc -l`
PS2=`grep '"detail".*"Il servizio funziona correttamente"' /tmp/api_monitor.log|wc -l`

MESSAGGIO=`cat /tmp/api_monitor.log`

if [ ${PS} -eq 1 -a ${PS2} -eq 1 ]
then
	echo "     OK: Accesso riuscito al 4° tentativo (dopo 3 falliti)"
else
	echo "     ERRORE: Accesso bloccato anche con solo 3 tentativi falliti (soglia troppo bassa)"
        echo "         PS:${PS}"
        echo "         PS2:${PS2}"
	echo "${MESSAGGIO}"
	echo ""
	echo "**********************************************"
	exit 2
fi

echo ""
echo "**********************************************"
