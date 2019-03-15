Feature: CRUD Delete Stub

Scenario: Eliminazione per una generica entità

Given url configUrl
And path resourcePath
And header Authorization = govwayConfAuth
When method delete
Then status 204