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

* def api_modi_rest_a01_custom_abilitato_entrambi = read('api_modi_rest_a01_custom_abilitato_entrambi.json')
* eval randomize(api_modi_rest_a01_custom_abilitato_entrambi, ["nome"])


* def api_modi_rest_a01_custom_entrambi_custom = read('api_modi_rest_a01_custom_entrambi_custom.json')
* eval randomize(api_modi_rest_a01_custom_entrambi_custom, ["nome"])


* def api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato = read('api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.json')
* eval randomize(api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato, ["nome"])


* def api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato = read('api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.json')
* eval randomize(api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato, ["nome"])


* def api_modi_rest_a01_qualsiasi_agid = read('api_modi_rest_a01_qualsiasi_agid.json')
* eval randomize(api_modi_rest_a01_qualsiasi_agid, ["nome"])


* def api_modi_rest_a01_qualsiasi_bearer = read('api_modi_rest_a01_qualsiasi_bearer.json')
* eval randomize(api_modi_rest_a01_qualsiasi_bearer, ["nome"])


* def api_modi_rest_a01_richiesta_agid = read('api_modi_rest_a01_richiesta_agid.json')
* eval randomize(api_modi_rest_a01_richiesta_agid, ["nome"])


* def api_modi_rest_a01_richiesta_bearer = read('api_modi_rest_a01_richiesta_bearer.json')
* eval randomize(api_modi_rest_a01_richiesta_bearer, ["nome"])


* def api_modi_rest_a01_risposta_agid = read('api_modi_rest_a01_risposta_agid.json')
* eval randomize(api_modi_rest_a01_risposta_agid, ["nome"])


* def api_modi_rest_a01_risposta_bearer = read('api_modi_rest_a01_risposta_bearer.json')
* eval randomize(api_modi_rest_a01_risposta_bearer, ["nome"])


* def api_modi_rest_a02_custom_abilitato_entrambi = read('api_modi_rest_a02_custom_abilitato_entrambi.json')
* eval randomize(api_modi_rest_a02_custom_abilitato_entrambi, ["nome"])


* def api_modi_rest_a02_custom_entrambi_custom = read('api_modi_rest_a02_custom_entrambi_custom.json')
* eval randomize(api_modi_rest_a02_custom_entrambi_custom, ["nome"])


* def api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato = read('api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.json')
* eval randomize(api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato, ["nome"])


* def api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato = read('api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.json')
* eval randomize(api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato, ["nome"])


* def api_modi_rest_a02_qualsiasi_agid = read('api_modi_rest_a02_qualsiasi_agid.json')
* eval randomize(api_modi_rest_a02_qualsiasi_agid, ["nome"])


* def api_modi_rest_a02_qualsiasi_bearer = read('api_modi_rest_a02_qualsiasi_bearer.json')
* eval randomize(api_modi_rest_a02_qualsiasi_bearer, ["nome"])


* def api_modi_rest_a02_richiesta_agid = read('api_modi_rest_a02_richiesta_agid.json')
* eval randomize(api_modi_rest_a02_richiesta_agid, ["nome"])


* def api_modi_rest_a02_richiesta_bearer = read('api_modi_rest_a02_richiesta_bearer.json')
* eval randomize(api_modi_rest_a02_richiesta_bearer, ["nome"])


* def api_modi_rest_a02_risposta_agid = read('api_modi_rest_a02_risposta_agid.json')
* eval randomize(api_modi_rest_a02_risposta_agid, ["nome"])


* def api_modi_rest_a02_risposta_bearer = read('api_modi_rest_a02_risposta_bearer.json')
* eval randomize(api_modi_rest_a02_risposta_bearer, ["nome"])


* def api_modi_rest_i01a01_qualsiasi = read('api_modi_rest_i01a01_qualsiasi.json')
* eval randomize(api_modi_rest_i01a01_qualsiasi, ["nome"])


* def api_modi_rest_i01a01_qualsiasi_digest = read('api_modi_rest_i01a01_qualsiasi_digest.json')
* eval randomize(api_modi_rest_i01a01_qualsiasi_digest, ["nome"])


