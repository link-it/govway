# I seguenti valori identificano un test e vengono configurati dalle funzioni
# di test di sotto.

# 		REST
# --- API Gateway --
# protocollo=api
# tipiTest="Proxy Validazione RateLimiting"
# Azioni [profiloSicurezza=none] - nessun profilo di sicurezza
# 	test: vengono registrate le transazioni
# 	test2: non vegono registrate le transazioni
#	test3: vengono registrate le transazioni solo su filesystem
#	test4: vengono registrate le transazioni sia su db che su filesystem

# -- ModI --
# protocollo=rest
# tipiTest="Proxy"

# Azioni [profiloSicurezza=digest] - digest: profilo con integrità del payload
#	  test: LineeGuida con doppio header nella sola richiesta
#	  test2: LineeGuida con dobbio header anche nella risposta + digestRichiesta
#	  test3: LineeGuida con doppio header nella sola richiesta + filtroDuplicati
#	  test4: LineeGuida con header Agid e header OAuth nella sola richiesta
#	  test5: LineeGuida con header Agid e header OAuth anche nella risposta + digestRichiesta
#	  test6: LineeGuida con header Agid e header OAuth nella sola richiesta + filtroDuplicati

#		SOAP
# --- API Gateway --
# protocollo=api
# tipiTest="Proxy Validazione RateLimiting"
# Azioni [profiloSicurezza=none]
#	test: vengono registrate le transazioni
#	test2: non vegono registrate le transazioni
#	test3: vengono registrate le transazioni solo su filesystem
#	test4: vengono registrate le transazioni sia su db che su filesystem

# -- ModI --
# protocollo=soap
# tipiTest="Proxy"
# Azioni [profiloSicurezza=digest]
#	  test: LineeGuida con integrità del payload nella sola richiesta
#	  test2: LineeGuida con integrità del payload anche nella risposta + digestRichiesta
#	  test3: LineeGuida con integrità del payload nella sola richiesta + filtroDuplicati
# Azioni [profiloSicurezza=auth]
#	  test4: LineeGuida con IDAuth nella sola richiesta
#	  test5: LineeGuida con IDAuth anche nella risposta
#	  test6: LineeGuida con IDAuth nella sola richiesta + filtroDuplicati

# TODO: rendi tipiTest una variabile normale e non un array, e togli il for quando la si usa
# TODO: Metti nell'elenco test quelli di rateLimiting


if [ ! -f ../config.properties ]
then
        echo "File config.properties not exists"
        exit 10
fi
source ../config.properties

jmeterRestTestFile=${BENCHMARK_HOME}/test/TestErogazioniRest.jmx
jmeterSoapTestFile=${BENCHMARK_HOME}/test/TestErogazioniSoap.jmx
jmeterGraphFile=${BENCHMARK_HOME}/test/GraphsGenerator.jmx


soggetto=ENTE
duration="60"
threads="50 100 200"
threadsRampUp=5
dimensioni="1024 51200 409600"
iterazioni=1
riscaldamento=false
keep=false
allTests=false

# ServerDelay
declare -A minMaxSleeps
minMaxSleeps[0]=1		# No Delay
minMaxSleeps[50]=100	# Intervallo 1
minMaxSleeps[100]=500	# Intervallo 2
minMaxSleeps[200]=1000  # Intervallo 3

function usage() {
	echo -e ""
	echo -e "UTILIZZO"
	echo -e "	NOTA: Tutte le opzioni vanno specificate prima dell'elenco dei test, altrimenti saranno ignorate!\n"
	echo -e "	./eseguiTest.sh [-a] [-r] [-i X] [-k] [-d X] [-s X] [-n X] [-u X] [-t X] test\n"
	echo -e "		-a: Esegui tutti i test                       (All)"
	echo -e " 		-r: Esegui il riscaldamento                   (Riscaldamento)"
	echo -e "		-i: Esege tutta la batteria di test X volte   (Iterazioni)"
	echo -e "		-k: Mantiene i csv intermedi                  (Keep)\n"
	echo -e "		Opzioni per iniziare una sola istanza di un test\n"
	echo -e "		-d: Specifica una delle possibili dimensioni  (Dimensione)"
	echo -e " 			1024, 51200, 409600"
	echo -e "		-s: Specifica il minSleep,determinando il max (Sleep)"
	echo -e "			0->1, 50->100, 100->500, 200->1000"
	echo -e "		-n: Numero di threads                         (Nthreads)"
	echo -e "		-u: Ramp-up                                   (ramp-Up)"
	echo -e "		-t: Durata in secondi                         (Time duration)\n"
	echo -e "		Scenari di test:\n"
	echo -e "		API Gateway REST = [ $elencoTestTrasparenteRest ]\n"
	echo -e "		API Gateway SOAP = [ $elencoTestTrasparenteSoap ]\n"
	echo -e "		ModI REST = [ $elencoTestModiRest ]\n"
	echo -e "		ModI SOAP = [ $elencoTestModiSoap ]\n"
}


