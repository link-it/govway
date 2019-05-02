Feature: CRUD Put Stub

Scenario: Aggiornamento generica entità

Given url configUrl
And path resourcePath
And  header Authorization = govwayConfAuth
And request body
And params query_params
When method put
Then assert responseStatus == 204