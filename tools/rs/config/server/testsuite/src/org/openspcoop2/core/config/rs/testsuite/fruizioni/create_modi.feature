Feature: Creazione Fruizioni ModI

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore_oauth = read('api_modi_oauth.json')
* eval randomize(api_petstore_oauth, ["nome"])
* eval api_petstore_oauth.referente = soggettoDefault

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

* def getExpectedOAUTH =
"""
function(modi) {
var expected = modi;

expected.keystore = expected.keystore != null ? expected.keystore : { "modalita" : "default" }

return expected;
} 
"""

* def getExpectedSOAP =
"""
function(modi) {
var expected = modi;

expected.richiesta.sicurezza_messaggio.certificate_chain = expected.richiesta.sicurezza_messaggio.certificate_chain != null ? expected.richiesta.sicurezza_messaggio.certificate_chain : false
expected.richiesta.sicurezza_messaggio.includi_signature_token = expected.richiesta.sicurezza_messaggio.includi_signature_token != null ? expected.richiesta.sicurezza_messaggio.includi_signature_token : false
expected.richiesta.sicurezza_messaggio.riferimento_x509 = expected.richiesta.sicurezza_messaggio.riferimento_x509 != null ? expected.richiesta.sicurezza_messaggio.riferimento_x509 : 'binary-security-token'
expected.richiesta.sicurezza_messaggio.time_to_live = expected.richiesta.sicurezza_messaggio.time_to_live != null ? expected.richiesta.sicurezza_messaggio.time_to_live : 300
expected.richiesta.sicurezza_messaggio.forma_canonica_xml = expected.richiesta.sicurezza_messaggio.forma_canonica_xml != null ? expected.richiesta.sicurezza_messaggio.forma_canonica_xml : 'exclusive-canonical-xml-10'
expected.richiesta.sicurezza_messaggio.algoritmo = expected.richiesta.sicurezza_messaggio.algoritmo != null ? expected.richiesta.sicurezza_messaggio.algoritmo : 'ECDSA-SHA-256'

expected.risposta.sicurezza_messaggio.verifica_wsa_to = expected.risposta.sicurezza_messaggio.verifica_wsa_to != null ? expected.risposta.sicurezza_messaggio.verifica_wsa_to : true

return expected;
} 
"""

* def getExpectedRest =
"""
function(modi, httpHeaderDefault) {
var expected = modi;

expected.richiesta.sicurezza_messaggio.riferimento_x509 = expected.richiesta.sicurezza_messaggio.riferimento_x509 != null ? expected.richiesta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.richiesta.sicurezza_messaggio.time_to_live = expected.richiesta.sicurezza_messaggio.time_to_live != null ? expected.richiesta.sicurezza_messaggio.time_to_live: 300
expected.richiesta.sicurezza_messaggio.algoritmo = expected.richiesta.sicurezza_messaggio.algoritmo != null ? expected.richiesta.sicurezza_messaggio.algoritmo: 'RS256'
expected.richiesta.sicurezza_messaggio.certificate_chain = expected.richiesta.sicurezza_messaggio.certificate_chain != null ? expected.richiesta.sicurezza_messaggio.certificate_chain: false
expected.richiesta.sicurezza_messaggio.header_http_firmare = expected.richiesta.sicurezza_messaggio.header_http_firmare !=null ? expected.richiesta.sicurezza_messaggio.header_http_firmare : httpHeaderDefault.header_http_firmare

expected.risposta.sicurezza_messaggio.riferimento_x509 = expected.risposta.sicurezza_messaggio.riferimento_x509 != null ? expected.risposta.sicurezza_messaggio.riferimento_x509 : 'richiesta'
expected.risposta.sicurezza_messaggio.verifica_audience = expected.risposta.sicurezza_messaggio.verifica_audience != null ? expected.risposta.sicurezza_messaggio.verifica_audience : true

return expected;
} 
"""


