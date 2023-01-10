#!/bin/bash

[ -f api_openssl.sh ] && source api_openssl.sh

DOMINIO="test"

[ $# -lt 3 ] && { echo "Usage: $(basename $0) <Nome dominio> <Common Name> <Distinguished Name>

Es.
$(basename $0) ${DOMINIO^^} $DOMINIO.esempio.it o=Esempio,c=it
"; exit 1; }

SOGGETTO="$1"
COMMON_NAME="$2"
DISTINGUISHED_NAME="$3"

WORK_DIR="$(readlink -f $(dirname $0))"
    # Inizializzazione
    build_CA_DB  "${SOGGETTO}" "${WORK_DIR}"

    # Genero certificato CA
	CA_SUBJECT="cn=CA ${SOGGETTO},${DISTINGUISHED_NAME}"
    certificato_ca "${SOGGETTO}" "${CA_SUBJECT}" "${WORK_DIR}"
    echo "-- GENERATO ${CA_SUBJECT}"

    # Genero certificato Signer OCSP
    OCSP_SUBJECT="cn=OCSP ${SOGGETTO},${DISTINGUISHED_NAME}"
	certificato_ocsp "${SOGGETTO}" "${OCSP_SUBJECT}" "${WORK_DIR}"
    echo "-- GENERATO ${OCSP_SUBJECT}"

    # Esempi certificati emessi dalla CA
	SERVER_SUBJECT="cn=${COMMON_NAME},${DISTINGUISHED_NAME}"
	certificato_ee "${SOGGETTO}" "${SERVER_SUBJECT}" 'server' "${WORK_DIR}"
    echo "-- GENERATO ${SERVER_SUBJECT}"

	CLIENT_SUBJECT="cn=Client-${COMMON_NAME},${DISTINGUISHED_NAME}"
	certificato_ee "${SOGGETTO}" "${CLIENT_SUBJECT}" 'client' "${WORK_DIR}"
    echo "-- GENERATO ${CLIENT_SUBJECT}"

cp ${SOGGETTO}/ca/index.txt .
cp ${SOGGETTO}/ca/certs/ocsp_TEST.cert.pem .
cp ${SOGGETTO}/ca/private/ocsp_TEST.key.pem .
cp ${SOGGETTO}/ca/certs/ca_TEST.cert.pem .
cp ${SOGGETTO}/ca/certs/ee*.pem .
keytool -importcert -alias ca -file ca_TEST.cert.pem -keystore ca_TEST.jks -storepass 123456 -storetype JKS -trustcacerts -noprompt
