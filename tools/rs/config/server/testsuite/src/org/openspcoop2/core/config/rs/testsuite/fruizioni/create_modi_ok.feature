Feature: Creazione Fruizioni ModI

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore_soap = read('api_modi_soap.json')
* eval randomize(api_petstore_soap, ["nome"])
* eval api_petstore_soap.referente = soggettoDefault
* def query_param_profilo_modi = {'profilo': 'ModI'}

* def api_petstore_rest = read('api_modi_rest.json')
* eval randomize(api_petstore_rest, ["nome"])
* eval api_petstore_rest.referente = soggettoDefault

* def getExpectedSOAP =
"""
function(modi) {
var expected = modi;
expected.risposta.sicurezza_messaggio.certificate_chain = expected.risposta.sicurezza_messaggio.certificate_chain != null ? expected.risposta.sicurezza_messaggio.certificate_chain == 'true' : false
expected.risposta.sicurezza_messaggio.includi_signature_token = expected.risposta.sicurezza_messaggio.includi_signature_token != null ? expected.risposta.sicurezza_messaggio.includi_signature_token == 'true' : false
expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'binary-security-token'
expected.risposta.sicurezza_messaggio.time_to_live = expected.risposta.sicurezza_messaggio.time_to_live != null ? expected.risposta.sicurezza_messaggio.time_to_live : 300
expected.risposta.sicurezza_messaggio.forma_canonica_xml = expected.risposta.sicurezza_messaggio.forma_canonica_xml != null ? expected.risposta.sicurezza_messaggio.forma_canonica_xml : 'exclusive-canonical-xml-10'
expected.risposta.sicurezza_messaggio.algoritmo = expected.risposta.sicurezza_messaggio.algoritmo != null ? expected.risposta.sicurezza_messaggio.algoritmo : 'ECDSA-SHA-256'

return expected;
} 
"""

* def getExpectedRest =
"""
function(modi) {
var expected = modi;
expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.risposta.sicurezza_messaggio.time_to_live = expected.risposta.sicurezza_messaggio.time_to_live != null ? expected.risposta.sicurezza_messaggio.time_to_live: 300
expected.risposta.sicurezza_messaggio.algoritmo = expected.risposta.sicurezza_messaggio.algoritmo != null ? expected.risposta.sicurezza_messaggio.algoritmo: 'RS256'
expected.risposta.sicurezza_messaggio.certificate_chain = expected.risposta.sicurezza_messaggio.certificate_chain != null ? expected.risposta.sicurezza_messaggio.certificate_chain == 'true': false

return expected;
} 
"""

@CreateFruizione_204_modi_SOAP
Scenario Outline: Fruizioni Creazione 204 SOAP <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('<nome>')
		* eval fruizione_petstore.api_nome = api_petstore_soap.nome
		* eval fruizione_petstore.fruizione_nome = api_petstore_soap.nome
		* eval fruizione_petstore.api_versione = api_petstore_soap.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api_petstore_soap.referente
		
		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_soap.nome + '/' + api_petstore_soap.versione

    * call create ({ resourcePath: 'api', body: api_petstore_soap, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedSOAP(erogazione_petstore.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_soap.json|
#|erogazione_modi_soap_wsa.json|
#|erogazione_modi_soap_truststore_ridefinito.json|
#|erogazione_modi_soap_keystore_ridefinito_file.json|
#|erogazione_modi_soap_keystore_ridefinito_path.json|
#|erogazione_modi_soap_algoritmo_RSA-SHA-512.json|
#|erogazione_modi_soap_algoritmo_DSA-SHA-256.json|
#|erogazione_modi_soap_algoritmo_ECDSA-SHA-384.json|
#|erogazione_modi_soap_algoritmo_ECDSA-SHA-512.json|
#|erogazione_modi_soap_algoritmo_RSA-SHA-256.json|
#|erogazione_modi_soap_algoritmo_RSA-SHA-384.json|
#|erogazione_modi_soap_canonica_exc_xml10.json|
#|erogazione_modi_soap_canonica_xml10.json|
#|erogazione_modi_soap_canonica_xml11.json|
#|erogazione_modi_soap_certificate_chain.json|
#|erogazione_modi_soap_includi_signature_token.json|
#|erogazione_modi_soap_rif_x509_binary_security_token.json|
#|erogazione_modi_soap_rif_x509_issuer_serial.json|
#|erogazione_modi_soap_rif_x509_ski-key-identifier.json|
#|erogazione_modi_soap_rif_x509_thumbprint-key-identifier.json|
#|erogazione_modi_soap_rif_x509_x509-key-identifier.json|


@CreateFruizione_204_modi_REST
Scenario Outline: Fruizioni Creazione 204 REST <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('<nome>')
		* eval fruizione_petstore.api_nome = api_petstore_rest.nome
		* eval fruizione_petstore.fruizione_nome = api_petstore_rest.nome
		* eval fruizione_petstore.api_versione = api_petstore_rest.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api_petstore_rest.referente
		
		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest.nome + '/' + api_petstore_rest.versione

    * call create ({ resourcePath: 'api', body: api_petstore_rest, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedRest(fruizione_petstore.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_rest.json|
#|erogazione_modi_rest_audience.json|
#|erogazione_modi_rest_algoritmo_ES256.json|
#|erogazione_modi_rest_algoritmo_ES384.json|
#|erogazione_modi_rest_algoritmo_ES512.json|
#|erogazione_modi_rest_algoritmo_RS256.json|
#|erogazione_modi_rest_algoritmo_RS384.json|
#|erogazione_modi_rest_algoritmo_RS512.json|
#|erogazione_modi_rest_certificate_chain.json|
#|erogazione_modi_rest_header_firmare.json|
#|erogazione_modi_rest_header_rif_509_risp.json|
#|erogazione_modi_rest_keystore_ridefinito_file.json|
#|erogazione_modi_rest_keystore_ridefinito_path.json|
#|erogazione_modi_rest_rif_multipli.json|
#|erogazione_modi_rest_truststore_ridefinito.json|
#|erogazione_modi_rest_ttl.json|
