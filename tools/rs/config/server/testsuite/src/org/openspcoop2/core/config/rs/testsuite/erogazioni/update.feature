Feature: Update Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_info_generali = read('api_info_generali.json')

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key
* def erogazione_petstore_path = 'erogazioni/' + petstore_key

* def connettore = read('connettore_erogazione_petstore.json')
* def info_generali = read('informazioni_generali_petstore.json')
* eval randomize(info_generali, ["nome"])
* def url_invocazione = read('erogazione_petstore_url_invocazione.json')
* def erogazione_versione = read('api_versione3.json')

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

@UpdateUrlInvocazione204
Scenario: Erogazioni Update Url Invocazione 204
    
    * eval url_invocazione.modalita = "interface-based"

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And request url_invocazione
    And params query_params
    When method put
    Then status 204
    
    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

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
