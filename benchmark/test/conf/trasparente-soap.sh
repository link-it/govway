elencoTestTrasparenteSoap="soap_proxy_no-trace soap_proxy_db-trace 
			soap12_proxy_no-trace soap12_proxy_db-trace 
			soap_proxy_file-trace soap_proxy_file-db-trace 
			soap_proxy_no-trace_validation soap_proxy_db-trace_validation
			soap_proxy_no-trace_rate-limiting soap_proxy_db-trace_rate-limiting
			soap_proxy_no-trace_rate-limiting_groupby-requester soap_proxy_db-trace_rate-limiting_groupby-requester
			soap_proxy_no-trace_rate-limiting_too-many-requests soap_proxy_db-trace_rate-limiting_too-many-requests soap_proxy_db-partial-trace_rate-limiting_too-many-requests
			soap_proxy_db-trace_rate-limiting_quota-divisa-nodi soap_proxy_db-trace_rate-limiting_redis-atomic-long
			soap_proxy_db-trace_rate-limiting_hazelcast-atomic-long soap_proxy_db-trace_rate-limiting_hazelcast-pn-counters
			soap_proxy_db-trace_rate-limiting_hazelcast-map soap_proxy_db-trace_rate-limiting_hazelcast-near-cache soap_proxy_db-trace_rate-limiting_hazelcast-local-cache
			soap_proxy_db-trace_trasformazioni_freemarker soap_proxy_db-trace_trasformazioni_velocity"

tests["soap_proxy_db-trace"]="soap_proxy_DBTrace"
tests["soap_proxy_no-trace"]="soap_proxy_NoTrace"
tests["soap12_proxy_db-trace"]="soap12_proxy_DBTrace"
tests["soap12_proxy_no-trace"]="soap12_proxy_NoTrace"
tests["soap_proxy_file-trace"]="soap_proxy_FileTrace"
tests["soap_proxy_file-db-trace"]="soap_proxy_FileDBTrace"
tests["soap_proxy_db-trace_validation"]="soap_proxy_DBTrace_Validazione"
tests["soap_proxy_no-trace_validation"]="soap_proxy_NoTrace_Validazione"
tests["soap_proxy_db-trace_rate-limiting"]="soap_proxy_DBTrace_RateLimiting"
tests["soap_proxy_no-trace_rate-limiting"]="soap_proxy_NoTrace_RateLimiting"
tests["soap_proxy_db-trace_rate-limiting_groupby-requester"]="soap_proxy_DBTrace_RateLimiting_GroupByRequester"
tests["soap_proxy_no-trace_rate-limiting_groupby-requester"]="soap_proxy_NoTrace_RateLimiting_GroupByRequester"
tests["soap_proxy_no-trace_rate-limiting_too-many-requests"]="soap_proxy_NoTrace_RateLimiting_TooManyRequests"
tests["soap_proxy_db-trace_rate-limiting_too-many-requests"]="soap_proxy_DBTrace_RateLimiting_TooManyRequests"
tests["soap_proxy_db-partial-trace_rate-limiting_too-many-requests"]="soap_proxy_DBPartialTrace_RateLimiting_TooManyRequests"
tests["soap_proxy_db-trace_rate-limiting_quota-divisa-nodi"]="soap_proxy_DBTrace_RateLimiting_QuotaDivisaNodi"
tests["soap_proxy_db-trace_rate-limiting_redis-atomic-long"]="soap_proxy_DBTrace_RateLimiting_RedisAtomicLong"
tests["soap_proxy_db-trace_rate-limiting_hazelcast-atomic-long"]="soap_proxy_DBTrace_RateLimiting_HazelcastAtomicLong"
tests["soap_proxy_db-trace_rate-limiting_hazelcast-pn-counters"]="soap_proxy_DBTrace_RateLimiting_HazelcastPNCounters"
tests["soap_proxy_db-trace_rate-limiting_hazelcast-map"]="soap_proxy_DBTrace_RateLimiting_HazelcastMap"
tests["soap_proxy_db-trace_rate-limiting_hazelcast-near-cache"]="soap_proxy_DBTrace_RateLimiting_HazelcastNearCache"
tests["soap_proxy_db-trace_rate-limiting_hazelcast-local-cache"]="soap_proxy_DBTrace_RateLimiting_HazelcastLocalCache"
tests["soap_proxy_db-trace_trasformazioni_freemarker"]="soap_proxy_DBTrace_Trasformazioni_Freemarker"
tests["soap_proxy_db-trace_trasformazioni_velocity"]="soap_proxy_DBTrace_Trasformazioni_Velocity"

