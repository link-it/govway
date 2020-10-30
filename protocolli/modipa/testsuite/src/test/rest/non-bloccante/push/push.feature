Feature: Test del profilo di interazione ModiPA REST non bloccante PUSH


Background:

* def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

* def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
* configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

* def url_fruizione_client_validazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestNonBlockingPushServer/v1"
* def url_fruizione_client_no_validazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"

* def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
* def check_traccia_richiesta_no_reply_to = read('./check-tracce/richiesta-no-reply-to.feature')
* def check_traccia_risposta = read('./check-tracce/risposta.feature')
* def check_traccia_risposta_no_cid = read('./check-tracce/risposta-no-correlation-id.feature')
* def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')

@test-ok
Scenario: Giro completo e senza errori

* def url_fruizione_server_validazione = govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"
* def updated_reply_to = govway_base_path + '/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1'

Given url url_fruizione_client_validazione
And path 'resources', 1, 'M'
And header X-ReplyTo = 'url_che_la_fruizione_sostituisce'
And header GovWay-TestSuite-Test-Id = 'test-ok-richiesta-client'
And request read('client-request.json')
When method post
Then status 202
And match header X-Correlation-ID == task_id
And match header GovWay-Conversation-ID == task_id

* call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })

Given url url_fruizione_server_validazione
And path 'MResponse'
And header X-Correlation-ID = task_id
And header GovWay-TestSuite-Test-Id = 'test-ok-risposta-server'
And request read('server-response.json')
When method post
Then status 200
And match response == read('server-response-response.json')
And match header GovWay-Conversation-ID == task_id

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerValidazione v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerValidazione v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@correlation-id-added-by-server
Scenario: L'erogazione del server deve aggiungere lo header X-Correlation-ID se non inserito dal backend

* def updated_reply_to = 'http://localhost:8080/govway/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1'

Given url url_fruizione_client_validazione
And path 'resources', 1, 'M'
And header X-ReplyTo = 'url_che_la_fruizione_sostituisce'
And header GovWay-TestSuite-Test-Id = 'correlation-id-added-by-server'
And request read('client-request.json')
When method post
Then status 202

* def task_id = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })
* call check_traccia_richiesta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], reply_to: updated_reply_to, cid: task_id })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@iniezione-header-id-collaborazione
Scenario: Aggiunta dello header X-Correlation-ID a partire dall'id collaborazione

* def url_fruizione_server_helper_collaborazione = govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClientHelperCollaborazioneNoValidazione/v1"

Given url url_fruizione_server_helper_collaborazione
And path 'MResponse'
And header GovWay-Conversation-ID = task_id
And header GovWay-TestSuite-Test-Id = 'iniezione-header-id-collaborazione'
And request read('server-response.json')
When method post
Then status 200
And match response == read('server-response-response.json')

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperCollaborazione v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperCollaborazione v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


Given url url_fruizione_server_helper_collaborazione
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'iniezione-header-id-collaborazione-query'
And request read('server-response.json')
And param govway_conversation_id = task_id
When method post
Then status 200
And match response == read('server-response-response.json')

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperCollaborazione v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperCollaborazione v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@iniezione-header-riferimento-id-richiesta
Scenario: Aggiunta dello header X-Correlation-ID a partire dal riferimento id richiesta

* def url_fruizione_server_helper_riferimento = govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClientHelperRiferimentoNoValidazione/v1"

Given url url_fruizione_server_helper_riferimento
And path 'MResponse'
And header GovWay-Relates-To = task_id
And header GovWay-TestSuite-Test-Id = 'iniezione-header-riferimento-id-richiesta'
And request read('server-response.json')
When method post
Then status 200
And match response == read('server-response-response.json')

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperRiferimento v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperRiferimento v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


Given url url_fruizione_server_helper_riferimento
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'iniezione-header-riferimento-id-richiesta-query'
And request read('server-response.json')
And param govway_relates_to = task_id
When method post
Then status 200
And match response == read('server-response-response.json')

* call check_traccia_risposta ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperRiferimento v1' })
* call check_traccia_risposta ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], cid: task_id, api_correlata: 'RestNonBlockingPushServerHelperRiferimento v1' })

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0], id_collaborazione: task_id })


@no-correlation-id-in-client-request-response
Scenario: La fruizione del client solleva errore se non trova lo header x-correlation-id nella risposta

* def updated_reply_to = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_client_no_validazione
And path 'resources', 1, 'M'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-client-request-response'
And request read('client-request.json')
When method post
Then status 502
And match response == read('error-bodies/no-correlation-id-in-client-request-response.json')
And match header GovWay-Transaction-ErrorType == "InteroperabilityInvalidResponse"

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
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

* def result = get_traccia(tid, 'Richiesta') 
* match result contains deep traccia_to_match

* def traccia_to_match = 
"""
([
    { name: 'ProfiloInterazione', value: 'nonBloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC01' },
    { name: 'ProfiloInterazioneAsincrona-Tipo', value: 'PUSH' },
    { name: 'ProfiloInterazioneAsincrona-Ruolo', value: 'Richiesta' }
])
"""

* def result = get_traccia(tid, 'Risposta') 
* match result contains deep traccia_to_match

* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@no-correlation-id-in-server-response-request
Scenario: L'erogazione del client verifica la presenza dello header x-correlation-id

* def url_erogazione_client_no_validazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

Given url url_erogazione_client_no_validazione
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And request read('server-response.json')
When method post
Then status 400
And match response == read('error-bodies/no-correlation-id-in-server-response-request.json')
And match header GovWay-Transaction-ErrorType == "InteroperabilityInvalidRequest"

* call check_traccia_risposta_no_cid ({tid: responseHeaders['GovWay-Transaction-ID'][0], api_correlata: 'RestNonBlockingPushServer v1' })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })

* def url_fruizione_server_no_validazione = govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

Given url url_fruizione_server_no_validazione
And path 'MResponse'
And header GovWay-TestSuite-Test-Id = 'no-correlation-id-in-server-response-request'
And request read('server-response.json')
When method post
Then status 400
And match response == read('error-bodies/no-correlation-id-in-server-response-request-fruizione.json')
And match header GovWay-Transaction-ErrorType == "BadRequest"


* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@no-x-reply-to-in-client-request
Scenario: L'erogazione del server verifica la presenza dello header x-reply-to

* def url_erogazione_server_no_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"

Given url url_erogazione_server_no_validazione
And path 'resources', 1, 'M'
And request read('client-request.json')
When method post
Then status 400
And match response == read('error-bodies/no-x-reply-to-in-client-request.json')
And match header GovWay-Transaction-ErrorType == "InteroperabilityInvalidRequest"


* call check_traccia_richiesta_no_reply_to ({tid: responseHeaders['GovWay-Transaction-ID'][0], cid: task_id })
* call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })
