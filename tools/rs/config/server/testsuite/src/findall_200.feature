Feature: FindAll 200 Template

Scenario:

    # CREATE 

    Given url configUrl
    And path resourcePath
    And  header Authorization = govwayConfAuth
    And request body
    And params query_params
    When method post
    Then assert responseStatus == 201

    # FINDALL

    Given url configUrl
    And path resourcePath
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.items == '#[]'

    # DELETE

    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
