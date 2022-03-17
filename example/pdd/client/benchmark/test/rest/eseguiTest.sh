if [ ! -f ../../config.properties ]
then
        echo "File config.properties not exists"
        exit 10
fi
source ../../config.properties

jmeterTestFile=${BENCHMARK_HOME}/test/rest/TestErogazioni.jmx
jmeterGraphFile=${BENCHMARK_HOME}/test/rest/GraphsGenerator.jmx

elencoTest="trasparente  trasparenteNoRegistrazione  trasparenteValidazione  trasparenteNoRegistrazioneValidazione  modiDoppioHeaderRichiesta  modiDoppioHeaderRichiestaRispostaDigestRichiesta  modiDoppioHeaderRichiestaFiltroDuplicati  modiHeaderAgidOAuthRichiesta  modiHeaderAgidOAuthRichiestaRispostaDigestRichiesta  modiHeaderAgidOAuthRichiestaFiltroDuplicati"

# Configurazione
duration="2"

# Threads
threads="50 100 200"	#; threads=50
threadsRampUp=5

# Dimensione dei test
dimensioni=1024			#; dimensioni="1024 51200 409600"
iterazioni=1
doAllTests=false
riscaldamento=false

# ServerDelay
declare -A minMaxSleeps
minMaxSleeps[0]=1		# No Delay
#minMaxSleeps[50]=100	# Intervallo 1
#minMaxSleeps[100]=500	# Intervallo 2
#minMaxSleeps[200]=1000 # Intervallo 3

function usage() {
	echo -e ""
	echo -e "UTILIZZO"
	echo -e "	./eseguiTest.sh [-r] [-a] [-n X] test\n"
	echo -e " 		-r: Esegui il riscaldamento"
	echo -e "		-a: Esegui tutti i test"
	echo -e "		-n: Esegue tutta la batteria di test X volte\n"
	echo -e "		test = [ $elencoTest ]\n"
}



# Aggiungere due opzioni per ogni test:
# TODO: Fare un test che fa tutto. Le dashboard comunque separate per funzioni. Alla fine csv unico, ordinato per nome sample


# I seguenti valori identificano un test e vengono configurati dalle funzioni
# di test di sotto.

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

