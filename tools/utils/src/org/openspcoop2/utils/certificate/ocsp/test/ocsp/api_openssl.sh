#!/bin/bash 

function getTag {
_T="$1"
shopt -s extglob
_T="${_T##+([[:space:]])}"
if [ "${_T:0:1}" == "/" ]
then
	T="${_T^^}"
	echo "${_T:1}"
else
	echo "${_T^^}"
fi
}

function getVal {
_V="$1"
shopt -s extglob
_V="${_V%%+([[:space:]])}"
_V="${_V//\//\\/}"
echo "${_V}"
}

function build_RDN {
T="$1"
V="$2"
RDN=

CLEANTAG=$(getTag "${T}")
CLEANVAL=$(getVal "${V}")
[ -n "${CLEANTAG}" -a -n "${CLEANVAL}" ] && RDN="${CLEANTAG}=${CLEANVAL}"
echo "$RDN"
}


function build_DN {
SUBJ="$1"

if [ -n "${SUBJ}" ]
then
        while IFS='=' read -d, TAG VAL
        do
                DN="${DN}/$(build_RDN "$TAG" "$VAL")"
        done <<<"${SUBJ},"

        if [ ${DN:0:2} == "//" ]
        then
                DN=
                while IFS='=' read -d/ TAG VAL
                do
                        DN="${DN}/$(build_RDN "$TAG" "$VAL")"
                done <<<"${SUBJ:1}/"
        fi
fi

echo "$DN"

}



function getCommonName {
SUBJ="$1"
CN=
if [ -n "${SUBJ}" ]
then
        while IFS='=' read -d, TAG VAL
        do
		[ "$(getTag ${TAG})" == "CN" ] && { CN="$(getVal "${VAL}")"; break; }
        done <<<"${SUBJ},"

fi

echo "$CN"

}



