Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}

* def getExpected =
"""
function(modi) {
var expected = modi;
expected.sicurezza_messaggio.informazioni_utente = expected.sicurezza_messaggio.informazioni_utente != null ? expected.sicurezza_messaggio.informazioni_utente : false
expected.sicurezza_messaggio.soap_firma_allegati = expected.sicurezza_messaggio.soap_firma_allegati != null ? expected.sicurezza_messaggio.soap_firma_allegati : false
expected.sicurezza_messaggio.digest_richiesta = expected.sicurezza_messaggio.digest_richiesta != null ? expected.sicurezza_messaggio.digest_richiesta : false
return expected;
} 
"""
@UpdateModi204
Scenario Outline: Api Update Interfaccia 204 <nome-test>

	* def api_modi_update = read('<nome-test>')
	* def api = read('api_modi_<tipo-test>.json')
	* eval randomize(api, ["nome"])
    * call create ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione, query_params: query_param_profilo_modi } )
    * call put ( { body: api_modi_update.modi, resourcePath: 'api/' + api.nome + '/' + api.versione + '/modi', query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api.nome + '/' + api.versione  + '/modi'})
	* def expected = getExpected(api_modi_update.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api.nome + '/' + api.versione })

Examples:
| nome-test | tipo-test |
| api_modi_rest_a01_custom_abilitato_entrambi.json | rest |
| api_modi_rest_a01_custom_entrambi_custom.json | rest |
| api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.json | rest |
| api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.json | rest |
| api_modi_rest_a01_qualsiasi_agid.json | rest |
| api_modi_rest_a01_qualsiasi_bearer.json | rest |
| api_modi_rest_a01_qualsiasi_agid_bearer.json | rest |
| api_modi_rest_a01_qualsiasi_agid_bearer_in_response.json | rest |
| api_modi_rest_a01_qualsiasi_custom.json | rest |
| api_modi_rest_a01_qualsiasi_custom_bearer.json | rest |
| api_modi_rest_a01_qualsiasi_custom_bearer_in_response.json | rest |
| api_modi_rest_a01_richiesta_agid.json | rest |
| api_modi_rest_a01_richiesta_bearer.json | rest |
| api_modi_rest_a01_risposta_agid.json | rest |
| api_modi_rest_a01_risposta_bearer.json | rest |
| api_modi_rest_a02_custom_abilitato_entrambi.json | rest |
| api_modi_rest_a02_custom_entrambi_custom.json | rest |
| api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.json | rest |
| api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.json | rest |
| api_modi_rest_a02_qualsiasi_agid.json | rest |
| api_modi_rest_a02_qualsiasi_bearer.json | rest |
| api_modi_rest_a02_richiesta_agid.json | rest |
| api_modi_rest_a02_richiesta_bearer.json | rest |
| api_modi_rest_a02_risposta_agid.json | rest |
| api_modi_rest_a02_risposta_bearer.json | rest |
| api_modi_rest_i01a01_qualsiasi_digest_info.json | rest |
| api_modi_rest_i01a01_qualsiasi_digest.json | rest |
| api_modi_rest_i01a01_qualsiasi_info.json | rest |
| api_modi_rest_i01a01_qualsiasi.json | rest |
| api_modi_rest_i01a01_richiesta_info.json | rest |
| api_modi_rest_i01a02_qualsiasi_digest_info.json | rest |
| api_modi_rest_i01a02_qualsiasi_digest.json | rest |
| api_modi_rest_i01a02_qualsiasi_info.json | rest |
| api_modi_rest_i01a02_qualsiasi.json | rest |
| api_modi_rest_i01a02_richiesta_info.json | rest |
| api_modi_soap_a01_qualsiasi.json | soap |
| api_modi_soap_a01_richiesta.json | soap |
| api_modi_soap_a01_risposta.json | soap |
| api_modi_soap_a02_qualsiasi.json | soap |
| api_modi_soap_a02_richiesta.json | soap |
| api_modi_soap_a02_risposta.json | soap |
| api_modi_soap_i01a01_qualsiasi_digest_info_utente.json | soap |
| api_modi_soap_i01a01_qualsiasi_digest.json | soap |
| api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.json | soap |
| api_modi_soap_i01a01_qualsiasi_firma_all_digest.json | soap |
| api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.json | soap |
| api_modi_soap_i01a01_qualsiasi_firma_all.json | soap |
| api_modi_soap_i01a01_qualsiasi_info_utente.json | soap |
| api_modi_soap_i01a01_qualsiasi.json | soap |
| api_modi_soap_i01a01_richiesta_firma_all_info_utente.json | soap |
| api_modi_soap_i01a01_richiesta_firma_all.json | soap |
| api_modi_soap_i01a01_richiesta_info_utente.json | soap |
| api_modi_soap_i01a01_richiesta.json | soap |
| api_modi_soap_i01a01_risposta_firma_all.json | soap |
| api_modi_soap_i01a01_risposta.json | soap |
| api_modi_soap_i01a02_qualsiasi_digest_info_utente.json | soap |
| api_modi_soap_i01a02_qualsiasi_digest.json | soap |
| api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.json | soap |
| api_modi_soap_i01a02_qualsiasi_firma_all_digest.json | soap |
| api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.json | soap |
| api_modi_soap_i01a02_qualsiasi_firma_all.json | soap |
| api_modi_soap_i01a02_qualsiasi_info_utente.json | soap |
| api_modi_soap_i01a02_qualsiasi.json | soap |
| api_modi_soap_i01a02_richiesta_firma_all_info_utente.json | soap |
| api_modi_soap_i01a02_richiesta_firma_all.json | soap |
| api_modi_soap_i01a02_richiesta_info_utente.json | soap |
| api_modi_soap_i01a02_richiesta.json | soap |
| api_modi_soap_i01a02_risposta_firma_all.json | soap |
| api_modi_soap_i01a02_risposta.json | soap |
