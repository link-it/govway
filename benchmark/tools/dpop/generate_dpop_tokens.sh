#!/bin/bash

# Script per generare token Authorization DPoP e token DPoP
#
# Authorization token firmato con ExampleServer.p12
# DPoP token firmato con ExampleClient2.p12
#
# Uso: ./generate_dpop_tokens.sh [SERVIZIO] [AZIONE]
#   SERVIZIO: nome del servizio (default: StressTestRestProxyDPoP)
#   AZIONE:   nome dell'azione (default: test10)
#
# Esempi:
#   ./generate_dpop_tokens.sh
#   ./generate_dpop_tokens.sh StressTestRestProxyDPoP test10
#   ./generate_dpop_tokens.sh MioServizio miaAzione

set -e

# Parametri da linea di comando con valori di default
SERVIZIO="${1:-StressTestRestProxyDPoP}"
AZIONE="${2:-test10}"

# Configurazione chiavi
AUTH_P12="/etc/govway/keys/xca/ExampleServer.p12"
AUTH_PASSWORD="123456"
AUTH_ALIAS="ExampleServer"

DPOP_P12="/etc/govway/keys/xca/ExampleClient2.p12"
DPOP_PASSWORD="123456"
DPOP_ALIAS="ExampleClient2"

# Configurazione token
ISS="StressTest"
SUB="StressTest"
AUD="StressTestRest"
CLIENT_ID="ApplicativoStressTest"

# DPoP config
HTM="PUT"
HTU="http://localhost:8080/govway/rest/in/ENTE/${SERVIZIO}/v1/${AZIONE}"

echo "=== Configurazione ==="
echo "Servizio: $SERVIZIO"
echo "Azione: $AZIONE"
echo "HTU: $HTU"
echo ""

# Directory temporanea per i file intermedi
TMPDIR=$(mktemp -d)
trap "rm -rf $TMPDIR" EXIT

echo "=== Estrazione chiavi dai file P12 ==="

# Estrai chiave privata e certificato per Authorization (ExampleClient1)
# Usa -legacy per supportare algoritmi di cifratura obsoleti (RC2-40-CBC)
openssl pkcs12 -in "$AUTH_P12" -passin pass:"$AUTH_PASSWORD" -nocerts -nodes -legacy -out "$TMPDIR/auth_private.pem" 2>/dev/null
openssl pkcs12 -in "$AUTH_P12" -passin pass:"$AUTH_PASSWORD" -clcerts -nokeys -legacy -out "$TMPDIR/auth_cert.pem" 2>/dev/null

# Estrai chiave privata e pubblica per DPoP (ExampleClient2)
openssl pkcs12 -in "$DPOP_P12" -passin pass:"$DPOP_PASSWORD" -nocerts -nodes -legacy -out "$TMPDIR/dpop_private.pem" 2>/dev/null
openssl pkcs12 -in "$DPOP_P12" -passin pass:"$DPOP_PASSWORD" -clcerts -nokeys -legacy -out "$TMPDIR/dpop_cert.pem" 2>/dev/null

# Estrai chiave pubblica per JWK
openssl x509 -in "$TMPDIR/dpop_cert.pem" -pubkey -noout > "$TMPDIR/dpop_public.pem"

# Estrai il certificato Authorization in formato DER per x5c
openssl x509 -in "$TMPDIR/auth_cert.pem" -outform DER -out "$TMPDIR/auth_cert.der"
X5C=$(openssl base64 -in "$TMPDIR/auth_cert.der" -e | tr -d '\n')

echo "=== Generazione JWK dalla chiave pubblica DPoP ==="

# Funzione per codifica base64url
base64url_encode() {
    openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n'
}

# Estrai modulo e esponente dalla chiave pubblica RSA per JWK
# Estrai il modulo (n) e l'esponente (e) dalla chiave pubblica
MODULUS_HEX=$(openssl rsa -pubin -in "$TMPDIR/dpop_public.pem" -modulus -noout 2>/dev/null | cut -d'=' -f2)
MODULUS_B64=$(echo "$MODULUS_HEX" | xxd -r -p | base64url_encode)

# L'esponente pubblico standard RSA e' 65537 (0x010001)
EXPONENT_B64="AQAB"

# Crea JWK per il token DPoP
JWK="{\"kty\":\"RSA\",\"n\":\"$MODULUS_B64\",\"e\":\"$EXPONENT_B64\"}"

echo "JWK: $JWK"

echo "=== Calcolo JWK Thumbprint (jkt) per il binding cnf ==="

# JWK Thumbprint secondo RFC 7638: SHA-256 del JWK canonico
# Il JWK canonico per RSA deve avere i membri in ordine alfabetico: e, kty, n
JWK_CANONICAL="{\"e\":\"$EXPONENT_B64\",\"kty\":\"RSA\",\"n\":\"$MODULUS_B64\"}"
JKT=$(echo -n "$JWK_CANONICAL" | openssl dgst -sha256 -binary | base64url_encode)

echo "JWK Thumbprint (jkt): $JKT"

echo "=== Generazione timestamp e scadenza ==="

# Timestamp corrente
IAT=$(date +%s)
NBF=$IAT
# Scadenza massima ragionevole (100 anni)
EXP=$((IAT + 3153600000))

