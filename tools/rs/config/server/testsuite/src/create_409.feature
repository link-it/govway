Feature: Template Create 409

Scenario: Template Test Create 409

    #CREATE
    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And request body
    And params query_params
    When method post
    Then assert responseStatus == 201

    #CREATE (CONFLICT)
    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And request body
    And params query_params
    When method post
    Then assert responseStatus == 409

    #DELETE
    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
