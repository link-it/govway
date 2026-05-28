@ignore
Feature: Teardown del gruppo dedicato creato dal setup di toggle-stato-api

Background:
* def basic = read('classpath:basic-auth.js')
* def govwayConfAuth = call basic configCred

Scenario: Rimuove il gruppo ToggleStatoAuthGruppo (cascade sul connettore associato)

    Given url configUrl
    And path 'erogazioni/api-monitor/1/gruppi/ToggleStatoAuthGruppo'
    And params ({ soggetto: soggettoDefault })
    And header Authorization = govwayConfAuth
    When method delete
    Then status 204
