Feature: Configurazione Validazione

Background:
    * def configurazione_validazione = read('classpath:bodies/configurazione-validazione.json')

@Update204
Scenario: Update Validazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'validazione'
    And header Authorization = govwayConfAuth
    And request configurazione_validazione
    And params query_params
    When method put
    Then status 204


@Get200
Scenario: Get Validazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'validazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200