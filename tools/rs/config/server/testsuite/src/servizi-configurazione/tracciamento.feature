Feature: Configurazione Servizi Tracciamento

Background:
    * def correlazione_risposta = read('classpath:bodies/correlazione-applicativa-risposta.json')
    * def correlazione_richiesta = read('classpath:bodies/correlazione-applicativa-richiesta.json')

@CRUDTracciamento
Scenario: CRUD TRACCIAMENTO
	
    # CREATE RICHIESTA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta'
    And header Authorization = govwayConfAuth
    And request correlazione_richiesta
    And params query_params
    When method post
    Then status 204

    # CREATE RISPOSTA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'risposta'
    And header Authorization = govwayConfAuth
    And request correlazione_risposta
    And params query_params
    When method post
    Then status 204


    # LIST RICHIESTA TODO match
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    #LIST RISPOSTA TODO match
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'risposta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200


    #GET RICHIESTA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta', '*'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    #GET RISPOSTA
    * def risposta_url = configUrl + '/' + servizio_path + '/configurazioni/tracciamento/correlazione-applicativa/risposta/%2Fpath%2F'
    
    Given url risposta_url
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    #UPDATE RICHIESTA
    * def update_richiesta =  read('classpath:bodies/correlazione-applicativa-richiesta-update.json')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta', '*'
    And header Authorization = govwayConfAuth
    And request update_richiesta
    And params query_params
    When method put
    Then status 204

    #UPDATE RISPOSTA
    * def update_risposta =  read('classpath:bodies/correlazione-applicativa-risposta-update.json')
    Given url risposta_url
    And header Authorization = govwayConfAuth
    And request update_risposta
    And params query_params
    When method put
    Then status 204
	
    # DELETE RICHIESTA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta', '*'
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204


    # DELETE RISPOSTA
    Given url risposta_url
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204