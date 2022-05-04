elencoTestModiRest="rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest_NoDuplicates rest_modi_DBTrace_Integriy_OAuth2Auth_OnlyRequest  rest_modi_DBTrace_Integriy_OAuth2Auth_RequestDigestInResponse  rest_modi_DBTrace_Integriy_OAuth2Auth_OnlyRequest_NoDuplicates"


function rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta"
}


function rest_modi_DBTrace_Integrity_AgidAuth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test2
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con dobbio header anche nella risposta + digestRichiesta"
}


function rest_modi_DBTrace_Integrity_AgidAuth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test3
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con doppio header nella sola richiesta + filtroDuplicati"
}


function rest_modi_DBTrace_Integriy_OAuth2Auth_OnlyRequest() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test4
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid e header OAuth nella sola richiesta"
}


function rest_modi_DBTrace_Integriy_OAuth2Auth_RequestDigestInResponse() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test5
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid e header OAuth anche nella risposta + digestRichiesta"
}


function rest_modi_DBTrace_Integriy_OAuth2Auth_OnlyRequest_NoDuplicates() {
	jmeterTestFile=${jmeterRestTestFile}
	profiloSicurezza=digest
	protocollo=rest
	tipiTest=Proxy
	azione=test6
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="LineeGuida con header Agid e header OAuth nella sola richiesta + filtroDuplicati"
}
