Feature: Configurazione Controllo Accessi Autenticazione

Background:
    * def autenticazione = read('classpath:bodies/controllo-accessi-autenticazione-token.json')

@Update204
Scenario: Update Autenticazione

    * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

    #Prima abilitiamo la gestione token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request autenticazione
    And params query_params
    When method put
    Then status 204

@Get200
Scenario: Get Autenticazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200