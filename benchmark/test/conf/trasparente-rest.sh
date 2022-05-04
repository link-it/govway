elencoTestTrasparenteRest="rest_proxy_DBTrace  rest_proxy_NoTrace  rest_proxy_FileTrace  rest_proxy_FileDBTrace  rest_proxy_DBTrace_Validazione rest_proxy_NoTrace_Validazione rest_proxy_DBTrace_RateLimiting"


function rest_proxy_DBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function rest_proxy_NoTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function rest_proxy_FileTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni solo su filesystem"
}



function rest_proxy_FileDBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni sia su database che su filesystem"
}



function rest_proxy_DBTrace_Validazione() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function rest_proxy_NoTrace_Validazione() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function rest_proxy_DBTrace_RateLimiting() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente"
}

