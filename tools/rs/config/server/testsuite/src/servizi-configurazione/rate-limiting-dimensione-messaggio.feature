Feature: Configurazione Servizi Rate Limiting

Background:

    * eval policy.configurazione.metrica = policy_type

@CRUDRatelimiting
Scenario: CRUD RATE LIMITING

    * eval randomize(policy, ["nome"])

    # CREATE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And request policy
    And params query_params
    When method post
    Then status 201

    # LIST
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains { total: 1, items: '#[1]' }

    * def policy_id = response.items[0].nome
    
    # GET
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains
    """
    {
        nome: #(policy.nome),
        stato: #(policy.stato),
        configurazione: #(policy.configurazione),
        soglia_ridefinita: #(policy.soglia_ridefinita),
        soglia_dimensione_richiesta: #(policy.soglia_dimensione_richiesta),
        soglia_dimensione_risposta: #(policy.soglia_dimensione_risposta),
        filtro: #(policy.filtro)
    }
    """

    * set policy_update.nome = policy_id + "Modificato"

    #UPDATE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    And request policy_update
    When method put
    Then status 204

    * def policy_id = policy_update.nome

    # GET (Ci assicuriamo che anche l'update abbia funzionato a dovere)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains
    """
    {
        nome: #(policy_update.nome),
        stato: #(policy_update.stato),
        configurazione: #(policy.configurazione),
        soglia_dimensione_richiesta: 10240,
        soglia_dimensione_risposta: 10240,
        filtro: #(policy_update.filtro)
    }
    """

	# DELETE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
