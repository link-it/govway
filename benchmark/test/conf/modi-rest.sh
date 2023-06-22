elencoTestModiRest="rest_modi_db-trace_integrity_agid-auth_request-only 
			rest_modi_db-trace_integrity_oauth2-auth_request-only rest_modi_db-trace_integrity02_oauth2-auth_request-only
			rest_modi_db-trace_integrity_agid-auth_request-digest-in-response 
			rest_modi_db-trace_integrity_oauth2-auth_request-digest-in-response rest_modi_db-trace_integrity02_oauth2-auth_request-digest-in-response
			rest_modi_db-trace_integrity_agid-auth_request-only_no-duplicates 
			rest_modi_db-trace_integrity_oauth2-auth_request-only_no-duplicates"

tests["rest_modi_db-trace_integrity_agid-auth_request-only"]=rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest
tests["rest_modi_db-trace_integrity_agid-auth_request-digest-in-response"]=rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse
tests["rest_modi_db-trace_integrity_agid-auth_request-only_no-duplicates"]=rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest_NoDuplicates
tests["rest_modi_db-trace_integrity_oauth2-auth_request-only"]=rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest
tests["rest_modi_db-trace_integrity02_oauth2-auth_request-only"]=rest_modi_DBTrace_Integrity02_OAuth2Auth_OnlyRequest
tests["rest_modi_db-trace_integrity_oauth2-auth_request-digest-in-response"]=rest_modi_DBTrace_Integrity_OAuth2Auth_RequestDigestInResponse
tests["rest_modi_db-trace_integrity02_oauth2-auth_request-digest-in-response"]=rest_modi_DBTrace_Integrity02_OAuth2Auth_RequestDigestInResponse
tests["rest_modi_db-trace_integrity_oauth2-auth_request-only_no-duplicates"]=rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest_NoDuplicates


function rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta"
}


function rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con dobbio header anche nella risposta + digestRichiesta"
}


function rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test3
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta + filtroDuplicati"
}


function rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_01 e header OAuth nella sola richiesta"
}

function rest_modi_DBTrace_Integrity02_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digestKid
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test7
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_02 e header OAuth nella sola richiesta"
}


function rest_modi_DBTrace_Integrity_OAuth2Auth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test5
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_01 e header OAuth anche nella risposta + digestRichiesta"
}

function rest_modi_DBTrace_Integrity02_OAuth2Auth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digestKid
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test8
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_02 e header OAuth anche nella risposta + digestRichiesta"
}

function rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	profiloMessaggi=none
	protocollo=rest
	tipiTest=Proxy
	azione=test6
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_01 e header OAuth nella sola richiesta + filtroDuplicati"
}
