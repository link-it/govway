#!/bin/bash

BASEURL="http://127.0.0.1:8080/govwayMonitor"
USERNAME="operatore"
PASSWORD="123456"

# Funzione helper per estrarre il CSRF token
get_csrf_token() {
	local cookie="$1"
	local output_file="/tmp/csrf_extract_$$"
	
	if [ -z "$cookie" ]; then
		curl -s -X GET -H 'Referer: '"${BASEURL}"'/public/login.jsf' "${BASEURL}/public/login.jsf" > "$output_file"
	else
		curl -s -X GET -H 'Referer: '"${BASEURL}"'/public/login.jsf' -H "Cookie: $cookie" "${BASEURL}/public/login.jsf" > "$output_file"
	fi
	
	local csrf=$(grep "_csrfLogin" "$output_file" | grep -oP 'value="[^"]+' | cut -d'"' -f2 | head -1)
	rm -f "$output_file"
	echo "$csrf"
}

# Funzione helper per estrarre il cookie
get_cookie() {
	local output_file="/tmp/cookie_extract_$$"
	curl -s -X GET -H 'Referer: '"${BASEURL}"'/public/login.jsf' "${BASEURL}/public/login.jsf" -D - > "$output_file" 2>&1
	local cookie=$(grep -i "Set-Cookie" "$output_file" | grep -oP 'JSESSIONID[^;]+')
	rm -f "$output_file"
	echo "$cookie"
}

# Funzione helper per eseguire il login
do_login() {
	local username="$1"
	local password="$2"
	local cookie="$3"
	local csrf="$4"
	local output_file="/tmp/login_result_$$"
	
	curl -s -X POST \
		-H 'Origin: http://127.0.0.1:8080' \
		-H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
		-H 'Referer: '"${BASEURL}"'/public/login.jsf' \
		-H "Cookie: ${cookie}" \
		-d "javax.faces.ViewState=j_id1&j_id48=j_id48&AJAXREQUEST=_viewRoot&username=${username}&password=${password}&loginBtn=loginBtn&_csrf=${csrf}" \
		"${BASEURL}/public/login.jsf" > "$output_file" 2>&1
	
	cat "$output_file"
	rm -f "$output_file"
}

# Funzione helper per eseguire il logout (restituisce il path del file con la risposta)
do_logout() {
	local cookie="$1"
	local csrf="$2"
	local output_file="/tmp/logout_result_$$"
	
	curl -s -i -X POST \
		-H 'Origin: http://127.0.0.1:8080' \
		-H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
		-H 'Referer: '"${BASEURL}"'/public/login.jsf' \
		-H "Cookie: ${cookie}" \
		-d "j_id10=j_id10&ps_usaSVG=true&_csrf=${csrf}&javax.faces.ViewState=j_id2&j_id33%3Ahidden=j_id33" \
		"${BASEURL}/public/transazioni.jsf" > "$output_file" 2>&1
	
	echo "$output_file"
}

# Funzione helper per verificare il successo del login
check_login_success() {
	local response="$1"
	if echo "$response" | grep -q "/govwayMonitor/commons/pages/welcome.jsf?usaSVG=true" || \
	   echo "$response" | grep -q "/govwayMonitor/transazioni/pages/form/transazioni.jsf"; then
		return 0
	else
		return 1
	fi
}

#sleep 30
echo ""
echo "**********************************************"
echo ""
echo " Verifica Accesso Console Monitoraggio       "
echo "                                 "

# Test accesso base
COOKIE=$(get_cookie)
if [ -z "${COOKIE}" ]; then
	echo "Problema di connessione - Cookie non trovato"
	echo ""
	echo "**********************************************"
	exit 1
fi

CSRF=$(get_csrf_token "$COOKIE")
if [ -z "${CSRF}" ]; then
	echo "Problema di connessione - CSRF non trovato"
	echo ""
	echo "**********************************************"
	exit 1
fi

RESPONSE=$(do_login "$USERNAME" "$PASSWORD" "$COOKIE" "$CSRF")
ERROR=$(echo "$RESPONSE" | grep "error" | grep -v 'id="errorsPlaceHolder"')

if [ ! -z "${ERROR}" ]; then
	echo "Login non riuscito - Errore trovato"
	echo ""
	echo "**********************************************"
	exit 1
fi

if check_login_success "$RESPONSE"; then
	echo "     Accesso GovWay : Login effettuato con successo"
else
	echo "Login non riuscito - Pagina attesa non trovata"
	echo ""
	echo "**********************************************"
	exit 1