# TODO: Prendi il realpath dello script eseguiTest.sh e usa un percorso assoluto
declare -A tests
. ./conf/trasparente-rest.sh
. ./conf/trasparente-soap.sh
. ./conf/modi-rest.sh
. ./conf/modi-soap.sh

elencoTest="$elencoTestTrasparenteRest $elencoTestTrasparenteSoap $elencoTestModiRest $elencoTestModiSoap"

function build_jmx_command() {
	echo "${binJMeter}/jmeter -n -t ${jmeterTestFile} -l ${resultDir}/OUTPUT.txt -JnodoRunIP=${nodoRunIP} -JnodoRunPort=${nodoRunPort} -JclientIP=${clientIP} -JtestFileDir=${testFileDir} -JlogDir=${logDir} -Jthreads=${threadNumber} -Jduration=${duration} -JthreadsRampUp=${threadsRampUp}  -Jdimensione=${dimensione} -Jprofilo=${profilo} -Jazione=${azione} -JtipoTest=${tipoTest} -Jsoggetto=${soggetto}  -JsleepMin=${sleepMin} -JsleepMax=${sleepMax} -JproxyHost=${proxyHost} -JproxyPort=${proxyPort} -Jprotocollo=${protocollo} -JprofiloSicurezza=${profiloSicurezza} -JdirResult=${outputDir} -j ${logDir}/jmeter.log -Jiterazione=$it -JtestName=${testConfigurator}"
}

function clean_db() {
	echo ""
	echo -e "====================="
	echo -e "LANCIO SCRIPT DI PULIZIA DB: $scriptDatabaseCleaner"
	echo -e "====================="
	echo -e ""
	if $scriptDatabaseCleaner; then
		echo -e ""
		echo -e "====================="
		echo -e "Script $scriptDatabaseCleaner eseguito con successo"
		echo -e "====================="
		echo -e ""
	else
		echo -e ""
		echo -e "====================="
		echo -e "ERRORE nell'esecuzione dello script $scriptDatabaseCleaner"
		echo -e "====================="
		echo -e ""
		exit 1
	fi

}

function run_jmx_test() {
	rm -rf ${outputDir}
	mkdir -p ${outputDir}
	rm -rf ${logDir}
	mkdir -p ${logDir}

	for threadNumber in $threads; do
		for dimensione in $dimensioni; do
			for tipoTest in $tipiTest; do
				for sleepMin in "${!minMaxSleeps[@]}"; do
					sleepMax=${minMaxSleeps[$sleepMin]}
					
					if [[ ! -z $scriptDatabaseCleaner ]]; then
						clean_db
					fi

					echo "$threadNumber"
					echo "- $dimensione"
					echo "--- ${profiloSicurezza}/$tipoTest"
					echo "------- delay min:${sleepMin} max:${sleepMax}"
					echo "--------- test (protocollo:${protocollo} profiloSicurezza:${profiloSicurezza} tipoTest:$tipoTest dimensione:$dimensione threads:$threadNumber azione:$azione sleepMin:$sleepMin sleepMax:$sleepMax) ..."
					echo ""

					it=1
					command=$(build_jmx_command)
					if $riscaldamento; then
						echo ""
						echo -e "====================="
						echo -e "Fase di Riscaldamento"
						echo -e "====================="
						echo ""
						echo "+ $command"
						eval $command
						rm ${resultDir}/OUTPUT.txt
					fi

					for it in $(seq 1 $iterazioni); do
						command=$(build_jmx_command)
						echo "+ $command"
						eval $command
						rm ${resultDir}/OUTPUT.txt
					done
					
					if [[ $debug == "false" ]]; then
						rm -rf ${logDir}/failed*
					fi

					echo ""
					echo "--------- test (protocollo:${protocollo} profiloSicurezza:${profiloSicurezza} tipoTest:${tipoTest} dimensione:$dimensione threads:$threadNumber azione:$azione sleepMin:$sleepMin sleepMax:$sleepMax) finished"
				done
			done
		done
	done
}

