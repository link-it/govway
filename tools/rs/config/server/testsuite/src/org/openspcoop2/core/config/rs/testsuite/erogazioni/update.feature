Feature: Update Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_soap_una_azione = read('api_soap_una_azione.json')
* eval randomize(api_soap_una_azione, ["nome"])
* eval api_soap_una_azione.referente = soggettoDefault

* def api_soap_una_azione_oneway = read('api_soap_una_azione_oneway.json')
* eval randomize(api_soap_una_azione_oneway, ["nome"])
* eval api_soap_una_azione_oneway.referente = soggettoDefault

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault

* def api_info_generali = read('api_info_generali.json')

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def erogazione_soap_una_azione = read('erogazione_soap_una_azione.json')
* eval erogazione_soap_una_azione.api_nome = api_soap_una_azione.nome
* eval erogazione_soap_una_azione.api_versione = api_soap_una_azione.versione
* eval erogazione_soap_una_azione.erogazione_nome = api_soap_una_azione.nome

* def erogazione_soap_una_azione_oneway = read('erogazione_soap_una_azione.json')
* eval erogazione_soap_una_azione_oneway.api_nome = api_soap_una_azione_oneway.nome
* eval erogazione_soap_una_azione_oneway.api_versione = api_soap_una_azione_oneway.versione
* eval erogazione_soap_una_azione_oneway.erogazione_nome = api_soap_una_azione_oneway.nome

* def erogazione_soap_piu_azioni = read('erogazione_soap_piu_azioni.json')
* eval erogazione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval erogazione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval erogazione_soap_piu_azioni.erogazione_nome = api_soap_piu_azioni.nome

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key
* def erogazione_petstore_path = 'erogazioni/' + petstore_key

* def soap_una_azione_key = erogazione_soap_una_azione.api_nome + '/' + erogazione_soap_una_azione.api_versione
* def api_soap_una_azione_path = 'api/' + soap_una_azione_key
* def erogazione_soap_una_azione_path = 'erogazioni/' + soap_una_azione_key

* def soap_una_azione_oneway_key = erogazione_soap_una_azione_oneway.api_nome + '/' + erogazione_soap_una_azione_oneway.api_versione
* def api_soap_una_azione_oneway_path = 'api/' + soap_una_azione_oneway_key
* def erogazione_soap_una_azione_oneway_path = 'erogazioni/' + soap_una_azione_oneway_key

* def soap_piu_azioni_key = erogazione_soap_piu_azioni.api_nome + '/' + erogazione_soap_piu_azioni.api_versione
* def api_soap_piu_azioni_path = 'api/' + soap_piu_azioni_key
* def erogazione_soap_piu_azioni_path = 'erogazioni/' + soap_piu_azioni_key

* def connettore = read('connettore_erogazione_http.json')
* def connettore_pkcs11 = read('connettore_erogazione_http_pkcs11.json')
* def connettore_pkcs12_trustAll = read('connettore_erogazione_http_trustAll_pkcs12.json')
* def connettore_crl_ocsp = read('connettore_erogazione_http_crl_ocsp.json')

