if [ ! -f ../../config.properties ]
then
        echo "File config.properties not exists"
        exit 10
fi
source ../../config.properties

jmeterTestFile=${BENCHMARK_HOME}/test/rest/TestErogazioni.jmx
jmeterGraphFile=${BENCHMARK_HOME}/test/rest/GraphsGenerator.jmx

# Secondi
#duration=20
duration=2

# Threads
threads="20 50"
#threads="20 50 100"
#threads="100 200"
threadsRampUp=5

# Dimensione dei test
dimensioni=1024
#dimensioni="1024 51200 409600"

# Soggetto
soggetto=ENTE

# Protocollo (api: ProfiloAPIGateway, rest: ModI)
#protocollo=api
protocollo=rest

# ProfiloSicurezza
# none: nessun profilo di sicurezza
# digest: profilo con integrità del payload
profiloSicurezza=digest

# --- API Gateway --
#tipiTest="Proxy Validazione"
# [profiloSicurezza=none]
# test: vengono registrate le transazioni
# test2: non vegono registrate le transazioni
#azione="test"
# -- ModI --
tipiTest="Proxy"
# [profiloSicurezza=digest]
# test: LineeGuida con doppio header nella sola richiesta
# test2: LineeGuida con dobbio header anche nella risposta + digestRichiesta
# test3: LineeGuida con doppio header nella sola richiesta + filtroDuplicati
# test4: LineeGuida con header Agid e header OAuth nella sola richiesta
# test5: LineeGuida con header Agid e header OAuth anche nella risposta + digestRichiesta
# test6: LineeGuida con header Agid e header OAuth nella sola richiesta + filtroDuplicati
azione=test

# ServerDelay
# NO delay:
sleepMin=0
sleepMax=1
# Intervallo 1
#sleepMin=50
#sleepMax=100
# Intervallo 2
#sleepMin=100
#sleepMax=500
# Intervallo 3
#sleepMin=200
#sleepMax=1000

rm -rf ${resultDir}
mkdir ${resultDir}
for threadNumber in $threads; do
	echo "$threadNumber"
	mkdir ${resultDir}/$threadNumber
	for dimensione in $dimensioni; do
		echo "- $dimensione"
		mkdir ${resultDir}/$threadNumber/$dimensione
		for tipoTest in $tipiTest; do
			echo "--- ${profiloSicurezza}/$tipoTest"
			mkdir ${resultDir}/$threadNumber/$dimensione/${profiloSicurezza}_${tipoTest}
			echo "------- delay min:${sleepMin} max:${sleepMax}"
			mkdir ${resultDir}/$threadNumber/$dimensione/${profiloSicurezza}_${tipoTest}/${sleepMin}_${sleepMax}
			echo "--------- test (profiloSicurezza:${profiloSicurezza} tipoTest:$tipoTest dimensione:$dimensione threads:$threadNumber) ..."
			${binJMeter}/jmeter -n -t ${jmeterTestFile} -l ${resultDir}/OUTPUT.txt -JnodoRunIP=${nodoRunIP} -JnodoRunPort=${nodoRunPort} -JclientIP=${clientIP} -JtestFileDir=${testFileDir} -JlogDir=${logDir} -Jthreads=${threadNumber} -Jduration=${duration} -JthreadsRampUp=${threadsRampUp}  -Jdimensione=${dimensione} -Jprofilo=${profilo} -Jazione=${azione} -JtipoTest=${tipoTest} -Jsoggetto=${soggetto}  -JsleepMin=${sleepMin} -JsleepMax=${sleepMax} -JproxyHost=${proxyHost} -JproxyPort=${proxyPort} -Jprotocollo=${protocollo} -JprofiloSicurezza=${profiloSicurezza} -JdirResult=${resultDir} -j ${logDir}/jmeter.log
			mv ${resultDir}/OUTPUT.txt ${resultDir}/$threadNumber/$dimensione/${profiloSicurezza}_${tipoTest}/${sleepMin}_${sleepMax}/
			echo "--------- test (profiloSicurezza:${profiloSicurezza} tipoTest:${tipoTest} dimensione:$dimensione threads:$threadNumber) finished"
		done
	done
