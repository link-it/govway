Feature: Aggiornamento Applicativi Server

Background:

    * call read('classpath:crud_commons.feature')

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


@Update204
Scenario Outline: Applicativi Server Aggiornamento 204 OK

    * def applicativo = read('applicativo.json') 
    * eval randomize(applicativo, ["nome"]);

    * def applicativo_key = applicativo.nome
    * def applicativo_update = read('<nome>')
    * eval applicativo_update.nome = applicativo.nome
    
    * call create ( { resourcePath: 'applicativi-server', body: applicativo,  key: applicativo_key } )
    * call put ( { resourcePath: 'applicativi-server/'+applicativo_key, body: applicativo_update } )
		* call get ( { resourcePath: 'applicativi-server', key: applicativo_key } )
    * match response == applicativo_update
    * call delete ({ resourcePath: 'applicativi-server/' + applicativo_key } )

Examples:
|nome|
|applicativo.json|
|applicativo_proprieta.json|


@UpdateConnettore204
Scenario Outline: Applicativi Server Aggiornamento Connettore 204 OK

    * def applicativo = read('applicativo.json') 
    * eval randomize(applicativo, ["nome"]);

    * def applicativo_key = applicativo.nome
    * def applicativo_update = read('applicativo.json')
    * eval applicativo_update.nome = applicativo.nome
    * def connettore_update = read('<nome>').connettore
    * eval applicativo_update.connettore = connettore_update
    
    * call create ( { resourcePath: 'applicativi-server', body: applicativo,  key: applicativo_key } )
    * call put ( { resourcePath: 'applicativi-server/'+applicativo_key, body: applicativo_update } )
		* call get ( { resourcePath: 'applicativi-server', key: applicativo_key } )
    * match response.connettore == getExpectedConnettore({connettore: connettore_update}).connettore
    * call delete ({ resourcePath: 'applicativi-server/' + applicativo_key } )

Examples:
|nome|
|applicativo.json|
|applicativo_proprieta.json|
|connettore_applicativo_server_echo.json|
|connettore_applicativo_server_file.json|
|connettore_applicativo_server_file_create_parent.json|
|connettore_applicativo_server_file_headers.json|
|connettore_applicativo_server_file_overwrite.json|
|connettore_applicativo_server_file_response.json|
|connettore_applicativo_server_file_response_delete.json|
|connettore_applicativo_server_file_response_headers.json|
|connettore_applicativo_server_file_response_wait.json|
|connettore_applicativo_server_jms.json|
|connettore_applicativo_server_jms_jndi_init_ctx.json|
|connettore_applicativo_server_jms_jndi_provider_url.json|
|connettore_applicativo_server_jms_jndi_url_pkg.json|
|connettore_applicativo_server_jms_send_as_bytes.json|
|connettore_applicativo_server_jms_tipo_coda_topic.json|
|connettore_applicativo_server_jms_user_password.json|
|connettore_applicativo_server_null.json|
|connettore_applicativo_server_plugin.json|
|connettore_applicativo_server_plugin_con_properties.json|

@UpdateConnettore400
Scenario Outline: Applicativi Server Aggiornamento Connettore 400

    * def applicativo = read('applicativo.json') 
    * eval randomize(applicativo, ["nome"]);

    * def applicativo_key = applicativo.nome
    * def applicativo_update = read('applicativo.json')
    * eval applicativo_update.nome = applicativo.nome
    * def connettore_update = read('<nome>').connettore
    * eval applicativo_update.connettore = connettore_update
    
    * call create ( { resourcePath: 'applicativi-server', body: applicativo,  key: applicativo_key } )
    
    Given url configUrl
    And path 'applicativi-server', applicativo_key
    And header Authorization = govwayConfAuth
    And request applicativo_update
    And params query_params
    When method put
    Then status 400
    
    * match response.detail == '<error>' 

    * call delete ({ resourcePath: 'applicativi-server/' + applicativo_key } )

Examples:
|nome|error
|connettore_applicativo_server_plugin_tipo_non_trovato.json|Tipo plugin [tipo_non_trovato] non trovato