function trasparente() {
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function trasparenteNoRegistrazione() {
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function trasparenteValidazione() {
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function trasparenteNoRegistrazioneValidazione() {
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiDoppioHeaderRichiesta() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiDoppioHeaderRichiestaRispostaDigestRichiesta() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiDoppioHeaderRichiestaFiltroDuplicati() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiHeaderAgidOAuthRichiesta() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiHeaderAgidOAuthRichiestaRispostaDigestRichiesta() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test5
	outputDir=${resultDir}/${FUNCNAME[0]}
}


function modiHeaderAgidOAuthRichiestaFiltroDuplicati() {
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test6
	outputDir=${resultDir}/${FUNCNAME[0]}
}

declare -A testDescriptions
testDescriptions[trasparente]="Vengono registrate le transazioni"
testDescriptions[trasparenteNoRegistrazione]="Non vengono registrate le transazioni"
testDescriptions[trasparenteValidazione]="Vengono registrate le transazioni"
testDescriptions[trasparenteNoRegistrazioneValidazione]="Non vengono registrate le transazioni"
testDescriptions[modiDoppioHeaderRichiesta]="LineeGuida con doppio header nella sola richiesta"
testDescriptions[modiDoppioHeaderRichiestaRispostaDigestRichiesta]="LineeGuida con dobbio header anche nella risposta + digestRichiesta"
testDescriptions[modiDoppioHeaderRichiestaFiltroDuplicati]="LineeGuida con doppio header nella sola richiesta + filtroDuplicati"
testDescriptions[modiHeaderAgidOAuthRichiesta]="LineeGuida con header Agid e header OAuth nella sola richiesta"
testDescriptions[modiHeaderAgidOAuthRichiestaRispostaDigestRichiesta]="LineeGuida con header Agid e header OAuth anche nella risposta + digestRichiesta"
testDescriptions[modiHeaderAgidOAuthRichiestaFiltroDuplicati]="LineeGuida con header Agid e header OAuth nella sola richiesta + filtroDuplicati"

function build_jmx_command() {
	echo "${binJMeter}/jmeter -n -t ${jmeterTestFile} -l ${resultDir}/OUTPUT.txt -JnodoRunIP=${nodoRunIP} -JnodoRunPort=${nodoRunPort} -JclientIP=${clientIP} -JtestFileDir=${testFileDir} -JlogDir=${logDir} -Jthreads=${threadNumber} -Jduration=${duration} -JthreadsRampUp=${threadsRampUp}  -Jdimensione=${dimensione} -Jprofilo=${profilo} -Jazione=${azione} -JtipoTest=${tipoTest} -Jsoggetto=${soggetto}  -JsleepMin=${sleepMin} -JsleepMax=${sleepMax} -JproxyHost=${proxyHost} -JproxyPort=${proxyPort} -Jprotocollo=${protocollo} -JprofiloSicurezza=${profiloSicurezza} -JdirResult=${outputDir} -j ${logDir}/jmeter.log -Jiterazione=$it"

}
function run_jmx_test() {
	rm -rf ${outputDir}
	mkdir -p ${outputDir}
	for threadNumber in $threads; do
		echo "$threadNumber"
		for dimensione in $dimensioni; do
			echo "- $dimensione"
			for tipoTest in $tipiTest; do
				for sleepMin in "${!minMaxSleeps[@]}"; do
					sleepMax=${minMaxSleeps[$sleepMin]}

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

					echo ""
					echo "--------- test (protocollo:${protocollo} profiloSicurezza:${profiloSicurezza} tipoTest:${tipoTest} dimensione:$dimensione threads:$threadNumber azione:$azione sleepMin:$sleepMin sleepMax:$sleepMax) finished"
				done
			done
		done
	done
}

# TODO: Aggiungere alle sample_variables una descrizione del test. (Certo verrà ripetuta ogni riga ma almeno ce la troviamo alla fine...)
# TODO: Rivedere l'uso delle sample_variables, utilizzare semmai solo la label ed estrarre le info per i csv derivati dalla label.
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
	${binJMeter}/jmeter -g ${outputDir}/${joinedCsv} -o ${outputDir}/dashboard -Jjmeter.reportgenerator.report_title="$1 - ${testDescriptions[$1]}"
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
	headers="$headers,iterazione,profiloSicurezza,sleepMax,sleepMin,protocollo,dimensione,tipoTest,azione,soggetto,threads,threads.ramp-up"
	echo $headers > ${outputDir}/aggregatiConPluginExtended.csv

	# Leggo le linee dal file e le arrichisco
	while read -r line; do
		label=`cut -f 1 -d "," <<< $line`

		if [ "$label" = "TOTAL" ]; then
			#echo "Skipping label $label"
			continue
		fi

		# Leggo la seconda riga ed estraggo gli ultimi cinque valori
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

		new_line="$line,$_iterazione,$_profiloSicurezza,$_sleepMax,$_sleepMin,$_protocollo,$_dimensione,$_tipo_test,$_azione,$_soggetto,$_threads,$_ramp_up"
		
		echo $new_line >> ${outputDir}/aggregatiConPluginExtended.csv

		#echo "==================="
	done < <(tail -n "+2" ${outputDir}/aggregatiConPlugin.csv)


	# Adesso eseguiamo il test "Dummy" che porterà poi alla generazione dei grafici
	set -x
	${binJMeter}/jmeter -n -t ${jmeterGraphFile} -JlogDir=${logDir}  -j ${logDir}/jmeter.log -JoutputFolder=${outputDir}/graphs -JjoinedResults=${outputDir}/${joinedCsv}
	set +x

	# Pulizia
	rm ${outputDir}/aggregatiConPlugin.csv
	rm ${outputDir}/joined-results.csv
	mv ${outputDir}/aggregatiConPluginExtended.csv ${outputDir}/result-aggregate.csv
}


# ====================================
#				MAIN
# ====================================

if [ $# = 0 ]; then
	usage
	exit
fi

# TODO: Dare la possibilità di rieseguire un test puntuale specificando dimensioni, min\MaxSleep, ramp_up, duration e nthreads

while getopts 'hran:' opt; do
  case "$opt" in
    r)
		riscaldamento=true
      	;;

    a)
		doAllTests=true
      	;;

    n)
		if (( $OPTARG - 0 <= 0 )); then
			echo "Numero di iterazioni non valido, inserire un valore positivo"
			exit 1
		fi
		iterazioni=$OPTARG
      	;;
   
    ?|h)
		usage
		exit 1
      	;;
  esac	
done
shift "$(($OPTIND -1))"

if $doAllTests; then
	testToRun=$elencoTest
else
	testToRun=$@
fi

rm -r ${resultDir}
mkdir -p ${resultDir}

for testConfigurator in $testToRun; do
	echo -e "\n==================================="
	echo -e "Esecuzione del test $testConfigurator"
	echo -e "===================================\n"

	if [[ $elencoTest =~ $testConfigurator ]]; then
		# Chiamo la funzione di test che configura l'ambiente.
		$testConfigurator
		# Eseguo il test jmx
		run_jmx_test
		# Aggrego i risultati e produco grafici e dashboard
		aggregate_report $testConfigurator
	else
		echo "ERRORE: Test $testConfigurator inesistente."
	fi
done

