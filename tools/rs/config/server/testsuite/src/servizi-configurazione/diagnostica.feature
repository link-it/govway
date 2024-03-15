Feature: Configurazione Registrazione Diagnostici

Background:
    * def diagDefault = read('classpath:bodies/registrazione-diagnostici-default.json')
    * def diag = read('classpath:bodies/registrazione-diagnostici.json')

@Update204
Scenario: Update Registrazione Diagnostici

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diagDefault

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'fatal'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'error-protocol'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'error-integration'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'info-protocol'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'info-integration'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'debug-low'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'debug-medium'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'debug-high'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    * eval diag.severita = 'all'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diag
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diag

    # ripristino default

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And request diagDefault
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'diagnostici'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains diagDefault


 