* def api_modi_rest_i01a01_qualsiasi_digest_info = read('api_modi_rest_i01a01_qualsiasi_digest_info.json')
* eval randomize(api_modi_rest_i01a01_qualsiasi_digest_info, ["nome"])


* def api_modi_rest_i01a01_qualsiasi_info = read('api_modi_rest_i01a01_qualsiasi_info.json')
* eval randomize(api_modi_rest_i01a01_qualsiasi_info, ["nome"])


* def api_modi_rest_i01a01_richiesta_info = read('api_modi_rest_i01a01_richiesta_info.json')
* eval randomize(api_modi_rest_i01a01_richiesta_info, ["nome"])


* def api_modi_rest_i01a02_qualsiasi = read('api_modi_rest_i01a02_qualsiasi.json')
* eval randomize(api_modi_rest_i01a02_qualsiasi, ["nome"])


* def api_modi_rest_i01a02_qualsiasi_digest = read('api_modi_rest_i01a02_qualsiasi_digest.json')
* eval randomize(api_modi_rest_i01a02_qualsiasi_digest, ["nome"])


* def api_modi_rest_i01a02_qualsiasi_digest_info = read('api_modi_rest_i01a02_qualsiasi_digest_info.json')
* eval randomize(api_modi_rest_i01a02_qualsiasi_digest_info, ["nome"])


* def api_modi_rest_i01a02_qualsiasi_info = read('api_modi_rest_i01a02_qualsiasi_info.json')
* eval randomize(api_modi_rest_i01a02_qualsiasi_info, ["nome"])


* def api_modi_rest_i01a02_richiesta_info = read('api_modi_rest_i01a02_richiesta_info.json')
* eval randomize(api_modi_rest_i01a02_richiesta_info, ["nome"])


* def api_modi_soap_a01_qualsiasi = read('api_modi_soap_a01_qualsiasi.json')
* eval randomize(api_modi_soap_a01_qualsiasi, ["nome"])


* def api_modi_soap_a01_richiesta = read('api_modi_soap_a01_richiesta.json')
* eval randomize(api_modi_soap_a01_richiesta, ["nome"])


* def api_modi_soap_a01_risposta = read('api_modi_soap_a01_risposta.json')
* eval randomize(api_modi_soap_a01_risposta, ["nome"])


* def api_modi_soap_a02_qualsiasi = read('api_modi_soap_a02_qualsiasi.json')
* eval randomize(api_modi_soap_a02_qualsiasi, ["nome"])


* def api_modi_soap_a02_richiesta = read('api_modi_soap_a02_richiesta.json')
* eval randomize(api_modi_soap_a02_richiesta, ["nome"])


* def api_modi_soap_a02_risposta = read('api_modi_soap_a02_risposta.json')
* eval randomize(api_modi_soap_a02_risposta, ["nome"])


