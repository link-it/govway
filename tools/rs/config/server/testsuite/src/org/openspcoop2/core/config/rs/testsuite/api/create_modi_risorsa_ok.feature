Feature: Creazione Risorsa Api ModI

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}
* def query_param_profilo_modipa = {'profilo': 'ModIPA'}

* def setSicurezzaMessaggio =
"""
function(apimodiazione, api) {
apimodiazione.modi.sicurezza_messaggio.stato = 'ridefinito'
apimodiazione.modi.sicurezza_messaggio.configurazione=api.modi.sicurezza_messaggio
} 
"""

* def getExpected =
"""
function(modi) {
var expected = modi;
expected.sicurezza_messaggio.configurazione.informazioni_utente = expected.sicurezza_messaggio.configurazione.informazioni_utente != null ? expected.sicurezza_messaggio.configurazione.informazioni_utente : false
expected.sicurezza_messaggio.configurazione.soap_firma_allegati = expected.sicurezza_messaggio.configurazione.soap_firma_allegati != null ? expected.sicurezza_messaggio.configurazione.soap_firma_allegati : false
expected.sicurezza_messaggio.configurazione.digest_richiesta = expected.sicurezza_messaggio.configurazione.digest_richiesta != null ? expected.sicurezza_messaggio.configurazione.digest_richiesta : false
return expected;
} 
"""

* def setRisorsaCorrelataApiNome = 
"""
function(x,  nome, api_modi_risorsa_crud){
 if(x.modi.interazione.risorsa_correlata != null) {
 x.modi.interazione.risorsa_correlata.api_nome=nome
 api_modi_risorsa_crud.modi.interazione.tipo=x.modi.interazione.tipo
 } 
}
"""

@CreateRisorsa204_modi
Scenario Outline: Api Create Risorsa 204 con profilo ModI <nome-test>
	* def api_rest = read('api_modi_rest.json')
	* eval randomize(api_rest, ["nome"])
	* def api_modi_risorsa_crud = read('api_modi_rest_risorsa_non_bloccante_push_richiesta.json')
	* def api_modi_risorsa = read('<nome-test>')
	* eval randomize(api_modi_risorsa, ["nome", "path"])
	* eval setRisorsaCorrelataApiNome(api_modi_risorsa, api_rest.nome, api_modi_risorsa_crud)
	* call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modipa } )
	* call create ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/risorse', body: api_modi_risorsa_crud, key: api_rest.nome + '/' + api_rest.versione + '/risorse' + api_modi_risorsa_crud.nome, query_params: query_param_profilo_modi } )
	
    * call create ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/risorse', body: api_modi_risorsa, key: api_rest.nome + '/' + api_rest.versione + '/risorse' + api_modi_risorsa.nome, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome})
    * match response.modi == api_modi_risorsa.modi
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome })
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })


Examples:
| nome-test |
| api_modi_rest_risorsa_crud.json |
| api_modi_rest_risorsa_bloccante.json |
| api_modi_rest_risorsa_non_bloccante_push_richiesta.json |
| api_modi_rest_risorsa_non_bloccante_pull_richiesta.json |
| api_modi_rest_risorsa_non_bloccante_push_risposta.json |
#| api_modi_rest_risorsa_non_bloccante_pull_risposta.json |

@CreateRisorsa204_modi
Scenario Outline: Api Create Risorsa 204 con profilo ModI <nome-test>
	* def api_rest = read('api_modi_rest.json')
	* eval randomize(api_rest, ["nome"])
	* call create ( { resourcePath: 'api', body: api_rest, key: api_rest.nome + '/' + api_rest.versione, query_params: query_param_profilo_modipa } )
	* def api_modi_risorsa = read('api_modi_rest_risorsa_crud.json');
	* eval randomize(api_modi_risorsa, ["nome", "path"])
	* def api_for_sicurezza_messaggio = read('<nome-test>');
	* eval setSicurezzaMessaggio(api_modi_risorsa, api_for_sicurezza_messaggio) 

    * call create ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione + '/risorse', body: api_modi_risorsa, key: api_rest.nome + '/' + api_rest.versione + '/risorse' + api_modi_risorsa.nome, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome})
	* def expected = getExpected(api_modi_risorsa.modi)
    * match response.modi == expected
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione  + '/risorse/' + api_modi_risorsa.nome })
	* call delete ( { resourcePath: 'api/' + api_rest.nome + '/' + api_rest.versione })


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
