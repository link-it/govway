Feature: Creazione Api

Background:

* call read('classpath:crud_commons.feature')

* def api_modi = read('api_modi_soap.json')
* eval randomize(api_modi, ["nome"])

* def api_modipa = read('api_modi_soap.json')
* eval api_modipa.nome=api_modipa.nome+'MODIPA'
* eval randomize(api_modipa, ["nome"])

* def api_no_modi = read('api_modi_error_no_modi.json')
* eval randomize(api_no_modi, ["nome"])
* remove api_no_modi.modi

* def api_soap_con_header_rest = read('api_modi_error_soap_header_rest.json')
* eval randomize(api_soap_con_header_rest, ["nome"])

* def api_soap_con_firma_allegati_nointegrity = read('api_modi_error_soap_firma_allegati_nointegrity.json')
* eval randomize(api_soap_con_firma_allegati_nointegrity, ["nome"])

* def api_soap_con_applicabilita_custom = read('api_modi_error_soap_applicabilita_custom.json')
* eval randomize(api_soap_con_applicabilita_custom, ["nome"])

* def api_soap_con_informazioni_utente_nointegrity_richiesta = read('api_modi_error_soap_informazioni_utente_nointegrity_richiesta.json')
* eval randomize(api_soap_con_informazioni_utente_nointegrity_richiesta, ["nome"])

* def api_soap_con_digest_richiesta_nointegrity_qualsiasi = read('api_modi_error_soap_digest_richiesta_nointegrity_qualsiasi.json')
* eval randomize(api_soap_con_digest_richiesta_nointegrity_qualsiasi, ["nome"])

* def api_rest_con_soap_firma_allegati = read('api_modi_error_soap_firma_allegati.json')
* eval randomize(api_rest_con_soap_firma_allegati, ["nome"])

* def api_rest_con_digest_richiesta = read('api_modi_error_digest_richiesta.json')
* eval randomize(api_rest_con_digest_richiesta, ["nome"])

* def api_rest_con_informazioni_utente = read('api_modi_error_informazioni_utente.json')
* eval randomize(api_rest_con_informazioni_utente, ["nome"])

* def api_rest_con_sicurezza_msg_senza_header = read('api_modi_error_rest_con_sicurezza_msg_senza_header.json')
* eval randomize(api_rest_con_sicurezza_msg_senza_header, ["nome"])

* def api_rest_applicabilita_custom_senza_elem = read('api_modi_error_rest_applicabilita_custom_senza_elem.json')
* eval randomize(api_rest_applicabilita_custom_senza_elem, ["nome"])

* def api_rest_applicabilita_richiesta_custom_senza_ct = read('api_rest_applicabilita_richiesta_custom_senza_ct.json')
* eval randomize(api_rest_applicabilita_richiesta_custom_senza_ct, ["nome"])

* def api_rest_applicabilita_risposta_custom_senza_ct = read('api_rest_applicabilita_risposta_custom_senza_ct.json')
* eval randomize(api_rest_applicabilita_risposta_custom_senza_ct, ["nome"])

* def api_rest_applicabilita_risposta_custom_senza_response_code = read('api_rest_applicabilita_risposta_custom_senza_response_code.json')
* eval randomize(api_rest_applicabilita_risposta_custom_senza_response_code, ["nome"])

* def api_rest_applicabilita_richiesta_abilitato_con_ct = read('api_rest_applicabilita_richiesta_abilitato_con_ct.json')
* eval randomize(api_rest_applicabilita_richiesta_abilitato_con_ct, ["nome"])

* def api_rest_applicabilita_risposta_abilitato_con_ct = read('api_rest_applicabilita_risposta_abilitato_con_ct.json')
* eval randomize(api_rest_applicabilita_risposta_abilitato_con_ct, ["nome"])

* def api_rest_applicabilita_risposta_abilitato_con_response_code = read('api_rest_applicabilita_risposta_abilitato_con_response_code.json')
* eval randomize(api_rest_applicabilita_risposta_abilitato_con_response_code, ["nome"])


* def query_param_profilo_modi = {'profilo': 'ModI'}
* def query_param_profilo_modipa = {'profilo': 'ModIPA'}


