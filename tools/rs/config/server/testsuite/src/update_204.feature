Feature: Template Update 204

Scenario: Template Update 204


#CREATE
Given url configUrl
And path resourcePath
And  header Authorization = govwayConfAuth
And request body
And params query_params
When method post
Then assert responseStatus == 201


#UPDATE
Given url configUrl
And path resourcePath, key
And header Authorization = govwayConfAuth
And request body_update
And params query_params
When method put
Then status 204


#DELETE
Given url configUrl
And path resourcePath , delete_key
And header Authorization = govwayConfAuth
And params query_params
When method delete
Then status 204
