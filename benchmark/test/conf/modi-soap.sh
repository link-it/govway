elencoTestModiSoap="soap_modi_db-trace_integrity_request-only soap_modi_db-trace_id-auth_request-only
			soap_modi_db-trace_integrity_request-digest-in-response soap_modi_db-trace_id-auth_request-digest-in-response
			soap_modi_db-trace_integrity_request-only_no-duplicates soap_modi_db-trace_id-auth_request-only_no-duplicates"

tests["soap_modi_db-trace_integrity_request-only"]=soap_modi_DBTrace_Integrity_OnlyRequest
tests["soap_modi_db-trace_integrity_request-digest-in-response"]=soap_modi_DBTrace_Integrity_RequestDigestInResponse
tests["soap_modi_db-trace_integrity_request-only_no-duplicates"]=soap_modi_DBTrace_Integrity_OnlyRequest_NoDuplicates
tests["soap_modi_db-trace_id-auth_request-only"]=soap_modi_DBTrace_IdAuth_OnlyRequest
tests["soap_modi_db-trace_id-auth_request-digest-in-response"]=soap_modi_DBTrace_IdAuth_RequestDigestInResponse
tests["soap_modi_db-trace_id-auth_request-only_no-duplicates"]=soap_modi_DBTrace_IdAuth_OnlyRequest_NoDuplicates

function soap_modi_DBTrace_Integrity_OnlyRequest() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=digest
	protocollo=soap
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload nella sola richiesta"
}


function soap_modi_DBTrace_Integrity_RequestDigestInResponse() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=digest-response
	protocollo=soap
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload anche nella risposta + digestRichiesta"
}


function soap_modi_DBTrace_Integrity_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=digest-no-duplicates
	protocollo=soap
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload nella sola richiesta + filtroDuplicati"
}


function soap_modi_DBTrace_IdAuth_OnlyRequest() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=auth
	protocollo=soap
	tipiTest=Proxy
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth nella sola richiesta"
}


function soap_modi_DBTrace_IdAuth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=auth-response
	protocollo=soap
	tipiTest=Proxy
	azione=test5
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth anche nella risposta"
}


function soap_modi_DBTrace_IdAuth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=auth-no-duplicates
	protocollo=soap
	tipiTest=Proxy
	azione=test6
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth nella sola richiesta + filtroDuplicati"
}
