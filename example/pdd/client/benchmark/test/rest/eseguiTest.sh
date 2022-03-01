if [ ! -f ../../config.properties ]
then
        echo "File config.properties not exists"
        exit 10
fi
source ../../config.properties

jmeterTestFile=${BENCHMARK_HOME}/test/rest/TestErogazioni.jmx

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
#server="50_100 100_500 200_1000"
server="50_100"

rm -rf ${resultDir}
mkdir ${resultDir}
for threadNumber in $threads; do
	echo "$threadNumber"
	mkdir ${resultDir}/$threadNumber
	for dimensione in $dimensioni; do
		echo "- $dimensione"
		mkdir ${resultDir}/$threadNumber/$dimensione
		for tipoTest in $tipiTest; do
			profilo=1
			echo "--- $tipoTest"
			mkdir ${resultDir}/$threadNumber/$dimensione/${profilo}_${tipoTest}
			for serverDelay in $server; do
				echo "------- delay:${serverDelay}"
				mkdir ${resultDir}/$threadNumber/$dimensione/${profilo}_${tipoTest}/${serverDelay}
				echo "--------- test (profilo:${profilo} tipoTest:$tipoTest dimensione:$dimensione threads:$threadNumber) ..."
				${binJMeter}/jmeter -n -t ${jmeterTestFile} -l ${resultDir}/OUTPUT.txt -JnodoRunIP=${nodoRunIP} -JnodoRunPort=${nodoRunPort} -JclientIP=${clientIP} -JtestFileDir=${testFileDir} -JlogDir=${logDir} -Jthreads=${threadNumber} -Jduration=${duration} -JthreadsRampUp=${threadsRampUp}  -Jdimensione=${dimensione} -Jprofilo=${profilo} -Jazione=${azione} -JtipoTest=${tipoTest} -Jsoggetto=${soggetto}  -JserverDelay=${serverDelay} -JproxyHost=${proxyHost} -JproxyPort=${proxyPort} -Jprotocollo=${protocollo} -JprofiloSicurezza=${profiloSicurezza}
				mv ${resultDir}/OUTPUT.txt ${resultDir}/$threadNumber/$dimensione/${profilo}_${tipoTest}/${serverDelay}/
				echo "--------- test (profilo:${profilo} tipoTest:${tipoTest} dimensione:$dimensione threads:$threadNumber) finished"
			done
		done
	done
done

