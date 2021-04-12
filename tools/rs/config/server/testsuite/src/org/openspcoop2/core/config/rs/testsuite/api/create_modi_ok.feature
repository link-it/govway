Feature: Creazione Api

Background:

* call read('classpath:crud_commons.feature')

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
	* def api_modipa = read('api_modi_soap.json')
	* eval randomize(api_modipa, ["nome"])
    * call create ( { resourcePath: 'api', body: api_modipa, key: api_modipa.nome + '/' + api_modipa.versione, query_params: query_param_profilo_modipa } )
	* call get ( { resourcePath: 'api', key: api_modipa.nome + '/' + api_modipa.versione  + '/modi'})
	* def expected = getExpected(api_modipa.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modipa.nome + '/' + api_modipa.versione })

@Create204_modi
Scenario Outline: Api Create 204 con profilo ModI <nome-test>
	* def api_modi = read('<nome-test>')
	* eval randomize(api_modi, ["nome"])
    * call create ( { resourcePath: 'api', body: api_modi, key: api_modi.nome + '/' + api_modi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi.nome + '/' + api_modi.versione  + '/modi'})
	* def expected = getExpected(api_modi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi.nome + '/' + api_modi.versione })


Examples:
| nome-test |
| api_modi_rest_a01_custom_abilitato_entrambi.json |
| api_modi_rest_a01_custom_entrambi_custom.json |
| api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.json |
| api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.json |
| api_modi_rest_a01_qualsiasi_agid.json |
| api_modi_rest_a01_qualsiasi_bearer.json |
| api_modi_rest_a01_richiesta_agid.json |
| api_modi_rest_a01_richiesta_bearer.json |
| api_modi_rest_a01_risposta_agid.json |
| api_modi_rest_a01_risposta_bearer.json |
| api_modi_rest_a02_custom_abilitato_entrambi.json |
| api_modi_rest_a02_custom_entrambi_custom.json |
| api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.json |
| api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.json |
| api_modi_rest_a02_qualsiasi_agid.json |
| api_modi_rest_a02_qualsiasi_bearer.json |
| api_modi_rest_a02_richiesta_agid.json |
| api_modi_rest_a02_richiesta_bearer.json |
| api_modi_rest_a02_risposta_agid.json |
| api_modi_rest_a02_risposta_bearer.json |
| api_modi_rest_i01a01_qualsiasi_digest_info.json |
| api_modi_rest_i01a01_qualsiasi_digest.json |
| api_modi_rest_i01a01_qualsiasi_info.json |
| api_modi_rest_i01a01_qualsiasi.json |
| api_modi_rest_i01a01_richiesta_info.json |
| api_modi_rest_i01a02_qualsiasi_digest_info.json |
| api_modi_rest_i01a02_qualsiasi_digest.json |
| api_modi_rest_i01a02_qualsiasi_info.json |
| api_modi_rest_i01a02_qualsiasi.json |
| api_modi_rest_i01a02_richiesta_info.json |
| api_modi_rest.json |
| api_modi_soap_a01_qualsiasi.json |
| api_modi_soap_a01_richiesta.json |
| api_modi_soap_a01_risposta.json |
| api_modi_soap_a02_qualsiasi.json |
| api_modi_soap_a02_richiesta.json |
| api_modi_soap_a02_risposta.json |
| api_modi_soap_i01a01_qualsiasi_digest_info_utente.json |
| api_modi_soap_i01a01_qualsiasi_digest.json |
| api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.json |
| api_modi_soap_i01a01_qualsiasi_firma_all_digest.json |
| api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.json |
| api_modi_soap_i01a01_qualsiasi_firma_all.json |
| api_modi_soap_i01a01_qualsiasi_info_utente.json |
| api_modi_soap_i01a01_qualsiasi.json |
| api_modi_soap_i01a01_richiesta_firma_all_info_utente.json |
| api_modi_soap_i01a01_richiesta_firma_all.json |
| api_modi_soap_i01a01_richiesta_info_utente.json |
| api_modi_soap_i01a01_richiesta.json |
| api_modi_soap_i01a01_risposta_firma_all.json |
| api_modi_soap_i01a01_risposta.json |
| api_modi_soap_i01a02_qualsiasi_digest_info_utente.json |
| api_modi_soap_i01a02_qualsiasi_digest.json |
| api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.json |
| api_modi_soap_i01a02_qualsiasi_firma_all_digest.json |
| api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.json |
| api_modi_soap_i01a02_qualsiasi_firma_all.json |
| api_modi_soap_i01a02_qualsiasi_info_utente.json |
| api_modi_soap_i01a02_qualsiasi.json |
| api_modi_soap_i01a02_richiesta_firma_all_info_utente.json |
| api_modi_soap_i01a02_richiesta_firma_all.json |
| api_modi_soap_i01a02_richiesta_info_utente.json |
| api_modi_soap_i01a02_richiesta.json |
| api_modi_soap_i01a02_risposta_firma_all.json |
| api_modi_soap_i01a02_risposta.json |