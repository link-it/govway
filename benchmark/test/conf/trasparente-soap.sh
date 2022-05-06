elencoTestTrasparenteSoap="soap_proxy_no-trace soap_proxy_db-trace soap_proxy_file-trace soap_proxy_file-db-trace 
			soap_proxy_no-trace_validation soap_proxy_db-trace_validation
			soap_proxy_no-trace_rate-limiting soap_proxy_db-trace_rate-limiting
			soap_proxy_no-trace_rate-limiting_too-many-requests soap_proxy_db-trace_rate-limiting_too-many-requests soap_proxy_db-partial-trace_rate-limiting_too-many-requests"

tests["soap_proxy_db-trace"]="soap_proxy_DBTrace"
tests["soap_proxy_no-trace"]="soap_proxy_NoTrace"
tests["soap_proxy_file-trace"]="soap_proxy_FileTrace"
tests["soap_proxy_file-db-trace"]="soap_proxy_FileDBTrace"
tests["soap_proxy_db-trace_validation"]="soap_proxy_DBTrace_Validazione"
tests["soap_proxy_no-trace_validation"]="soap_proxy_NoTrace_Validazione"
tests["soap_proxy_db-trace_rate-limiting"]="soap_proxy_DBTrace_RateLimiting"
tests["soap_proxy_no-trace_rate-limiting"]="soap_proxy_NoTrace_RateLimiting"
tests["soap_proxy_no-trace_rate-limiting_too-many-requests"]="soap_proxy_NoTrace_RateLimiting_TooManyRequests"
tests["soap_proxy_db-trace_rate-limiting_too-many-requests"]="soap_proxy_DBTrace_RateLimiting_TooManyRequests"
tests["soap_proxy_db-partial-trace_rate-limiting_too-many-requests"]="soap_proxy_DBPartialTrace_RateLimiting_TooManyRequests"

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


function soap_proxy_FileTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni solo su filesystem"
}


function soap_proxy_FileDBTrace() {
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
	profiloSicurezza=azione2
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


function soap_proxy_NoTrace_RateLimiting() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=RateLimiting
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente senza tracciamento"
}


function soap_proxy_NoTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione5
	protocollo=api
	tipiTest=RateLimiting
	azione=test5
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy senza tracciamento"
}


function soap_proxy_DBTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=RateLimiting
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy"
}


function soap_proxy_DBPartialTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=api
	tipiTest=RateLimiting
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy, registrazione transazioni parziale."
}
