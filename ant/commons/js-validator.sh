# js validator 

SRC=$1

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

node -c ${SRC}
