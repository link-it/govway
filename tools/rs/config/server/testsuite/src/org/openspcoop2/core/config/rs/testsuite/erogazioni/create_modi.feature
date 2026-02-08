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

* def api_petstore_rest_signalhub = read('api_modi_rest_signalhub.json')
* eval randomize(api_petstore_rest_signalhub, ["nome"])
* eval api_petstore_rest_signalhub.referente = soggettoDefault

* def api_petstore_rest_pdnd_integrity = read('api_modi_pdnd_intergrity.json')
* eval randomize(api_petstore_rest_pdnd_integrity, ["nome"])
* eval api_petstore_rest_pdnd_integrity.referente = soggettoDefault

* def api_petstore_soap_signalhub = read('api_modi_soap_signalhub.json')
* eval randomize(api_petstore_soap_signalhub, ["nome"])
* eval api_petstore_soap_signalhub.referente = soggettoDefault

* def applicativo_signalhub = read('applicativo_client_signalhub.json')
* eval randomize(applicativo_signalhub, ["nome", "credenziali.username"])

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

* def getExpectedRestOAuth =
"""
function(modi, httpHeaderDefault) {
var expected = modi;
expected.risposta.sicurezza_messaggio.time_to_live = expected.risposta.sicurezza_messaggio.time_to_live != null ? expected.risposta.sicurezza_messaggio.time_to_live: 300
expected.risposta.sicurezza_messaggio.algoritmo = expected.risposta.sicurezza_messaggio.algoritmo != null ? expected.risposta.sicurezza_messaggio.algoritmo: 'RS256'
expected.risposta.sicurezza_messaggio.header_http_firmare = expected.risposta.sicurezza_messaggio.header_http_firmare !=null ? expected.risposta.sicurezza_messaggio.header_http_firmare : httpHeaderDefault.header_http_firmare

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
|erogazione_modi_soap_wsa_ttl.json|
|erogazione_modi_soap_header_firmare.json|
|erogazione_modi_soap_truststore_ridefinito.json|
|erogazione_modi_soap_truststore_ridefinito_hsm.json|
|erogazione_modi_soap_truststore_ridefinito_jwk.json|
|erogazione_modi_soap_truststore_ridefinito_pdnd.json|
|erogazione_modi_soap_truststore_ridefinito_ocsp.json|
|erogazione_modi_soap_keystore_ridefinito_file.json|
|erogazione_modi_soap_keystore_ridefinito_path.json|
|erogazione_modi_soap_keystore_ridefinito_path_jwk.json|
|erogazione_modi_soap_keystore_ridefinito_path_keypair.json|
|erogazione_modi_soap_keystore_ridefinito_path_keypair_nopasswd.json|
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



@CreatePetstore204_modi_audit_SOAP
Scenario Outline: Erogazioni Creazione Petstore 204 REST <nome>

	* def erogazione_petstore = read('<nome>')
	* eval erogazione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_soap_audit.versione
		
	* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione

	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedSOAP(erogazione_petstore.modi)
	* match response.modi == expected
	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|erogazione_modi_soap_audit_same.json|
|erogazione_modi_soap_audit_different.json|



@CreatePetstore400_modi_audit_SOAP
Scenario Outline: Erogazioni Creazione Petstore 400 <nome>

	* def erogazione_petstore = read('<nome>')
	* eval erogazione_petstore.erogazione_nome = api_petstore_soap_audit.nome
	* eval erogazione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_soap_audit.versione
		
	* def petstore_key = erogazione_petstore.api_soap_servizio + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione

    	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create_400 ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
    	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|errore
|erogazione_modi_soap_audit_different_senza_valore_atteso.json|Audience di audit non definito|







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
		* def expected = getExpectedRest(erogazione_petstore.modi, header_firmare_default)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest.json|
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
|erogazione_modi_rest_keystore_ridefinito_path_jwk.json|
|erogazione_modi_rest_keystore_ridefinito_path_keypair.json|
|erogazione_modi_rest_keystore_ridefinito_path_keypair_nopasswd.json|
|erogazione_modi_rest_keystore_ridefinito_hsm.json|
|erogazione_modi_rest_rif_multipli.json|
|erogazione_modi_rest_truststore_ridefinito.json|
|erogazione_modi_rest_truststore_ridefinito_trust_senza_password.json|
|erogazione_modi_rest_truststore_ridefinito_hsm.json|
|erogazione_modi_rest_truststore_ridefinito_jwk.json|
|erogazione_modi_rest_truststore_ridefinito_pdnd.json|
|erogazione_modi_rest_truststore_ridefinito_ocsp.json|
|erogazione_modi_rest_ttl.json|
|erogazione_modi_rest_claims.json|




@CreatePetstore204_modi_REST_oauth
Scenario Outline: Erogazioni Creazione Petstore 204 REST Oauth <nome>

		* def erogazione_petstore = read('<nome>')
		* eval erogazione_petstore.erogazione_nome = api_petstore_rest_pdnd_integrity.nome
		* eval erogazione_petstore.api_nome = api_petstore_rest_pdnd_integrity.nome
		* eval erogazione_petstore.api_versione = api_petstore_rest_pdnd_integrity.versione
		
		* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest_pdnd_integrity.nome + '/' + api_petstore_rest_pdnd_integrity.versione

    * call create ({ resourcePath: 'api', body: api_petstore_rest_pdnd_integrity, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
    * def expected = getExpectedRestOAuth(erogazione_petstore.modi, header_firmare_default)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest_apiOAuth.json|
|erogazione_modi_rest_oauth_keystore_ridefinito_kid.json|




@CreatePetstore204_modi_REST_contemporaneita
Scenario Outline: Erogazioni Creazione Petstore 204 REST <nome> con configurazione contemporanea dei 2 header AGID

		* def erogazione_petstore = read('<nome>')
		* eval erogazione_petstore.erogazione_nome = api_petstore_rest_contemporaneita.nome
		* eval erogazione_petstore.api_nome = api_petstore_rest_contemporaneita.nome
		* eval erogazione_petstore.api_versione = api_petstore_rest_contemporaneita.versione
		
		* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_rest_contemporaneita.nome + '/' + api_petstore_rest_contemporaneita.versione

    * call create ({ resourcePath: 'api', body: api_petstore_rest_contemporaneita, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedRest(erogazione_petstore.modi, header_firmare_default)
    * match response.modi == expected
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest_contemporaneita_default.json|
|erogazione_modi_rest_contemporaneita_diverso_default.json|

@CreatePetstore400_modi
Scenario Outline: Erogazioni Creazione Petstore 400 <nome>

		* def erogazione_petstore = read('<nome>')
		* def api = read('<api>')
		* eval randomize(api, ["nome"])
		* eval api.referente = soggettoDefault
		* eval erogazione_petstore.erogazione_nome = api.nome
		* eval erogazione_petstore.api_nome = api.nome
		* eval erogazione_petstore.api_versione = api.versione
		
		* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
		* def api_petstore_path = 'api/' + api.nome + '/' + api.versione

    * call create ({ resourcePath: 'api', body: api, query_params: query_param_profilo_modi })
    * call create_400 ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
    * match response.detail == '<errore>'
    * call delete ( { resourcePath: 'api/'+ api.nome + '/' + api.versione } )


Examples:
|nome|api|errore
|erogazione_modi_rest_x5u_no_richiesta.json|api_modi_rest.json|Impossibile settare URL X5U con riferimento x509 [x5c]|
|erogazione_modi_rest_no_x5u.json|api_modi_rest.json|Specificare URL X5U con riferimento x509 [x5u]|
|erogazione_modi_rest.json|api_modi_rest_no_sicurezza.json|Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata|
|erogazione_modi_soap.json|api_modi_soap_no_sicurezza.json|Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata|
|erogazione_modi_rest_contemporaneita_default.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la richiesta|
|erogazione_modi_rest_contemporaneita_soloRisposta.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la risposta|
|erogazione_modi_rest_kid_non_compatibile.json|api_modi_rest.json|I campi \'kid\' e \'identificativo\' non sono compatibili con il profilo di sicurezza messaggio configurato nell\'API|




@CreatePetstore204_modi_audit_REST
Scenario Outline: Erogazioni Creazione Petstore 204 REST <nome>

	* def erogazione_petstore = read('<nome>')
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_audit.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione

    	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
    	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedRest(erogazione_petstore.modi, header_firmare_default)
    	* match response.modi == expected
    	* call delete ({ resourcePath: 'erogazioni/' + petstore_key, query_params: query_param_profilo_modi } )
    	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|erogazione_modi_rest_audit_same.json|
|erogazione_modi_rest_audit_different.json|



@CreatePetstore400_modi_audit_REST
Scenario Outline: Erogazioni Creazione Petstore 400 <nome>

	* def erogazione_petstore = read('<nome>')
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_audit.versione
		
	* def petstore_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione

    	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
	* call create_400 ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
    	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|errore
|erogazione_modi_rest_audit_different_senza_valore_atteso.json|Audience di audit non definito|

@CreatePetstore204_modi_signalhub_REST
Scenario Outline: Erogazioni Creazione Petstore signalhub <nome>

	# Creo soggetto fruitore del servizio signal-push
	* def soggetto_signal_hub = {'nome': 'SoggettoPetStoreSignalHub', 'dominio': 'interno'}
	* eval randomize(soggetto_signal_hub, ["nome"])
	* call create ( { resourcePath: 'soggetti', body: soggetto_signal_hub, query_params: query_param_profilo_modi } )

	* def query_param_profilo_signal_hub = ({'profilo': 'ModI', 'soggetto': soggetto_signal_hub.nome })
	
	# creo fruizione per il soggetto appena creato erogata da PDND	
	* def fruizione_signalhub = read('fruizione_modi_rest_signalhub.json')
	* eval randomize(fruizione_signalhub, ["fruizione_nome"])
	* def fruizione_key = 'PDND/' + fruizione_signalhub.fruizione_nome + '/' + fruizione_signalhub.api_versione
	* call create ( { resourcePath: 'fruizioni', body: fruizione_signalhub, query_params: query_param_profilo_signal_hub } )

	# imposto l'autorizzazione della fruizione a puntuale
	* def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
	* call put ( { resourcePath: 'fruizioni/' + fruizione_key + '/configurazioni/controllo-accessi/autorizzazione', body: autorizzazione, query_params: query_param_profilo_signal_hub } )

	# creo l'api di che pubblichera le informazioni di pseudonanonimizazzione
	* call create ( { resourcePath: 'api', body: api_petstore_rest_signalhub, query_params: query_param_profilo_modi } )
	
	# creo l'applicativo http-basic per l'accesso alla pubblicazione dei segnali
	* call create ( { resourcePath: 'applicativi', body: applicativo_signalhub, query_params: query_param_profilo_signal_hub } )

	# associo l'applicativo alla pubblicazione dei segnali
	* call create ( { resourcePath: 'fruizioni/' + fruizione_key + '/configurazioni/controllo-accessi/autorizzazione/applicativi', body: { applicativo: applicativo_signalhub.nome }, query_params: query_param_profilo_signal_hub} )

	# creo l'erogazione che pubblichera le informazioni di pseudonanonimizazzione
	* def erogazione_petstore = read('<nome>')
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.modi.informazioni_generali.descriptor_id[0] = erogazione_petstore.modi.informazioni_generali.service_id
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.modi.informazioni_generali.descriptor_id[1] = erogazione_petstore.modi.informazioni_generali.service_id
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_signalhub.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_signalhub.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_signalhub.versione
	* eval erogazione_petstore.modi.informazioni_generali.signal_hub.applicativo = applicativo_signalhub.nome

	* def erogazione_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* call create ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: erogazione_key, query_params: query_param_profilo_signal_hub } )
	
	# controllo che le proprieta modi siano correttamente impostate
	* call get ( { resourcePath: 'erogazioni', key: erogazione_key + '/modi', query_params: query_param_profilo_signal_hub } )
	* match response.modi == erogazione_petstore.modi
	
	# rimuovo l'applicativo agli applicativi autorizzati alla fruizione
	* call delete ({ resourcePath: 'fruizioni/' + fruizione_key +'/configurazioni/controllo-accessi/autorizzazione/applicativi/' + applicativo_signalhub.nome , query_params: query_param_profilo_signal_hub })
	
        # rimuovo l'erogazione
        * call delete ({ resourcePath: 'erogazioni/' + erogazione_key, query_params: query_param_profilo_signal_hub } )

	# rimuovo l'applicativo
	* call delete ({ resourcePath: 'applicativi/' + applicativo_signalhub.nome , query_params: query_param_profilo_signal_hub })

	# rimuovo l'api dell erogazione
	* def api_key = api_petstore_rest_signalhub.nome + '/' + api_petstore_rest_signalhub.versione
	* call delete ({ resourcePath: 'api/' + api_key, query_params: query_param_profilo_modi } )
	
	# rimuovo la fruizione
	* call delete ({ resourcePath: 'fruizioni/' + fruizione_key, query_params: query_param_profilo_signal_hub } )
	
	# rimuovo il soggetto fruitore della fruizione
	* call delete ({ resourcePath: 'soggetti/' + soggetto_signal_hub.nome, query_params: query_param_profilo_modi } )

Examples:
|nome|
|erogazione_modi_rest_signalhub.json|
|erogazione_modi_rest_signalhub_no_pseudoanonimizzazione.json|

@CreatePetstore400_modi_signalhub_REST
Scenario Outline: Erogazioni Creazione Petstore signalhub 400 <campo_null>

	# Creo soggetto fruitore del servizio signal-push
	* def soggetto_signal_hub = {'nome': 'SoggettoPetStoreSignalHub', 'dominio': 'interno'}
	* eval randomize(soggetto_signal_hub, ["nome"])
	* call create ( { resourcePath: 'soggetti', body: soggetto_signal_hub, query_params: query_param_profilo_modi } )

	* def query_param_profilo_signal_hub = ({'profilo': 'ModI', 'soggetto': soggetto_signal_hub.nome })

	# creo fruizione per il soggetto appena creato erogata da PDND
	* def fruizione_signalhub = read('fruizione_modi_rest_signalhub.json')
	* eval randomize(fruizione_signalhub, ["fruizione_nome"])
	* def fruizione_key = 'PDND/' + fruizione_signalhub.fruizione_nome + '/' + fruizione_signalhub.api_versione
	* call create ( { resourcePath: 'fruizioni', body: fruizione_signalhub, query_params: query_param_profilo_signal_hub } )

	# imposto l'autorizzazione della fruizione a puntuale
	* def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
	* call put ( { resourcePath: 'fruizioni/' + fruizione_key + '/configurazioni/controllo-accessi/autorizzazione', body: autorizzazione, query_params: query_param_profilo_signal_hub } )

	# creo l'api di che pubblichera le informazioni di pseudonanonimizazzione
	* call create ( { resourcePath: 'api', body: api_petstore_rest_signalhub, query_params: query_param_profilo_modi } )

	# creo l'applicativo http-basic per l'accesso alla pubblicazione dei segnali
	* call create ( { resourcePath: 'applicativi', body: applicativo_signalhub, query_params: query_param_profilo_signal_hub } )

	# associo l'applicativo alla pubblicazione dei segnali
	* call create ( { resourcePath: 'fruizioni/' + fruizione_key + '/configurazioni/controllo-accessi/autorizzazione/applicativi', body: { applicativo: applicativo_signalhub.nome }, query_params: query_param_profilo_signal_hub} )

	# creo l'erogazione che pubblichera le informazioni di pseudonanonimizazzione
	* def erogazione_petstore = read('erogazione_modi_rest_signalhub.json')
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.modi.informazioni_generali.descriptor_id[0] = erogazione_petstore.modi.informazioni_generali.service_id
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.modi.informazioni_generali.descriptor_id[1] = erogazione_petstore.modi.informazioni_generali.service_id
	* eval randomize(erogazione_petstore.modi.informazioni_generali, ["service_id"])
	* eval erogazione_petstore.erogazione_nome = api_petstore_rest_signalhub.nome
	* eval erogazione_petstore.api_nome = api_petstore_rest_signalhub.nome
	* eval erogazione_petstore.api_versione = api_petstore_rest_signalhub.versione
	* eval erogazione_petstore.modi.informazioni_generali.signal_hub.applicativo = applicativo_signalhub.nome

	# rimuovo il campo specificato
	* eval delete erogazione_petstore.modi.informazioni_generali.signal_hub['<campo_null>']

	* def erogazione_key = erogazione_petstore.erogazione_nome + '/' + erogazione_petstore.api_versione
	* call create_400 ( { resourcePath: 'erogazioni', body: erogazione_petstore,  key: erogazione_key, query_params: query_param_profilo_signal_hub } )
	* match response.detail == '<errore>'

	# rimuovo l'applicativo agli applicativi autorizzati alla fruizione
	* call delete ({ resourcePath: 'fruizioni/' + fruizione_key +'/configurazioni/controllo-accessi/autorizzazione/applicativi/' + applicativo_signalhub.nome , query_params: query_param_profilo_signal_hub })

	# rimuovo l'applicativo
	* call delete ({ resourcePath: 'applicativi/' + applicativo_signalhub.nome , query_params: query_param_profilo_signal_hub })

	# rimuovo l'api dell erogazione
	* def api_key = api_petstore_rest_signalhub.nome + '/' + api_petstore_rest_signalhub.versione
	* call delete ({ resourcePath: 'api/' + api_key, query_params: query_param_profilo_modi } )

	# rimuovo la fruizione
	* call delete ({ resourcePath: 'fruizioni/' + fruizione_key, query_params: query_param_profilo_signal_hub } )

	# rimuovo il soggetto fruitore della fruizione
	* call delete ({ resourcePath: 'soggetti/' + soggetto_signal_hub.nome, query_params: query_param_profilo_modi } )

Examples:
|campo_null|errore|
|risorsa|I campi : [risorsa] impostati a null in caso di pseudoanonimizzazione abilitata devono essere abilitati|
|algoritmo|I campi : [algoritmo] impostati a null in caso di pseudoanonimizzazione abilitata devono essere abilitati|
|dimensione_seme|I campi : [dimensione_seme] impostati a null in caso di pseudoanonimizzazione abilitata devono essere abilitati|
|giorni_rotazione|I campi : [giorni_rotazione] impostati a null in caso di pseudoanonimizzazione abilitata devono essere abilitati|

