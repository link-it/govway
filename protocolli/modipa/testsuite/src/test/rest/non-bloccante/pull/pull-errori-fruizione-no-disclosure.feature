
Feature: Test Della fruizione con mock proxy per il profilo di interazione non bloccante rest di tipo PULL senza la disclosure degli errori di interoperabilità


Background:
    * def body_req = read('classpath:bodies/nonblocking-rest-request.json')
    
    * configure followRedirects = false

    * url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoNonBlockingRestPullProxyNoValidazione/v1"

    # Disabilito la error disclosure
    * def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')

    * def problem = read('classpath:test/rest/non-bloccante/pull/error-bodies/invalid-response-from-implementation.json')

    * def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
    * def check_traccia_richiesta_stato = read('./check-tracce/richiesta-stato.feature')
    * def check_traccia_risposta = read('./check-tracce/risposta.feature')
    * def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')



@request-task-no-location
Scenario: Test Fruizione con header location rimosso dal proxy

    * def task_id = "Test-Location-Removed-From-Ack"
    
    Given path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 202, returnHttpHeader:'Location: /tasks/queue/' + task_id})
    When method post
    Then status 502
    And match response == problem
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'


    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@request-task-not-202
Scenario: Richiesta processamento con stato diverso da 202

    * def task_id = "Test-Status-Not-202"
    
    Given path 'tasks', 'queue'
    And request body_req
    And params ({ returnCode: 202, returnHttpHeader:'Location: /tasks/queue/' + task_id })
    When method post
    Then status 502
    And match response == problem
    And match header GovWay-Conversation-ID == task_id
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'


    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: 'Test-Status-Not-202' })



@invalid-status-from-request
Scenario: Richiesta stato operazione con stato http diverso da 200 e 303

    * def task_id = "Test-Invalid-Status-Request"

    Given path 'tasks', 'queue', task_id
    And params ({ returnCode: 200, destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/pending.json', destFileContentType: 'application/json' })
    When method get
    Then status 502
    And match response == problem
    And match header GovWay-Conversation-ID == task_id
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })



@no-location-from-status
Scenario: Richiesta stato operazione completata senza header location

    * def task_id = "Test-Location-Removed-From-Status"

    * def completed_params = 
    """
    ({ 
        returnCode: 303,
        returnHttpHeader: 'Location: http://127.0.0.1:8080/TestService/echo/tasks/result/' + task_id,
        destFile: '/etc/govway/test/protocolli/modipa/rest/non-bloccante/completed.json',
        destFileContentType: 'application/json' 
        })
    """

    Given path 'tasks', 'queue', task_id
    And params completed_params
    When method get
    Then status 502
    And match response == problem
    And match header GovWay-Conversation-ID == task_id
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'

    * call check_traccia_richiesta_stato ({tid: responseHeaders['GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })

@task-response-not-200
Scenario: Ottenimento risorsa processata con stato diverso da 200 OK    

    * def task_id = "Test-Response-Not-200"

    Given path 'tasks', 'result', task_id
    And params ({ returnCode: 200 })
    When method get
    Then status 502
    And match response == problem
    And match header GovWay-Conversation-ID == task_id
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'

    * call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})

    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })