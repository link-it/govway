# js minifier (prerequisito: npm install -g terser)

SRC=$1
DEST=$2

if [ -z "${SRC}" ]
then
	echo "File sorgente non fornito"
	exit 1
fi
if [ ! -f "${SRC}"  ]
then
	echo "File sorgente fornito '${SRC}' non è un file"
	exit 1
fi

if [ -z "${DEST}" ]
then
	echo "File destinazione non fornito"
	exit 1
fi
if [ -e "${DEST}"  ]
then
	echo "File destinazione fornito '${DEST}' già esistente"
	exit 1
fi

terser ${SRC} -o ${DEST} -c -m