# Genera JTI unici e purposeId
JTI_AUTH=$(cat /proc/sys/kernel/random/uuid 2>/dev/null || uuidgen)
JTI_DPOP=$(cat /proc/sys/kernel/random/uuid 2>/dev/null || uuidgen)
PURPOSE_ID=$(cat /proc/sys/kernel/random/uuid 2>/dev/null || uuidgen)

echo "IAT: $IAT"
echo "EXP: $EXP"
echo "JTI Auth: $JTI_AUTH"
echo "JTI DPoP: $JTI_DPOP"
echo "Purpose ID: $PURPOSE_ID"

echo "=== Costruzione token Authorization ==="

# Header Authorization (con kid e x5c)
AUTH_HEADER=$(cat <<EOF | tr -d '\n\t '
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "$AUTH_ALIAS",
  "x5c": ["$X5C"]
}
EOF
)

# Payload Authorization con cnf.jkt per il binding DPoP
AUTH_PAYLOAD=$(cat <<EOF | tr -d '\n\t '
{
  "iat": $IAT,
  "nbf": $NBF,
  "exp": $EXP,
  "jti": "$JTI_AUTH",
  "aud": "$AUD",
  "client_id": "$CLIENT_ID",
  "iss": "$ISS",
  "sub": "$SUB",
  "purposeId": "$PURPOSE_ID",
  "cnf": {
    "jkt": "$JKT"
  }
}
EOF
)

# Codifica header e payload in base64url
AUTH_HEADER_B64=$(echo -n "$AUTH_HEADER" | base64url_encode)
AUTH_PAYLOAD_B64=$(echo -n "$AUTH_PAYLOAD" | base64url_encode)

# Firma
AUTH_SIGNING_INPUT="${AUTH_HEADER_B64}.${AUTH_PAYLOAD_B64}"
AUTH_SIGNATURE=$(echo -n "$AUTH_SIGNING_INPUT" | openssl dgst -sha256 -sign "$TMPDIR/auth_private.pem" | base64url_encode)

# Token Authorization completo
AUTH_TOKEN="${AUTH_SIGNING_INPUT}.${AUTH_SIGNATURE}"

echo ""
echo "=== TOKEN AUTHORIZATION ==="
echo ""
echo "DPoP $AUTH_TOKEN"

echo ""
echo "=== Costruzione token DPoP ==="

# Calcola ath (access token hash) - SHA-256 dell'access token codificato base64url
ATH=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -binary | base64url_encode)

echo "Access Token Hash (ath): $ATH"

# Header DPoP (con jwk inline)
DPOP_HEADER=$(cat <<EOF | tr -d '\n\t '
{
  "alg": "RS256",
  "typ": "dpop+jwt",
  "jwk": $JWK
}
EOF
)

# Payload DPoP
DPOP_PAYLOAD=$(cat <<EOF | tr -d '\n\t '
{
  "jti": "$JTI_DPOP",
  "htm": "$HTM",
  "htu": "$HTU",
  "iat": $IAT,
  "ath": "$ATH"
}
EOF
)

# Codifica header e payload in base64url
DPOP_HEADER_B64=$(echo -n "$DPOP_HEADER" | base64url_encode)
DPOP_PAYLOAD_B64=$(echo -n "$DPOP_PAYLOAD" | base64url_encode)

# Firma
DPOP_SIGNING_INPUT="${DPOP_HEADER_B64}.${DPOP_PAYLOAD_B64}"
DPOP_SIGNATURE=$(echo -n "$DPOP_SIGNING_INPUT" | openssl dgst -sha256 -sign "$TMPDIR/dpop_private.pem" | base64url_encode)

# Token DPoP completo
DPOP_TOKEN="${DPOP_SIGNING_INPUT}.${DPOP_SIGNATURE}"

echo ""
echo "=== TOKEN DPOP ==="
echo ""
echo "$DPOP_TOKEN"

echo ""
echo "=== HEADER PER CURL ==="
echo ""
echo "Authorization: DPoP $AUTH_TOKEN"
echo ""
echo "DPoP: $DPOP_TOKEN"

echo ""
echo "=== ESEMPIO CURL ==="
echo ""
echo "curl -X PUT '$HTU' \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -H 'Authorization: DPoP $AUTH_TOKEN' \\"
echo "  -H 'DPoP: $DPOP_TOKEN' \\"
echo "  -d '{\"test\": \"data\"}'"

echo ""
echo "=== VERIFICA STRUTTURA TOKEN ==="
echo ""
echo "--- Authorization Header (decodificato) ---"
echo "$AUTH_HEADER" | python3 -m json.tool 2>/dev/null || echo "$AUTH_HEADER"
echo ""
echo "--- Authorization Payload (decodificato) ---"
echo "$AUTH_PAYLOAD" | python3 -m json.tool 2>/dev/null || echo "$AUTH_PAYLOAD"
echo ""
echo "--- DPoP Header (decodificato) ---"
echo "$DPOP_HEADER" | python3 -m json.tool 2>/dev/null || echo "$DPOP_HEADER"
echo ""
echo "--- DPoP Payload (decodificato) ---"
echo "$DPOP_PAYLOAD" | python3 -m json.tool 2>/dev/null || echo "$DPOP_PAYLOAD"
