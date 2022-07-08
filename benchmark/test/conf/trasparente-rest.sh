elencoTestTrasparenteRest="rest_proxy_no-trace rest_proxy_db-trace rest_proxy_file-trace rest_proxy_file-db-trace 
			rest_proxy_no-trace_validation rest_proxy_db-trace_validation
			rest_proxy_no-trace_rate-limiting rest_proxy_db-trace_rate-limiting
			rest_proxy_no-trace_rate-limiting_groupby-requester rest_proxy_db-trace_rate-limiting_groupby-requester
			rest_proxy_no-trace_rate-limiting_too-many-requests rest_proxy_db-trace_rate-limiting_too-many-requests rest_proxy_db-partial-trace_rate-limiting_too-many-requests
			rest_proxy_db-trace_rate-limiting_quota-divisa-nodi rest_proxy_db-trace_rate-limiting_redis-atomic-long
			rest_proxy_db-trace_rate-limiting_hazelcast-atomic-long rest_proxy_db-trace_rate-limiting_hazelcast-pn-counters
			rest_proxy_db-trace_rate-limiting_hazelcast-map rest_proxy_db-trace_rate-limiting_hazelcast-near-cache rest_proxy_db-trace_rate-limiting_hazelcast-local-cache
			rest_proxy_no-trace_multipart rest_proxy_db-trace_multipart rest_proxy_no-trace_multipart_validation rest_proxy_db-trace_multipart_validation
			rest_proxy_db-trace_trasformazioni_freemarker rest_proxy_db-trace_trasformazioni_velocity"

tests["rest_proxy_db-trace"]="rest_proxy_DBTrace"
tests["rest_proxy_no-trace"]="rest_proxy_NoTrace"
tests["rest_proxy_file-trace"]="rest_proxy_FileTrace"
tests["rest_proxy_file-db-trace"]="rest_proxy_FileDBTrace"
tests["rest_proxy_db-trace_validation"]="rest_proxy_DBTrace_Validazione"
tests["rest_proxy_no-trace_validation"]="rest_proxy_NoTrace_Validazione"
tests["rest_proxy_db-trace_rate-limiting"]="rest_proxy_DBTrace_RateLimiting"
tests["rest_proxy_no-trace_rate-limiting"]="rest_proxy_NoTrace_RateLimiting"
tests["rest_proxy_db-trace_rate-limiting_groupby-requester"]="rest_proxy_DBTrace_RateLimiting_GroupByRequester"
tests["rest_proxy_no-trace_rate-limiting_groupby-requester"]="rest_proxy_NoTrace_RateLimiting_GroupByRequester"
tests["rest_proxy_no-trace_rate-limiting_too-many-requests"]="rest_proxy_NoTrace_RateLimiting_TooManyRequests"
tests["rest_proxy_db-trace_rate-limiting_too-many-requests"]="rest_proxy_DBTrace_RateLimiting_TooManyRequests"
tests["rest_proxy_db-partial-trace_rate-limiting_too-many-requests"]="rest_proxy_DBPartialTrace_RateLimiting_TooManyRequests"
tests["rest_proxy_db-trace_rate-limiting_quota-divisa-nodi"]="rest_proxy_DBTrace_RateLimiting_QuotaDivisaNodi"
tests["rest_proxy_db-trace_rate-limiting_redis-atomic-long"]="rest_proxy_DBTrace_RateLimiting_RedisAtomicLong"
tests["rest_proxy_db-trace_rate-limiting_hazelcast-atomic-long"]="rest_proxy_DBTrace_RateLimiting_HazelcastAtomicLong"
tests["rest_proxy_db-trace_rate-limiting_hazelcast-pn-counters"]="rest_proxy_DBTrace_RateLimiting_HazelcastPNCounters"
tests["rest_proxy_db-trace_rate-limiting_hazelcast-map"]="rest_proxy_DBTrace_RateLimiting_HazelcastMap"
tests["rest_proxy_db-trace_rate-limiting_hazelcast-near-cache"]="rest_proxy_DBTrace_RateLimiting_HazelcastNearCache"
tests["rest_proxy_db-trace_rate-limiting_hazelcast-local-cache"]="rest_proxy_DBTrace_RateLimiting_HazelcastLocalCache"
tests["rest_proxy_db-trace_multipart"]="rest_proxy_DBTrace_Multipart"
tests["rest_proxy_no-trace_multipart"]="rest_proxy_NoTrace_Multipart"
tests["rest_proxy_db-trace_multipart_validation"]="rest_proxy_DBTrace_Multipart_Validazione"
tests["rest_proxy_no-trace_multipart_validation"]="rest_proxy_NoTrace_Multipart_Validazione"
tests["rest_proxy_db-trace_trasformazioni_freemarker"]="rest_proxy_DBTrace_Trasformazioni_Freemarker"
tests["rest_proxy_db-trace_trasformazioni_velocity"]="rest_proxy_DBTrace_Trasformazioni_Velocity"

