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

#TODO: Fare una get che verifichi l'effettiva disabilitazione
@Abilita 
Scenario: Disabilita Configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'stato'
    And header Authorization = govwayConfAuth
    And request stato_disabilitato
    And params query_params
    When method put
    Then status 204


@Get200
Scenario: Get Configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'registrazione-messaggi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200