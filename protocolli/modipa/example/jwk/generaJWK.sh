OP2_DIST=$1
LIB=$2
PUBLIC_KEY=$3
OUTPUT_FILE=$4
ALIAS=$5

java -classpath ${OP2_DIST}/*:${LIB}/commons/*:${LIB}/security/*:${LIB}/cxf/*:${LIB}/jackson/*:${LIB}/swagger/* org.openspcoop2.utils.certificate.JWKPublicKeyConverter ${PUBLIC_KEY} ${OUTPUT_FILE} ${ALIAS} false true