* def api_modi_soap_i01a01_qualsiasi = read('api_modi_soap_i01a01_qualsiasi.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_digest = read('api_modi_soap_i01a01_qualsiasi_digest.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_digest, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_digest_info_utente = read('api_modi_soap_i01a01_qualsiasi_digest_info_utente.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_digest_info_utente, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_firma_all = read('api_modi_soap_i01a01_qualsiasi_firma_all.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_firma_all, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_firma_all_digest = read('api_modi_soap_i01a01_qualsiasi_firma_all_digest.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_firma_all_digest, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente = read('api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_firma_all_info_utente = read('api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_firma_all_info_utente, ["nome"])


* def api_modi_soap_i01a01_qualsiasi_info_utente = read('api_modi_soap_i01a01_qualsiasi_info_utente.json')
* eval randomize(api_modi_soap_i01a01_qualsiasi_info_utente, ["nome"])


* def api_modi_soap_i01a01_richiesta = read('api_modi_soap_i01a01_richiesta.json')
* eval randomize(api_modi_soap_i01a01_richiesta, ["nome"])


* def api_modi_soap_i01a01_richiesta_firma_all = read('api_modi_soap_i01a01_richiesta_firma_all.json')
* eval randomize(api_modi_soap_i01a01_richiesta_firma_all, ["nome"])


* def api_modi_soap_i01a01_richiesta_firma_all_info_utente = read('api_modi_soap_i01a01_richiesta_firma_all_info_utente.json')
* eval randomize(api_modi_soap_i01a01_richiesta_firma_all_info_utente, ["nome"])


* def api_modi_soap_i01a01_richiesta_info_utente = read('api_modi_soap_i01a01_richiesta_info_utente.json')
* eval randomize(api_modi_soap_i01a01_richiesta_info_utente, ["nome"])


* def api_modi_soap_i01a01_risposta = read('api_modi_soap_i01a01_risposta.json')
* eval randomize(api_modi_soap_i01a01_risposta, ["nome"])


* def api_modi_soap_i01a01_risposta_firma_all = read('api_modi_soap_i01a01_risposta_firma_all.json')
* eval randomize(api_modi_soap_i01a01_risposta_firma_all, ["nome"])


* def api_modi_soap_i01a02_qualsiasi = read('api_modi_soap_i01a02_qualsiasi.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_digest = read('api_modi_soap_i01a02_qualsiasi_digest.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_digest, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_digest_info_utente = read('api_modi_soap_i01a02_qualsiasi_digest_info_utente.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_digest_info_utente, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_firma_all = read('api_modi_soap_i01a02_qualsiasi_firma_all.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_firma_all, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_firma_all_digest = read('api_modi_soap_i01a02_qualsiasi_firma_all_digest.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_firma_all_digest, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente = read('api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_firma_all_info_utente = read('api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_firma_all_info_utente, ["nome"])


* def api_modi_soap_i01a02_qualsiasi_info_utente = read('api_modi_soap_i01a02_qualsiasi_info_utente.json')
* eval randomize(api_modi_soap_i01a02_qualsiasi_info_utente, ["nome"])


* def api_modi_soap_i01a02_richiesta = read('api_modi_soap_i01a02_richiesta.json')
* eval randomize(api_modi_soap_i01a02_richiesta, ["nome"])


* def api_modi_soap_i01a02_richiesta_firma_all = read('api_modi_soap_i01a02_richiesta_firma_all.json')
* eval randomize(api_modi_soap_i01a02_richiesta_firma_all, ["nome"])


* def api_modi_soap_i01a02_richiesta_firma_all_info_utente = read('api_modi_soap_i01a02_richiesta_firma_all_info_utente.json')
* eval randomize(api_modi_soap_i01a02_richiesta_firma_all_info_utente, ["nome"])


* def api_modi_soap_i01a02_richiesta_info_utente = read('api_modi_soap_i01a02_richiesta_info_utente.json')
* eval randomize(api_modi_soap_i01a02_richiesta_info_utente, ["nome"])


* def api_modi_soap_i01a02_risposta = read('api_modi_soap_i01a02_risposta.json')
* eval randomize(api_modi_soap_i01a02_risposta, ["nome"])


* def api_modi_soap_i01a02_risposta_firma_all = read('api_modi_soap_i01a02_risposta_firma_all.json')
* eval randomize(api_modi_soap_i01a02_risposta_firma_all, ["nome"])


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

@Create204_api_modi_rest_a01_custom_abilitato_entrambi
Scenario: Api Create 204 con api_modi_rest_a01_custom_abilitato_entrambi
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_custom_abilitato_entrambi, key: api_modi_rest_a01_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a01_custom_abilitato_entrambi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a01_custom_abilitato_entrambi.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_abilitato_entrambi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a01_custom_abilitato_entrambi.versione })
@Create204_api_modi_rest_a01_custom_entrambi_custom
Scenario: Api Create 204 con api_modi_rest_a01_custom_entrambi_custom
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_custom_entrambi_custom, key: api_modi_rest_a01_custom_entrambi_custom.nome + '/' + api_modi_rest_a01_custom_entrambi_custom.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_custom_entrambi_custom.nome + '/' + api_modi_rest_a01_custom_entrambi_custom.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_entrambi_custom.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_custom_entrambi_custom.nome + '/' + api_modi_rest_a01_custom_entrambi_custom.versione })
@Create204_api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato
Scenario: Api Create 204 con api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato, key: api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a01_custom_ric_abilitato_ris_disabilitato.versione })
@Create204_api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato
Scenario: Api Create 204 con api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato, key: api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a01_custom_ric_disabilitato_ris_abilitato.versione })
@Create204_api_modi_rest_a01_qualsiasi_agid
Scenario: Api Create 204 con api_modi_rest_a01_qualsiasi_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_qualsiasi_agid, key: api_modi_rest_a01_qualsiasi_agid.nome + '/' + api_modi_rest_a01_qualsiasi_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_qualsiasi_agid.nome + '/' + api_modi_rest_a01_qualsiasi_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_qualsiasi_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_qualsiasi_agid.nome + '/' + api_modi_rest_a01_qualsiasi_agid.versione })
@Create204_api_modi_rest_a01_qualsiasi_bearer
Scenario: Api Create 204 con api_modi_rest_a01_qualsiasi_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_qualsiasi_bearer, key: api_modi_rest_a01_qualsiasi_bearer.nome + '/' + api_modi_rest_a01_qualsiasi_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_qualsiasi_bearer.nome + '/' + api_modi_rest_a01_qualsiasi_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_qualsiasi_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_qualsiasi_bearer.nome + '/' + api_modi_rest_a01_qualsiasi_bearer.versione })
@Create204_api_modi_rest_a01_richiesta_agid
Scenario: Api Create 204 con api_modi_rest_a01_richiesta_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_richiesta_agid, key: api_modi_rest_a01_richiesta_agid.nome + '/' + api_modi_rest_a01_richiesta_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_richiesta_agid.nome + '/' + api_modi_rest_a01_richiesta_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_richiesta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_richiesta_agid.nome + '/' + api_modi_rest_a01_richiesta_agid.versione })
@Create204_api_modi_rest_a01_richiesta_bearer
Scenario: Api Create 204 con api_modi_rest_a01_richiesta_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_richiesta_bearer, key: api_modi_rest_a01_richiesta_bearer.nome + '/' + api_modi_rest_a01_richiesta_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_richiesta_bearer.nome + '/' + api_modi_rest_a01_richiesta_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_richiesta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_richiesta_bearer.nome + '/' + api_modi_rest_a01_richiesta_bearer.versione })
@Create204_api_modi_rest_a01_risposta_agid
Scenario: Api Create 204 con api_modi_rest_a01_risposta_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_risposta_agid, key: api_modi_rest_a01_risposta_agid.nome + '/' + api_modi_rest_a01_risposta_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_risposta_agid.nome + '/' + api_modi_rest_a01_risposta_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_risposta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_risposta_agid.nome + '/' + api_modi_rest_a01_risposta_agid.versione })
@Create204_api_modi_rest_a01_risposta_bearer
Scenario: Api Create 204 con api_modi_rest_a01_risposta_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a01_risposta_bearer, key: api_modi_rest_a01_risposta_bearer.nome + '/' + api_modi_rest_a01_risposta_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a01_risposta_bearer.nome + '/' + api_modi_rest_a01_risposta_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a01_risposta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a01_risposta_bearer.nome + '/' + api_modi_rest_a01_risposta_bearer.versione })
@Create204_api_modi_rest_a02_custom_abilitato_entrambi
Scenario: Api Create 204 con api_modi_rest_a02_custom_abilitato_entrambi
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_custom_abilitato_entrambi, key: api_modi_rest_a02_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a02_custom_abilitato_entrambi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a02_custom_abilitato_entrambi.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_abilitato_entrambi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_custom_abilitato_entrambi.nome + '/' + api_modi_rest_a02_custom_abilitato_entrambi.versione })
@Create204_api_modi_rest_a02_custom_entrambi_custom
Scenario: Api Create 204 con api_modi_rest_a02_custom_entrambi_custom
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_custom_entrambi_custom, key: api_modi_rest_a02_custom_entrambi_custom.nome + '/' + api_modi_rest_a02_custom_entrambi_custom.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_custom_entrambi_custom.nome + '/' + api_modi_rest_a02_custom_entrambi_custom.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_entrambi_custom.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_custom_entrambi_custom.nome + '/' + api_modi_rest_a02_custom_entrambi_custom.versione })
@Create204_api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato
Scenario: Api Create 204 con api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato, key: api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.nome + '/' + api_modi_rest_a02_custom_ric_abilitato_ris_disabilitato.versione })
@Create204_api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato
Scenario: Api Create 204 con api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato, key: api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.nome + '/' + api_modi_rest_a02_custom_ric_disabilitato_ris_abilitato.versione })
@Create204_api_modi_rest_a02_qualsiasi_agid
Scenario: Api Create 204 con api_modi_rest_a02_qualsiasi_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_qualsiasi_agid, key: api_modi_rest_a02_qualsiasi_agid.nome + '/' + api_modi_rest_a02_qualsiasi_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_qualsiasi_agid.nome + '/' + api_modi_rest_a02_qualsiasi_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_qualsiasi_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_qualsiasi_agid.nome + '/' + api_modi_rest_a02_qualsiasi_agid.versione })
@Create204_api_modi_rest_a02_qualsiasi_bearer
Scenario: Api Create 204 con api_modi_rest_a02_qualsiasi_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_qualsiasi_bearer, key: api_modi_rest_a02_qualsiasi_bearer.nome + '/' + api_modi_rest_a02_qualsiasi_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_qualsiasi_bearer.nome + '/' + api_modi_rest_a02_qualsiasi_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_qualsiasi_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_qualsiasi_bearer.nome + '/' + api_modi_rest_a02_qualsiasi_bearer.versione })
@Create204_api_modi_rest_a02_richiesta_agid
Scenario: Api Create 204 con api_modi_rest_a02_richiesta_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_richiesta_agid, key: api_modi_rest_a02_richiesta_agid.nome + '/' + api_modi_rest_a02_richiesta_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_richiesta_agid.nome + '/' + api_modi_rest_a02_richiesta_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_richiesta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_richiesta_agid.nome + '/' + api_modi_rest_a02_richiesta_agid.versione })
@Create204_api_modi_rest_a02_richiesta_bearer
Scenario: Api Create 204 con api_modi_rest_a02_richiesta_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_richiesta_bearer, key: api_modi_rest_a02_richiesta_bearer.nome + '/' + api_modi_rest_a02_richiesta_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_richiesta_bearer.nome + '/' + api_modi_rest_a02_richiesta_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_richiesta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_richiesta_bearer.nome + '/' + api_modi_rest_a02_richiesta_bearer.versione })
@Create204_api_modi_rest_a02_risposta_agid
Scenario: Api Create 204 con api_modi_rest_a02_risposta_agid
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_risposta_agid, key: api_modi_rest_a02_risposta_agid.nome + '/' + api_modi_rest_a02_risposta_agid.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_risposta_agid.nome + '/' + api_modi_rest_a02_risposta_agid.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_risposta_agid.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_risposta_agid.nome + '/' + api_modi_rest_a02_risposta_agid.versione })
@Create204_api_modi_rest_a02_risposta_bearer
Scenario: Api Create 204 con api_modi_rest_a02_risposta_bearer
    * call create ( { resourcePath: 'api', body: api_modi_rest_a02_risposta_bearer, key: api_modi_rest_a02_risposta_bearer.nome + '/' + api_modi_rest_a02_risposta_bearer.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_a02_risposta_bearer.nome + '/' + api_modi_rest_a02_risposta_bearer.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_a02_risposta_bearer.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_a02_risposta_bearer.nome + '/' + api_modi_rest_a02_risposta_bearer.versione })
