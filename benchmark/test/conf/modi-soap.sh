elencoTestModiSoap="soap_modi_DBTrace_Integrity_OnlyRequest  soap_modi_DBTrace_Integrity_RequestDigestInResponse  soap_modi_DBTrace_Integrity_OnlyRequest_NoDuplicates soap_modi_DBTrace_IdAuth_OnlyRequest  soap_modi_DBTrace_IdAuth_RequestDigestInResponse soap_modi_DBTrace_IdAuth_OnlyRequest_NoDuplicates"

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
	profiloSicurezza=digest
	protocollo=soap
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con integrità del payload anche nella risposta + digestRichiesta"
}


function soap_modi_DBTrace_Integrity_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=digest
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
	profiloSicurezza=auth
	protocollo=soap
	tipiTest=Proxy
	azione=test5
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth anche nella risposta"
}


function soap_modi_DBTrace_IdAuth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=auth
	protocollo=soap
	tipiTest=Proxy
	azione=test6
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con IdAuth nella sola richiesta + filtroDuplicati"
}
