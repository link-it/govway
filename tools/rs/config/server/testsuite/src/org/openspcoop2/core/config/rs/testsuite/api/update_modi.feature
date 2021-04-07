Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_modi_soap.json')
* eval randomize(api, ["nome"])

* def api_rest = read('api_modi_rest.json')
* eval randomize(api_rest, ["nome"])

* def api_update = read('api_modi_soap_update.json')
* def api_modi_soap_a01_qualsiasi = read('api_modi_soap_a01_qualsiasi.json')

* def api_modi_soap_a01_richiesta = read('api_modi_soap_a01_richiesta.json')

* def api_modi_soap_a01_risposta = read('api_modi_soap_a01_risposta.json')

* def api_modi_soap_a02_qualsiasi = read('api_modi_soap_a02_qualsiasi.json')

* def api_modi_soap_a02_richiesta = read('api_modi_soap_a02_richiesta.json')

* def api_modi_soap_a02_risposta = read('api_modi_soap_a02_risposta.json')

* def api_modi_soap_i01a01_qualsiasi = read('api_modi_soap_i01a01_qualsiasi.json')

* def api_modi_soap_i01a01_qualsiasi_digest = read('api_modi_soap_i01a01_qualsiasi_digest.json')

* def api_modi_soap_i01a01_qualsiasi_digest_info_utente = read('api_modi_soap_i01a01_qualsiasi_digest_info_utente.json')

* def api_modi_soap_i01a01_qualsiasi_firma_all = read('api_modi_soap_i01a01_qualsiasi_firma_all.json')

* def api_modi_soap_i01a01_qualsiasi_firma_all_digest = read('api_modi_soap_i01a01_qualsiasi_firma_all_digest.json')

* def api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente = read('api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.json')

* def api_modi_soap_i01a01_qualsiasi_firma_all_info_utente = read('api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.json')

* def api_modi_soap_i01a01_qualsiasi_info_utente = read('api_modi_soap_i01a01_qualsiasi_info_utente.json')

* def api_modi_soap_i01a01_richiesta = read('api_modi_soap_i01a01_richiesta.json')

* def api_modi_soap_i01a01_richiesta_firma_all = read('api_modi_soap_i01a01_richiesta_firma_all.json')

* def api_modi_soap_i01a01_richiesta_firma_all_info_utente = read('api_modi_soap_i01a01_richiesta_firma_all_info_utente.json')

* def api_modi_soap_i01a01_richiesta_info_utente = read('api_modi_soap_i01a01_richiesta_info_utente.json')

* def api_modi_soap_i01a01_risposta = read('api_modi_soap_i01a01_risposta.json')

* def api_modi_soap_i01a01_risposta_firma_all = read('api_modi_soap_i01a01_risposta_firma_all.json')

* def api_modi_soap_i01a02_qualsiasi = read('api_modi_soap_i01a02_qualsiasi.json')

* def api_modi_soap_i01a02_qualsiasi_digest = read('api_modi_soap_i01a02_qualsiasi_digest.json')

* def api_modi_soap_i01a02_qualsiasi_digest_info_utente = read('api_modi_soap_i01a02_qualsiasi_digest_info_utente.json')

* def api_modi_soap_i01a02_qualsiasi_firma_all = read('api_modi_soap_i01a02_qualsiasi_firma_all.json')

* def api_modi_soap_i01a02_qualsiasi_firma_all_digest = read('api_modi_soap_i01a02_qualsiasi_firma_all_digest.json')

* def api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente = read('api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.json')

* def api_modi_soap_i01a02_qualsiasi_firma_all_info_utente = read('api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.json')

* def api_modi_soap_i01a02_qualsiasi_info_utente = read('api_modi_soap_i01a02_qualsiasi_info_utente.json')

* def api_modi_soap_i01a02_richiesta = read('api_modi_soap_i01a02_richiesta.json')

* def api_modi_soap_i01a02_richiesta_firma_all = read('api_modi_soap_i01a02_richiesta_firma_all.json')

* def api_modi_soap_i01a02_richiesta_firma_all_info_utente = read('api_modi_soap_i01a02_richiesta_firma_all_info_utente.json')

* def api_modi_soap_i01a02_richiesta_info_utente = read('api_modi_soap_i01a02_richiesta_info_utente.json')

