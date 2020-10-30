Feature: Test per il profilo di interazione non bloccante rest di tipo PULL senza la disclosure degli errori di interoperabilità

Background:

# La url verso l'erogazione che effettua la validazione.
* def url_invocazione_validazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoNonBlockingRestPull/v1"

# La url verso l'erogazione che non effettua la validazione, serve per controllare gli errori ModiPA
* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoNonBlockingRestPullNoValidazione/v1"

* def url_erogazione_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/ApiDemoNonBlockingRestPull/v1"
* def url_erogazione_no_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/ApiDemoNonBlockingRestPullNoValidazione/v1"

* def body_req = read('classpath:bodies/nonblocking-rest-request.json')
* def task_uid = "32bb4c13-e898-4c12-94db-4ddb18de7919"
* configure followRedirects = false

# Disabilito la error disclosure
* def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')

* def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/invalid-response-from-implementation.json')

* def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
* def check_traccia_richiesta_stato = read('./check-tracce/richiesta-stato.feature')
* def check_traccia_risposta = read('./check-tracce/risposta.feature')
* def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')

@request-task-no-location
Scenario: Richiesta processamento con stato 202 e senza Header Location    

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
    
    Given url url_invocazione
    And path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 201, returnHttpHeader:'Location: /tasks/queue/' + task_uid})
    When method post
    Then status 502
    And match response == problem

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: null })

@invalid-status-from-request
Scenario: Richiesta stato operazione con stato http diverso da 200 e 303

    Given url url_invocazione
    And path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 202, returnHttpHeader:'Location: /tasks/queue/' + task_uid})
    When method post
    Then status 202

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_uid })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_uid })

    Given url url_invocazione
    And path 'tasks', 'queue', 'not_ready_uid'
    And params ({ returnCode: 201, destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/pending.json', destFileContentType: 'application/json' })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: 'not_ready_uid' })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: 'not_ready_uid' })

@no-location-from-status
Scenario: Richiesta stato operazione completata senza header location

    Given url url_invocazione
    And path 'tasks', 'queue', task_uid
    And params ({ returnCode: 303, destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/completed.json', destFileContentType: 'application/json' })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_uid })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_uid })


@task-response-not-200
Scenario: Ottenimento risorsa processata con stato diverso da 200 OK    

    Given url url_invocazione
    And path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 202, returnHttpHeader:'Location: /tasks/queue/' + task_uid})
    When method post
    Then status 202

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_uid })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_uid })

    * def completed_params = 
    """
    ({ 
        returnCode: 303,
        returnHttpHeader: 'Location: http://127.0.0.1:8080/TestService/echo/tasks/result/' + task_uid,
        destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/completed.json',
        destFileContentType: 'application/json' 
        })
    """
    * def expected_location = url_erogazione_no_validazione + "/tasks/result/" + task_uid

    Given url url_invocazione
    And path 'tasks', 'queue', task_uid
    And params completed_params
    When method get
    Then status 303
    And match header Location == expected_location

    * def additional_check = ([ { name: 'ProfiloInterazioneAsincrona-Location', value: expected_location } ])

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0], additional_check: additional_check})
    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], additional_check: additional_check})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_uid })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_uid })

    Given url url_invocazione
    And path 'tasks', 'result', task_uid
    And params ({ returnCode: 202 })
    When method get
    Then status 502
    And match response == problem

    * call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_uid })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_uid })