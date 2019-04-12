Feature: CRUD Delete Stub

Scenario: Eliminazione per una generica entità

Given url configUrl
And path resourcePath
And  params query_params
And header Authorization = govwayConfAuth
And params query_params
When method delete
Then status 204