function rest_proxy_DBTrace() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
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
	profiloMessaggi=none
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
	profiloMessaggi=none
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
	profiloMessaggi=none
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
	profiloMessaggi=none
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
	profiloMessaggi=none
	protocollo=api
	tipiTest=Validazione
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function rest_proxy_DBTrace_RateLimiting() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
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
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente senza tracciamento"
}


function rest_proxy_DBTrace_RateLimiting_GroupByRequester() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test7
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente, con richiedenti unici"
}


function rest_proxy_NoTrace_RateLimiting_GroupByRequester() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test6
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente, con richiedenti unici, senza tracciamento"
}


function rest_proxy_NoTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
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
	profiloMessaggi=none
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
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento policy e tracciamento parziale"
}


function rest_proxy_DBTrace_RateLimiting_QuotaDivisaNodi() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (quota divisa sui nodi)"
}

function rest_proxy_DBTrace_RateLimiting_RedisAtomicLong() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test7
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (redis atomic long)"
}

function rest_proxy_DBTrace_RateLimiting_HazelcastAtomicLong() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test5
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast atomic long)"
}

function rest_proxy_DBTrace_RateLimiting_HazelcastPNCounters() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test6
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast pn counters)"
}

function rest_proxy_DBTrace_RateLimiting_HazelcastMap() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast map)"
}

function rest_proxy_DBTrace_RateLimiting_HazelcastNearCache() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test3
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast near cache)"
}

function rest_proxy_DBTrace_RateLimiting_HazelcastLocalCache() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast local cache)"
}


function rest_proxy_DBTrace_Multipart() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=multipart
	protocollo=api
	tipiTest=RequestMultipart
	azione=test
	contentType='multipart/form-data; boundary=\"----=_Part_0_1037475674.1651780088034\"'
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function rest_proxy_NoTrace_Multipart() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=multipart
	protocollo=api
	tipiTest=RequestMultipart
	azione=test2
	contentType='multipart/form-data; boundary=\"----=_Part_0_1037475674.1651780088034\"'
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function rest_proxy_DBTrace_Multipart_Validazione() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=multipart
	protocollo=api
	tipiTest=RequestMultipart
	azione=test3
	contentType='multipart/form-data; boundary=\"----=_Part_0_1037475674.1651780088034\"'
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni; viene attuata la validazione dei contenuti"
}


function rest_proxy_NoTrace_Multipart_Validazione() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=multipart
	protocollo=api
	tipiTest=RequestMultipart
	azione=test4
	contentType='multipart/form-data; boundary=\"----=_Part_0_1037475674.1651780088034\"'
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni; viene attuata la validazione dei contenuti"
}

function rest_proxy_DBTrace_Trasformazioni_Freemarker() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=Trasformazioni
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni; viene attuata una trasformazione freemarker"
}

function rest_proxy_DBTrace_Trasformazioni_Velocity() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=api
	tipiTest=Trasformazioni
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni; viene attuata una trasformazione velocity"
}

