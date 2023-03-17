elencoTestModiOutSoap="out-soap_modi_db-trace_integrity_request-only out-soap_modi_db-trace_id-auth_request-only
			out-soap_modi_db-trace_integrity_request-digest-in-response out-soap_modi_db-trace_id-auth_request-digest-in-response"

tests["out-soap_modi_db-trace_integrity_request-only"]=out_soap_modi_DBTrace_Integrity_OnlyRequest
tests["out-soap_modi_db-trace_integrity_request-digest-in-response"]=out_soap_modi_DBTrace_Integrity_RequestDigestInResponse
tests["out-soap_modi_db-trace_id-auth_request-only"]=out_soap_modi_DBTrace_IdAuth_OnlyRequest
tests["out-soap_modi_db-trace_id-auth_request-digest-in-response"]=out_soap_modi_DBTrace_IdAuth_RequestDigestInResponse

function out_soap_modi_DBTrace_Integrity_OnlyRequest() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=none
	protocollo=soap
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload nella sola richiesta"
}


function out_soap_modi_DBTrace_Integrity_RequestDigestInResponse() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione2
	protocollo=soap
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload anche nella risposta + digestRichiesta"
}


function out_soap_modi_DBTrace_IdAuth_OnlyRequest() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione4
	protocollo=soap
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test4
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth nella sola richiesta"
}


function out_soap_modi_DBTrace_IdAuth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione5
	protocollo=soap
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test5
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth anche nella risposta"
}

