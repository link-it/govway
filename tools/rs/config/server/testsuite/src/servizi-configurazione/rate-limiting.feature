Feature: Configurazione Servizi Rate Limiting

Background:

    * eval policy.policy = policy_type

@CRUDRatelimiting
Scenario: CRUD RATE LIMITING

#BUG2: Si può davvero settare nel rate-limiting delle erogazioni l'applicativo fruitore nel criterio collezionamento dati? Si, è un bug della console a non permetterlo.
#           perchè dalla consol non si può.

    * eval randomize(policy, ["nome"])

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
    And match response contains
    """
    {
        nome: #(policy.nome),
        stato: #(policy.stato),
        soglia_ridefinita: #(policy.soglia_ridefinita),
        soglia_valore: #(policy.soglia_valore),
        filtro: #(policy.filtro),
        criterio_collezionamento_dati: #(policy.criterio_collezionamento_dati),
        identificativo: #(policy_id)
    }
    """

    #UPDATE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    And request policy_update
    When method put
    Then status 204

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
        stato: #(policy_update.stato),
        soglia_ridefinita: #(policy_update.soglia_ridefinita),
        filtro: #(policy_update.filtro),
        criterio_collezionamento_dati: #(policy_update.criterio_collezionamento_dati),
        identificativo: #(policy_id)
    }
    """

	# DELETE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
