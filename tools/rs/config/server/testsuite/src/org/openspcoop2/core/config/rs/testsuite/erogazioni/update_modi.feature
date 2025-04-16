Feature: Creazione Erogazioni ModI

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore_soap = read('api_modi_soap.json')
* eval randomize(api_petstore_soap, ["nome"])
* eval api_petstore_soap.referente = soggettoDefault

* def api_petstore_soap_audit = read('api_modi_soap_audit.json')
* eval randomize(api_petstore_soap_audit, ["nome"])
* eval api_petstore_soap_audit.referente = soggettoDefault

* def query_param_profilo_modi = {'profilo': 'ModI'}

* def api_petstore_rest = read('api_modi_rest.json')
* eval randomize(api_petstore_rest, ["nome"])
* eval api_petstore_rest.referente = soggettoDefault

* def api_petstore_rest_contemporaneita = read('api_modi_rest_contemporaneita.json')
* eval randomize(api_petstore_rest_contemporaneita, ["nome"])
* eval api_petstore_rest_contemporaneita.referente = soggettoDefault

* def api_petstore_rest_audit = read('api_modi_rest_audit.json')
* eval randomize(api_petstore_rest_audit, ["nome"])
* eval api_petstore_rest_audit.referente = soggettoDefault

* def header_firmare_default = read('api_modi_header_firmare_default.json')

* def getExpectedSOAP =
"""
function(modi) {
var expected = modi;
expected.risposta.sicurezza_messaggio.certificate_chain = expected.risposta.sicurezza_messaggio.certificate_chain != null ? expected.risposta.sicurezza_messaggio.certificate_chain : false
expected.risposta.sicurezza_messaggio.includi_signature_token = expected.risposta.sicurezza_messaggio.includi_signature_token != null ? expected.risposta.sicurezza_messaggio.includi_signature_token : false
expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'binary-security-token'
expected.risposta.sicurezza_messaggio.time_to_live = expected.risposta.sicurezza_messaggio.time_to_live != null ? expected.risposta.sicurezza_messaggio.time_to_live : 300
expected.risposta.sicurezza_messaggio.forma_canonica_xml = expected.risposta.sicurezza_messaggio.forma_canonica_xml != null ? expected.risposta.sicurezza_messaggio.forma_canonica_xml : 'exclusive-canonical-xml-10'
expected.risposta.sicurezza_messaggio.algoritmo = expected.risposta.sicurezza_messaggio.algoritmo != null ? expected.risposta.sicurezza_messaggio.algoritmo : 'ECDSA-SHA-256'

return expected;
} 
"""

* def getExpectedRest =
"""
function(modi, httpHeaderDefault) {
var expected = modi;
expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.risposta.sicurezza_messaggio.time_to_live = expected.risposta.sicurezza_messaggio.time_to_live != null ? expected.risposta.sicurezza_messaggio.time_to_live: 300
expected.risposta.sicurezza_messaggio.algoritmo = expected.risposta.sicurezza_messaggio.algoritmo != null ? expected.risposta.sicurezza_messaggio.algoritmo: 'RS256'
expected.risposta.sicurezza_messaggio.certificate_chain = expected.risposta.sicurezza_messaggio.certificate_chain != null ? expected.risposta.sicurezza_messaggio.certificate_chain: false
expected.risposta.sicurezza_messaggio.header_http_firmare = expected.risposta.sicurezza_messaggio.header_http_firmare !=null ? expected.risposta.sicurezza_messaggio.header_http_firmare : httpHeaderDefault.header_http_firmare

return expected;
} 
"""

@UpdatePetstore_modi_SOAP
Scenario Outline: Erogazioni Aggiornamento Petstore SOAP <nome>

		* def erogazione_petstore = read('erogazione_modi_soap.json')
		* eval erogazione_petstore.api_nome = api_petstore_soap.nome
		* eval erogazione_petstore.api_versione = api_petstore_soap.versione
		
		* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_soap.nome + '/' + api_petstore_soap.versione

		* def erogazione_petstore_update = read('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore_soap, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call put ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi}, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedSOAP(erogazione_petstore_update.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_soap_wsa_ttl.json|
|erogazione_modi_soap_header_firmare.json|
|erogazione_modi_soap_truststore_ridefinito.json|
|erogazione_modi_soap_truststore_ridefinito_hsm.json|
|erogazione_modi_soap_truststore_ridefinito_pdnd.json|
|erogazione_modi_soap_truststore_ridefinito_ocsp.json|
|erogazione_modi_soap_keystore_ridefinito_file.json|
|erogazione_modi_soap_keystore_ridefinito_path.json|
|erogazione_modi_soap_keystore_ridefinito_hsm.json|
|erogazione_modi_soap_algoritmo_RSA-SHA-512.json|
|erogazione_modi_soap_algoritmo_DSA-SHA-256.json|
|erogazione_modi_soap_algoritmo_ECDSA-SHA-384.json|
|erogazione_modi_soap_algoritmo_ECDSA-SHA-512.json|
|erogazione_modi_soap_algoritmo_RSA-SHA-256.json|
|erogazione_modi_soap_algoritmo_RSA-SHA-384.json|
|erogazione_modi_soap_canonica_exc_xml10.json|
|erogazione_modi_soap_canonica_xml10.json|
|erogazione_modi_soap_canonica_xml11.json|
|erogazione_modi_soap_certificate_chain.json|
|erogazione_modi_soap_includi_signature_token.json|
|erogazione_modi_soap_rif_x509_binary_security_token.json|
|erogazione_modi_soap_rif_x509_issuer_serial.json|
|erogazione_modi_soap_rif_x509_ski-key-identifier.json|
|erogazione_modi_soap_rif_x509_thumbprint-key-identifier.json|
|erogazione_modi_soap_rif_x509_x509-key-identifier.json|




@UpdatePetstore_modi_audit_SOAP
Scenario Outline: Erogazioni Aggiornamento Petstore SOAP <nome>

	* def erogazione_petstore = read('erogazione_modi_soap.json')
	* eval erogazione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_soap_audit.versione
		
	* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call put ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi}, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedSOAP(erogazione_petstore_update.modi)
	* match response.modi == expected
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|erogazione_modi_soap_audit_same.json|
|erogazione_modi_soap_audit_different.json|




