OP2_DIST=$1
LIB=$2
PRIVATE_KEY=$3
PUBLIC_KEY=$4
OUTPUT_FILE=$5
ALIAS=$6

java -classpath ${OP2_DIST}/*:${LIB}/commons/*:${LIB}/security/*:${LIB}/cxf/*:${LIB}/jackson/*:${LIB}/swagger/* org.openspcoop2.utils.certificate.JWKPrivateKeyConverter ${PUBLIC_KEY} ${PRIVATE_KEY} ${OUTPUT_FILE} ${ALIAS} true true
