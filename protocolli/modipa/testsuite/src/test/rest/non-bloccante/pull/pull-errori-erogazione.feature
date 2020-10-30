Feature: Test rilevamento errori lato erogazione

Background:

* configure followRedirects = false

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoNonBlockingRestPullProxyNoValidazione/v1"
* url url_invocazione

* def body_req = read('classpath:bodies/nonblocking-rest-request.json')

* def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
* configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

* def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
* def check_traccia_richiesta_stato = read('./check-tracce/richiesta-stato.feature')
* def check_traccia_risposta = read('./check-tracce/risposta.feature')
* def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')


@request-task-no-location
Scenario: Richiesta processamento con stato 202 e senza Header Location
    
    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/request-task-no-location-erogazione.json')

    Given url url_invocazione
    And path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 202 })
    When method post
    Then status 502
    And match response == problem

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: null })

@request-task-not-202
Scenario: Richiesta processamento con stato diverso da 202

    * def task_id = "Test-Erogazione-Status-Not-202"
    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/request-task-not-202-erogazione.json')    
    
    Given path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 201, returnHttpHeader:'Location: /tasks/queue/' + task_id})
    When method post
    Then status 502
    And match response == problem

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})
    
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: null })

@invalid-status-from-request
Scenario: Richiesta stato operazione con stato http diverso da 200 e 303

    * def task_id = "Test-Erogazione-Invalid-Status-Request"
    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/invalid-status-from-request-erogazione.json')    

    Given url url_invocazione
    And path 'tasks', 'queue', task_id
    And params ({ returnCode: 201, destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/pending.json', destFileContentType: 'application/json' })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@no-location-from-status
Scenario: Richiesta stato operazione completata senza header location

    * def task_id = "Test-Erogazione-Location-Removed-From-Status"
    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/no-location-from-status-erogazione.json')

    Given url url_invocazione
    And path 'tasks', 'queue', task_id
    And params ({ returnCode: 303, destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/completed.json', destFileContentType: 'application/json' })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

@task-response-not-200
Scenario: Ottenimento risorsa processata con stato diverso da 200 OK    

    * def task_id = "Test-Erogazione-Response-Not-200"
    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/task-response-not-200-erogazione.json')


    Given url url_invocazione
    And path 'tasks', 'result', task_id
    And params ({ returnCode: 202 })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