@Create204_api_modi_rest_i01a01_qualsiasi
Scenario: Api Create 204 con api_modi_rest_i01a01_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a01_qualsiasi, key: api_modi_rest_i01a01_qualsiasi.nome + '/' + api_modi_rest_i01a01_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a01_qualsiasi.nome + '/' + api_modi_rest_i01a01_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a01_qualsiasi.nome + '/' + api_modi_rest_i01a01_qualsiasi.versione })
@Create204_api_modi_rest_i01a01_qualsiasi_digest
Scenario: Api Create 204 con api_modi_rest_i01a01_qualsiasi_digest
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a01_qualsiasi_digest, key: api_modi_rest_i01a01_qualsiasi_digest.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a01_qualsiasi_digest.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a01_qualsiasi_digest.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest.versione })
@Create204_api_modi_rest_i01a01_qualsiasi_digest_info
Scenario: Api Create 204 con api_modi_rest_i01a01_qualsiasi_digest_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a01_qualsiasi_digest_info, key: api_modi_rest_i01a01_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a01_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_digest_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a01_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_digest_info.versione })
@Create204_api_modi_rest_i01a01_qualsiasi_info
Scenario: Api Create 204 con api_modi_rest_i01a01_qualsiasi_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a01_qualsiasi_info, key: api_modi_rest_i01a01_qualsiasi_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a01_qualsiasi_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_qualsiasi_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a01_qualsiasi_info.nome + '/' + api_modi_rest_i01a01_qualsiasi_info.versione })
@Create204_api_modi_rest_i01a01_richiesta_info
Scenario: Api Create 204 con api_modi_rest_i01a01_richiesta_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a01_richiesta_info, key: api_modi_rest_i01a01_richiesta_info.nome + '/' + api_modi_rest_i01a01_richiesta_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a01_richiesta_info.nome + '/' + api_modi_rest_i01a01_richiesta_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a01_richiesta_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a01_richiesta_info.nome + '/' + api_modi_rest_i01a01_richiesta_info.versione })
@Create204_api_modi_rest_i01a02_qualsiasi
Scenario: Api Create 204 con api_modi_rest_i01a02_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a02_qualsiasi, key: api_modi_rest_i01a02_qualsiasi.nome + '/' + api_modi_rest_i01a02_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a02_qualsiasi.nome + '/' + api_modi_rest_i01a02_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a02_qualsiasi.nome + '/' + api_modi_rest_i01a02_qualsiasi.versione })
@Create204_api_modi_rest_i01a02_qualsiasi_digest
Scenario: Api Create 204 con api_modi_rest_i01a02_qualsiasi_digest
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a02_qualsiasi_digest, key: api_modi_rest_i01a02_qualsiasi_digest.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a02_qualsiasi_digest.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a02_qualsiasi_digest.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest.versione })
@Create204_api_modi_rest_i01a02_qualsiasi_digest_info
Scenario: Api Create 204 con api_modi_rest_i01a02_qualsiasi_digest_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a02_qualsiasi_digest_info, key: api_modi_rest_i01a02_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a02_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_digest_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a02_qualsiasi_digest_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_digest_info.versione })
@Create204_api_modi_rest_i01a02_qualsiasi_info
Scenario: Api Create 204 con api_modi_rest_i01a02_qualsiasi_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a02_qualsiasi_info, key: api_modi_rest_i01a02_qualsiasi_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a02_qualsiasi_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_qualsiasi_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a02_qualsiasi_info.nome + '/' + api_modi_rest_i01a02_qualsiasi_info.versione })
@Create204_api_modi_rest_i01a02_richiesta_info
Scenario: Api Create 204 con api_modi_rest_i01a02_richiesta_info
    * call create ( { resourcePath: 'api', body: api_modi_rest_i01a02_richiesta_info, key: api_modi_rest_i01a02_richiesta_info.nome + '/' + api_modi_rest_i01a02_richiesta_info.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_rest_i01a02_richiesta_info.nome + '/' + api_modi_rest_i01a02_richiesta_info.versione  + '/modi'})
	* def expected = getExpected(api_modi_rest_i01a02_richiesta_info.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_rest_i01a02_richiesta_info.nome + '/' + api_modi_rest_i01a02_richiesta_info.versione })
    
