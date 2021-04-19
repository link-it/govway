Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}
* def servizio = 'EsempioServizioComposto'

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
expected.sicurezza_messaggio.configurazione.informazioni_utente = expected.sicurezza_messaggio.configurazione.informazioni_utente != null ? expected.sicurezza_messaggio.configurazione.informazioni_utente == 'true' : false
expected.sicurezza_messaggio.configurazione.soap_firma_allegati = expected.sicurezza_messaggio.configurazione.soap_firma_allegati != null ? expected.sicurezza_messaggio.configurazione.soap_firma_allegati == 'true' : false
expected.sicurezza_messaggio.configurazione.digest_richiesta = expected.sicurezza_messaggio.configurazione.digest_richiesta != null ? expected.sicurezza_messaggio.configurazione.digest_richiesta == 'true' : false
return expected;
} 
"""

* def setAzioneCorrelataApiNome = 
"""
function(x,  nome, api_modi_azione_richiesta){
 if(x.modi.interazione.azione_correlata != null) {
 x.modi.interazione.azione_correlata.api_nome=nome
 x.modi.interazione.azione_correlata.servizio=servizio
 api_modi_azione_richiesta.modi.interazione.tipo=x.modi.interazione.tipo
 } 
}
"""


* def setModi =
"""
function(apimodiazione, api) {
api.nome = apimodiazione.nome
} 
"""



@UpdateAzioneModi204
Scenario Outline: Api Update Interfaccia 204 modi azione <nome-test>

	* def api_soap = read('api_modi_soap.json')
	* eval randomize(api_soap, ["nome"])
	* call create ( { resourcePath: 'api', body: api_soap, key: api_soap.nome + '/' + api_soap.versione, query_params: query_param_profilo_modi } )

	* def api_modi_azione_richiesta = read('api_modi_soap_azione_non_bloccante_push_richiesta.json')

	* def api_modi_azione1 = read('api_modi_soap_azione_non_bloccante_push_richiesta.json');
	* eval randomize(api_modi_azione1, ["nome"])
    * call create ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni', body: api_modi_azione1, key: api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni' + api_modi_azione1.nome, query_params: query_param_profilo_modi } )

	* def api_for_update = read('<nome-test>');
	* eval setModi(api_modi_azione1, api_for_update)
	* eval setAzioneCorrelataApiNome(api_for_update, api_soap.nome, api_modi_azione_richiesta)
    * call create ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni', body: api_modi_azione_richiesta, key: api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni' + api_modi_azione_richiesta.nome, query_params: query_param_profilo_modi } )
    * call put ( { body: api_for_update, resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni/' + api_for_update.nome, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_soap.nome + '/' + api_soap.versione  + '/servizi/'+servizio+'/azioni/' + api_for_update.nome})
    * match response.modi == api_for_update.modi
	* call delete ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione  + '/servizi/'+servizio+'/azioni/' + api_for_update.nome })
	* call delete ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione })

Examples:
| nome-test |
| api_modi_soap_azione_bloccante.json |
| api_modi_soap_azione_non_bloccante_push_richiesta.json |
| api_modi_soap_azione_non_bloccante_pull_richiesta.json |
| api_modi_soap_azione_non_bloccante_push_risposta.json |
| api_modi_soap_azione_non_bloccante_pull_risposta.json |



@UpdateAzioneModi204_sicurezzaMessaggio
Scenario Outline: Api Update Interfaccia 204 modi azione sicurezza messaggio <nome-test>

	* def api_soap = read('api_modi_soap.json')
	* eval randomize(api_soap, ["nome"])
	* call create ( { resourcePath: 'api', body: api_soap, key: api_soap.nome + '/' + api_soap.versione, query_params: query_param_profilo_modi } )
	* def api_modi_azione = read('api_modi_soap_azione_bloccante.json');
	* eval randomize(api_modi_azione, ["nome"])
    * call create ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni', body: api_modi_azione, key: api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni' + api_modi_azione.nome, query_params: query_param_profilo_modi } )
	* def api_for_sicurezza_messaggio = read('<nome-test>');
	* eval setSicurezzaMessaggio(api_modi_azione, api_for_sicurezza_messaggio) 
    * call put ( { body: api_modi_azione, resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione + '/servizi/'+servizio+'/azioni/' + api_modi_azione.nome, query_params: query_param_profilo_modi } )
	* call get ( { resourcePath: 'api', key: api_soap.nome + '/' + api_soap.versione  + '/servizi/'+servizio+'/azioni/' + api_modi_azione.nome})
	* def expected = getExpected(api_modi_azione.modi)
    * match response.modi == expected
	* call delete ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione  + '/servizi/'+servizio+'/azioni/' + api_modi_azione.nome })
	* call delete ( { resourcePath: 'api/' + api_soap.nome + '/' + api_soap.versione })

Examples:
| nome-test |
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
