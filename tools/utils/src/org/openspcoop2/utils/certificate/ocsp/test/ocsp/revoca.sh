#!/bin/bash

[ -f api_openssl.sh ] && source api_openssl.sh

DOMINIO="test"

[ $# -lt 3 ] && { echo "Usage: $(basename $0) <Nome cliente> <Common Name> <Distinguished Name>

Es.
$(basename $0) ${DOMINIO^^} $DOMINIO.esempio.it o=Esempio,c=it
"; exit 1; }

SOGGETTO="$1"
COMMON_NAME="$2"
DISTINGUISHED_NAME="$3"

WORK_DIR="$(readlink -f $(dirname $0))"


    # Revoca del certificato con il subject indicato
    SUBJECT="cn=${COMMON_NAME},${DISTINGUISHED_NAME}"
	revoke_ee "${SOGGETTO}" "${SUBJECT}" "${WORK_DIR}"
	echo "-- REVOCATO ${SUBJECT}"

cp -f ${SOGGETTO}/ca/index.txt .