* def info_generali = read('informazioni_generali_petstore.json')
* eval randomize(info_generali, ["nome"])
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
	} else if (tipo == 'plugin') {
		expected.connettore.proprieta = expected.connettore.proprieta != null ? expected.connettore.proprieta: new Array()
	}
	
	return expected
} 
"""

@UpdateConnettoreMessageBox
Scenario: Erogazioni Update Connettore MessageBox

		* def connettore_update = read ('connettore_erogazione_message_box.json')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 400

    * match response.detail == 'Impossibile associare un connettore di tipo message-box a una erogazione (o gruppo) che non sia composto da tutte e sole Operation SOAP OneWay'
    
    * def applicativo = read('applicativo_server.json') 
    * eval randomize(applicativo, ["nome"]);

    * def applicativo_key = applicativo.nome
    * def applicativo_update = read('applicativo_server.json')
    * eval applicativo_update.nome = applicativo.nome
    * def connettore_app_update = connettore_update.connettore
    * eval applicativo_update.connettore = connettore_app_update
    
    * call create ( { resourcePath: 'applicativi-server', body: applicativo,  key: applicativo_key } )
    * call put ( { resourcePath: 'applicativi-server/'+applicativo_key, body: applicativo_update } )
    
		* def connettore_update_applicativo_server_msg_box = read ('connettore_applicativo_server_erogazione_petstore.json')
    * eval connettore_update_applicativo_server_msg_box.connettore.applicativo = applicativo.nome
		
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update_applicativo_server_msg_box
    And params query_params
    When method put
    Then status 400

    * match response.detail == 'Impossibile associare un connettore di tipo message-box a una erogazione (o gruppo) che non sia composto da tutte e sole Operation SOAP OneWay'
    
    * call delete ({ resourcePath: 'applicativi-server/' + applicativo_key } )
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

    * call create ({ resourcePath: 'api', body: api_soap_una_azione_oneway })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_una_azione_oneway })

    Given url configUrl
    And path 'erogazioni', soap_una_azione_oneway_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', soap_una_azione_oneway_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.connettore.tipo == 'message-box'
    * match response.connettore.autenticazione_http.username == connettore_update.connettore.autenticazione_http.username
    
    
    Given url configUrl
    And path 'erogazioni', soap_una_azione_oneway_key
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

		* match response.connettore == 'disabilitato [MessageBox]'
		
		* def api_soap_una_azione_oneway2 = read('api_soap_una_azione_oneway.json')
		* eval randomize(api_soap_una_azione_oneway2, ["nome"])
		* eval api_soap_una_azione_oneway2.referente = soggettoDefault

		
		* def erogazione_soap_una_azione_oneway2 = read('erogazione_soap_una_azione.json')
		* eval erogazione_soap_una_azione_oneway2.api_nome = api_soap_una_azione_oneway2.nome
		* eval erogazione_soap_una_azione_oneway2.api_versione = api_soap_una_azione_oneway2.versione
		* eval erogazione_soap_una_azione_oneway2.erogazione_nome = api_soap_una_azione_oneway2.nome
		* eval randomize(erogazione_soap_una_azione_oneway2, ["erogazione_nome"])
		
		* def soap_una_azione_oneway_key2 = erogazione_soap_una_azione_oneway2.erogazione_nome + '/' + erogazione_soap_una_azione_oneway2.api_versione
		* def api_soap_una_azione_oneway_path2 = 'api/' + soap_una_azione_oneway_key2
		
    * call create ({ resourcePath: 'api', body: api_soap_una_azione_oneway2 })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_una_azione_oneway2 })

    Given url configUrl
    And path 'erogazioni', soap_una_azione_oneway_key2, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 400
    
    * match response.detail contains 'possiede già l\'utente (http-basic) indicato per il servizio \'Servizio IntegrationManager/MessageBox\''
    
    * call delete ({ resourcePath: 'erogazioni/' + soap_una_azione_oneway_key2 })
    * call delete ({ resourcePath: api_soap_una_azione_oneway_path2 })
    * call delete ({ resourcePath: 'erogazioni/' + soap_una_azione_oneway_key })
    * call delete ({ resourcePath: api_soap_una_azione_oneway_path })

@UpdateConnettore204
Scenario Outline: Erogazioni Update Connettore 204

		* def connettore_update = read ('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response == getExpectedConnettore(connettore_update)
    
    Given url configUrl
    And path 'erogazioni', petstore_key
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

		* match response.connettore == '<connettore>'
    
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|nome|connettore|
|connettore_erogazione_http.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_http_trust_senza_password.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_http_keystore_senza_password.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_echo.json|[echo] govway://echo|
|connettore_erogazione_null.json|[null] govway://dev/null|
|connettore_erogazione_plugin.json|[plugin] custom|
|connettore_erogazione_plugin_con_properties.json|[plugin] custom|
|connettore_erogazione_jms.json|[jms] nome_coda|
|connettore_erogazione_jms_jndi_init_ctx.json|[jms] nome_coda|
|connettore_erogazione_jms_jndi_provider_url.json|[jms] nome_coda|
|connettore_erogazione_jms_jndi_url_pkg.json|[jms] nome_coda|
|connettore_erogazione_jms_send_as_bytes.json|[jms] nome_coda|
|connettore_erogazione_jms_tipo_coda_topic.json|[jms] nome_coda|
|connettore_erogazione_jms_user_password.json|[jms] nome_coda|
|connettore_erogazione_file.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_create_parent.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_headers.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_overwrite.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_response.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_response_delete.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_response_headers.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_response_wait.json|[file] /tmp/abc.txt|
|connettore_erogazione_file_response_permissions.json|[file] /tmp/abc.txt|
|connettore_erogazione_apikey.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_apikey_appid.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_apikey_appid_custom.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_apikey_custom.json|https://ginovadifretta.it/petstore|
|connettore_erogazione_status_empty.json|[status] govway://status|
|connettore_erogazione_status_verifica_statistica.json|[status] govway://status|
|connettore_erogazione_status_verifica_statistica_custom_lifetime.json|[status] govway://status|



@UpdateConnettore400
Scenario Outline: Erogazioni Update Connettore 400

		* def connettore_update = read ('<nome>')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 400
    
    * match response.detail == '<error>' 
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|nome|error|
|connettore_erogazione_plugin_tipo_non_trovato.json|Tipo plugin [tipo_non_trovato] non trovato|


@UpdateConnettoreApplicativoServer204
Scenario: Erogazioni Update Connettore 204

		* def connettore_update_noserver = read ('connettore_erogazione_http.json')
		* def app_server = read ('applicativo_server.json')
		* eval randomize(app_server, ["nome"])
		* def connettore_update = read ('connettore_applicativo_server_erogazione_petstore.json')
		* eval connettore_update.connettore.applicativo = app_server.nome

		* def app_server_2 = read ('applicativo_server.json')
		* eval randomize(app_server_2, ["nome"])
		* def connettore_update_2 = read ('connettore_applicativo_server_erogazione_petstore.json')
		* eval connettore_update_2.connettore.applicativo = app_server_2.nome

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ({ resourcePath: 'applicativi-server', body: app_server })
    * call create ({ resourcePath: 'applicativi-server', body: app_server_2 })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == connettore_update

    Given url configUrl
    And path 'erogazioni', petstore_key
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

		* match response.connettore == 'https://ginovadifretta.it/petstore'
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update_2
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == connettore_update_2
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update_noserver
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == getExpectedConnettore(connettore_update_noserver)
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: 'applicativi-server/' + app_server.nome })
    * call delete ({ resourcePath: 'applicativi-server/' + app_server_2.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettoreGruppoApplicativoServer204	
Scenario: Erogazioni Update Connettore gruppo ApplicativoServer OK

		* def gruppo_petstore = read ('gruppo_petstore.json')
		* def query_params = {'gruppo': 'GruppoJson'}

		* def connettore_update_noserver = read ('connettore_erogazione_http.json')
		* def app_server = read ('applicativo_server.json')
		* eval randomize(app_server, ["nome"])
		* def connettore_update = read ('connettore_applicativo_server_erogazione_petstore.json')
		* eval connettore_update.connettore.applicativo = app_server.nome

		* def app_server_2 = read ('applicativo_server.json')
		* eval randomize(app_server_2, ["nome"])
		* def connettore_update_2 = read ('connettore_applicativo_server_erogazione_petstore.json')
		* eval connettore_update_2.connettore.applicativo = app_server_2.nome

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ({ resourcePath: 'applicativi-server', body: app_server })
    * call create ({ resourcePath: 'applicativi-server', body: app_server_2 })
    * call create ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == connettore_update
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update_2
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == connettore_update_2
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_update_noserver
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == getExpectedConnettore(connettore_update_noserver)
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: 'applicativi-server/' + app_server.nome })
    * call delete ({ resourcePath: 'applicativi-server/' + app_server_2.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettoreGruppo204	
Scenario: Erogazioni Update Connettore gruppo OK

		* def gruppo_petstore = read ('gruppo_petstore.json')
		* def query_params = {'gruppo': 'GruppoJson'}

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})


    Given url configUrl
    And path 'erogazioni', petstore_key
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		* match response.connettore == 'https://ginovadifretta.it/petstore'
    

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == getExpectedConnettore(connettore)

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

		* match response == getExpectedConnettore(connettore)
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		# il connettore in creazione e' solo HTTP
    * def conn = erogazione_petstore.connettore
    * eval conn.tipo = 'http'
    
		* match response == getExpectedConnettore({connettore: conn})

    Given url configUrl
    And path 'erogazioni', petstore_key
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		* match response.connettore == 'Connettori ridefiniti nei gruppi'
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore204_PKCS11
Scenario: Erogazioni Update Connettore 204 (PKCS11)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_pkcs11
    And params query_params
    When method put
    Then status 204
      
   Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_pkcs11
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
    
@UpdateConnettore204_PKCS12_trustAll
Scenario: Erogazioni Update Connettore 204 (PKCS12 per keystore, trustAll)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_pkcs12_trustAll
    And params query_params
    When method put
    Then status 204
      
   Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_pkcs12_trustAll
   
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
    
@UpdateConnettore204_CRL_OCSP
Scenario: Erogazioni Update Connettore 204 (CRL e OCSP)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore_crl_ocsp
    And params query_params
    When method put
    Then status 204
      
   Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match response == connettore_crl_ocsp
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore204_connettoreDebug
Scenario Outline: Erogazioni Update Connettore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

		* eval connettore.connettore.debug=<debug>
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
        
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/connettore'} )
    
		* match response.connettore.debug == <debug>
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|debug|
|true|
|false|

@UpdateInfoGenerali204
Scenario: Erogazioni Update Info Generali 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'informazioni'
    And header Authorization = govwayConfAuth
    And request info_generali
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + info_generali.nome + '/' + erogazione_petstore.api_versione })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateUrlInvocazioneRest204
Scenario Outline: Erogazioni Update Url Invocazione 204 per API REST
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
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
Scenario Outline: Erogazioni Update Url Invocazione 204 per API SOAP
    
    * call create ({ resourcePath: 'api', body: api_soap_una_azione })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_una_azione })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + soap_una_azione_key })
    * call delete ({ resourcePath: api_soap_una_azione_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "content-based" | "nomeinutile" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
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
| "url-based" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "url-based" | "nomeinutile" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | "MRequest" | null | true | 204 | "static" | null | null | false |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\?]*).*" | true |

@UpdateUrlInvocazioneSoapPiuAzioni204
Scenario Outline: Erogazioni Update Url Invocazione 204 per API SOAP con piu azioni
    
    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_piu_azioni })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "static" | "MRequest" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | "/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\?]*).*" | true |


@UpdateVersione204
Scenario: Erogazioni Update Versione 204
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * eval api_petstore.versione = 3
    * eval erogazione_versione.api_versione = 3

    * call create ({ resourcePath: 'api', body: api_petstore })
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    And request erogazione_versione
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
    * call delete ({ resourcePath: 'api/' + api_petstore.nome + '/' + api_petstore.versione })

@UpdateConnettore404
Scenario: Erogazioni Update 404

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 404

@UpdateDescrizione
Scenario: Erogazioni Update Descrizione 204
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4000
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descrizione_4000.descrizione

    Given url configUrl
    And path 'erogazioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4001
    When method put
    Then status 400

    * match response.detail contains 'Max length is \'4000\', found \'4001\''

    Given url configUrl
    And path 'erogazioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_null
    When method put
    Then status 204

    Given url configUrl
    And path 'erogazioni', petstore_key , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'
        
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
