Feature: Configurazione Registrazione Messaggi

Background:
    * def registrazione_messaggi = read('classpath:bodies/registrazione-messaggi.json')

@Update204
Scenario: Update Registrazione Messaggi

    Given url configUrl
    And path servizio_path, 'configurazioni', 'registrazione-messaggi'
    And header Authorization = govwayConfAuth
    And request registrazione_messaggi
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'registrazione-messaggi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains registrazione_messaggi
 