done

# Alla fine del test concatena i risultati e produce le statistiche e la dashboard

# TODO: Non so perchè questo non funziona, mi da un solo csv.
#	ricorro al for per prendere tutti i filenames
#	outputs=(${resultDir}/*.csv)
#	echo "OUTPUTS: " $outputs


echo "==================================="
echo "GENERAZIONE DATI AGGREGATI E REPORT"
echo "==================================="


# prendo un csv qualunque per pescare gli headers
joinedCsv="joined-results.csv"
someoutput=(${resultDir}/*.csv) # TODO Accertati che questa cosa funzioni anche su altre bash, perchè in questo caso mi da solo un csv?
headers=`head -1 $someoutput`

# Pesco tutti i csv prodotti
files=""
for output in ${resultDir}/*.csv; do
	files="${files} ${output}"
done

# Scrivo headers e concateno tutti i csv prodotti
echo $headers > ${resultDir}/${joinedCsv}
tail -q -n +2 $files >> ${resultDir}/${joinedCsv}

# Produco dashboard e statistiche di jmeter
set -x
${binJMeter}/jmeter -g ${resultDir}/${joinedCsv} -o ${resultDir}/dashboard
set +x
echo "JMeter Report: ${resultDir}/dashboard/index.html"

# Produco statistiche aggregate in csv
set -x
${binJMeter}/JMeterPluginsCMD.sh --generate-csv ${resultDir}/aggregatiConPlugin.csv --input-jtl ${resultDir}/${joinedCsv} --plugin-type AggregateReport
set +x
echo "Aggregate Statistics: ${resultDir}/aggregatiConPlugin.csv"

echo "========================"
echo "Arricchisco il file ${resultDir}/aggregatiConPlugin.csv con i dati di input.."

# Arricchisto il file aggregatiConPlugin.csv con i valori di input dei test
# Per l'occasione creo un nuovo file.
headers=`sed -n '1{p;q}' ${resultDir}/aggregatiConPlugin.csv`
headers="$headers,dimensione,tipoTest,azione,soggetto,threads,threads.ramp-up"
echo $headers > ${resultDir}/aggregatiConPluginExtended.csv

# Leggo le linee dal file e le arrichisco
while read -r line; do
	label=`cut -f 1 -d "," <<< $line`

	if [ "$label" = "TOTAL" ]; then
		#echo "Skipping label $label"
		continue
	fi

	# Leggo la seconda riga ed estraggo gli ultimi cinque valori
	csvfile=${resultDir}/${label}.csv
	#echo "CSV FILE: $csvfile"

	riga=`sed -n '2{p;q}' $csvfile`
	#echo "Seconda Riga: $riga"

	IFS=, read -r -a test_input_values <<< "$riga"
	#echo "ARRAY: $test_input_values"

	ramp_up=${test_input_values[-1]}
	threads=${test_input_values[-2]}
	soggetto=${test_input_values[-3]}
	azione=${test_input_values[-4]}
	tipo_test=${test_input_values[-5]}
	dimensione=${test_input_values[-6]}

	new_line="$line,$dimensione,$tipo_test,$azione,$soggetto,$threads,$ramp_up"
	#echo "New Line: $new_line"

	echo $new_line >> ${resultDir}/aggregatiConPluginExtended.csv

	#echo "==================="
done < <(tail -n "+2" ${resultDir}/aggregatiConPlugin.csv)


# Adesso eseguiamo il test "Dummy" che porterà poi alla generazione dei grafici
set -x
${binJMeter}/jmeter -n -t ${jmeterGraphFile} -JlogDir=${logDir}  -j ${logDir}/jmeter.log -JoutputFolder=${resultDir}/graphs -JjoinedResults=${resultDir}/${joinedCsv}
set +x
