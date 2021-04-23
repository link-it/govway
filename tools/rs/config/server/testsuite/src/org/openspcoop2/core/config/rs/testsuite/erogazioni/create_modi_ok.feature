Feature: Creazione Erogazioni ModI

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
expected.risposta.sicurezza_messaggio.certificate_chain = expected.risposta.sicurezza_messaggio.certificate_chain != null ? expected.risposta.sicurezza_messaggio.certificate_chain: false

return expected;
} 
"""

@CreatePetstore204_modi_SOAP
Scenario Outline: Erogazioni Creazione Petstore 204 SOAP <nome>

		* def erogazione_petstore = read('<nome>')
		* eval erogazione_petstore.api_nome = api_petstore_soap.nome
		* eval erogazione_petstore.api_versione = api_petstore_soap.versione
		
		* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_soap.nome + '/' + api_petstore_soap.versione

    * call create ({ resourcePath: 'api', body: api_petstore_soap, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedSOAP(erogazione_petstore.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_soap.json|
|erogazione_modi_soap_wsa.json|

@CreatePetstore204_modi_REST
Scenario Outline: Erogazioni Creazione Petstore 204 REST <nome>

		* def erogazione_petstore = read('<nome>')
		* eval erogazione_petstore.erogazione_nome = api_petstore_rest.nome
		* eval erogazione_petstore.api_nome = api_petstore_rest.nome
		* eval erogazione_petstore.api_versione = api_petstore_rest.versione
		
		* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest.nome + '/' + api_petstore_rest.versione

    * call create ({ resourcePath: 'api', body: api_petstore_rest, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedRest(erogazione_petstore.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest.json|
|erogazione_modi_rest_audience.json|