Feature: Update Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def api_soap_una_azione = read('api_soap_una_azione.json')
* eval randomize(api_soap_una_azione, ["nome"])
* eval api_soap_una_azione.referente = soggettoDefault
* def api_soap_una_azione_path = 'api/' + api_soap_una_azione.nome + '/' + api_soap_una_azione.versione

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault
* def api_soap_piu_azioni_path = 'api/' + api_soap_piu_azioni.nome + '/' + api_soap_piu_azioni.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione

* def fruizione_soap_una_azione = read('fruizione_soap_una_azione.json')
* eval fruizione_soap_una_azione.api_nome = api_soap_una_azione.nome
* eval fruizione_soap_una_azione.api_versione = api_soap_una_azione.versione
* eval fruizione_soap_una_azione.fruizione_nome = api_soap_una_azione.nome
* eval fruizione_soap_una_azione.erogatore = erogatore.nome

* def fruizione_soap_piu_azioni = read('fruizione_soap_piu_azioni.json')
* eval fruizione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval fruizione_soap_piu_azioni.fruizione_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.erogatore = erogatore.nome

* def soap_una_azione_key = fruizione_petstore.erogatore + '/' + fruizione_soap_una_azione.api_nome + '/' + fruizione_soap_una_azione.api_versione
* def fruizione_soap_una_azione_path = 'fruizioni/' + soap_una_azione_key

* def soap_piu_azioni_key = fruizione_petstore.erogatore + '/' + fruizione_soap_piu_azioni.api_nome + '/' + fruizione_soap_piu_azioni.api_versione
* def fruizione_soap_piu_azioni_path = 'fruizioni/' + soap_piu_azioni_key

* def api_info_generali = read('api_info_generali.json')

* def connettore = read('connettore_erogazione_petstore.json')
* def info_generali = read('informazioni_generali_petstore.json')
* def erogazione_versione = read('api_versione3.json')

@UpdateConnettore204
Scenario: Update Fruizioni Connettore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 204
    
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@UpdateInfoGenerali204
Scenario: Update Fruizioni Info Generali 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', petstore_key, 'informazioni'
    And header Authorization = govwayConfAuth
    And request info_generali
    And params query_params
    When method put
    Then status 204
    
    
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_petstore.erogatore + '/' + info_generali.nome + '/' + fruizione_petstore.api_versione })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@UpdateUrlInvocazioneRest204
Scenario Outline: Fruizioni Update Url Invocazione 204 per API REST
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>
   
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
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
Scenario Outline: Fruizioni Update Url Invocazione 204 per API SOAP
    
    * call create ({ resourcePath: 'api', body: api_soap_una_azione })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_soap_una_azione })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', soap_una_azione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'fruizioni/' + soap_una_azione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_soap_una_azione_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "content-based" | null | "concat($.test,'#',$.prova2)" | false | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | false |
| "content-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "content-based" | "nomeinutile" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | "X-ProvaP" | null | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "header-based" | null | "X-ProvaP" | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | "X-ProvaP" | null | false | 204 | "header-based" | "X-ProvaP" | null | false |
| "header-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
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
| "url-based" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "url-based" | "nomeinutile" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | "MRequest" | null | true | 204 | "static" | null | null | false |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_una_azione.erogatore+"/(?:gw_)?"+fruizione_soap_una_azione.fruizione_nome+"/v1/([^/\|^?]*).*" | true |

@UpdateUrlInvocazioneSoapPiuAzioni204
Scenario Outline: Fruizioni Update Url Invocazione 204 per API SOAP con piu azioni
    
    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_soap_piu_azioni })

    * def url_invocazione = read('fruizione_petstore_url_invocazione.json')

    * eval url_invocazione.modalita = <mode>

    * eval if (<nome> != null) url_invocazione.nome = <nome>

    * eval if (<pattern> != null) url_invocazione.pattern = <pattern>

    * eval url_invocazione.force_interface = <force>

Given url configUrl
    And path 'fruizioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status <status>

Given url configUrl
    And path 'fruizioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == <mode-get>
    And assert response.nome == <nome-get>
    And assert response.pattern == <pattern-get>
    And match response.force_interface == <force-get>

    * call delete ({ resourcePath: 'fruizioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })

Examples:
| mode | nome | pattern | force | status | mode-get | nome-get | pattern-get | force-get |
| "content-based" | null | "concat($.test,'#',$.prova2)" | true | 204 | "content-based" | null | "concat($.test,'#',$.prova2)" | true |
| "header-based" | null | "X-ProvaP" | true | 204 | "header-based" | "X-ProvaP" | null | true |
| "input-based" | null | null | true | 204 | "input-based" | null | null | true |
| "interface-based" | null | null | true | 204 | "interface-based" | null | null | false |
| "soap-action-based" | null | null | true | 204 | "soap-action-based" | null | null | true |
| "url-based" | null | "/.(espressioneRegolare)?" | true | 204 | "url-based" | null | "/.(espressioneRegolare)?" | true |
| "static" | "MRequest" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | "AzioneNonEsistente" | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | "patternInutile" | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\|^?]*).*" | true |
| "static" | null | null | true | 400 | "url-based" | null | ".*/(?:gw_)?"+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+"/(?:gw_)?"+fruizione_soap_piu_azioni.fruizione_nome+"/v1/([^/\|^?]*).*" | true |



@UpdateVersione204
Scenario: Update Fruizioni Versione 204
   
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * eval api_petstore.versione = 3
    * eval erogazione_versione.api_versione = 3

    * call create ({ resourcePath: 'api', body: api_petstore })
    
    Given url configUrl
    And path 'fruizioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    And request erogazione_versione
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: 'api/' + api_petstore.nome + '/' + api_petstore.versione })
    * call delete ({ resourcePath: api_petstore_path })


@UpdateConnettore404
Scenario: Update Fruizioni 404

    Given url configUrl
    And path 'fruizioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    And request connettore
    And params query_params
    When method put
    Then status 404