@Create204_api_modi_soap_a01_qualsiasi
Scenario: Api Create 204 con api_modi_soap_a01_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_soap_a01_qualsiasi, key: api_modi_soap_a01_qualsiasi.nome + '/' + api_modi_soap_a01_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a01_qualsiasi.nome + '/' + api_modi_soap_a01_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a01_qualsiasi.nome + '/' + api_modi_soap_a01_qualsiasi.versione })
    
@Create204_api_modi_soap_a01_richiesta
Scenario: Api Create 204 con api_modi_soap_a01_richiesta
    * call create ( { resourcePath: 'api', body: api_modi_soap_a01_richiesta, key: api_modi_soap_a01_richiesta.nome + '/' + api_modi_soap_a01_richiesta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a01_richiesta.nome + '/' + api_modi_soap_a01_richiesta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a01_richiesta.nome + '/' + api_modi_soap_a01_richiesta.versione })
    
@Create204_api_modi_soap_a01_risposta
Scenario: Api Create 204 con api_modi_soap_a01_risposta
    * call create ( { resourcePath: 'api', body: api_modi_soap_a01_risposta, key: api_modi_soap_a01_risposta.nome + '/' + api_modi_soap_a01_risposta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a01_risposta.nome + '/' + api_modi_soap_a01_risposta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a01_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a01_risposta.nome + '/' + api_modi_soap_a01_risposta.versione })
    