function soap_proxy_DBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Proxy
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function soap_proxy_NoTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=Proxy
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function soap12_proxy_DBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=soap12
	protocollo=api
	tipiTest=Proxy
	azione=test
	contentType=application/soap+xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function soap12_proxy_NoTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=soap12-azione2
	protocollo=api
	tipiTest=Proxy
	azione=test2
	contentType=application/soap+xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function soap_proxy_FileTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=Proxy
	azione=test3
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni solo su filesystem"
}


function soap_proxy_FileDBTrace() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=api
	tipiTest=Proxy
	azione=test4
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni sia su database che su filesystem"
}


function soap_proxy_DBTrace_Validazione() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Validazione
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni"
}


function soap_proxy_NoTrace_Validazione() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=Validazione
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Non vengono registrate le transazioni"
}


function soap_proxy_DBTrace_RateLimiting() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimiting
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente"
}


function soap_proxy_NoTrace_RateLimiting() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=RateLimiting
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente senza tracciamento"
}


function soap_proxy_DBTrace_RateLimiting_GroupByRequester() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione7
	protocollo=api
	tipiTest=RateLimiting
	azione=test7
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente, con richiedenti unici"
}


function soap_proxy_NoTrace_RateLimiting_GroupByRequester() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione6
	protocollo=api
	tipiTest=RateLimiting
	azione=test6
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente, con richiedenti unici, senza tracciamento"
}


function soap_proxy_NoTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione5
	protocollo=api
	tipiTest=RateLimiting
	azione=test5
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy senza tracciamento"
}


function soap_proxy_DBTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=RateLimiting
	azione=test3
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy"
}


function soap_proxy_DBPartialTrace_RateLimiting_TooManyRequests() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=api
	tipiTest=RateLimiting
	azione=test4
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente con superamento della policy, registrazione transazioni parziale."
}

function soap_proxy_DBTrace_RateLimiting_QuotaDivisaNodi() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (quota divisa sui nodi)"
}

function soap_proxy_DBTrace_RateLimiting_RedisAtomicLong() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione7
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test7
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (redis atomic long)"
}

function soap_proxy_DBTrace_RateLimiting_HazelcastAtomicLong() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione5
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test5
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast atomic long)"
}

function soap_proxy_DBTrace_RateLimiting_HazelcastPNCounters() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione6
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test6
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast pn counters)"
}

function soap_proxy_DBTrace_RateLimiting_HazelcastMap() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast map)"
}

function soap_proxy_DBTrace_RateLimiting_HazelcastNearCache() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test3
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast near cache)"
}

function soap_proxy_DBTrace_RateLimiting_HazelcastLocalCache() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=api
	tipiTest=RateLimitingDistribuito
	azione=test4
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Test policy rate limiting complessiva e per richiedente (hazelcast local cache)"
}


function soap_proxy_DBTrace_Trasformazioni_Freemarker() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=api
	tipiTest=Trasformazioni
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni; viene attuata una trasformazione freemarker"
}

function soap_proxy_DBTrace_Trasformazioni_Velocity() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=api
	tipiTest=Trasformazioni
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Vengono registrate le transazioni; viene attuata una trasformazione velocity"
}
