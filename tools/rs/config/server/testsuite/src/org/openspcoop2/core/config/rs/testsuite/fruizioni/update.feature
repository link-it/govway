Feature: Update Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def api_soap_una_azione = read('api_soap_una_azione.json')
* eval randomize(api_soap_una_azione, ["nome"])
* eval api_soap_una_azione.referente = soggettoDefault
* def api_soap_una_azione_path = 'api/' + api_soap_una_azione.nome + '/' + api_soap_una_azione.versione

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault
* def api_soap_piu_azioni_path = 'api/' + api_soap_piu_azioni.nome + '/' + api_soap_piu_azioni.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione

* def fruizione_soap_una_azione = read('fruizione_soap_una_azione.json')
* eval fruizione_soap_una_azione.api_nome = api_soap_una_azione.nome
* eval fruizione_soap_una_azione.api_versione = api_soap_una_azione.versione
* eval fruizione_soap_una_azione.fruizione_nome = api_soap_una_azione.nome
* eval fruizione_soap_una_azione.erogatore = erogatore.nome

* def fruizione_soap_piu_azioni = read('fruizione_soap_piu_azioni.json')
* eval fruizione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval fruizione_soap_piu_azioni.fruizione_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.erogatore = erogatore.nome

* def soap_una_azione_key = fruizione_petstore.erogatore + '/' + fruizione_soap_una_azione.api_nome + '/' + fruizione_soap_una_azione.api_versione
* def fruizione_soap_una_azione_path = 'fruizioni/' + soap_una_azione_key

* def soap_piu_azioni_key = fruizione_petstore.erogatore + '/' + fruizione_soap_piu_azioni.api_nome + '/' + fruizione_soap_piu_azioni.api_versione
* def fruizione_soap_piu_azioni_path = 'fruizioni/' + soap_piu_azioni_key

* def api_info_generali = read('api_info_generali.json')

* def connettore = read('connettore_fruizione_http.json')
* def connettore_pkcs11 = read('connettore_fruizione_http_pkcs11.json')
* def connettore_pkcs12_trustAll = read('connettore_fruizione_http_trustAll_pkcs12.json')
* def connettore_crl_ocsp = read('connettore_fruizione_http_crl_ocsp.json')

* def info_generali = read('informazioni_generali_petstore.json')
* def erogazione_versione = read('api_versione3.json')

* def descrizione_4000 = read('../api/api_descrizione_4000.json')
* def descrizione_4001 = read('../api/api_descrizione_4001.json')
* def descrizione_null = read('../api/api_descrizione_null.json')

* def getExpectedConnettore =
"""
function(connettore) {

	var expected = connettore
	expected.connettore.debug =expected.connettore.debug != null ? expected.connettore.debug : false 

	var tipo = connettore.connettore.tipo
	if(tipo == 'http') {
			if(expected.connettore.autenticazione_https != null) {
				expected.connettore.autenticazione_https.trust_all_server_certs = expected.connettore.autenticazione_https.trust_all_server_certs != null ? expected.connettore.autenticazione_https.trust_all_server_certs : false  
				expected.connettore.autenticazione_https.server.algoritmo = expected.connettore.autenticazione_https.server.algoritmo != null ? expected.connettore.autenticazione_https.server.algoritmo : 'PKIX'
			}  
	} else if (tipo == 'file') {
		expected.connettore.richiesta.create_parent_dir = expected.connettore.richiesta.create_parent_dir != null ? expected.connettore.richiesta.create_parent_dir : false
		expected.connettore.richiesta.overwrite_if_exists=expected.connettore.richiesta.overwrite_if_exists != null ? expected.connettore.richiesta.overwrite_if_exists: false
		if(expected.connettore.risposta != null) {
		expected.connettore.risposta.delete_after_read = expected.connettore.risposta.delete_after_read != null ? expected.connettore.risposta.delete_after_read: false
		}
	}
	
	return expected
} 
"""

