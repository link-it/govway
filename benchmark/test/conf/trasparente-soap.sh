elencoTestTrasparenteSoap="soap_proxy_DBTrace  soap_proxy_NoTrace  soap_proxy_FileTrace  soap_proxy_FileDBTrace  soap_proxy_DBTrace_Validazione soap_proxy_NoTrace_Validazione soap_proxy_DBTrace_RateLimiting"

function soap_proxy_DBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function soap_proxy_NoTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function soapProxy_FileTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni solo su filesystem"
}


function soapProxy_FileDBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=api
	tipiTest=Proxy
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni sia su database che su filesystem"
}


function soap_proxy_DBTrace_Validazione() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function soap_proxy_NoTrace_Validazione() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function soap_proxy_DBTrace_RateLimiting() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente"
}