fi
echo ""



RESPONSE=$(do_login "inesistente" "$PASSWORD" "$COOKIE" "$CSRF")

if check_login_success "$RESPONSE"; then
	echo "Login riuscito - Atteso deny per utente non esistente"
	echo ""
	echo "**********************************************"
	exit 1
else
	echo " Accesso GovWay : Login negato con successo" > /dev/null
fi

RESPONSE=$(do_login "$USERNAME" "errata" "$COOKIE" "$CSRF")

if check_login_success "$RESPONSE"; then
	echo "Login riuscito - Atteso deny per password errata"
	echo ""
	echo "**********************************************"
	exit 1
else
	echo " Accesso GovWay : Login negato con successo" > /dev/null
fi


RESPONSE=$(do_login "operatore" "$PASSWORD" "$COOKIE" "$CSRF")

if check_login_success "$RESPONSE"; then
	echo "Login riuscito - Atteso deny per utente non abilitato"
	echo ""
	echo "**********************************************"
	exit 1
else
	echo " Accesso GovWay : Login negato con successo" > /dev/null
fi
echo "     Autorizzazioni: OK"
echo ""





echo " Verifica Session Fixation (CWE-384)        "
echo ""

# Esegue la prima chiamata e cattura il valore del cookie JSESSIONID (pre-auth)
COOKIE1=$(get_cookie)
echo "     Primo cookie (pre-auth): $COOKIE1"
echo ""

# Esegue il login e cattura il cookie post-auth
CSRF1=$(get_csrf_token "$COOKIE1")
OUTPUT_FILE="/tmp/login_cookie_$$"
curl -s -X POST \
	-H 'Origin: http://127.0.0.1:8080' \
	-H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
	-H 'Referer: '"${BASEURL}"'/public/login.jsf' \
	-H "Cookie: ${COOKIE1}" \
	-d "javax.faces.ViewState=j_id1&j_id48=j_id48&AJAXREQUEST=_viewRoot&username=${USERNAME}&password=${PASSWORD}&loginBtn=loginBtn&_csrf=${CSRF1}" \
	"${BASEURL}/public/login.jsf" -D - > "$OUTPUT_FILE" 2>&1

COOKIE2=$(grep -i "Set-Cookie" "$OUTPUT_FILE" | grep -oP 'JSESSIONID[^;]+' | head -1)
echo "     Secondo cookie (after login): $COOKIE2"
rm -f "$OUTPUT_FILE"

# Confronta i due valori
if [ "$COOKIE1" != "$COOKIE2" ]; then
	echo "     Verifica cookie pre-autenticazione con cookie dopo autenticazione: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali"
	echo ""
	echo "**********************************************"    
	exit 1
fi

# Verifica che il cookie2 funzioni
RESPONSE_CHECK=$(curl -s -X GET -H "Cookie: ${COOKIE2}" "${BASEURL}/commons/pages/welcome.jsf?usaSVG=true")
if echo "$RESPONSE_CHECK" | grep -q "govwayMonitor" || echo "$RESPONSE_CHECK" | grep -q "welcome"; then
	echo "     Verifica accesso dopo login: OK"
else
	echo "     Rilevata problema di accesso dopo login"
	echo ""
	echo "**********************************************"    
	exit 1
fi
echo ""

# Esegue una seconda chiamata con login e cattura il valore del cookie JSESSIONID
COOKIE_NEW=$(get_cookie)
CSRF_NEW=$(get_csrf_token "$COOKIE_NEW")
OUTPUT_FILE="/tmp/login_cookie2_$$"
curl -s -X POST \
	-H 'Origin: http://127.0.0.1:8080' \
	-H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
	-H 'Referer: '"${BASEURL}"'/public/login.jsf' \
	-H "Cookie: ${COOKIE_NEW}" \
	-d "javax.faces.ViewState=j_id1&j_id48=j_id48&AJAXREQUEST=_viewRoot&username=${USERNAME}&password=${PASSWORD}&loginBtn=loginBtn&_csrf=${CSRF_NEW}" \
	"${BASEURL}/public/login.jsf" -D - > "$OUTPUT_FILE" 2>&1

COOKIE3=$(grep -i "Set-Cookie" "$OUTPUT_FILE" | grep -oP 'JSESSIONID[^;]+' | head -1)
echo "     Terzo cookie: $COOKIE3"
rm -f "$OUTPUT_FILE"

