Feature: Test profilo di interazione ModiPA SOAP non bloccante PUSH

Background:

* def task_id = "8695a025-0931-4af4-9c76-26421374c7f2"

* def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
* configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

* def url_fruizione_client_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapNonBlockingPushServer/v1"
* def url_fruizione_server_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1"

* def url_fruizione_client_no_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapNonBlockingPushServerNoValidazione/v1"
* def url_fruizione_server_no_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

* def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
* def check_traccia_richiesta_no_reply_to = read('./check-tracce/richiesta-no-reply-to.feature')
* def check_traccia_risposta = read('./check-tracce/risposta.feature')
* def check_traccia_risposta_no_cid = read('./check-tracce/risposta-no-correlation-id.feature')
* def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')


@test-ok
Scenario: Giro completo e senza errori

* def updated_reply_to = govway_base_path + '/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1'

Given url url_fruizione_client_validazione
And header GovWay-TestSuite-Test-Id = 'test-ok-richiesta-client'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_validazione
And request read('client-request.xml')
When method post
Then status 200
And match response == read('client-request-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

Given url url_fruizione_server_validazione
And header GovWay-TestSuite-Test-Id = 'test-ok-risposta-server'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_server_validazione
And request read('server-response.xml')
When method post
Then status 200
And match response == read('server-response-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServer v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServer v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

@correlation-id-added-by-server
Scenario: L'erogazione del server deve aggiungere lo header X-Correlation-ID se non inserito dal backend

* def updated_reply_to = govway_base_path + '/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1'

Given url url_fruizione_client_validazione
And header GovWay-TestSuite-Test-Id = 'correlation-id-added-by-server'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_validazione
And request read('client-request.xml')
When method post
Then status 200
And match /Envelope/Header/X-Correlation-ID == responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]


* def task_id = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })


@iniezione-header-id-collaborazione
Scenario: Aggiunta dello header X-Correlation-ID a partire dall'id collaborazione

* def url_fruizione_server_helper_collaborazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientHelperCollaborazione/v1"

Given url url_fruizione_server_helper_collaborazione
And header GovWay-TestSuite-Test-Id = 'iniezione-header-id-collaborazione'
And header Content-Type = 'application/soap+xml'
And header GovWay-Conversation-ID = task_id
And header action = url_fruizione_server_helper_collaborazione
And request read('server-response-no-correlation-id.xml')
When method post
Then status 200
And match response == read('server-response-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperCollaborazione v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperCollaborazione v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

Given url url_fruizione_server_helper_collaborazione
And header GovWay-TestSuite-Test-Id = 'iniezione-header-id-collaborazione-query'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_server_helper_collaborazione
And request read('server-response-no-correlation-id.xml')
And param govway_conversation_id = task_id
When method post
Then status 200
And match response == read('server-response-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperCollaborazione v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperCollaborazione v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@iniezione-header-riferimento-id-richiesta
Scenario: Aggiunta dello header X-Correlation-ID a partire dal riferimento id richiesta

* def url_fruizione_server_helper_riferimento = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientHelperRiferimento/v1"

Given url url_fruizione_server_helper_riferimento
And header GovWay-TestSuite-Test-Id = 'iniezione-header-riferimento-id-richiesta'
And header Content-Type = 'application/soap+xml'
And header GovWay-Relates-To = task_id
And header action = url_fruizione_server_helper_riferimento
And request read('server-response-no-correlation-id.xml')
When method post
Then status 200
And match response == read('server-response-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperRiferimento v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperRiferimento v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

Given url url_fruizione_server_helper_riferimento
And header GovWay-TestSuite-Test-Id = 'iniezione-header-riferimento-id-richiesta-query'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_server_helper_riferimento
And request read('server-response-no-correlation-id.xml')
And param govway_relates_to = task_id
When method post
Then status 200
And match response == read('server-response-response.xml')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperRiferimento v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'SoapNonBlockingPushServerHelperRiferimento v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@no-correlation-id-in-client-request-response
Scenario: La fruizione del client solleva errore se non trova lo header x-correlation-id nella risposta

* def updated_reply_to = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_client_no_validazione
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-client-request-response'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_validazione
And request read('client-request.xml')
When method post
Then status 500
And match response == read('error-bodies/no-correlation-id-in-client-request-response.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PUSH' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'Richiesta' },
    { name: 'ProfiloInterazioneAsincrona-ReplyTo', value: updated_reply_to }
])
"""
* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid, 'Richiesta') 
* match result contains deep traccia_to_match

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@no-correlation-id-in-server-response-request
Scenario: L'erogazione del client verifica la presenza dello header x-correlation-id

* def url_erogazione_client_no_validazione = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_erogazione_client_no_validazione
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And header Content-Type = 'application/soap+xml'
And header action = url_erogazione_client_no_validazione
And request read('server-response-no-correlation-id.xml')
When method post
Then status 500
And match response == read('error-bodies/no-correlation-id-in-server-response-request.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


* call check_traccia_risposta_no_cid ({tid: responseHeaders['GovWay-Transaction-ID'][0], api_correlata: 'SoapNonBlockingPushServerNoValidazione v1' })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })

* def url_fruizione_server_no_validazione = govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_server_no_validazione
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And header Content-Type = 'application/soap+xml'
And header action = url_fruizione_client_no_validazione
And request read('server-response-no-correlation-id.xml')
When method post
Then status 500
And match response == read('error-bodies/no-correlation-id-in-server-response-request-fruizione.xml')

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@no-x-reply-to-in-client-request
Scenario: L'erogazione del server verifica la presenza dello header x-reply-to

* def url_erogazione_server_no_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/SoapNonBlockingPushServerNoValidazione/v1"

Given url url_erogazione_server_no_validazione
And request read('client-request-no-reply-to.xml')
And header Content-Type = 'application/soap+xml'
And header action = url_erogazione_server_no_validazione
When method post
Then status 500
And match response == read('error-bodies/no-x-reply-to-in-client-request.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


* call check_traccia_richiesta_no_reply_to ({tid: responseHeaders['GovWay-Transaction-ID'][0] })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })