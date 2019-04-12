Feature: Template delete 404

Scenario: Template delete 404

    Given url configUrl
    And path resourcePath , key
    And header Authorization = govwayConfAuth
    When method delete
    Then status 404