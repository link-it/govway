elencoTestTrasparenteRest="rest_proxy_no-trace rest_proxy_db-trace rest_proxy_file-trace rest_proxy_file-db-trace 
			rest_proxy_no-trace_validation rest_proxy_db-trace_validation
			rest_proxy_no-trace_rate-limiting rest_proxy_db-trace_rate-limiting
			rest_proxy_no-trace_rate-limiting_too-many-requests rest_proxy_db-trace_rate-limiting_too-many-requests rest_proxy_db-partial-trace_rate-limiting_too-many-requests"

tests["rest_proxy_db-trace"]="rest_proxy_DBTrace"
tests["rest_proxy_no-trace"]="rest_proxy_NoTrace"
tests["rest_proxy_file-trace"]="rest_proxy_FileTrace"
tests["rest_proxy_file-db-trace"]="rest_proxy_FileDBTrace"
tests["rest_proxy_db-trace_validation"]="rest_proxy_DBTrace_Validazione"
tests["rest_proxy_no-trace_validation"]="rest_proxy_NoTrace_Validazione"
tests["rest_proxy_db-trace_rate-limiting"]="rest_proxy_DBTrace_RateLimiting"
tests["rest_proxy_no-trace_rate-limiting"]="rest_proxy_NoTrace_RateLimiting"
tests["rest_proxy_no-trace_rate-limiting_too-many-requests"]="rest_proxy_NoTrace_RateLimiting_TooManyRequests"
tests["rest_proxy_db-trace_rate-limiting_too-many-requests"]="rest_proxy_DBTrace_RateLimiting_TooManyRequests"
tests["rest_proxy_db-partial-trace_rate-limiting_too-many-requests"]="rest_proxy_DBPartialTrace_RateLimiting_TooManyRequests"

function rest_proxy_DBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function rest_proxy_NoTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function rest_proxy_FileTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test3
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni solo su filesystem"
}



function rest_proxy_FileDBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni sia su database che su filesystem"
}



function rest_proxy_DBTrace_Validazione() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test
	contentType=application/json
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
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente"
}


function rest_proxy_NoTrace_RateLimiting() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente senza tracciamento"
}


function rest_proxy_NoTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test5
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento policy senza tracciamento"
}


function rest_proxy_DBTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test3
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento policy"
}


function rest_proxy_DBPartialTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento policy e tracciamento parziale"
}
