Feature: Template Update 400

Scenario: Template Update 400

#UPDATE
Given url configUrl
And path resourcePath
And header Authorization = govwayConfAuth
And request body
And params query_params
When method put
Then status 400