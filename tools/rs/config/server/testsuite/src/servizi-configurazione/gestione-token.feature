Feature: Configurazione Controllo Accessi Gestione Token

Background:
    * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

Scenario: Update Gestione Token

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains gestione_token_body