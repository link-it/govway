#!/bin/bash

BASEURL="http://127.0.0.1:8080/govwayConsole"
DATA="login=amministratore&password=123456"

#sleep 30
echo ""
echo "**********************************************"
echo ""
echo " Verifica Accesso Console        "
echo "                                 "
CHK=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do"  | grep messages-title-text | awk -F">" '{print $2}' | awk -F"<" '{print " Accesso GovWay : " $1}')
if [ "X$CHK" == "X" ];
then
	echo "Problema di connessione"
	echo ""
	echo "**********************************************"
	exit 1
else
	if [ " Accesso GovWay : Login effettuato con successo" == "$CHK" ]
	then
		echo "$CHK" 
	else 
		echo "Login non riuscito"
		echo "[$CHK]" 
		echo ""
		echo "**********************************************"
		exit 1
	fi
fi 
echo ""

echo " Verifica Session Fixation (CWE-384)        "
echo ""

# Esegue la prima chiamata e cattura il valore del cookie JSESSIONID
COOKIE1=$(curl -s -X GET "$BASEURL"/ -D - | grep -i "Set-Cookie" | grep -oP 'JSESSIONID_GW_CONSOLE=[^;]+')
echo "     Primo cookie (pre-auth): $COOKIE1"
echo ""

# Esegue la seconda chiamata e cattura nuovamente il valore
COOKIE2=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do" -D - | grep -i "Set-Cookie" | grep -oP 'JSESSIONID_GW_CONSOLE=[^;]+')
echo "     Secondo cookie (after login): $COOKIE2"

# Confronta i due valori
if [ "$COOKIE1" != "$COOKIE2" ]; then
	echo "     Verifica cookie pre-autenticazione con cookie dopo autenticazione: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali"
	echo ""
	echo "**********************************************"    
	exit 1
fi
LOCATION=$(curl -s -i -X GET -H "Cookie: $COOKIE2" "$BASEURL"/ | grep -i "^Location:" | awk '{print $2}' | tr -d '\r')
EXPECTED="${BASEURL}/messagePage.do?mpText=Console+ripristinata+con+successo.&mpType=info-sintetico"
if [ "$LOCATION" = "$EXPECTED" ]; then
	echo "     Verifica accesso dopo login: OK"
else
	echo "     Rilevata problema di accesso dopo login"
	echo "     Atteso:   $EXPECTED"
	echo "     Ricevuto: $LOCATION"
	echo ""
	echo "**********************************************"    
	exit 1
fi
echo ""

# Esegue una seconda chiamata con login e cattura il valore del cookie JSESSIONID
COOKIE3=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do" -D - | grep -i "Set-Cookie" | grep -oP 'JSESSIONID_GW_CONSOLE=[^;]+')
echo "     Terzo cookie: $COOKIE3"

# Confronta i due valori
if [ "$COOKIE2" != "$COOKIE3" ]; then
	echo "     Verifica cookie post-autenticazione1 con cookie dopo nuova autenticazione: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali (test2)"
	echo ""
	echo "**********************************************"    
	exit 1
fi
LOCATION=$(curl -s -i -X GET -H "Cookie: $COOKIE3" "$BASEURL"/ | grep -i "^Location:" | awk '{print $2}' | tr -d '\r')
EXPECTED="${BASEURL}/messagePage.do?mpText=Console+ripristinata+con+successo.&mpType=info-sintetico"
if [ "$LOCATION" = "$EXPECTED" ]; then
	echo "     Verifica accesso dopo login: OK"
else
	echo "     Rilevata problema di accesso dopo login"
	echo "     Atteso:   $EXPECTED"
	echo "     Ricevuto: $LOCATION"
	echo ""
	echo "**********************************************"    
	exit 1
fi
echo ""

# Effettuo logout
echo " Verifica Logout Console        "
echo ""
RESPONSE=$(curl -s -i -X GET -H "Cookie: ${COOKIE3}" "${BASEURL}/logout.do")
COOKIE_LOGOUT=$(echo "$RESPONSE" | grep -i "^Set-Cookie:" | awk '{print $2}' | tr -d '\r')
echo "     Cookie dopo logout: $COOKIE_LOGOUT"
BODY=$(echo "$RESPONSE" | sed -n '/^\r\{0,1\}$/,$p' | tail -n +2)
# Verifica che nel body ci sia la stringa desiderata
if echo "$BODY" | grep -q "Logout effettuato con successo"; then
	echo "     Logout effettuato con successo"
else
	echo "     La stringa 'Logout effettuato con successo' NON è presente; logout fallito?"
	echo ""
	echo "**********************************************"    
	exit 1
