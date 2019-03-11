NOME="$1"
if [ -z "${NOME}" ]
then
	echo "Errore: indicare un nome"
	echo "Usage: multipleOU.sh NOME"
	exit 2
fi

CARATTERI_ACCENTATI="$2"
if [ -z "${CARATTERI_ACCENTATI}" ]
then
	CARATTERI_ACCENTATI=false
fi

OU_CARATTERI_ACCENTATI="===àèéìùò£==="
OU_CARATTERI_SPECIALI="===?()!.:;,-_[]{}*+@#'$%&\/==="
OU="OU=govway 1st internal (primo piano, scalab) OU/OU=govway 2nd external (secondo piano scala c) OU/OU=govway Nth OU con una lunghezza per superare i 255 caratteri/OU= caratteri speciali ${OU_CARATTERI_SPECIALI}/OU= Piano=2, Scala=B, porta=3"
if [ "true" == "${CARATTERI_ACCENTATI}" ]
then
	OU="${OU}/OU= caratteri accentati ${OU_CARATTERI_ACCENTATI}"
fi
echo "Produco ou: ${OU}"

rm -rf ${NOME}
mkdir ${NOME}
pushd ${NOME}

openssl req -new -out ${NOME}.csr -newkey rsa:2048 -sha256 -nodes -keyout ${NOME}.key -subj "/C=IT/ST=Italy/L=Pisa/O=govway Organization/${OU}/CN=${NOME}"

openssl x509 -in ${NOME}.csr -out ${NOME}.pem -req -signkey ${NOME}.key -days 7300

openssl pkcs12 -export -out ${NOME}.p12 -inkey ${NOME}.key -in ${NOME}.pem -password pass:123456 -name ${NOME}

keytool -importkeystore -srckeystore ${NOME}.p12 -srcstoretype PKCS12 -srcstorepass 123456 -deststoretype JKS -destkeystore ${NOME}.jks -deststorepass 123456

openssl x509 -outform der -in ${NOME}.pem -out ${NOME}.cer

popd


