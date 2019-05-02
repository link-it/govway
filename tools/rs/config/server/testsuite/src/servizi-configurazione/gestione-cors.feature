Feature: Configurazione Gestione Cors

Background:
    * def gestione_cors = read('classpath:bodies/gestione-cors-petstore.json')

@Update204
Scenario: Update Gestione CORS

    Given url configUrl
    And path servizio_path, 'configurazioni', 'gestione-cors'
    And header Authorization = govwayConfAuth
    And request gestione_cors
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'gestione-cors'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response == gestione_cors


@Get200
Scenario: Get Gestione CORS

    Given url configUrl
    And path servizio_path, 'configurazioni', 'gestione-cors'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200