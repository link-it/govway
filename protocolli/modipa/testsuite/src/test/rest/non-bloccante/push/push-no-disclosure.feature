Feature: Test del profilo di interazione push non bloccante ModiPA senza disclosure di informazioni sugli errori

* def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

* def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')

@no-correlation-id-in-client-request-response
Scenario: La fruizione del client solleva errore se non trova lo header x-correlation-id

* def url_fruizione_client_no_validazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"

Given url url_fruizione_client_no_validazione
And path 'resources', 1, 'M'
And header X-ReplyTo = 'url_che_la_fruizione_sostituisce'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-client-request-response'
And request read('client-request.json')
When method post
Then status 502
And match response == read('error-bodies/invalid-response-from-api-implementation.json')
And match header GovWay-Transaction-ErrorType == 'InvalidResponse'



@no-correlation-id-in-server-response-request
Scenario: L'erogazione del client verifica la presenza dello header x-correlation-id

* def url_erogazione_client_no_validazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

Given url url_erogazione_client_no_validazione
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And request read('server-response.json')
When method post
Then status 400
And match response == read('error-bodies/interop-invalid-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def url_fruizione_server_no_validazione = govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_server_no_validazione
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And request read('server-response.json')
When method post
Then status 400
And match response == read('error-bodies/bad-request.json')
And match header GovWay-Transaction-ErrorType == 'BadRequest'



@no-x-reply-to-in-client-request
Scenario: L'erogazione del server verifica la presenza dello header x-reply-to

* def url_erogazione_server_no_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"

Given url url_erogazione_server_no_validazione
And path 'resources', 1, 'M'
And request read('client-request.json')
When method post
Then status 400
And match response == read('error-bodies/interop-invalid-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

