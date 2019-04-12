Feature: Configurazione Servizi Caching Risposta

Background:

    * def cache_risposta = read('classpath:bodies/caching-risposta.json')

@Update204
Scenario: Update Caching

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And request cache_risposta
    And params query_params
    When method put
    Then status 204

@Get200
Scenario: Get Caching

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

# TODO: Matchare la risposta e fare un unico scenario che fa l'update, la get e matcha la risposta.