@Create204_api_modi_soap_a02_qualsiasi
Scenario: Api Create 204 con api_modi_soap_a02_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_soap_a02_qualsiasi, key: api_modi_soap_a02_qualsiasi.nome + '/' + api_modi_soap_a02_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a02_qualsiasi.nome + '/' + api_modi_soap_a02_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a02_qualsiasi.nome + '/' + api_modi_soap_a02_qualsiasi.versione })
    
@Create204_api_modi_soap_a02_richiesta
Scenario: Api Create 204 con api_modi_soap_a02_richiesta
    * call create ( { resourcePath: 'api', body: api_modi_soap_a02_richiesta, key: api_modi_soap_a02_richiesta.nome + '/' + api_modi_soap_a02_richiesta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a02_richiesta.nome + '/' + api_modi_soap_a02_richiesta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a02_richiesta.nome + '/' + api_modi_soap_a02_richiesta.versione })
    
@Create204_api_modi_soap_a02_risposta
Scenario: Api Create 204 con api_modi_soap_a02_risposta
    * call create ( { resourcePath: 'api', body: api_modi_soap_a02_risposta, key: api_modi_soap_a02_risposta.nome + '/' + api_modi_soap_a02_risposta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_a02_risposta.nome + '/' + api_modi_soap_a02_risposta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_a02_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_a02_risposta.nome + '/' + api_modi_soap_a02_risposta.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi, key: api_modi_soap_i01a01_qualsiasi.nome + '/' + api_modi_soap_i01a01_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi.nome + '/' + api_modi_soap_i01a01_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi.nome + '/' + api_modi_soap_i01a01_qualsiasi.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_digest
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_digest
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_digest, key: api_modi_soap_i01a01_qualsiasi_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_digest_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_digest_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_digest_info_utente, key: api_modi_soap_i01a01_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_digest_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_firma_all, key: api_modi_soap_i01a01_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_firma_all_digest
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_firma_all_digest
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_firma_all_digest, key: api_modi_soap_i01a01_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente, key: api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_digest_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_firma_all_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_firma_all_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_firma_all_info_utente, key: api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_firma_all_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_qualsiasi_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_qualsiasi_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_qualsiasi_info_utente, key: api_modi_soap_i01a01_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_qualsiasi_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a01_qualsiasi_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_richiesta
Scenario: Api Create 204 con api_modi_soap_i01a01_richiesta
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_richiesta, key: api_modi_soap_i01a01_richiesta.nome + '/' + api_modi_soap_i01a01_richiesta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_richiesta.nome + '/' + api_modi_soap_i01a01_richiesta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_richiesta.nome + '/' + api_modi_soap_i01a01_richiesta.versione })
    
