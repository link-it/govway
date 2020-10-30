Feature: Test profilo di interazione ModiPA SOAP non bloccante PUSH

Background:

* def task_id = "8695a025-0931-4af4-9c76-26421374c7f2"

* def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')

* def url_fruizione_client_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapNonBlockingPushServer/v1"
* def url_fruizione_server_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1"

* def url_fruizione_client_no_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapNonBlockingPushServerNoValidazione/v1"
* def url_fruizione_server_no_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"


@no-correlation-id-in-client-request-response
Scenario: La fruizione del client solleva errore se non trova lo header x-correlation-id nella risposta

* def updated_reply_to = govway_base_path + "/rest/in/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_client_no_validazione
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-client-request-response'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_validazione
And request read('client-request.xml')
When method post
Then status 500
And match response == read('error-bodies/invalid-response-from-api-implementation.xml')
And match header GovWay-Transaction-ErrorType == 'InvalidResponse'



@no-correlation-id-in-server-response-request
Scenario: L'erogazione del client verifica la presenza dello header x-correlation-id

* def url_erogazione_client_no_validazione = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_erogazione_client_no_validazione
And header Content-Type = 'application/soap+xml'
And header action = url_erogazione_client_no_validazione
And request read('server-response-no-correlation-id.xml')
When method post
Then status 500
And match response == read('error-bodies/interop-invalid-request.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


* def url_fruizione_server_no_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_server_no_validazione
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request-no-disclosure'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_no_validazione
And request read('server-response-no-correlation-id.xml')
When method post
Then status 500
And match response == read('error-bodies/bad-request.xml')


@no-x-reply-to-in-client-request
Scenario: L'erogazione del server verifica la presenza dello header x-reply-to

* def url_erogazione_server_no_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/SoapNonBlockingPushServerNoValidazione/v1"

Given url url_erogazione_server_no_validazione
And request read('client-request-no-reply-to.xml')
And header Content-Type = 'application/soap+xml'
And header action = url_erogazione_server_no_validazione
When method post
Then status 500
And match response == read('error-bodies/interop-invalid-request.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'
