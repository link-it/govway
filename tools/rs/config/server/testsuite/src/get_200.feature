Feature: Get 200 Template

Scenario: Template Test Get 200

    # CREATE

    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And request body
    And params query_params
    When method post
    Then assert responseStatus == 204

    # READ

    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    # DELETE

    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
