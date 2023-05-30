Feature: Creazione Api modi con errori

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}


@Create409_modi
Scenario: Api Create 409 con profilo ModI
	* def api_modi = read('api_modi_soap.json')
	* eval randomize(api_modi, ["nome"])
	* call create_409 ( { resourcePath: 'api', body: api_modi, key: api_modi.nome + '/' + api_modi.versione, query_params: query_param_profilo_modi } )


@Create400_api
Scenario Outline: Api Create 400 con <nome-test>
	* def api_modi = read('<nome-test>.json')
	* eval randomize(api_modi, ["nome"])
    * call create_400 ( { resourcePath: 'api', body: api_modi, key: api_modi.nome + '/' + api_modi.versione, query_params: query_param_profilo_modi } )
    * match response.detail == '<error>'

Examples:
| nome-test | error |
| api_modi_error_no_modi | Specificare la configurazione \'ModI\' |
| api_modi_error_soap_applicabilita_custom | sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP |
| api_modi_error_soap_digest_richiesta_nointegrity_qualsiasi | sicurezza_messaggio.digest_richiesta specificato con pattern auth01 o applicabilita richiesta |
| api_modi_error_soap_firma_allegati_nointegrity | sicurezza_messaggio.soap_firma_allegati specificato con pattern auth01 |
| api_modi_error_soap_header_rest | sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP |
| api_modi_error_soap_informazioni_utente_nointegrity_richiesta | sicurezza_messaggio.informazioni_utente (audit-pattern: audit-legacy) specificato con pattern auth01 |
| api_modi_error_rest_soap_firma_allegati | sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST |
| api_modi_error_rest_sicurezza_msg_senza_header | sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern auth01 |
| api_modi_error_rest_applicabilita_custom_senza_elem | sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita custom |
| api_modi_error_rest_applicabilita_richiesta_custom_senza_ct | sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta custom |
| api_modi_error_rest_applicabilita_risposta_custom_senza_ct | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom |
| api_modi_error_rest_applicabilita_richiesta_abilitato_con_ct | sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta abilitato |
| api_modi_error_rest_applicabilita_risposta_abilitato_con_ct | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato |
| api_modi_error_rest_applicabilita_risposta_abilitato_con_response_code | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato |
| api_modi_error_rest_applicabilita_risposta_custom_senza_response_code | sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom |
| api_modi_rest_i01a01_richiesta_token_pdnd_singolo_header | sicurezza_messaggio.rest_header deve essere definita come \'agid+bearer-request\' o \'custom+bearer-request\' con una generazione token non locale e un pattern di sicurezza \'integrity01-auth01\' |
| api_modi_rest_i02a01_richiesta_token_oauth_singolo_header | sicurezza_messaggio.rest_header deve essere definita come \'agid+bearer-request\' o \'custom+bearer-request\' con una generazione token non locale e un pattern di sicurezza \'integrity02-auth01\' |
| api_modi_rest_i02a01_richiesta_token_locale | sicurezza_messaggio.generazione_token, valorizzato con \'locale\', non è compatibile con il pattern \'integrity02-auth01\' |
| api_modi_soap_i02a01_richiesta_token_oauth | sicurezza_messaggio.pattern \'integrity02-auth01\' non utilizzabile con API SOAP |
| api_modi_rest_i02a01_richiesta_token_default | sicurezza_messaggio.generazione_token deve essere valorizzato con il pattern \'integrity02-auth01\' |
| api_modi_rest_a01_audit01_schemaNonValido | sicurezza_messaggio.schema_audit specificato \'non-esistente\' con pattern di audit \'audit-rest01\' non valido: lo schema indicato non risultato configurato |
| api_modi_rest_a01_audit01_nonApplicabile | sicurezza_messaggio.informazioni_utente specificato con applicabilità risposta |
| api_modi_rest_a01_audit02_tokenLocale | sicurezza_messaggio.informazioni_utente con audit-pattern \'audit-rest02\' richiede la generazione di un token oauth o pdnd |
| api_modi_soap_a01_audit01_schemaNonValido | sicurezza_messaggio.schema_audit specificato \'non-esistente\' con pattern di audit \'audit-rest01\' non valido: lo schema indicato non risultato configurato |
| api_modi_soap_a01_audit01_schemaNonApplicabile | sicurezza_messaggio.informazioni_utente specificato con applicabilità risposta |
| api_modi_soap_a01_audit02_tokenLocale | sicurezza_messaggio.informazioni_utente con audit-pattern \'audit-rest02\' richiede la generazione di un token oauth o pdnd |