@UpdatePetstore400_modi_audit_SOAP
Scenario Outline: Erogazioni Creazione Petstore 400 <nome>

* def erogazione_petstore = read('erogazione_modi_soap.json')
	* eval erogazione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_soap_audit.versione
		
	* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )

	* call update_400 ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|errore
|erogazione_modi_soap_audit_different_senza_valore_atteso.json|Audience di audit non definito|






@UpdatePetstore_modi_REST
Scenario Outline: Erogazioni Aggiornamento Petstore REST <nome>

	* def erogazione_petstore = read('erogazione_modi_rest.json')
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest.nome + '/' + api_petstore_rest.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api_petstore_rest, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call put ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedRest(erogazione_petstore_update.modi, header_firmare_default)
	* match response.modi == expected
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest_audience_ttl.json|
|erogazione_modi_rest_algoritmo_ES256.json|
|erogazione_modi_rest_algoritmo_ES384.json|
|erogazione_modi_rest_algoritmo_ES512.json|
|erogazione_modi_rest_algoritmo_RS256.json|
|erogazione_modi_rest_algoritmo_RS384.json|
|erogazione_modi_rest_algoritmo_RS512.json|
|erogazione_modi_rest_certificate_chain.json|
|erogazione_modi_rest_header_firmare.json|
|erogazione_modi_rest_header_rif_509_risp.json|
|erogazione_modi_rest_keystore_ridefinito_file.json|
|erogazione_modi_rest_keystore_ridefinito_file_keystore_senza_password.json|
|erogazione_modi_rest_keystore_ridefinito_path.json|
|erogazione_modi_rest_keystore_ridefinito_hsm.json|
|erogazione_modi_rest_rif_multipli.json|
|erogazione_modi_rest_truststore_ridefinito.json|
|erogazione_modi_rest_truststore_ridefinito_trust_senza_password.json|
|erogazione_modi_rest_truststore_ridefinito_hsm.json|
|erogazione_modi_rest_truststore_ridefinito_pdnd.json|
|erogazione_modi_rest_truststore_ridefinito_ocsp.json|
|erogazione_modi_rest_ttl.json|
|erogazione_modi_rest_claims.json|


@UpdatePetstore_modi_REST_contemporaneita
Scenario Outline: Erogazioni Aggiornamento Petstore REST <nome> con configurazione contemporanea dei 2 header AGID

		* def erogazione_petstore = read('erogazione_modi_rest.json')
		* eval erogazione_petstore.erogazione_nome = api_petstore_rest_contemporaneita.nome
		* eval erogazione_petstore.api_nome = api_petstore_rest_contemporaneita.nome
		* eval erogazione_petstore.api_versione = api_petstore_rest_contemporaneita.versione
		
		* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest_contemporaneita.nome + '/' + api_petstore_rest_contemporaneita.versione

		* def erogazione_petstore_update = read('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore_rest_contemporaneita, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call put ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedRest(erogazione_petstore_update.modi, header_firmare_default)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest_contemporaneita_default.json|
|erogazione_modi_rest_contemporaneita_diverso_default.json|


@UpdateErogazione_400_modi
Scenario Outline: Erogazioni Aggiornamento Petstore 400 <nome>

	* def erogazione_petstore = read('erogazione_modi_rest.json')
	* def api = read('<api>')
	* eval randomize(api, ["nome"])
	* eval api.referente = soggettoDefault
	* eval erogazione_petstore.erogazione_nome = api.nome
	* eval erogazione_petstore.api_nome = api.nome
	* eval erogazione_petstore.api_versione = api.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api.nome + '/' + api.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call update_400 ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ( { resourcePath: 'api/'+ api.nome + '/' + api.versione } )


Examples:
|nome|api|errore
|erogazione_modi_rest_x5u_no_richiesta.json|api_modi_rest.json|Impossibile settare URL X5U con riferimento x509 [x5c]|
|erogazione_modi_rest_no_x5u.json|api_modi_rest.json|Specificare URL X5U con riferimento x509 [x5u]|
|erogazione_modi_rest_contemporaneita_default.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la richiesta|
|erogazione_modi_rest_contemporaneita_soloRisposta.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la risposta|



@UpdatePetstore204_modi_audit_REST
Scenario Outline: Erogazioni Creazione Petstore 204 REST <nome>

	* def erogazione_petstore = read('erogazione_modi_rest.json')
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_audit.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call put ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedRest(erogazione_petstore_update.modi, header_firmare_default)
	* match response.modi == expected
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|erogazione_modi_rest_audit_same.json|
|erogazione_modi_rest_audit_different.json|




@UpdatePetstore400_modi_audit_REST
Scenario Outline: Erogazioni Creazione Petstore 400 <nome>

	* def erogazione_petstore = read('erogazione_modi_rest.json')

	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_audit.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione

	* def erogazione_petstore_update = read('<nome>')

	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call update_400 ( { resourcePath: 'erogazioni/'+petstore_key+'/modi', body: {modi: erogazione_petstore_update.modi},  query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|errore
|erogazione_modi_rest_audit_different_senza_valore_atteso.json|Audience di audit non definito|