* def api_modi_soap_i01a02_risposta = read('api_modi_soap_i01a02_risposta.json')

* def api_modi_soap_i01a02_risposta_firma_all = read('api_modi_soap_i01a02_risposta_firma_all.json')

* def api_modi_rest_a01_custom_abilitato_entrambi = read('api_modi_rest_a01_custom_abilitato_entrambi.json')

* def api_modi_rest_a01_custom_entrambi_custom = read('api_modi_rest_a01_custom_entrambi_custom.json')

* def api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato = read('api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.json')

* def api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato = read('api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.json')

* def api_modi_rest_a01_qualsiasi_agid = read('api_modi_rest_a01_qualsiasi_agid.json')

* def api_modi_rest_a01_qualsiasi_bearer = read('api_modi_rest_a01_qualsiasi_bearer.json')

* def api_modi_rest_a01_richiesta_agid = read('api_modi_rest_a01_richiesta_agid.json')

* def api_modi_rest_a01_richiesta_bearer = read('api_modi_rest_a01_richiesta_bearer.json')

* def api_modi_rest_a01_risposta_agid = read('api_modi_rest_a01_risposta_agid.json')

* def api_modi_rest_a01_risposta_bearer = read('api_modi_rest_a01_risposta_bearer.json')

* def api_modi_rest_a02_custom_abilitato_entrambi = read('api_modi_rest_a02_custom_abilitato_entrambi.json')

* def api_modi_rest_a02_custom_entrambi_custom = read('api_modi_rest_a02_custom_entrambi_custom.json')

* def api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato = read('api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.json')

* def api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato = read('api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.json')

* def api_modi_rest_a02_qualsiasi_agid = read('api_modi_rest_a02_qualsiasi_agid.json')

* def api_modi_rest_a02_qualsiasi_bearer = read('api_modi_rest_a02_qualsiasi_bearer.json')

* def api_modi_rest_a02_richiesta_agid = read('api_modi_rest_a02_richiesta_agid.json')

* def api_modi_rest_a02_richiesta_bearer = read('api_modi_rest_a02_richiesta_bearer.json')

* def api_modi_rest_a02_risposta_agid = read('api_modi_rest_a02_risposta_agid.json')

* def api_modi_rest_a02_risposta_bearer = read('api_modi_rest_a02_risposta_bearer.json')

* def api_modi_rest_i01a01_qualsiasi = read('api_modi_rest_i01a01_qualsiasi.json')

* def api_modi_rest_i01a01_qualsiasi_digest = read('api_modi_rest_i01a01_qualsiasi_digest.json')

* def api_modi_rest_i01a01_qualsiasi_digest_info = read('api_modi_rest_i01a01_qualsiasi_digest_info.json')

* def api_modi_rest_i01a01_qualsiasi_info = read('api_modi_rest_i01a01_qualsiasi_info.json')

* def api_modi_rest_i01a01_richiesta_info = read('api_modi_rest_i01a01_richiesta_info.json')

* def api_modi_rest_i01a02_qualsiasi = read('api_modi_rest_i01a02_qualsiasi.json')

* def api_modi_rest_i01a02_qualsiasi_digest = read('api_modi_rest_i01a02_qualsiasi_digest.json')

* def api_modi_rest_i01a02_qualsiasi_digest_info = read('api_modi_rest_i01a02_qualsiasi_digest_info.json')

* def api_modi_rest_i01a02_qualsiasi_info = read('api_modi_rest_i01a02_qualsiasi_info.json')

* def api_modi_rest_i01a02_richiesta_info = read('api_modi_rest_i01a02_richiesta_info.json')

