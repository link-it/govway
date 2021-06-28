Feature: Update Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_soap_una_azione = read('api_soap_una_azione.json')
* eval randomize(api_soap_una_azione, ["nome"])
* eval api_soap_una_azione.referente = soggettoDefault

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault

* def api_info_generali = read('api_info_generali.json')

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def erogazione_soap_una_azione = read('erogazione_soap_una_azione.json')
* eval erogazione_soap_una_azione.api_nome = api_soap_una_azione.nome
* eval erogazione_soap_una_azione.api_versione = api_soap_una_azione.versione
* eval erogazione_soap_una_azione.erogazione_nome = api_soap_una_azione.nome

* def erogazione_soap_piu_azioni = read('erogazione_soap_piu_azioni.json')
* eval erogazione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval erogazione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval erogazione_soap_piu_azioni.erogazione_nome = api_soap_piu_azioni.nome

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key
* def erogazione_petstore_path = 'erogazioni/' + petstore_key

* def soap_una_azione_key = erogazione_soap_una_azione.api_nome + '/' + erogazione_soap_una_azione.api_versione
* def api_soap_una_azione_path = 'api/' + soap_una_azione_key
* def erogazione_soap_una_azione_path = 'erogazioni/' + soap_una_azione_key

* def soap_piu_azioni_key = erogazione_soap_piu_azioni.api_nome + '/' + erogazione_soap_piu_azioni.api_versione
* def api_soap_piu_azioni_path = 'api/' + soap_piu_azioni_key
* def erogazione_soap_piu_azioni_path = 'erogazioni/' + soap_piu_azioni_key

* def connettore = read('connettore_erogazione_petstore.json')
* def info_generali = read('informazioni_generali_petstore.json')
* eval randomize(info_generali, ["nome"])
* def erogazione_versione = read('api_versione3.json')

* def getExpectedConnettoreHTTPS =
"""
function(connettore) {
var expected = connettore
expected.connettore.autenticazione_https.trust_all_server_certs = expected.connettore.autenticazione_https.trust_all_server_certs != null ? expected.connettore.autenticazione_https.trust_all_server_certs : false  
expected.connettore.autenticazione_https.server.algoritmo = expected.connettore.autenticazione_https.server.algoritmo != null ? expected.connettore.autenticazione_https.server.algoritmo : 'PKIX'  
expected.connettore.debug =expected.connettore.debug != null ? expected.connettore.debug : false 
return expected
} 
"""

* def getExpectedConnettoreHTTP =
"""
function(connettore) {
var expected = connettore
expected.connettore.tipo = 'http'
expected.connettore.debug =expected.connettore.debug != null ? expected.connettore.debug : false 
return expected
} 
"""

@UpdateConnettore204
Scenario: Erogazioni Update Connettore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore_gruppo_200
Scenario: Erogazioni Update Connettore gruppo OK

		* def gruppo_petstore = read ('gruppo_petstore.json')
		* def query_params = {'gruppo': 'GruppoJson'}

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    * match response == getExpectedConnettoreHTTPS(connettore)

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

		* match response == getExpectedConnettoreHTTPS(connettore)
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

		* match response == getExpectedConnettoreHTTP({connettore: erogazione_petstore.connettore})
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateConnettore204_connettoreDebug
Scenario Outline: Erogazioni Update Connettore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

		* eval connettore.connettore.debug=<debug>
    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
        
		* call get ( { resourcePath: 'erogazioni', key: petstore_key + '/connettore'} )
    
		* match response.connettore.debug == <debug>
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|debug|
|true|
|false|

@UpdateInfoGenerali204
Scenario: Erogazioni Update Info Generali 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'informazioni'
    And header Authorization = govwayConfAuth
    And request info_generali
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + info_generali.nome + '/' + erogazione_petstore.api_versione })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateUrlInvocazioneRest204
Scenario Outline: Erogazioni Update Url Invocazione 204 per API REST
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "content-based" | "nomeinutile" | null | true | 400 | "interface-based" | null | null | false |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "input-based" | "vieneignorato" | null | true | 204 | "input-based" | null | null | true |
| "input-based" | null | "vieneignorato" | true | 204 | "input-based" | null | null | true |
| "input-based" | null | null | false | 204 | "input-based" | null | null | false |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "interface-based" | null | null | false | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "url-based" | null | null | true | 400 | "interface-based" | null | null | false |
| "static" | null | null | true | 400 | "interface-based" | null | null | false |

@UpdateUrlInvocazioneSoap204
Scenario Outline: Erogazioni Update Url Invocazione 204 per API SOAP
    
    * call create ({ resourcePath: 'api', body: api_soap_una_azione })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_una_azione })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + soap_una_azione_key })
    * call delete ({ resourcePath: api_soap_una_azione_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "content-based" | "nomeinutile" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "input-based" | "vieneignorato" | null | true | 204 | "input-based" | null | null | true |
| "input-based" | null | "vieneignorato" | true | 204 | "input-based" | null | null | true |
| "input-based" | null | null | false | 204 | "input-based" | null | null | false |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "interface-based" | null | null | false | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | "vieneignorato" | null | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | null | "vieneignorato" | true | 204 | "soap-action-based" | null | null | true |
| "soap-action-based" | null | null | false | 204 | "soap-action-based" | null | null | false |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "url-based" | null | "/.(espressioneRegolare)?" | false | 204 | "url-based" | null | "/.(espressioneRegolare)?" | false |
| "url-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "url-based" | "nomeinutile" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | "MRequest" | null | true | 204 | "static" | null | null | false |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_una_azione.erogazione_nome+"/v1/([^/\|^?]*).*" | true |

@UpdateUrlInvocazioneSoapPiuAzioni204
Scenario Outline: Erogazioni Update Url Invocazione 204 per API SOAP con piu azioni
    
    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_piu_azioni })

    * def url_invocazione = read('erogazione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'erogazioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'erogazioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'erogazioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "static" | "MRequest" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+"/(?:gw_)?"+erogazione_soap_piu_azioni.erogazione_nome+"/v1/([^/\|^?]*).*" | true |


@UpdateVersione204
Scenario: Erogazioni Update Versione 204
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * eval api_petstore.versione = 3
    * eval erogazione_versione.api_versione = 3

    * call create ({ resourcePath: 'api', body: api_petstore })
    
    Given url configUrl
    And path 'erogazioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    And request erogazione_versione
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
    * call delete ({ resourcePath: 'api/' + api_petstore.nome + '/' + api_petstore.versione })

@UpdateConnettore404
Scenario: Erogazioni Update 404

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 404