@UpdateConnettore204
Scenario Outline: Update Fruizioni Connettore 204

		* def connettore_update = read ('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		* match response == getExpectedConnettore(connettore_update)


    Given url configUrl
    And path 'fruizioni', petstore_key
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		* match response.connettore == '<connettore>'
    
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|nome|connettore|
|connettore_fruizione_http.json|https://ginovadifretta.it/petstore|
|connettore_fruizione_echo.json|[echo] govway://echo|
|connettore_fruizione_null.json|[null] govway://dev/null|
|connettore_fruizione_plugin.json|[plugin] custom|
|connettore_fruizione_plugin_con_properties.json|[plugin] custom|
|connettore_fruizione_jms.json|[jms] nome_coda|
|connettore_fruizione_jms_jndi_init_ctx.json|[jms] nome_coda|
|connettore_fruizione_jms_jndi_provider_url.json|[jms] nome_coda|
|connettore_fruizione_jms_jndi_url_pkg.json|[jms] nome_coda|
|connettore_fruizione_jms_send_as_bytes.json|[jms] nome_coda|
|connettore_fruizione_jms_tipo_coda_topic.json|[jms] nome_coda|
|connettore_fruizione_jms_user_password.json|[jms] nome_coda|
|connettore_fruizione_file.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_create_parent.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_headers.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_overwrite.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_response.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_response_delete.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_response_headers.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_response_wait.json|[file] /tmp/abc.txt|
|connettore_fruizione_file_response_permissions.json|[file] /tmp/abc.txt|
|connettore_fruizione_apikey.json|https://ginovadifretta.it/petstore|
|connettore_fruizione_apikey_appid.json|https://ginovadifretta.it/petstore|
|connettore_fruizione_apikey_appid_custom.json|https://ginovadifretta.it/petstore|
|connettore_fruizione_apikey_custom.json|https://ginovadifretta.it/petstore|

@UpdateConnettore400
Scenario Outline: Erogazioni Update Connettore 400

		* def connettore_update = read ('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 400
    
    * match response.detail == '<error>' 
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|nome|error|
|connettore_fruizione_plugin_tipo_non_trovato.json|Tipo plugin [tipo_non_trovato] non trovato|

@UpdateConnettoreGruppo204
Scenario: Update Fruizioni Connettore gruppo 204

		* def gruppo_petstore = read ('gruppo_petstore.json')
		* def query_params = {'gruppo': 'GruppoJson'}

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ( { resourcePath: 'fruizioni/' + petstore_key + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})

    Given url configUrl
    And path 'fruizioni', petstore_key
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.connettore == 'https://ginovadifretta.it/petstore'

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    
    * match response == getExpectedConnettore(connettore)

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    
    * match response == getExpectedConnettore(connettore)

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200


		# il connettore in creazione e' solo HTTP
    * def conn = fruizione_petstore.connettore
    * eval conn.tipo = 'http'

    * match response == getExpectedConnettore({connettore: conn})

    Given url configUrl
    And path 'fruizioni', petstore_key
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.connettore == 'Connettori ridefiniti nei gruppi'    

@UpdateConnettore204_PKCS11
Scenario: Update Fruizioni Connettore 204 (PKCS11)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_pkcs11
    And params query_params
    When method put
    Then status 204
   
   Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_pkcs11
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
    
@UpdateConnettore204_PKCS12_trustAll
Scenario: Update Fruizioni Connettore 204 (PKCS12 per keystore, trustAll)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_pkcs12_trustAll
    And params query_params
    When method put
    Then status 204
   
   Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_pkcs12_trustAll
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore204_CRL_OCSP
Scenario: Update Fruizioni Connettore 204 (CRL e OCSP)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_crl_ocsp
    And params query_params
    When method put
    Then status 204
   
   Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_crl_ocsp
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore204_connettoreDebug
Scenario Outline: Update Fruizioni Connettore 204 connettore Debug

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

		* eval connettore.connettore.debug=<debug>
    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/connettore'} )
    
		* match response.connettore.debug == <debug>
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|debug|
|true|
|false|


@UpdateInfoGenerali204
Scenario: Update Fruizioni Info Generali 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'informazioni'
    And header Authorization = govwayConfAuth
    And request info_generali
    And params query_params
    When method put
    Then status 204
    
    
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_petstore.erogatore + '/' + info_generali.nome + '/' + fruizione_petstore.api_versione })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateUrlInvocazioneRest204
Scenario Outline: Fruizioni Update Url Invocazione 204 per API REST
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>
   
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "content-based" | "nomeinutile" | null | true | 400 | "interface-based" | null | null | false |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "input-based" | "vieneignorato" | null | true | 204 | "input-based" | null | null | true |
| "input-based" | null | "vieneignorato" | true | 204 | "input-based" | null | null | true |
| "input-based" | null | null | false | 204 | "input-based" | null | null | false |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "interface-based" | null | null | false | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "url-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "static" | null | null | true | 400 | "interface-based" | null | null | false |


@UpdateUrlInvocazioneSoap204
Scenario Outline: Fruizioni Update Url Invocazione 204 per API SOAP
    
    * call create ({ resourcePath: 'api', body: api_soap_una_azione })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_soap_una_azione })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'fruizioni/' + soap_una_azione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_soap_una_azione_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "content-based" | "nomeinutile" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "input-based" | "vieneignorato" | null | true | 204 | "input-based" | null | null | true |
| "input-based" | null | "vieneignorato" | true | 204 | "input-based" | null | null | true |
| "input-based" | null | null | false | 204 | "input-based" | null | null | false |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "interface-based" | null | null | false | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | "vieneignorato" | null | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | null | "vieneignorato" | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | null | null | false | 204 | "soap-action-based" | null | null | false |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "url-based" | null | "/.(espressioneRegolare)?" | false | 204 | "url-based" | null | "/.(espressioneRegolare)?" | false |
| "url-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "url-based" | "nomeinutile" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | "MRequest" | null | true | 204 | "static" | null | null | false |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\?]*).*" | true |

@UpdateUrlInvocazioneSoapPiuAzioni204
Scenario Outline: Fruizioni Update Url Invocazione 204 per API SOAP con piu azioni
    
    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_soap_piu_azioni })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'fruizioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "static" | "MRequest" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\?]*).*" | true |



@UpdateVersione204
Scenario: Update Fruizioni Versione 204
   
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * eval api_petstore.versione = 3
    * eval erogazione_versione.api_versione = 3

    * call create ({ resourcePath: 'api', body: api_petstore })
    
    Given url configUrl
    And path 'fruizioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    And request erogazione_versione
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: 'api/' + api_petstore.nome + '/' + api_petstore.versione })
    * call delete ({ resourcePath: api_petstore_path })


@UpdateConnettore404
Scenario: Update Fruizioni 404

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 404

@UpdateDescrizione
Scenario: Fruizioni Update Descrizione 204
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4000
    When method put
    Then status 204

    Given url configUrl
    And path 'fruizioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descrizione_4000.descrizione

    Given url configUrl
    And path 'fruizioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4001
    When method put
    Then status 400

    * match response.detail contains 'Max length is \'4000\', found \'4001\''

    Given url configUrl
    And path 'fruizioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_null
    When method put
    Then status 204

    Given url configUrl
    And path 'fruizioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'
        
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