fi
# Confronta i due valori dei cookier
if [ "$COOKIE3" != "$COOKIE_LOGOUT" ]; then
	echo "     Verifica cookie post-autenticazione2 con cookie dopo logout: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali (test dopo logout)"
	echo ""
	echo "**********************************************"    
	exit 1
fi
echo ""





echo " Verifica Brute Force - CWE-307"
echo ""

# Test 1: 4 tentativi falliti + 1 corretto (deve essere bloccato)
echo "     Test 1: Blocco dopo 4 tentativi falliti"

DATA_WRONG="login=amministratore&password=passworderrata"

# Esegue 4 tentativi con password errata
for i in {1..4}; do
	echo "       Tentativo fallito $i/4..."
	curl -s -X POST -d "$DATA_WRONG" "$BASEURL/login.do" > /dev/null
done

# Quinto tentativo con password corretta (deve essere bloccato)
echo "       Tentativo 5/5 con password corretta (dovrebbe essere bloccato)..."
RESPONSE_BLOCKED=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do")

if echo "$RESPONSE_BLOCKED" | grep -q "Utenza bloccata, superato il numero di tentativi di accesso massimo!"; then
	echo "     OK: Utenza correttamente bloccata dopo 4 tentativi falliti"
else
	echo "     ERRORE: Utenza NON bloccata dopo 4 tentativi falliti"
	echo "     Risposta ricevuta:"
	echo "$RESPONSE_BLOCKED" | head -20
	echo ""
	echo "**********************************************"    
	exit 1
fi

# Verifica che continui ad essere bloccato
echo "     Verifica che l'utenza rimanga bloccata..."
RESPONSE_BLOCKED2=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do")

if echo "$RESPONSE_BLOCKED2" | grep -q "Utenza bloccata, superato il numero di tentativi di accesso massimo!"; then
	echo "     OK: Utenza ancora bloccata"
else
	echo "     ERRORE: Utenza NON più bloccata (dovrebbe rimanere bloccata)"
	echo ""
	echo "**********************************************"    
	exit 1
fi

# Attende 6 secondi per lo sblocco automatico
echo "     Attesa di 16 secondi per sblocco automatico..."
sleep 16

# Verifica che ora il login funzioni
echo "     Tentativo di login dopo attesa..."
CHK=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do"  | grep messages-title-text | awk -F">" '{print $2}' | awk -F"<" '{print " Accesso GovWay : " $1}')
if [ "X$CHK" == "X" ];
then
	echo "Problema di connessione"
	echo ""
	echo "**********************************************"
	exit 1
else
	if [ " Accesso GovWay : Login effettuato con successo" == "$CHK" ]
	then
		echo "$CHK" 
	else 
		echo "Login non riuscito"
		echo "[$CHK]" 
		echo ""
		echo "**********************************************"
		exit 1
	fi
fi 

echo ""
echo "     Test 2: 3 tentativi falliti + 1 corretto (deve funzionare)"

# Attendo un attimo per sicurezza
sleep 2

# Esegue 3 tentativi con password errata
for i in {1..3}; do
    echo "       Tentativo fallito $i/3..."
    curl -s -X POST -d "$DATA_WRONG" "$BASEURL/login.do" > /dev/null
done

# Quarto tentativo con password corretta (deve funzionare)
echo "       Tentativo 4/4 con password corretta (dovrebbe funzionare)..."
COOKIE_TEST2=$(curl -s -X POST -d "$DATA" "$BASEURL/login.do" -D - | grep -i "Set-Cookie" | grep -oP 'JSESSIONID_GW_CONSOLE=[^;]+')

if [ -n "$COOKIE_TEST2" ]; then
    # Verifica che l'accesso sia effettivamente riuscito
    LOCATION_TEST2=$(curl -s -i -X GET -H "Cookie: $COOKIE_TEST2" "$BASEURL"/ | grep -i "^Location:" | awk '{print $2}' | tr -d '\r')
    EXPECTED="${BASEURL}/messagePage.do?mpText=Console+ripristinata+con+successo.&mpType=info-sintetico"
    
    if [ "$LOCATION_TEST2" = "$EXPECTED" ]; then
        echo "     OK: Login riuscito al 4° tentativo (dopo 3 falliti)"
        # Effettuo logout per pulizia
        curl -s -X GET -H "Cookie: ${COOKIE_TEST2}" "${BASEURL}/logout.do" > /dev/null
    else
        echo "     ERRORE: Login non riuscito correttamente"
        echo "     Atteso:   $EXPECTED"
        echo "     Ricevuto: $LOCATION_TEST2"
	echo ""
	echo "**********************************************"
	exit 1
    fi
else
    echo "     ERRORE: Login bloccato anche con solo 3 tentativi falliti (soglia troppo bassa)"
    exit 1
fi

echo ""
echo "**********************************************"

