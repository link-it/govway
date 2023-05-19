CHIAVI=$1
OUTPUT=$2

echo "{" > ${OUTPUT}
echo '  "keys" : [ ' >> ${OUTPUT}
i=0
FILES="${CHIAVI}/*"
for f in $FILES
do
	if [ ! $i == 0 ]
	then
		echo "," >> ${OUTPUT}
	fi
	echo "Processing [$i] $f"
	cat ${f} >> ${OUTPUT}
	echo "" >> ${OUTPUT}
	((i=i+1))
done
echo '   ]' >> ${OUTPUT}
echo '}' >> ${OUTPUT}
