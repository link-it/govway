Feature: Configurazione Servizi Rate Limiting

Background:

    * eval policy.configurazione.risorsa = policy_type
    * eval policy.configurazione.intervallo = policy_intervallo

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
        identificazione: #(policy.identificazione),
        configurazione: #(policy.configurazione),
        soglia_ridefinita: #(policy.soglia_ridefinita),
        soglia_valore: #(policy.soglia_valore),
        filtro: #(policy.filtro),
        criterio_collezionamento_dati: #(policy.criterio_collezionamento_dati)
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
        identificazione: #(policy.identificazione),
        configurazione: #(policy.configurazione),
        soglia_ridefinita: #(policy_update.soglia_ridefinita),
        filtro: #(policy_update.filtro),
        criterio_collezionamento_dati: #(policy_update.criterio_collezionamento_dati)
    }
    """

	# DELETE
    Given url configUrl
    And path servizio_path, 'configurazioni', 'rate-limiting', policy_id
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
