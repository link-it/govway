Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}

@UpdateModi400
Scenario Outline: Api Update Interfaccia 400 <nome-test>

	* def api_modi_update = read('<nome-test>.json')
	* def api = read('api_modi_<tipo-test>.json')
	* eval randomize(api, ["nome"])
    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    Given url configUrl
	And path 'api/' + api.nome + '/' + api.versione + '/modi'
	And  header Authorization = govwayConfAuth
	And request api_modi_update.modi
	And params query_param_profilo_modi
	When method put
	Then assert responseStatus == 400
	* match response.detail == '<error>'
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

Examples:
| nome-test | error | tipo-test |
| api_modi_error_soap_applicabilita_custom | sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP | soap |
| api_modi_error_soap_digest_richiesta_nointegrity_qualsiasi | sicurezza_messaggio.digest_richiesta specificato con pattern auth01 o applicabilita richiesta | soap |
| api_modi_error_soap_firma_allegati_nointegrity | sicurezza_messaggio.soap_firma_allegati specificato con pattern auth01 | soap |
| api_modi_error_soap_header_rest | sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP | soap |
| api_modi_error_soap_informazioni_utente_nointegrity_richiesta | sicurezza_messaggio.informazioni_utente (audit-pattern: audit-legacy) specificato con pattern auth01 | soap |
| api_modi_error_rest_soap_firma_allegati | sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST | rest |
| api_modi_error_rest_sicurezza_msg_senza_header | sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern auth01 | rest |
| api_modi_error_rest_applicabilita_custom_senza_elem | sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita custom | rest |
| api_modi_error_rest_applicabilita_richiesta_custom_senza_ct | sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta custom | rest |
| api_modi_error_rest_applicabilita_risposta_custom_senza_ct | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom | rest |
| api_modi_error_rest_applicabilita_richiesta_abilitato_con_ct | sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta abilitato | rest |
| api_modi_error_rest_applicabilita_risposta_abilitato_con_ct | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato | rest |
| api_modi_error_rest_applicabilita_risposta_abilitato_con_response_code | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato | rest |
| api_modi_error_rest_applicabilita_risposta_custom_senza_response_code | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom | rest |
| api_modi_rest_i01a01_richiesta_token_pdnd_singolo_header | sicurezza_messaggio.rest_header deve essere definita come \'agid+bearer-request\' o \'custom+bearer-request\' con una generazione token non locale e un pattern di sicurezza \'integrity01-auth01\' | rest |
| api_modi_rest_i02a01_richiesta_token_oauth_singolo_header | sicurezza_messaggio.rest_header deve essere definita come \'agid+bearer-request\' o \'custom+bearer-request\' con una generazione token non locale e un pattern di sicurezza \'integrity02-auth01\' | rest |
| api_modi_rest_i02a01_richiesta_token_locale | sicurezza_messaggio.generazione_token, valorizzato con \'locale\', non è compatibile con il pattern \'integrity02-auth01\' | rest |
| api_modi_soap_i02a01_richiesta_token_oauth | sicurezza_messaggio.pattern \'integrity02-auth01\' non utilizzabile con API SOAP | soap |
| api_modi_rest_i02a01_richiesta_token_default | sicurezza_messaggio.generazione_token deve essere valorizzato con il pattern \'integrity02-auth01\' | rest |
| api_modi_rest_a01_audit01_schemaNonValido | sicurezza_messaggio.schema_audit specificato \'non-esistente\' con pattern di audit \'audit-rest01\' non valido: lo schema indicato non risultato configurato | rest |
| api_modi_rest_a01_audit01_nonApplicabile | sicurezza_messaggio.informazioni_utente specificato con applicabilità risposta | rest |
| api_modi_rest_a01_audit02_tokenLocale | sicurezza_messaggio.informazioni_utente con audit-pattern \'audit-rest02\' richiede la generazione di un token oauth o pdnd | rest |
| api_modi_soap_a01_audit01_schemaNonValido | sicurezza_messaggio.schema_audit specificato \'non-esistente\' con pattern di audit \'audit-rest01\' non valido: lo schema indicato non risultato configurato | soap |
| api_modi_soap_a01_audit01_schemaNonApplicabile | sicurezza_messaggio.informazioni_utente specificato con applicabilità risposta | soap |
| api_modi_soap_a01_audit02_tokenLocale | sicurezza_messaggio.informazioni_utente con audit-pattern \'audit-rest02\' richiede la generazione di un token oauth o pdnd | soap |