* def getExpected =
"""
function(modi) {
var expected = modi;
expected.sicurezza_messaggio.informazioni_utente = expected.sicurezza_messaggio.informazioni_utente != null ? expected.sicurezza_messaggio.informazioni_utente == 'true' : false
expected.sicurezza_messaggio.soap_firma_allegati = expected.sicurezza_messaggio.soap_firma_allegati != null ? expected.sicurezza_messaggio.soap_firma_allegati == 'true' : false
expected.sicurezza_messaggio.digest_richiesta = expected.sicurezza_messaggio.digest_richiesta != null ? expected.sicurezza_messaggio.digest_richiesta == 'true' : false
return expected;
} 
"""
@Create204_modipa
Scenario: Api Create 204 con profilo ModIPA
    * call create ( { resourcePath: 'api', body: api_modipa, key: api_modipa.nome + '/' + api_modipa.versione, query_params: query_param_profilo_modipa } )
	* call get ( { resourcePath: 'api', key: api_modipa.nome + '/' + api_modipa.versione  + '/modi'})
	* def expected = getExpected(api_modipa.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modipa.nome + '/' + api_modipa.versione })
    

@Create204_modi
Scenario: Api Create 204 con profilo ModI
    * call create ( { resourcePath: 'api', body: api_modi, key: api_modi.nome + '/' + api_modi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi.nome + '/' + api_modi.versione  + '/modi'})
	* def expected = getExpected(api_modi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi.nome + '/' + api_modi.versione })

@Create409_modi
Scenario: Api Create 409 con profilo ModI
    * call create_409 ( { resourcePath: 'api', body: api_modi, key: api_modi.nome + '/' + api_modi.versione, query_params: query_param_profilo_modi } )



## ERRORS 

### General
 
