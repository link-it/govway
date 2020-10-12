Feature: Configurazione Servizi Tracciamento

Background:
    * def proprieta1 = read('classpath:bodies/proprieta.json')
    * def proprieta2 = read('classpath:bodies/proprieta2.json')

@CRUDTracciamento
Scenario: CRUD TRACCIAMENTO

    # LIST Ps
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.proprieta == '#[]'
	
    # CREATE P1
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And request proprieta1
    And params query_params
    When method post
    Then status 201

    # CREATE P2
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And request proprieta2
    And params query_params
    When method post
    Then status 201

    # LIST Ps
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.proprieta[0] contains { 'nome': 'NomeProprieta1' }
    And match response.proprieta[1] contains { 'nome': 'NomeProprieta2' }
    And match response.proprieta[0] contains { 'valore': 'ValoreProprieta1' }
    And match response.proprieta[1] contains { 'valore': 'ValoreProprieta2' }

    #GET P1 CREATA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta1'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.nome == 'NomeProprieta1'
    And match response.valore == 'ValoreProprieta1'

    #GET P2 CREATA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.nome == 'NomeProprieta2'
    And match response.valore == 'ValoreProprieta2'

    #UPDATE P2
    * def update_proprieta =  read('classpath:bodies/proprieta2modificata.json')
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2'
    And header Authorization = govwayConfAuth
    And request update_proprieta
    And params query_params
    When method put
    Then status 204

    #GET P2 vecchia non piu esistente
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 404

    #GET P1 CREATA
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta1'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.nome == 'NomeProprieta1'
    And match response.valore == 'ValoreProprieta1'

    #GET P2 modificata
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2modificato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.nome == 'NomeProprieta2modificato'
    And match response.valore == 'ValoreProprieta2modificato'
	
    # LIST Ps
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.proprieta[0] contains { 'nome': 'NomeProprieta1' }
    And match response.proprieta[1] contains { 'nome': 'NomeProprieta2modificato' }
    And match response.proprieta[0] contains { 'valore': 'ValoreProprieta1' }
    And match response.proprieta[1] contains { 'valore': 'ValoreProprieta2modificato' }

    # DELETE P1
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta1'
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    #GET P1 non piu' esistente
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta1'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 404

    #GET P2 modificata
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2modificato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.nome == 'NomeProprieta2modificato'
    And match response.valore == 'ValoreProprieta2modificato'

    # DELETE P2
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2modificato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    #GET P2 non piu' esistente
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta', 'NomeProprieta2modificato'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 404

    # LIST Ps
    Given url configUrl
    And path servizio_path, 'configurazioni', 'proprieta'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.proprieta == '#[]'