@Create204_api_modi_soap_i01a01_richiesta_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a01_richiesta_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_richiesta_firma_all, key: api_modi_soap_i01a01_richiesta_firma_all.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_richiesta_firma_all.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_richiesta_firma_all.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all.versione })
    
@Create204_api_modi_soap_i01a01_richiesta_firma_all_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_richiesta_firma_all_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_richiesta_firma_all_info_utente, key: api_modi_soap_i01a01_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_firma_all_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_richiesta_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a01_richiesta_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_richiesta_info_utente, key: api_modi_soap_i01a01_richiesta_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_richiesta_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_richiesta_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_richiesta_info_utente.nome + '/' + api_modi_soap_i01a01_richiesta_info_utente.versione })
    
@Create204_api_modi_soap_i01a01_risposta
Scenario: Api Create 204 con api_modi_soap_i01a01_risposta
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_risposta, key: api_modi_soap_i01a01_risposta.nome + '/' + api_modi_soap_i01a01_risposta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_risposta.nome + '/' + api_modi_soap_i01a01_risposta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_risposta.nome + '/' + api_modi_soap_i01a01_risposta.versione })
    
@Create204_api_modi_soap_i01a01_risposta_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a01_risposta_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a01_risposta_firma_all, key: api_modi_soap_i01a01_risposta_firma_all.nome + '/' + api_modi_soap_i01a01_risposta_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a01_risposta_firma_all.nome + '/' + api_modi_soap_i01a01_risposta_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a01_risposta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a01_risposta_firma_all.nome + '/' + api_modi_soap_i01a01_risposta_firma_all.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi, key: api_modi_soap_i01a02_qualsiasi.nome + '/' + api_modi_soap_i01a02_qualsiasi.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi.nome + '/' + api_modi_soap_i01a02_qualsiasi.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi.nome + '/' + api_modi_soap_i01a02_qualsiasi.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_digest
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_digest
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_digest, key: api_modi_soap_i01a02_qualsiasi_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_digest_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_digest_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_digest_info_utente, key: api_modi_soap_i01a02_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_digest_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_firma_all, key: api_modi_soap_i01a02_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_firma_all.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_firma_all_digest
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_firma_all_digest
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_firma_all_digest, key: api_modi_soap_i01a02_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_digest.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente, key: api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_digest_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_firma_all_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_firma_all_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_firma_all_info_utente, key: api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_firma_all_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_qualsiasi_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_qualsiasi_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_qualsiasi_info_utente, key: api_modi_soap_i01a02_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_qualsiasi_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_qualsiasi_info_utente.nome + '/' + api_modi_soap_i01a02_qualsiasi_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_richiesta
Scenario: Api Create 204 con api_modi_soap_i01a02_richiesta
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_richiesta, key: api_modi_soap_i01a02_richiesta.nome + '/' + api_modi_soap_i01a02_richiesta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_richiesta.nome + '/' + api_modi_soap_i01a02_richiesta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_richiesta.nome + '/' + api_modi_soap_i01a02_richiesta.versione })
    
