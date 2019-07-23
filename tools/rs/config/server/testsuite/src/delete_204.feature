Feature: Template delete 204

Scenario: Template delete 204

    #CREATE
    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And params query_params
    And request body
    When method post
    Then assert responseStatus == 201

    #DELETE
    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