@Create400_no_modi
Scenario: Api Create 400 con profilo ModI e mancanza dell'elemento modi nel body
    * call create_400 ( { resourcePath: 'api', body: api_no_modi, key: api_no_modi.nome + '/' + api_no_modi.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'Specificare la configurazione \'ModI\''

### SOAP
@Create400_api_soap_con_header_rest
Scenario: Api Create 400 con tipo SOAP e presenza di header_rest
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_header_rest, key: api_soap_con_header_rest.nome + '/' + api_soap_con_header_rest.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP'

@Create400_api_soap_con_firma_allegati_nointegrity
Scenario: Api Create 400 con tipo SOAP e presenza di firma_allegati ma pattern non integrity_*
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_firma_allegati_nointegrity, key: api_soap_con_firma_allegati_nointegrity.nome + '/' + api_soap_con_firma_allegati_nointegrity.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.soap_firma_allegati specificato con pattern auth01'

@Create400_api_soap_con_applicabilita_custom
Scenario: Api Create 400 con tipo SOAP e applicabilita custom
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_applicabilita_custom, key: api_soap_con_applicabilita_custom.nome + '/' + api_soap_con_applicabilita_custom.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP'

@Create400_api_soap_con_informazioni_utente_nointegrity_richiesta
Scenario: Api Create 400 con tipo SOAP e presenza di informazioni utente ma pattern non integrity_* o applicabilita risposta
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_informazioni_utente_nointegrity_richiesta, key: api_soap_con_informazioni_utente_nointegrity_richiesta.nome + '/' + api_soap_con_informazioni_utente_nointegrity_richiesta.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.informazioni_utente specificato con pattern auth01 o applicabilita risposta'

@Create400_api_soap_con_digest_richiesta_nointegrity_qualsiasi
Scenario: Api Create 400 con tipo SOAP e presenza di digest richiesta ma pattern non integrity_* o applicabilita non entrambi
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_digest_richiesta_nointegrity_qualsiasi, key: api_soap_con_digest_richiesta_nointegrity_qualsiasi.nome + '/' + api_soap_con_digest_richiesta_nointegrity_qualsiasi.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.digest_richiesta specificato con pattern auth01 o applicabilita richiesta'

@Create400_api_soap_con_header_rest
Scenario: Api Create 400 con tipo SOAP e presenza di header_rest
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_header_rest, key: api_soap_con_header_rest.nome + '/' + api_soap_con_header_rest.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP'

@Create400_api_soap_con_header_rest
Scenario: Api Create 400 con tipo SOAP e presenza di header_rest
    * call create_400 ( { resourcePath: 'api', body: api_soap_con_header_rest, key: api_soap_con_header_rest.nome + '/' + api_soap_con_header_rest.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP'


### REST
@Create400_rest_con_soap_firma_allegati
Scenario: Api Create 400 con tipo REST e presenza di soap_firma_allegati
    * call create_400 ( { resourcePath: 'api', body: api_rest_con_soap_firma_allegati, key: api_rest_con_soap_firma_allegati.nome + '/' + api_rest_con_soap_firma_allegati.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST'

@Create400_rest_con_digest_richiesta
Scenario: Api Create 400 con tipo REST e presenza di digest_richiesta
    * call create_400 ( { resourcePath: 'api', body: api_rest_con_digest_richiesta, key: api_rest_con_digest_richiesta.nome + '/' + api_rest_con_digest_richiesta.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.digest_richiesta specificato con servizio di tipo REST'

@Create400_rest_con_informazioni_utente
Scenario: Api Create 400 con tipo REST e presenza di informazioni_utente
    * call create_400 ( { resourcePath: 'api', body: api_rest_con_informazioni_utente, key: api_rest_con_informazioni_utente.nome + '/' + api_rest_con_informazioni_utente.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.informazioni_utente specificato con servizio di tipo REST'

@Create400_api_rest_con_sicurezza_msg_senza_header
Scenario: Api Create 400 con tipo REST con sicurezza messaggio ma senza header
    * call create_400 ( { resourcePath: 'api', body: api_rest_con_sicurezza_msg_senza_header, key: api_rest_con_sicurezza_msg_senza_header.nome + '/' + api_rest_con_sicurezza_msg_senza_header.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern auth01'

@Create400_api_rest_applicabilita_custom_senza_elem
Scenario: Api Create 400 con tipo REST con applicabilita custom senza elemento applicabilita_custom
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_custom_senza_elem, key: api_rest_applicabilita_custom_senza_elem.nome + '/' + api_rest_applicabilita_custom_senza_elem.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita custom'

@Create400_api_rest_applicabilita_richiesta_custom_senza_ct
Scenario: Api Create 400 con tipo REST con applicabilita richesta custom senza content-type
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_richiesta_custom_senza_ct, key: api_rest_applicabilita_richiesta_custom_senza_ct.nome + '/' + api_rest_applicabilita_richiesta_custom_senza_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta custom'

@Create400_api_rest_applicabilita_risposta_custom_senza_ct
Scenario: Api Create 400 con tipo REST con applicabilita risposta custom senza content-type
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_risposta_custom_senza_ct, key: api_rest_applicabilita_risposta_custom_senza_ct.nome + '/' + api_rest_applicabilita_risposta_custom_senza_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom'

@Create400_api_rest_applicabilita_risposta_custom_senza_response_code
Scenario: Api Create 400 con tipo REST con applicabilita risposta custom senza response_code
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_risposta_custom_senza_ct, key: api_rest_applicabilita_risposta_custom_senza_ct.nome + '/' + api_rest_applicabilita_risposta_custom_senza_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta custom'

@Create400_api_rest_applicabilita_richiesta_abilitato_con_ct
Scenario: Api Create 400 con tipo REST con applicabilita richesta abilitato con content-type
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_richiesta_abilitato_con_ct, key: api_rest_applicabilita_richiesta_abilitato_con_ct.nome + '/' + api_rest_applicabilita_richiesta_abilitato_con_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta abilitato'

@Create400_api_rest_applicabilita_risposta_abilitato_con_ct
Scenario: Api Create 400 con tipo REST con applicabilita risposta abilitato con content-type
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_risposta_abilitato_con_ct, key: api_rest_applicabilita_risposta_abilitato_con_ct.nome + '/' + api_rest_applicabilita_risposta_abilitato_con_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato'

@Create400_api_rest_applicabilita_risposta_abilitato_con_response_code
Scenario: Api Create 400 con tipo REST con applicabilita risposta abilitato con response_code
    * call create_400 ( { resourcePath: 'api', body: api_rest_applicabilita_risposta_abilitato_con_ct, key: api_rest_applicabilita_risposta_abilitato_con_ct.nome + '/' + api_rest_applicabilita_risposta_abilitato_con_ct.versione, query_params: query_param_profilo_modi } )
    * match response.detail == 'sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta abilitato'


