DIR=$1

if [ -z "${DIR}" ]
then
	echo "Directory non fornita"
	exit 1
fi
if [ ! -d "${DIR}"  ]
then
	echo "File indicato '${DIR}' non è una directory"
	exit 1
fi

ERRORS=$(find ${DIR} -name "*.js" -type f -exec node -c {} \; 2>&1 | grep -v "Syntax OK")
if [ -n "$ERRORS" ]; then
    echo "$ERRORS"
    exit 1
else
    echo "All JavaScript files are valid"
    exit 0
fi
