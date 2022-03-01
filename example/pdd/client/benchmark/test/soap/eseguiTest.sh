if [ ! -f ../../config.properties ]
then
        echo "File config.properties not exists"
        exit 10
fi
source ../../config.properties

jmeterTestFile=${BENCHMARK_HOME}/test/soap/TestErogazioni.jmx

# Secondi
#duration=20
duration=2

# Threads
#threads="20 50 100"
threads="3"
threadsRampUp=5

# Dimensione dei test
dimensioni="1024"
#dimensioni="1024 51200 409600"

# Soggetto
soggetto=ENTE

# Protocollo (api: ProfiloAPIGateway, soap: ModI)
#protocollo=api
protocollo=soap

# ProfiloSicurezza
# none: nessun profilo di sicurezza
# digest: profilo con integrità del payload
# auth: profilo con wssecurity utilizzato solamente per l'autenticazione
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
# test: LineeGuida con integrità del payload nella sola richiesta
# test2: LineeGuida con integrità del payload anche nella risposta + digestRichiesta
# test3: LineeGuida con integrità del payload nella sola richiesta + filtroDuplicati
# [profiloSicurezza=auth]
# test4: LineeGuida con IDAuth nella sola richiesta
# test5: LineeGuida con IDAuth anche nella risposta
# test6: LineeGuida con IDAuth nella sola richiesta + filtroDuplicati
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
			${binJMeter}/jmeter -n -t ${jmeterTestFile} -l ${resultDir}/OUTPUT.txt -JnodoRunIP=${nodoRunIP} -JnodoRunPort=${nodoRunPort} -JclientIP=${clientIP} -JtestFileDir=${testFileDir} -JlogDir=${logDir} -Jthreads=${threadNumber} -Jduration=${duration} -JthreadsRampUp=${threadsRampUp}  -Jdimensione=${dimensione} -Jprofilo=${profilo} -Jazione=${azione} -JtipoTest=${tipoTest} -Jsoggetto=${soggetto}  -JsleepMin=${sleepMin} -JsleepMax=${sleepMax} -JproxyHost=${proxyHost} -JproxyPort=${proxyPort} -Jprotocollo=${protocollo} -JprofiloSicurezza=${profiloSicurezza}
			mv ${resultDir}/OUTPUT.txt ${resultDir}/$threadNumber/$dimensione/${profiloSicurezza}_${tipoTest}/${sleepMin}_${sleepMax}/
			echo "--------- test (profiloSicurezza:${profiloSicurezza} tipoTest:${tipoTest} dimensione:$dimensione threads:$threadNumber) finished"
		done
	done
done