@Create204_api_modi_soap_i01a02_richiesta_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a02_richiesta_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_richiesta_firma_all, key: api_modi_soap_i01a02_richiesta_firma_all.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_richiesta_firma_all.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_richiesta_firma_all.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all.versione })
    
@Create204_api_modi_soap_i01a02_richiesta_firma_all_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_richiesta_firma_all_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_richiesta_firma_all_info_utente, key: api_modi_soap_i01a02_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_firma_all_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_richiesta_firma_all_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_firma_all_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_richiesta_info_utente
Scenario: Api Create 204 con api_modi_soap_i01a02_richiesta_info_utente
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_richiesta_info_utente, key: api_modi_soap_i01a02_richiesta_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_info_utente.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_richiesta_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_info_utente.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_richiesta_info_utente.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_richiesta_info_utente.nome + '/' + api_modi_soap_i01a02_richiesta_info_utente.versione })
    
@Create204_api_modi_soap_i01a02_risposta
Scenario: Api Create 204 con api_modi_soap_i01a02_risposta
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_risposta, key: api_modi_soap_i01a02_risposta.nome + '/' + api_modi_soap_i01a02_risposta.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_risposta.nome + '/' + api_modi_soap_i01a02_risposta.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_risposta.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_risposta.nome + '/' + api_modi_soap_i01a02_risposta.versione })
    
@Create204_api_modi_soap_i01a02_risposta_firma_all
Scenario: Api Create 204 con api_modi_soap_i01a02_risposta_firma_all
    * call create ( { resourcePath: 'api', body: api_modi_soap_i01a02_risposta_firma_all, key: api_modi_soap_i01a02_risposta_firma_all.nome + '/' + api_modi_soap_i01a02_risposta_firma_all.versione, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_modi_soap_i01a02_risposta_firma_all.nome + '/' + api_modi_soap_i01a02_risposta_firma_all.versione  + '/modi'})
	* def expected = getExpected(api_modi_soap_i01a02_risposta_firma_all.modi)
    * match response == expected
	* call delete ( { resourcePath: 'api/' + api_modi_soap_i01a02_risposta_firma_all.nome + '/' + api_modi_soap_i01a02_risposta_firma_all.versione })





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