# Confronta i due valori
if [ "$COOKIE2" != "$COOKIE3" ]; then
	echo "     Verifica cookie post-autenticazione1 con cookie dopo nuova autenticazione: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali (test2)"
	echo ""
	echo "**********************************************"    
	exit 1
fi

# Verifica che il cookie3 funzioni
RESPONSE_CHECK=$(curl -s -X GET -H "Cookie: ${COOKIE3}" "${BASEURL}/commons/pages/welcome.jsf?usaSVG=true")
if echo "$RESPONSE_CHECK" | grep -q "govwayMonitor" || echo "$RESPONSE_CHECK" | grep -q "welcome"; then
	echo "     Verifica accesso dopo login: OK"
else
	echo "     Rilevata problema di accesso dopo login"
	echo ""
	echo "**********************************************"    
	exit 1
fi
echo ""

# Effettuo logout
echo " Verifica Logout Console        "
echo ""


# Eseguo il logout - la funzione restituisce il path del file con la risposta
LOGOUT_FILE=$(do_logout "$COOKIE_AUTHENTICATED" "$CSRF_NEW")

# Verifica HTTP Status Code 302
HTTP_STATUS=$(head -1 "$LOGOUT_FILE" | awk '{print $2}')
if [ "$HTTP_STATUS" != "302" ]; then
	echo "     ERRORE: HTTP status atteso 302, ricevuto $HTTP_STATUS"
	echo ""
	echo "**********************************************"
	rm -f "$LOGOUT_FILE"
	exit 1
fi
echo "     HTTP Status 302: OK"

# Verifica Location header
LOCATION=$(grep -i "^Location:" "$LOGOUT_FILE" | awk '{print $2}' | tr -d '\r')
if echo "$LOCATION" | grep -q "/public/login.jsf"; then
	echo "     Location redirect a login.jsf: OK"
else
	echo "     ERRORE: Location non punta a login.jsf"
	echo "     Location ricevuta: $LOCATION"
	echo ""
	echo "**********************************************"
	rm -f "$LOGOUT_FILE"
	exit 1
fi

# Verifica che il cookie sia cambiato - prova senza ancora ^
COOKIE_AFTER_LOGOUT=$(grep -i "Set-Cookie" "$LOGOUT_FILE" | grep -oP 'JSESSIONID[^;]+' | head -1)
echo "     Cookie prima del logout: $COOKIE_AUTHENTICATED"
echo "     Cookie dopo il logout: $COOKIE_AFTER_LOGOUT"

if [ "$COOKIE_AUTHENTICATED" != "$COOKIE_AFTER_LOGOUT" ] && [ ! -z "$COOKIE_AFTER_LOGOUT" ]; then
	echo "     Verifica cookie post-autenticazione con cookie dopo logout: OK sono diversi"
else
	echo "     Rilevata vulnerabilità Session Fixation (CWE-384): i cookie sono uguali (test dopo logout)"
	echo ""
	echo "**********************************************"
	rm -f "$LOGOUT_FILE"
	exit 1
fi

rm -f "$LOGOUT_FILE"
echo ""

echo " Verifica Brute Force - CWE-307"
echo ""

# Test 1: 4 tentativi falliti + 1 corretto (deve essere bloccato)
echo "     Test 1: Blocco dopo 4 tentativi falliti"

PASSWORD_WRONG="passworderrata"

# Esegue 4 tentativi con password errata
for i in {1..4}; do
	echo "       Tentativo fallito $i/4..."
	COOKIE_TEMP=$(get_cookie)
	CSRF_TEMP=$(get_csrf_token "$COOKIE_TEMP")
	do_login "$USERNAME" "$PASSWORD_WRONG" "$COOKIE_TEMP" "$CSRF_TEMP" > /dev/null
done

# Quinto tentativo con password corretta (deve essere bloccato)
echo "       Tentativo 5/5 con password corretta (dovrebbe essere bloccato)..."
COOKIE_BLOCKED=$(get_cookie)
CSRF_BLOCKED=$(get_csrf_token "$COOKIE_BLOCKED")
RESPONSE_BLOCKED=$(do_login "$USERNAME" "$PASSWORD" "$COOKIE_BLOCKED" "$CSRF_BLOCKED")

if echo "$RESPONSE_BLOCKED" | grep -q "Utenza bloccata, superato il numero di tentativi di accesso massimo!" || \
   echo "$RESPONSE_BLOCKED" | grep -q "bloccata" || \
   echo "$RESPONSE_BLOCKED" | grep -q "locked"; then
	echo "     OK: Utenza correttamente bloccata dopo 4 tentativi falliti"
