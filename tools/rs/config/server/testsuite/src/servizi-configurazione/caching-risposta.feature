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

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains cache_risposta

@UpdateOpzioniCacheControl204
Scenario: Update Caching con Opzioni di Cache Control diverse da quelle di default
    * eval cache_risposta.control_no_cache = false
    * eval cache_risposta.control_no_store = true
    * eval cache_risposta.control_max_age = false;

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And request cache_risposta
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains cache_risposta

@UpdateOpzioniCacheControlConRegole204
Scenario: Update Caching con Opzioni di Cache Control diverse da quelle di default
  
    * def cache_risposta_regole = read('classpath:bodies/caching-risposta-regole.json')
    * eval cache_risposta_regole.control_no_cache = false
    * eval cache_risposta_regole.control_no_store = true
    * eval cache_risposta_regole.control_max_age = false;

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And request cache_risposta_regole
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains cache_risposta



@Get200
Scenario: Get Caching

    Given url configUrl
    And path servizio_path, 'configurazioni', 'caching-risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200