* def query_param_profilo_modi = {'profilo': 'ModI'}
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
@UpdateModi204_api_modi_rest_a01_custom_abilitato_entrambi
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_custom_abilitato_entrambi

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_custom_abilitato_entrambi.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_abilitato_entrambi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_custom_entrambi_custom
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_custom_entrambi_custom

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_custom_entrambi_custom.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_entrambi_custom.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_qualsiasi_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_qualsiasi_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_qualsiasi_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_qualsiasi_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_qualsiasi_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_qualsiasi_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_qualsiasi_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_qualsiasi_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_richiesta_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_richiesta_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_richiesta_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_richiesta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_richiesta_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_richiesta_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_richiesta_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_richiesta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_risposta_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_risposta_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_risposta_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_risposta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a01_risposta_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a01_risposta_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a01_risposta_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_risposta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_custom_abilitato_entrambi
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_custom_abilitato_entrambi

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_custom_abilitato_entrambi.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_abilitato_entrambi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_custom_entrambi_custom
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_custom_entrambi_custom

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_custom_entrambi_custom.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_entrambi_custom.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_qualsiasi_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_qualsiasi_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_qualsiasi_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_qualsiasi_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_qualsiasi_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_qualsiasi_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_qualsiasi_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_qualsiasi_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_richiesta_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_richiesta_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_richiesta_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_richiesta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_richiesta_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_richiesta_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_richiesta_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_richiesta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_risposta_agid
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_risposta_agid

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_risposta_agid.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_risposta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_a02_risposta_bearer
Scenario: Api Update Interfaccia 204 api_modi_rest_a02_risposta_bearer

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_a02_risposta_bearer.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_risposta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a01_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a01_qualsiasi

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a01_qualsiasi.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a01_qualsiasi_digest
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a01_qualsiasi_digest

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a01_qualsiasi_digest.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a01_qualsiasi_digest_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a01_qualsiasi_digest_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a01_qualsiasi_digest_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_digest_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a01_qualsiasi_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a01_qualsiasi_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a01_qualsiasi_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a01_richiesta_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a01_richiesta_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a01_richiesta_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_richiesta_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a02_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a02_qualsiasi

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a02_qualsiasi.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a02_qualsiasi_digest
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a02_qualsiasi_digest

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a02_qualsiasi_digest.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a02_qualsiasi_digest_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a02_qualsiasi_digest_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a02_qualsiasi_digest_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_digest_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a02_qualsiasi_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a02_qualsiasi_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a02_qualsiasi_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_modi_rest_i01a02_richiesta_info
Scenario: Api Update Interfaccia 204 api_modi_rest_i01a02_richiesta_info

    * call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_rest_i01a02_richiesta_info.modi, resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_richiesta_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })

@UpdateModi204_api_update
Scenario: Api Update Interfaccia 204 api_update

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_update, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_update)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

	@UpdateModi204_api_modi_soap_a01_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_soap_a01_qualsiasi

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a01_qualsiasi.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_a01_richiesta
Scenario: Api Update Interfaccia 204 api_modi_soap_a01_richiesta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a01_richiesta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_a01_risposta
Scenario: Api Update Interfaccia 204 api_modi_soap_a01_risposta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a01_risposta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_a02_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_soap_a02_qualsiasi

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a02_qualsiasi.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_a02_richiesta
Scenario: Api Update Interfaccia 204 api_modi_soap_a02_richiesta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a02_richiesta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_a02_risposta
Scenario: Api Update Interfaccia 204 api_modi_soap_a02_risposta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_a02_risposta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_digest
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_digest

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_digest.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_digest_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_digest_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_digest_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_firma_all_digest
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_firma_all_digest

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_firma_all_digest.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_firma_all_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_firma_all_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_qualsiasi_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_qualsiasi_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_qualsiasi_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_richiesta
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_richiesta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_richiesta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_richiesta_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_richiesta_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_richiesta_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_richiesta_firma_all_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_richiesta_firma_all_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_richiesta_firma_all_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_richiesta_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_richiesta_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_richiesta_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_risposta
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_risposta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_risposta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a01_risposta_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a01_risposta_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a01_risposta_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_risposta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_digest
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_digest

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_digest.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_digest_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_digest_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_digest_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_firma_all_digest
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_firma_all_digest

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_firma_all_digest.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_firma_all_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_firma_all_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_qualsiasi_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_qualsiasi_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_qualsiasi_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_richiesta
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_richiesta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_richiesta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_richiesta_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_richiesta_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_richiesta_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_richiesta_firma_all_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_richiesta_firma_all_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_richiesta_firma_all_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_richiesta_info_utente
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_richiesta_info_utente

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_richiesta_info_utente.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_risposta
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_risposta

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_risposta.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

@UpdateModi204_api_modi_soap_i01a02_risposta_firma_all
Scenario: Api Update Interfaccia 204 api_modi_soap_i01a02_risposta_firma_all

    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_soap_i01a02_risposta_firma_all.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_risposta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })
	