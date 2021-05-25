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

expected.richiesta.sicurezza_messaggio.certificate_chain = expected.richiesta.sicurezza_messaggio.certificate_chain != null ? expected.richiesta.sicurezza_messaggio.certificate_chain == 'true' : false
expected.richiesta.sicurezza_messaggio.includi_signature_token = expected.richiesta.sicurezza_messaggio.includi_signature_token != null ? expected.richiesta.sicurezza_messaggio.includi_signature_token == 'true' : false
expected.richiesta.sicurezza_messaggio.riferimento_x509 = expected.richiesta.sicurezza_messaggio.riferimento_x509 != null ? expected.richiesta.sicurezza_messaggio.riferimento_x509 : 'binary-security-token'
expected.richiesta.sicurezza_messaggio.time_to_live = expected.richiesta.sicurezza_messaggio.time_to_live != null ? expected.richiesta.sicurezza_messaggio.time_to_live : 300
expected.richiesta.sicurezza_messaggio.forma_canonica_xml = expected.richiesta.sicurezza_messaggio.forma_canonica_xml != null ? expected.richiesta.sicurezza_messaggio.forma_canonica_xml : 'exclusive-canonical-xml-10'
expected.richiesta.sicurezza_messaggio.algoritmo = expected.richiesta.sicurezza_messaggio.algoritmo != null ? expected.richiesta.sicurezza_messaggio.algoritmo : 'ECDSA-SHA-256'

expected.risposta.sicurezza_messaggio.verifica_wsa_to = expected.risposta.sicurezza_messaggio.verifica_wsa_to != null ? expected.risposta.sicurezza_messaggio.verifica_wsa_to == 'true' : true

return expected;
} 
"""

* def getExpectedRest =
"""
function(modi) {
var expected = modi;

expected.richiesta.sicurezza_messaggio.riferimento_x509 = expected.richiesta.sicurezza_messaggio.riferimento_x509 != null ? expected.richiesta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.richiesta.sicurezza_messaggio.time_to_live = expected.richiesta.sicurezza_messaggio.time_to_live != null ? expected.richiesta.sicurezza_messaggio.time_to_live: 300
expected.richiesta.sicurezza_messaggio.algoritmo = expected.richiesta.sicurezza_messaggio.algoritmo != null ? expected.richiesta.sicurezza_messaggio.algoritmo: 'RS256'
expected.richiesta.sicurezza_messaggio.certificate_chain = expected.richiesta.sicurezza_messaggio.certificate_chain != null ? expected.richiesta.sicurezza_messaggio.certificate_chain == 'true': false

expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.risposta.sicurezza_messaggio.verifica_audience = expected.risposta.sicurezza_messaggio.verifica_audience != null ? expected.risposta.sicurezza_messaggio.verifica_audience == 'true' : true

return expected;
} 
"""

@UpdatePetstore_modi_SOAP_fruizioni
Scenario Outline: Fruizioni Aggiornamento Petstore SOAP <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('fruizione_modi_soap.json')
		* eval fruizione_petstore.api_nome = api_petstore_soap.nome
		* eval fruizione_petstore.fruizione_nome = api_petstore_soap.nome
		* eval fruizione_petstore.api_versione = api_petstore_soap.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api_petstore_soap.referente
		
		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_soap.nome + '/' + api_petstore_soap.versione

		* def fruizione_petstore_update = read('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore_soap, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call put ( { resourcePath: 'fruizioni/'+petstore_key+'/modi', body: {modi: fruizione_petstore_update.modi}, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedSOAP(fruizione_petstore_update.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|fruizione_modi_soap_algoritmo_DSA-SHA-256.json|
|fruizione_modi_soap_algoritmo_ECDSA-SHA-256.json|
|fruizione_modi_soap_algoritmo_ECDSA-SHA-384.json|
|fruizione_modi_soap_algoritmo_ECDSA-SHA-512.json|
|fruizione_modi_soap_algoritmo_RSA-SHA-256.json|
|fruizione_modi_soap_algoritmo_RSA-SHA-384.json|
|fruizione_modi_soap_certificate_chain.json|
|fruizione_modi_soap_forma_canonica_xcxml10.json|
|fruizione_modi_soap_forma_canonica_xml10.json|
|fruizione_modi_soap_forma_canonica_xml11.json|
|fruizione_modi_soap_rif_x509_bst.json|
|fruizione_modi_soap_rif_x509_isstk.json|
|fruizione_modi_soap_rif_x509_ski.json|
|fruizione_modi_soap_rif_x509_tki.json|
|fruizione_modi_soap_rif_x509_xki.json|
|fruizione_modi_soap_signature_token.json|
|fruizione_modi_soap_ttl.json|
|fruizione_modi_soap_wsato.json|
|fruizione_modi_soap_iu_codice_ente.json|
|fruizione_modi_soap_iu_userid.json|
|fruizione_modi_soap_iu_indirizzo_ip.json|
|fruizione_modi_soap_risposta_truststore_ridefinito.json|

@UpdatePetstore_modi_REST_fruizioni
Scenario Outline: Fruizioni Aggiornamento Petstore REST <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('fruizione_modi_rest.json')
		* eval fruizione_petstore.api_nome = api_petstore_rest.nome
		* eval fruizione_petstore.fruizione_nome = api_petstore_rest.nome
		* eval fruizione_petstore.api_versione = api_petstore_rest.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api_petstore_rest.referente

		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest.nome + '/' + api_petstore_rest.versione

		* def fruizione_petstore_update = read('<nome>')

		* call create ({ resourcePath: 'api', body: api_petstore_rest, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call put ( { resourcePath: 'fruizioni/'+petstore_key+'/modi', body: {modi: fruizione_petstore_update.modi}, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedRest(fruizione_petstore_update.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_rest_algoritmo_ES256.json|
|fruizione_modi_rest_algoritmo_ES384.json|
|fruizione_modi_rest_algoritmo_ES512.json|
|fruizione_modi_rest_algoritmo_RS256.json|
|fruizione_modi_rest_algoritmo_RS384.json|
|fruizione_modi_rest_algoritmo_RS512.json|
|fruizione_modi_rest_audience.json|
|fruizione_modi_rest_certificate_chain.json|
|fruizione_modi_rest_header.json|
|fruizione_modi_rest_ttl.json|
|fruizione_modi_rest_iu_codice_ente.json|
|fruizione_modi_rest_iu_userid.json|
|fruizione_modi_rest_iu_indirizzo_ip.json|

@UpdateFruizione_400_modi
Scenario Outline: Fruizioni Aggiornamento modi 400 <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('<fruizione_originale>')
		* def api = read('<api>')
		* eval randomize(api, ["nome"])
		* eval api.referente = soggettoDefault
		
		* eval fruizione_petstore.api_nome = api.nome
		* eval fruizione_petstore.fruizione_nome = api.nome
		* eval fruizione_petstore.api_versione = api.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api.referente

		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api.nome + '/' + api.versione

		* def fruizione_petstore_update = read('<nome>')

		* call create ({ resourcePath: 'api', body: api, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call update_400 ( { resourcePath: 'fruizioni/'+petstore_key+'/modi', body: {modi: fruizione_petstore_update.modi}, query_params: query_param_profilo_modi } )
    * match response.detail == '<errore>'
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|fruizione_originale|api|errore
|fruizione_modi_soap_iu_codice_ente.json|fruizione_modi_soap.json|api_modi_soap_no_info_utente.json|Impossibile settare info utente|
|fruizione_modi_rest_iu_codice_ente.json|fruizione_modi_rest.json|api_modi_rest_no_info_utente.json|Impossibile settare info utente|
