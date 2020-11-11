Feature: Status

Background:

* call read('classpath:crud_commons.feature')


@Get200
Scenario: Status Get 200 OK

    Given url configUrl
    And path 'status'
    And  header Authorization = govwayConfAuth
    When method get
    Then assert responseStatus == 200
    And match header Content-Type == 'application/problem+json'
    And match response contains { 'type' : 'https://httpstatuses.com/200', 'title' : 'OK', 'status' : 200, 'detail' : 'Il servizio funziona correttamente' }

