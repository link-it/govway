elencoTestModiOutRest="out-rest_modi_db-trace_integrity_agid-auth_request-only 
                        out-rest_modi_db-trace_integrity_oauth2-auth_request-only out-rest_modi_db-trace_integrity02_oauth2-auth_request-only
			out-rest_modi_db-trace_integrity_agid-auth_request-digest-in-response 
                        out-rest_modi_db-trace_integrity_oauth2-auth_request-digest-in-response out-rest_modi_db-trace_integrity02_oauth2-auth_request-digest-in-response
			out-rest_modi_db-trace_audit_agid-auth_request-only out-rest_modi_db-trace_audit_oauth2-auth_request-only"

tests["out-rest_modi_db-trace_integrity_agid-auth_request-only"]=out_rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest
tests["out-rest_modi_db-trace_integrity_agid-auth_request-digest-in-response"]=out_rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse
tests["out-rest_modi_db-trace_integrity_oauth2-auth_request-only"]=out_rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest
tests["out-rest_modi_db-trace_integrity02_oauth2-auth_request-only"]=out_rest_modi_DBTrace_Integrity02_OAuth2Auth_OnlyRequest
tests["out-rest_modi_db-trace_integrity_oauth2-auth_request-digest-in-response"]=out_rest_modi_DBTrace_Integrity_OAuth2Auth_RequestDigestInResponse
tests["out-rest_modi_db-trace_integrity02_oauth2-auth_request-digest-in-response"]=out_rest_modi_DBTrace_Integrity02_OAuth2Auth_RequestDigestInResponse
tests["out-rest_modi_db-trace_audit_agid-auth_request-only"]=out_rest_modi_DBTrace_Audit_AgidAuth_OnlyRequest
tests["out-rest_modi_db-trace_audit_oauth2-auth_request-only"]=out_rest_modi_DBTrace_Audit_OAuth2Auth_OnlyRequest


function out_rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta"
}


function out_rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test2
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con dobbio header anche nella risposta + digestRichiesta"
}


function out_rest_modi_DBTrace_Integrity_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test4
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_01 e header OAuth nella sola richiesta"
}

function out_rest_modi_DBTrace_Integrity02_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test7
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_02 e header OAuth nella sola richiesta"
}


function out_rest_modi_DBTrace_Integrity_OAuth2Auth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test5
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_01 e header OAuth anche nella risposta + digestRichiesta"
}

function out_rest_modi_DBTrace_Integrity02_OAuth2Auth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test8
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_02 e header OAuth anche nella risposta + digestRichiesta"
}

function out_rest_modi_DBTrace_Audit_AgidAuth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test9
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta con in più il token di audit"
}

function out_rest_modi_DBTrace_Audit_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=none
	profiloMessaggi=none
	protocollo=rest
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test10
	contentType=application/json
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid con pattern Integrity_REST_02 e header OAuth nella sola richiesta con in più il token di audit"
}