function build_CA_DB {
	SOGGETTO="$1"
        WORK_DIR="$2"
	NOME_NOSPAZI="${SOGGETTO// /_}"
	PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
	CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"

if [ -f ${PKI_DIR}/serial \
-a -f ${PKI_DIR}/index.txt \
-a -f ${PKI_DIR}/index.txt.attr \
-a -f ${CONF_FILE} ]
then
	echo "Database gia' inizializzato per ${SOGGETTO}"
else

        echo "Creazione database per ${SOGGETTO}"
        mkdir -p  ${PKI_DIR}/{certs,newcerts,crl,private,csr}
        chmod 700 ${PKI_DIR}/private
        touch ${PKI_DIR}/index.txt
        echo 'unique_subject = yes' > ${PKI_DIR}/index.txt.attr
        printf "%.2x" $(( $RANDOM % 256 ))  > ${PKI_DIR}/serial # il seriale deve essere esadecimale scritto con un numero pari di cifre
cat - << EOCONF > ${CONF_FILE}
[ ca ]
default_ca = CA_default

[ CA_default ]
# Directory and file locations.
dir               = ${PKI_DIR}
certs             = \$dir/certs
database          = \$dir/index.txt
crl_dir           = \$dir/crl
new_certs_dir     = \$dir/newcerts
serial            = \$dir/serial
RANDFILE          = \$dir/private/.rand

# SHA-1 is deprecated, so use SHA-2 instead.
default_md        = sha256

name_opt          = ca_default
cert_opt          = ca_default
default_days      = 3650
preserve          = no
policy            = policy_loose

# The root key and root certificate.
private_key       = \$dir/private/ca_${NOME_NOSPAZI}.key.pem
certificate       = \$dir/certs/ca_${NOME_NOSPAZI}.cert.pem

crl               = $dir/crl/${NOME_NOSPAZI}.crl.pem
default_crl_days  = 30


[ policy_loose ]
countryName             = optional
stateOrProvinceName     = optional
localityName            = optional
organizationName        = optional
organizationalUnitName  = optional
commonName              = supplied
emailAddress            = optional

[ req ]
default_bits        = 2048
distinguished_name  = req_distinguished_name
string_mask         = utf8only

# SHA-1 is deprecated, so use SHA-2 instead.
default_md          = sha256

# Extension to add when the -x509 option is used.
x509_extensions     = v3_ca

[ req_distinguished_name ]
# See <https://en.wikipedia.org/wiki/Certificate_signing_request>.
countryName                     = Country Name (2 letter code)
stateOrProvinceName             = State or Province Name
localityName                    = Locality Name
0.organizationName              = Organization Name
organizationalUnitName          = Organizational Unit Name
commonName                      = Common Name
emailAddress                    = Email Address

# Optionally, specify some defaults.
countryName_default             = IT
stateOrProvinceName_default     = 
localityName_default            =
0.organizationName_default      = $SOGGETTO
organizationalUnitName_default  =
emailAddress_default            =

[ocsp_info]
caIssuers;URI.0 = https://127.0.0.1:8444/ca_TEST.cert.pem
caIssuers;URI.1 = https://127.0.0.1:8445/ca_TEST.cert.pem

[ v3_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[ v3_intermediate_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true, pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign

[ client_cert ]
basicConstraints = CA:FALSE
nsCertType = client
nsComment = "OpenSSL Generated Client Certificate"
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer:always
keyUsage = critical, nonRepudiation, digitalSignature, keyEncipherment
extendedKeyUsage = clientAuth
authorityInfoAccess = @ocsp_info

[ server_cert ]
basicConstraints = CA:FALSE
nsCertType = server
nsComment = "OpenSSL Generated Server Certificate"
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer:always
keyUsage = critical, nonRepudiation, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
authorityInfoAccess = @ocsp_info

[ clientserver_cert ]
basicConstraints = CA:FALSE
nsCertType = server,client
nsComment = "OpenSSL Generated Client e Server Certificate"
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer:always
keyUsage = critical, nonRepudiation, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth,clientAuth
authorityInfoAccess = @ocsp_info

[ ocsp_cert ]
basicConstraints = CA:FALSE
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer:always
keyUsage                = critical,digitalSignature
extendedKeyUsage        = critical,OCSPSigning

EOCONF

fi

}

function certificato_ca {
SOGGETTO="$1"
CA_SUBJECT="$2"
WORKDIR="$3"

NOME_NOSPAZI="${SOGGETTO// /_}"
PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"

if [ -f ${PKI_DIR}/serial \
-a -f ${PKI_DIR}/index.txt \
-a -f ${PKI_DIR}/index.txt.attr \
-a -f ${CONF_FILE} ]
then
        CommonName=$(getCommonName "${CA_SUBJECT}")	
	if [ -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem" \
	-a -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt" \
	-a -f "${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem" ]
	then
		echo "Certification Authority per ${SOGGETTO} gia' esistente."
	else
        	echo "Creazione Certification Authority per ${SOGGETTO}"
	        echo "Creo chiave privata Root CA su ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem"

        	openssl genrsa -out ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem 4096


	        echo "Creo certificato Root CA su ${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem"
        	CA_SUBJ_CANONICO=$(build_DN "${CA_SUBJECT}")
	        openssl req -config ${CONF_FILE} \
        	-key ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem \
	        -new -x509 -days 10950  -extensions v3_ca \
        	-out ${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem \
	        -subj "${CA_SUBJ_CANONICO}"
        	chmod 444 ${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem

	        cat /dev/urandom |tr -dc '[:alnum:][:digit:]_.;,@%#!^&*()+-'|head -c 20 > ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
        	openssl rsa \
	        -in  ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem \
        	-aes256 \
	        -passout file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
        	-out  ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem.withpasswd

	        echo "Proteggo la chiave privata Root CA con password"
        	/bin/mv -f ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem.withpasswd ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem
	        chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
        	chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem
	fi
else
	echo "Database per ${SOGGETTO} non inizializzato."
fi
}




function certificato_ocsp {
SOGGETTO="$1"
OCSP_SUBJECT="$2"
WORKDIR="$3"

        NOME_NOSPAZI="${SOGGETTO// /_}"
        PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
        CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"

if [ -f ${PKI_DIR}/serial \
-a -f ${PKI_DIR}/index.txt \
-a -f ${PKI_DIR}/index.txt.attr \
-a -f ${CONF_FILE} ]
then

	if [ -f "${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem"  \
	-a -f "${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt" \
	-a -f "${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem" ]
	then
		echo "Richiesta di certificazione su ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem Gia esistente"
	else
	        echo "Creazione End Entity per ${SOGGETTO}"
        	echo "Creo chiave privata End Entity su ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem"

	        openssl genrsa -out ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem 2048

        	cat /dev/urandom |tr -dc '[:alnum:][:digit:]_.;,@%#!^&*()+-'|head -c 16 > ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt
	        openssl rsa \
        	-in  ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem \
	        -aes128 \
        	-passout file:${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt \
	        -out  ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem.withpasswd

	        echo "Proteggo la chiave privata End Entity con password"
        	#/bin/mv -f ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem.withpasswd ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem
	        chmod 400 ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt
        	chmod 400 ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem

	        echo "Creo richiesta di certificazione su ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem"
        	OCSP_SUBJ_CANONICO=$(build_DN "${OCSP_SUBJECT}")
	        openssl req -config ${CONF_FILE} \
        	-key ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.key.pem \
	        -passin file:${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt \
        	-new -sha256 \
		-out ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem \
	        -subj "${OCSP_SUBJ_CANONICO}"
        	chmod 400 ${PKI_DIR}/private/ocsp_${NOME_NOSPAZI}.README.txt
	fi
	if [ -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem" \
	-a -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt" \
	-a -f "${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem" ]
	then
		if [ -f "${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem" ]
		then
			echo "Richiesta di certificazione ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem gia' firmata su ${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem"
		else
	        	echo "Firmo richiesta di certificazione ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem su ${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem"

		        chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
        		openssl ca -config ${CONF_FILE} \
		        -batch \
        		-passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
	        	-extensions ocsp_cert -days 3650 -notext -md sha256 \
	        	-in ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem \
		        -out ${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem

                        openssl ca -config ${CONF_FILE} \
                        -batch \
						-gencrl \
                        -passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
                        -out ${PKI_DIR}/crl/${NOME_NOSPAZI}_crl.pem

        		chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
		        chmod 444 ${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem


		fi
	else
        	echo "Impossibile firmare richiesta di certificazione ${PKI_DIR}/csr/ocsp_${NOME_NOSPAZI}.csr.pem su ${PKI_DIR}/certs/ocsp_${NOME_NOSPAZI}.cert.pem"
	        echo "Errore: Certification Autority non esistente su ${PKI_DIR}"
	
	fi
else
        echo "Database per ${SOGGETTO} non inizializzato."
fi

}



function certificato_ee {
SOGGETTO="$1"
EE_SUBJECT="$2"
TIPO="$3"
# Fisso entrambe le caratteristiche per facilità nei test
TIPO="clientserver"
WORKDIR="$4"

        NOME_NOSPAZI="${SOGGETTO// /_}"
        PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
        CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"

if [ -f ${PKI_DIR}/serial \
-a -f ${PKI_DIR}/index.txt \
-a -f ${PKI_DIR}/index.txt.attr \
-a -f ${CONF_FILE} ]
then
        RealCommonName=$(getCommonName "${EE_SUBJECT}")
        CommonName="${RealCommonName// /_}"
        CommonName="${CommonName//\//_}"


	if [ -f "${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem"  \
	-a -f "${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt" \
	-a -f "${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem" ]
	then
		echo "Richiesta di certificazione su ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem Gia esistente"
	else
	        echo "Creazione End Entity per ${SOGGETTO}"
        	echo "Creo chiave privata End Entity su ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem"

	        openssl genrsa -out ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem 2048

        	#cat /dev/urandom |tr -dc '[:alnum:][:digit:]_.;,@%#!^&*()+-'|head -c 16 > ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt
		echo "123456" > ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt
	        openssl rsa \
        	-in  ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem \
	        -aes128 \
        	-passout file:${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt \
	        -out  ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem.withpasswd

	        echo "Proteggo la chiave privata End Entity con password"
        	/bin/mv -f ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem.withpasswd ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem
	        chmod 400 ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt
        	chmod 400 ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem

	        echo "Creo richiesta di certificazione su ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem"
        	EE_SUBJ_CANONICO=$(build_DN "${EE_SUBJECT}")
	        openssl req -config ${CONF_FILE} \
        	-key ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.key.pem \
	        -passin file:${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt \
        	-new -sha256 \
		-out ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem \
	        -subj "${EE_SUBJ_CANONICO}"
        	chmod 400 ${PKI_DIR}/private/ee_${NOME_NOSPAZI}_${CommonName}.README.txt
	fi
	if [ -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem" \
	-a -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt" \
	-a -f "${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem" ]
	then
		if [ -f "${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem" ]
		then
			echo "Richiesta di certificazione ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem gia' firmata su ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"
		else
	        	echo "Firmo richiesta di certificazione ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem su ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"

		        chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
        		openssl ca -config ${CONF_FILE} \
		        -batch \
        		-passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
	        	-extensions ${TIPO}_cert -days 3650 -notext -md sha256 \
	        	-in ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem \
		        -out ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem

                        openssl ca -config ${CONF_FILE} \
                        -batch \
						-gencrl \
                        -passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
                        -out ${PKI_DIR}/crl/${NOME_NOSPAZI}_crl.pem

        		chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
		        chmod 444 ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem


		fi
	else
        	echo "Impossibile firmare richiesta di certificazione ${PKI_DIR}/csr/ee_${NOME_NOSPAZI}_${CommonName}.csr.pem su ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"
	        echo "Errore: Certification Autority non esistente su ${PKI_DIR}"
	
	fi
else
        echo "Database per ${SOGGETTO} non inizializzato."
fi

}

function revoke_ee {
SOGGETTO="$1"
EE_SUBJECT="$2"
WORKDIR="$3"

        NOME_NOSPAZI="${SOGGETTO// /_}"
        PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
        CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"

# -crl_reason option where the reason is one of the following:
# unspecified
# keyCompromise
# CACompromise
# affiliationChanged
# superseded
# cessationOfOperation
# certificateHold
CRL_REASON="cessationOfOperation"

if [ -f ${PKI_DIR}/serial \
-a -f ${PKI_DIR}/index.txt \
-a -f ${PKI_DIR}/index.txt.attr \
-a -f ${CONF_FILE} ]
then
        RealCommonName=$(getCommonName "${EE_SUBJECT}")
        CommonName="${RealCommonName// /_}"
        CommonName="${CommonName//\//_}"
	if [ -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.key.pem" \
	-a -f "${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt" \
	-a -f "${PKI_DIR}/certs/ca_${NOME_NOSPAZI}.cert.pem" ]
	then
		if [ -f "${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem" ]
		then
			echo "Revoco certificato ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"

			chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
			openssl ca -config ${CONF_FILE} \
			-batch \
			-passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
			-revoke  ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem \
			-crl_reason ${CRL_REASON}

			openssl ca -config ${CONF_FILE} \
			-batch \
			-gencrl \
			-passin file:${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt \
			-out ${PKI_DIR}/crl/${NOME_NOSPAZI}_crl.pem

			chmod 400 ${PKI_DIR}/private/ca_${NOME_NOSPAZI}.README.txt
			chmod 444 ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem

		else
			echo "Impossibile revocare certificato ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"
	        echo "Errore: Certificato End Entity non esistentte su ${PKI_DIR}"

		fi
	else
        	echo "Impossibile revocare certificato ${PKI_DIR}/certs/ee_${NOME_NOSPAZI}_${CommonName}.cert.pem"
	        echo "Errore: Certification Autority non esistente su ${PKI_DIR}"
	
	fi

else
        echo "Database per ${SOGGETTO} non inizializzato."
fi

}


function ocsp_reponder_printcmd {
SOGGETTO="$1"
WORKDIR="$2"

        NOME_NOSPAZI="${SOGGETTO// /_}"
        PKI_DIR="${WORK_DIR}/${NOME_NOSPAZI}/ca"
        CONF_FILE="${WORK_DIR}/${NOME_NOSPAZI}/openssl_${NOME_NOSPAZI}.conf"
		#Openssl 1.1 non supporta passin, per questo ho generato la chiave ocsp senza password
		echo "OCSP response is signed by an responder certificate (case 2: same CA)"
		echo "openssl ocsp -index index.txt -port 64900 -rsigner ocsp_${NOME_NOSPAZI}.cert.pem -rkey ocsp_${NOME_NOSPAZI}.key.pem -CA ca_${NOME_NOSPAZI}.cert.pem -text -out /tmp/ocsp.log"
		echo ""
		echo "OCSP response is signed by an responder certificate (case 3: differente CA)"
		echo "openssl ocsp -index index.txt -port 64901 -rsigner ../crl/ExampleClient1.crt -rkey ../crl/ExampleClient1.key -CA ca_${NOME_NOSPAZI}.cert.pem -text -out /tmp/ocsp.log"

}
