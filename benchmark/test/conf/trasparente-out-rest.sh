elencoTestTrasparenteOutRest="out-rest_proxy_no-trace out-rest_proxy_db-trace"

tests["out-rest_proxy_db-trace"]="out_rest_proxy_DBTrace"
tests["out-rest_proxy_no-trace"]="out_rest_proxy_NoTrace"

function out_rest_proxy_DBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function out_rest_proxy_NoTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}

