elencoTestSPCoopSoap="soap_spcoop soap_spcoop_no-duplicates"

tests["soap_spcoop"]=soap_spcoop
tests["soap_spcoop_no-duplicates"]=soap_spcoop_NoDuplicates

function soap_spcoop() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=spcoop
	protocollo=spcoop
	bound=in
	tipiTest=Proxy
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Profilo SPCoop"
}


function soap_spcoop_NoDuplicates() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=spcoopFiltroDuplicati
	protocollo=spcoop
	bound=in
	tipiTest=Proxy
	azione=test2
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Profilo SPCoop con filtro duplicati"
}

