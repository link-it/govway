Feature: Configurazione Servizi Rate Limiting

Background:


@CRUDRatelimiting
Scenario: CRUD RATE LIMITING

    # CREATE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 204


    # LIST
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains { total: 1, items: '#[1]' }

    * def policy_id = response.items[0].identificativo
    
    # GET
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    #UPDATE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    And request policy_update
    When method put
    Then status 204

	# DELETE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
