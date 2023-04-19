elencoTestTrasparenteOutSoap="out-soap_proxy_no-trace out-soap_proxy_db-trace 
			out-soap12_proxy_no-trace out-soap12_proxy_db-trace"

tests["out-soap_proxy_db-trace"]="out_soap_proxy_DBTrace"
tests["out-soap_proxy_no-trace"]="out_soap_proxy_NoTrace"
tests["out-soap12_proxy_db-trace"]="out_soap12_proxy_DBTrace"
tests["out-soap12_proxy_no-trace"]="out_soap12_proxy_NoTrace"

function out_soap_proxy_DBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function out_soap_proxy_NoTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function out_soap12_proxy_DBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=soap12
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=application/soap+xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function out_soap12_proxy_NoTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=soap12-azione2
	protocollo=api
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test2
	contentType=application/soap+xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}

