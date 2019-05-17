Feature: Get 404 Template

Scenario: Template Test Get 404

Given url configUrl
And path resourcePath
And header Authorization = govwayConfAuth
And params query_params
When method get
Then status 404