else
	# Verifica se NON è stato fatto il login con successo
	if check_login_success "$RESPONSE_BLOCKED"; then
		echo "     ERRORE: Utenza NON bloccata dopo 4 tentativi falliti - login riuscito"
		echo ""
		echo "**********************************************"    
		exit 1
	else
		echo "     OK: Utenza correttamente bloccata dopo 4 tentativi falliti (verifica generica)"
	fi
fi

# Verifica che continui ad essere bloccato
echo "     Verifica che l'utenza rimanga bloccata..."
COOKIE_BLOCKED2=$(get_cookie)
CSRF_BLOCKED2=$(get_csrf_token "$COOKIE_BLOCKED2")
RESPONSE_BLOCKED2=$(do_login "$USERNAME" "$PASSWORD" "$COOKIE_BLOCKED2" "$CSRF_BLOCKED2")

if echo "$RESPONSE_BLOCKED2" | grep -q "Utenza bloccata, superato il numero di tentativi di accesso massimo!" || \
   echo "$RESPONSE_BLOCKED2" | grep -q "bloccata" || \
   echo "$RESPONSE_BLOCKED2" | grep -q "locked"; then
	echo "     OK: Utenza ancora bloccata"
else
	# Verifica se NON è stato fatto il login con successo
	if check_login_success "$RESPONSE_BLOCKED2"; then
		echo "     ERRORE: Utenza NON più bloccata (dovrebbe rimanere bloccata)"
		echo ""
		echo "**********************************************"    
		exit 1
	else
		echo "     OK: Utenza ancora bloccata (verifica generica)"
	fi
fi

# Attende 16 secondi per lo sblocco automatico
echo "     Attesa di 16 secondi per sblocco automatico..."
sleep 16

# Verifica che ora il login funzioni
echo "     Tentativo di login dopo attesa..."
COOKIE_AFTER=$(get_cookie)
CSRF_AFTER=$(get_csrf_token "$COOKIE_AFTER")
RESPONSE_AFTER=$(do_login "$USERNAME" "$PASSWORD" "$COOKIE_AFTER" "$CSRF_AFTER")

ERROR=$(echo "$RESPONSE_AFTER" | grep "error" | grep -v 'id="errorsPlaceHolder"')

if [ ! -z "${ERROR}" ]; then
	echo "     ERRORE: Login fallito dopo attesa"
	echo ""
	echo "**********************************************"
	exit 1
fi

if check_login_success "$RESPONSE_AFTER"; then
	echo "     OK: Login riuscito dopo attesa di 16 secondi"
else
	echo "     ERRORE: Login fallito dopo attesa"
	echo ""
	echo "**********************************************"
	exit 1
fi

echo ""
echo "     Test 2: 3 tentativi falliti + 1 corretto (deve funzionare)"

# Attendo un attimo per sicurezza
sleep 2

# Esegue 3 tentativi con password errata
for i in {1..3}; do
	echo "       Tentativo fallito $i/3..."
	COOKIE_TEMP=$(get_cookie)
	CSRF_TEMP=$(get_csrf_token "$COOKIE_TEMP")
	do_login "$USERNAME" "$PASSWORD_WRONG" "$COOKIE_TEMP" "$CSRF_TEMP" > /dev/null
done

# Quarto tentativo con password corretta (deve funzionare)
echo "       Tentativo 4/4 con password corretta (dovrebbe funzionare)..."
COOKIE_TEST2=$(get_cookie)
CSRF_TEST2=$(get_csrf_token "$COOKIE_TEST2")
RESPONSE_TEST2=$(do_login "$USERNAME" "$PASSWORD" "$COOKIE_TEST2" "$CSRF_TEST2")

ERROR=$(echo "$RESPONSE_TEST2" | grep "error" | grep -v 'id="errorsPlaceHolder"')

if [ ! -z "${ERROR}" ]; then
	echo "     ERRORE: Login bloccato anche con solo 3 tentativi falliti (soglia troppo bassa)"
	echo ""
	echo "**********************************************"
	exit 1
fi

if check_login_success "$RESPONSE_TEST2"; then
	echo "     OK: Login riuscito al 4° tentativo (dopo 3 falliti)"
else
	echo "     ERRORE: Login non riuscito correttamente"
	echo ""
	echo "**********************************************"
	exit 1
fi

echo ""
echo "**********************************************"
