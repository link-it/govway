elencoTestSPCoopOutSoap="out-soap_spcoop"

tests["out-soap_spcoop"]=out_soap_spcoop

function out_soap_spcoop() {
	jmeterTestFile=${jmeterSoapTestFile}
	profiloSicurezza=azione3
	protocollo=spcoop
	bound=out/ENTE
	tipiTest=ProxyFruitore
	azione=test
	contentType=text/xml; charset=UTF-8
	outputDir=${resultDir}/${FUNCNAME[0]}
	description="Profilo SPCoop con profilo OneWay"
}
