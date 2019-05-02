Feature: Configurazione Abilita/Disabilita Stato

Background:

* def stato_disabilitato = ({ abilitato: false })
* def stato_abilitato = ({ abilitato: true })


@Abilita
Scenario: Abilita Configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'stato'
    And header Authorization = govwayConfAuth
    And request stato_abilitato
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'stato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response == stato_abilitato


@Disabilita 
Scenario: Disabilita Configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'stato'
    And header Authorization = govwayConfAuth
    And request stato_disabilitato
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'stato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response == stato_disabilitato