Feature: Template Update 404

Scenario: Template Update 404

#UPDATE
Given url configUrl
And path resourcePath, key
And header Authorization = govwayConfAuth
And request body
When method put
Then status 404