function aggregate_report() {	
	echo ""
	echo "==================================="
	echo "GENERAZIONE DATI AGGREGATI E REPORT"
	echo "==================================="
	echo ""

	joinedCsv="joined-results.csv"

	# prendo un csv qualunque per pescare gli headers
	# TODO Accertati che questa cosa funzioni anche su altre bash, perchè in questo caso mi da solo un csv?
	someoutput=(${outputDir}/*.csv) 	
	headers=`head -1 $someoutput`

	# Pesco tutti i csv prodotti
	files=""
	for output in ${outputDir}/*.csv; do
		files="${files} ${output}"
	done

	# Scrivo headers e concateno tutti i csv prodotti
	echo $headers > ${outputDir}/${joinedCsv}
	tail -q -n +2 $files >> ${outputDir}/${joinedCsv}

	# Produco dashboard e statistiche di jmeter
	set -x
	${binJMeter}/jmeter -g ${outputDir}/${joinedCsv} -o ${outputDir}/dashboard -Jjmeter.reportgenerator.report_title="$1 - $description"
	set +x

	echo -e "\n========================"
	echo -e "JMeter Report: ${outputDir}/dashboard/index.html"
	echo -e "========================\n"

	# Produco statistiche aggregate in csv
	set -x
	${binJMeter}/JMeterPluginsCMD.sh --generate-csv ${outputDir}/aggregatiConPlugin.csv --input-jtl ${outputDir}/${joinedCsv} --plugin-type AggregateReport
	set +x

	echo -e "\n========================"
	echo -e "Aggregate Statistics: ${outputDir}/aggregatiConPlugin.csv"
	echo -e "========================\n"

	echo -e "\n========================"
	echo -e "Arricchisco il file ${outputDir}/aggregatiConPlugin.csv con i dati di input.."
	echo -e "========================\n"

	# Arricchisco il file aggregatiConPlugin.csv con i valori di input dei test
	# Per l'occasione creo un nuovo file.
	headers=`sed -n '1{p;q}' ${outputDir}/aggregatiConPlugin.csv`
	headers="$headers,riscaldamento,testName,iterazione,profiloSicurezza,sleepMax,sleepMin,protocollo,dimensione,tipoTest,azione,soggetto,threads,threads.ramp-up"
	echo $headers > ${outputDir}/aggregatiConPluginExtended.csv

	# Leggo le linee dal file degli aggregati e le arrichisco
	while read -r line; do
		# La label identifica il csv dal quale prendere i valori di test
		label=`cut -f 1 -d "," <<< $line`

		if [ "$label" = "TOTAL" ]; then
			#echo "Skipping label $label"
			continue
		fi

		# Leggo la seconda riga ed estraggo gli ultimi valori
		csvfile=${outputDir}/${label}.csv
		#echo "CSV FILE: $csvfile"

		riga=`sed -n '2{p;q}' $csvfile`
		#echo "Seconda Riga: $riga"

		IFS=, read -r -a test_input_values <<< "$riga"

		_ramp_up=${test_input_values[-1]}
		_threads=${test_input_values[-2]}
		_soggetto=${test_input_values[-3]}
		_azione=${test_input_values[-4]}
		_tipo_test=${test_input_values[-5]}
		_dimensione=${test_input_values[-6]}
		_protocollo=${test_input_values[-7]}
		_sleepMin=${test_input_values[-8]}
		_sleepMax=${test_input_values[-9]}
		_profiloSicurezza=${test_input_values[-10]}
		_iterazione=${test_input_values[-11]}
		_testName=${test_input_values[-12]}
		_riscaldamento=$riscaldamento

		new_line="$line,$_riscaldamento,$_testName,$_iterazione,$_profiloSicurezza,$_sleepMax,$_sleepMin,$_protocollo,$_dimensione,$_tipo_test,$_azione,$_soggetto,$_threads,$_ramp_up"

		echo $new_line >> ${outputDir}/aggregatiConPluginExtended.csv

		#echo "==================="
	done < <(tail -n "+2" ${outputDir}/aggregatiConPlugin.csv)

	# Adesso eseguiamo il test "Dummy" che porterà poi alla generazione dei grafici
	set -x
	${binJMeter}/jmeter -n -t ${jmeterGraphFile} -JlogDir=${logDir}  -j ${logDir}/jmeter.log -JoutputFolder=${outputDir}/graphs -JjoinedResults=${outputDir}/${joinedCsv}
	set +x

	# Pulizia
	if [[ $keep == "false" ]]; then
		echo "============================"
		echo "Cancello risultati Intermedi"
		echo "============================"
		echo "$files"
		rm -rf $files
	fi

	rm ${outputDir}/aggregatiConPlugin.csv
	rm ${outputDir}/joined-results.csv
	mv ${outputDir}/aggregatiConPluginExtended.csv ${outputDir}/result-aggregate.csv
}


# ====================================
#				MAIN
# ====================================


while getopts 'ahri:kd:s:n:u:t:' opt; do
	case "$opt" in
		a)
			allTests=true
			;;
		r)
			riscaldamento=true
			;;

		i)
			if (( $OPTARG - 0 <= 0 )); then
				echo "Numero di iterazioni non valido, inserire un valore positivo"
				exit 1
			fi
			iterazioni=$OPTARG
			;;
		k)
			keep=true
			;;
		d)
			if [[ ! $dimensioni =~ $OPTARG ]]; then
				echo "Argomento -d errato, le dimensioni possibili sono: $dimensioni"
				exit 1
			fi
			dimensioni=$OPTARG
			;;
		s)
			max=${minMaxSleeps[$OPTARG]}
			if [[ -z $max ]]; then
				echo "Argomento -s errato, le dimensioni possibili sono ${!minMaxSleeps[@]}"
				exit 1
			fi
			unset minMaxSleeps
			declare -A minMaxSleeps=( [$OPTARG]=$max )
			;;
		n)
			if (( $OPTARG - 0 <= 0 )); then
				echo "Argomento -n errato, inserire un valore positivo per il numero dei threads"
				exit 1
			fi
			threads=$OPTARG
			;;
		u)
			if (( $OPTARG - 0 <= 0 )); then
				echo "Argomento -u errato, inserire un valore positivo per il ramp-up"
				exit 1
			fi
			threadsRampUp=$OPTARG
			;;
		t)
			if (( $OPTARG - 0 <= 0 )); then
				echo "Argomento -t errato, inserire un valore positivo per la durata"
				exit 1
			fi
			duration=$OPTARG
			;;

		?|h)
			usage
			exit 1
			;;
	esac	
done
shift "$(($OPTIND -1))"

if $allTests; then
	testToRun=$elencoTest
else
	testToRun=$@
	for t in $testToRun; do
		if [[ ! $elencoTest =~ $t ]]; then
			echo "ERRORE: Test $t inesistente."
			exit 1
		fi
	done
fi

if [[ ! "$testToRun" ]]; then
	usage
	exit 1
fi

echo -e "\n==================================="
echo -e "Esecuzione batteria di test: $testToRun"
echo -e "===================================\n"

startDate=$(date -I)
startHour=$(date +%H)

rm -r ${resultDir}
mkdir -p ${resultDir}

for testConfigurator in $testToRun; do
	echo -e "\n==================================="
	echo -e "Esecuzione del test $testConfigurator"
	echo -e "===================================\n"

	# Chiamo la funzione di test che configura l'ambiente.
	#$testConfigurator
	${tests[$testConfigurator]}
	if (( $? != 0 )); then
		echo "Errore durante la configurazione del test: $testConfigurator uscita..."
		exit 1
	fi
	
	# Eseguo il test jmx
	run_jmx_test
	# Aggrego i risultati e produco grafici e dashboard
	aggregate_report $testConfigurator

	# In maniera incrementale concateno tutti i risultati aggregati, ordinandoli per label.
	# Ad essere ordinati devono essere i singoli csv con i dati aggregati da concatenare
	# e poi in quello finale. In questo modo nel csv finale abbiamo righe contigue che 
	# identificano lo stesso test e che sono ordinate per label.
	# I csv aggregati sono già ordinati, per cui è sufficiente concatenarli

	# Prendo un csv qualunque per pescare gli headers
	someoutput=(${resultDir}/*/result-aggregate.csv)
	headers=`head -1 $someoutput`

	# Pesco tutti i csv prodotti
	files=""
	for output in ${resultDir}/*/result-aggregate.csv; do
		files="${files} ${output}"
	done

	# Scrivo headers e concateno tutti i csv prodotti
	echo $headers > ${resultDir}/allResults.csv
	tail -q -n +2 $files >> ${resultDir}/allResults.csv
done

# Creo il file dei risultati minimo, con solo il throughput
awk 'BEGIN { FPAT = "([^,]+)|(\"[^\"]+\")" } { print $1 "," $11 }' ${resultDir}/allResults.csv > ${resultDir}/allResults-throughput.csv

rm -f ${resultDir}/*.tar.gz

resultName="govway-benchmark-results-$startDate-$startHour.tgz"
echo "SCRIVO IN $resultName"

tar -czf $resultName -C ${resultDir} .
mv $resultName $resultDir