@CreateFruizione_204_modi_OAUTH
Scenario Outline: Fruizioni Creazione 204 OAUTH <nome>

		* def erogatore = read('soggetto_erogatore.json')
		* eval randomize (erogatore, ["nome", "credenziali.username"])
		
		* def fruizione_petstore = read('<nome>')
		* eval fruizione_petstore.api_nome = api_petstore_oauth.nome
		* eval fruizione_petstore.fruizione_nome = api_petstore_oauth.nome
		* eval fruizione_petstore.api_versione = api_petstore_oauth.versione
		* eval fruizione_petstore.erogatore = erogatore.nome
		* eval fruizione_petstore.api_referente = api_petstore_oauth.referente
		
		* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
		* def api_petstore_path = 'api/' + api_petstore_oauth.nome + '/' + api_petstore_oauth.versione

    * call create ({ resourcePath: 'api', body: api_petstore_oauth, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
		* def expected = getExpectedOAUTH(fruizione_petstore.modi)
    * match response.modi == expected
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_oauth.json|
|fruizione_modi_oauth_keystore_default.json|
|fruizione_modi_oauth_keystore_ridefinito.json|
|fruizione_modi_oauth_keystore_ridefinito_archivio.json|
|fruizione_modi_oauth_keystore_ridefinito_hsm.json|



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
	* def expected = getExpectedSOAP(fruizione_petstore.modi)
	* match response.modi == expected
	* call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|fruizione_modi_soap.json|
|fruizione_modi_soap_header_firmare.json|
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
|fruizione_modi_soap_risposta_truststore_ridefinito_hsm.json|
|fruizione_modi_soap_risposta_truststore_ridefinito_ocsp.json|
|fruizione_modi_soap_ttl_risposta.json|
|fruizione_modi_soap_wsato_verifica_risposta.json|
|fruizione_modi_soap_wsato_verifica_risposta_disabilitata.json|
|fruizione_modi_soap_keystore_fruizione_default.json|
|fruizione_modi_soap_keystore_fruizione_ridefinito.json|
|fruizione_modi_soap_keystore_fruizione_ridefinito_archivio.json|
|fruizione_modi_soap_keystore_fruizione_ridefinito_hsm.json|
|fruizione_modi_soap_keystore_fruizione_dati_oauth.json|




@CreateFruizione_204_modi_audit_SOAP
Scenario Outline: Fruizioni Creazione 204 SOAP <nome>

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
	* eval fruizione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval fruizione_petstore.fruizione_nome = api_petstore_soap_audit.nome
	* eval fruizione_petstore.api_versione = api_petstore_soap_audit.versione
	* eval fruizione_petstore.erogatore = erogatore.nome
	* eval fruizione_petstore.api_referente = api_petstore_soap_audit.referente
		
	* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione

	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedSOAP(fruizione_petstore.modi)
	* match response.modi == expected
	* call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|fruizione_modi_soap_audit_same.json|
|fruizione_modi_soap_audit_different.json|




@CreateFruizione_400_modi_audit_SOAP
Scenario Outline: Fruizioni Creazione 400 <nome>

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
	* eval fruizione_petstore.api_nome = api_petstore_soap_audit.nome
	* eval fruizione_petstore.fruizione_nome = api_petstore_soap_audit.nome
	* eval fruizione_petstore.api_versione = api_petstore_soap_audit.versione
	* eval fruizione_petstore.erogatore = erogatore.nome
	* eval fruizione_petstore.api_referente = api_petstore_soap_audit.referente
		
	* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_soap_audit.nome + '/' + api_petstore_soap_audit.versione
	* call create ({ resourcePath: 'api', body: api_petstore_soap_audit, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create_400 ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|errore
|fruizione_modi_soap_audit_different_senza_valore_atteso.json|Audience di audit non definito|
|fruizione_modi_soap_audit_claim_non_definiti.json|L\'audit definito nella API richiede la definizione delle seguenti informazioni: [userID, userLocation]|
|fruizione_modi_soap_audit_userID_claim_non_definito.json|L\'audit definito nella API richiede la definizione delle seguenti informazioni: [userID]|
|fruizione_modi_soap_audit_claim_sconosciuto.json|Impossibile settare info audit \'userIDerrato\': non risulta esistere una informazione con il nome indicato|





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
	* def expected = getExpectedRest(fruizione_petstore.modi, header_firmare_default)
	* match response.modi == expected
	* call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_rest.json|
|fruizione_modi_rest_claims.json|
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
|fruizione_modi_rest_ttl_risposta.json|
|fruizione_modi_rest_risposta_truststore_ridefinito.json|
|fruizione_modi_rest_risposta_truststore_ridefinito_hsm.json|
|fruizione_modi_rest_risposta_truststore_ridefinito_ocsp.json|
|fruizione_modi_rest_audience_verifica_risposta.json|
|fruizione_modi_rest_audience_verifica_risposta_disabilitata.json|
|fruizione_modi_rest_keystore_fruizione_default.json|
|fruizione_modi_rest_keystore_fruizione_ridefinito.json|
|fruizione_modi_rest_keystore_fruizione_ridefinito_archivio.json|
|fruizione_modi_rest_keystore_fruizione_ridefinito_hsm.json|
|fruizione_modi_rest_keystore_fruizione_dati_oauth.json|


@CreateFruizione_204_modi_REST_contemporaneita
Scenario Outline: Fruizioni Creazione 204 REST <nome> con configurazione contemporanea dei 2 header AGID

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
	* eval fruizione_petstore.api_nome = api_petstore_rest_contemporaneita.nome
	* eval fruizione_petstore.fruizione_nome = api_petstore_rest_contemporaneita.nome
	* eval fruizione_petstore.api_versione = api_petstore_rest_contemporaneita.versione
	* eval fruizione_petstore.erogatore = erogatore.nome
	* eval fruizione_petstore.api_referente = api_petstore_rest_contemporaneita.referente
		
	* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_contemporaneita.nome + '/' + api_petstore_rest_contemporaneita.versione
	* call create ({ resourcePath: 'api', body: api_petstore_rest_contemporaneita, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedRest(fruizione_petstore.modi, header_firmare_default)
	* match response.modi == expected
	* call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|
|fruizione_modi_rest_contemporaneita_default.json|
|fruizione_modi_rest_contemporaneita_diversoDefault.json|


@CreateFruizione_400_modi
Scenario Outline: Fruizioni Creazione 400 <nome>

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
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
	* call create ({ resourcePath: 'api', body: api, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create_400 ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|api|errore
|fruizione_modi_oauth_token_non_esistente.json|api_modi_oauth.json|Policy indicata per l\'autenticazione basata su token, non esiste|
|fruizione_modi_rest.json|api_modi_rest_no_sicurezza.json|Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata|
|fruizione_modi_soap.json|api_modi_soap_no_sicurezza.json|Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata|
|fruizione_modi_soap_iu_codice_ente.json|api_modi_soap_no_info_utente.json|Impossibile settare info utente|
|fruizione_modi_rest_iu_codice_ente.json|api_modi_rest_no_info_utente.json|Impossibile settare info utente|
|fruizione_modi_rest_contemporaneita_default.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la richiesta|
|fruizione_modi_rest_contemporaneita_soloRisposta.json|api_modi_rest.json|L\'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la risposta|



@CreateFruizione_204_modi_audit_REST
Scenario Outline: Fruizioni Creazione 204 REST <nome>

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
	* eval fruizione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval fruizione_petstore.fruizione_nome = api_petstore_rest_audit.nome
	* eval fruizione_petstore.api_versione = api_petstore_rest_audit.versione
	* eval fruizione_petstore.erogatore = erogatore.nome
	* eval fruizione_petstore.api_referente = api_petstore_rest_audit.referente
		
	* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione

	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/modi', query_params: query_param_profilo_modi } )
	* def expected = getExpectedRest(fruizione_petstore.modi, header_firmare_default)
	* match response.modi == expected
	* call delete ({ resourcePath: 'fruizioni/' + petstore_key, query_params: query_param_profilo_modi } )
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )

Examples:
|nome|
|fruizione_modi_rest_audit_same.json|
|fruizione_modi_rest_audit_different.json|




@CreateFruizione_400_modi_audit_REST
Scenario Outline: Fruizioni Creazione 400 <nome>

	* def erogatore = read('soggetto_erogatore.json')
	* eval randomize (erogatore, ["nome", "credenziali.username"])
		
	* def fruizione_petstore = read('<nome>')
	* eval fruizione_petstore.api_nome = api_petstore_rest_audit.nome
	* eval fruizione_petstore.fruizione_nome = api_petstore_rest_audit.nome
	* eval fruizione_petstore.api_versione = api_petstore_rest_audit.versione
	* eval fruizione_petstore.erogatore = erogatore.nome
	* eval fruizione_petstore.api_referente = api_petstore_rest_audit.referente
		
	* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.fruizione_nome + '/' + fruizione_petstore.api_versione
	* def api_petstore_path = 'api/' + api_petstore_rest_audit.nome + '/' + api_petstore_rest_audit.versione
	* call create ({ resourcePath: 'api', body: api_petstore_rest_audit, query_params: query_param_profilo_modi })
	* call create ({ resourcePath: 'soggetti', body: erogatore })
	* call create_400 ( { resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key, query_params: query_param_profilo_modi } )
	* match response.detail == '<errore>'
	* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
	* call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi } )


Examples:
|nome|errore
|fruizione_modi_rest_audit_different_senza_valore_atteso.json|Audience di audit non definito|
|fruizione_modi_rest_audit_claim_non_definiti.json|L\'audit definito nella API richiede la definizione delle seguenti informazioni: [userID, userLocation]|
|fruizione_modi_rest_audit_userID_claim_non_definito.json|L\'audit definito nella API richiede la definizione delle seguenti informazioni: [userID]|
|fruizione_modi_rest_audit_claim_sconosciuto.json|Impossibile settare info audit \'userIDerrato\': non risulta esistere una informazione con il nome indicato|
