#!/bin/bash

[ -f api_openssl.sh ] && source api_openssl.sh

DOMINIO="test"

[ $# -lt 1 ] && { echo "Usage: $(basename $0) <Nome cliente>

Es.
$(basename $0) ${DOMINIO^^}
"; exit 1; }

SOGGETTO="$1"
COMMON_NAME="$2"
DISTINGUISHED_NAME="$3"

WORK_DIR="$(readlink -f $(dirname $0))"

echo "Avviare l'OCSP responder con il seguente comando"
echo 
ocsp_reponder_printcmd ${SOGGETTO} ${WORK_DIR}



