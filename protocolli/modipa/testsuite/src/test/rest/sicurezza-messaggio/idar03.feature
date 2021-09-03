Feature: Testing Sicurezza Messaggio ModiPA IDAR03

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    
    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }




@connettivita-base
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar03testheader', value: 'TestHeaderRequest' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar03testheader', value: 'TestHeaderResponse' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })



@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/invalid-token-signature-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@manomissione-payload-richiesta
Scenario: Il payload della richiesta viene modificato in modo da non far coincidere la firma e fare arrabbiare l'erogazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@manomissione-payload-risposta
Scenario: Il payload della risposta viene modificato in modo da non far coincidere la firma e fare arrabbiare la fruizione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/manomissione-token-risposta.json')


@manomissione-header-http-firmati-richiesta
Scenario: Lo header da firmare IDAR03TestHeader viene manomesso nella richiesta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 502


@manomissione-header-http-firmati-risposta
Scenario: Lo header da firmare IDAR03TestHeader viene manomesso nella risposta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 502
And match response == read('error-bodies/manomissione-header-http-firmati-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@assenza-header-digest-richiesta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@assenza-header-digest-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-digest-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@response-without-payload
Scenario: Test di un endpoint che non ha il payload nella risposta

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1"

Given url url_invocazione
And path 'resources', 'object'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 201
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def request_digest = get client_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud'     })


@response-without-payload-tampered-header
Scenario: Modifco uno degli header firmati in una risposta senza payload

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1"

Given url url_invocazione
And path 'resources', 'object'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar03-tampered-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/manomissione-header-http-firmati-risposta.json')


@response-without-payload-digest-richiesta
Scenario: Test di un endpoint che non ha il payload nella risposta

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1"

Given url url_invocazione
And path 'resources', 'object'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar03-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 201
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def request_digest = get client_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


@informazioni-utente-header
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-CS-User = "utente-token"
And header GovWay-CS-IPUser = "ip-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token' }
    
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def check_traccia_iu = read('check-tracce/check-traccia-info-utente.feature')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })



@informazioni-utente-query
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-query'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And param govway_cs_user = "utente-token"
And param govway_cs_ipuser = "ip-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token' }
    
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def check_traccia_iu = read('check-tracce/check-traccia-info-utente.feature')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })


@informazioni-utente-mixed
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-mixed'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-CS-User = "utente-token"
And param govway_cs_ipuser = "ip-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token' }
    
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def check_traccia_iu = read('check-tracce/check-traccia-info-utente.feature')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })


@informazioni-utente-static
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtenteStatic/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-static'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'codice-ente-static' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token-static' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token-static' }
    
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def check_traccia_iu = read('check-tracce/check-traccia-info-utente.feature')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })



@informazioni-utente-custom
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http custom

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtenteCustom/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-custom'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header CodiceEnteCustom = 'codice-ente-custom'
And header UserIDUtenteCustom = "utente-token"
And header IndirizzoIPUtenteCustom = "ip-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'codice-ente-custom' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token' }
    
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def check_traccia_iu = read('check-tracce/check-traccia-info-utente.feature')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_iu ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia_iu ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })



@no-informazioni-utente-at-erogazione
Scenario: All'erogazione non arrivano nel token i claim con le informazioni utente

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'no-informazioni-utente-at-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502

@no-informazioni-utente-at-fruizione
Scenario: Alla fruizione non passo gli header per l'informazione utente, facendola arrabbiare

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400
And match response == read('error-bodies/no-informazioni-utente-at-fruizione.json')
And match header GovWay-Transaction-ErrorType == "BadRequest"


@connettivita-base-header-bearer
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderBearer/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar03-header-bearer'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })


@idar03-token-richiesta
Scenario: Giro ok idar03 con il token soltanto nella richiesta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03TokenRichiesta/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-token-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")

* def request_digest = get client_token $.payload.signed_headers..digest


* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta })



@idar03-token-risposta
Scenario: Giro ok idar03 con il token soltanto nella risposta
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03TokenRisposta/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-token-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def response_digest = get server_token $.payload.signed_headers..digest


* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta })



@idar03-token-azione-puntuale
Scenario: Giro ok idar03 con il token abilitato solo sulla richiesta per una specifica azione, globalmente è richiesta/risposta.

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03TokenAzionePuntuale/v1"
And path 'resources', 'object'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-token-azione-puntuale'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 201
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")

* def request_digest = get client_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

# Gli faccio fare anche una put per verificare che in quest'altro caso il token c'è sia nella richiesta che nella risposta
# Utilizzo la risposta con le proprietà invertite perchè il proxy karate le inverte
* def resp =
"""
({
        "a" : {
        "a2": "RGFuJ3MgVG9vbHMgYXJlIGNvb2wh",
        "a1s": [ 1, 2 ]
      },
      "b": "Stringa di esempio"
})
"""

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03TokenAzionePuntuale/v1"
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-token-azione-puntuale-default'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method put
Then status 200
And match response == resp
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })



@doppi-header
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


@doppi-header-manomissione-payload-richiesta
Scenario: Il payload della richiesta viene modificato in modo da non far coincidere la firma e fare arrabbiare l'erogazione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-manomissione-payload-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400


@doppi-header-manomissione-payload-risposta
Scenario: Il payload della risposta viene modificato in modo da non far coincidere la firma e fare arrabbiare la fruizione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-manomissione-payload-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/manomissione-token-risposta.json')

@doppi-header-assenza-header-digest-richiesta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare l'erogazione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400

@doppi-header-assenza-header-digest-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-digest-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-digest-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

@doppi-header-assenza-header-authorization-richiesta
Scenario: Il proxy rimuove lo header Authorization per far arrabbiare l'erogazione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-authorization-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400

@doppi-header-assenza-header-authorization-risposta
Scenario: Il proxy rimuove lo header Authorization per far arrabbiare la fruizione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-authorization-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-authorization-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

@doppi-header-assenza-header-agid-jwt-signature-richiesta
Scenario: Il proxy rimuove lo header Agid-JWT-Signature per far arrabbiare l'erogazione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-agid-jwt-signature-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400

@doppi-header-assenza-header-agid-jwt-signature-risposta
Scenario: Il proxy rimuove lo header Agid-JWT-Signature per far arrabbiare la fruizione (presenza sia dell'header Authorization che Agid-JWT-Signature)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-assenza-header-agid-jwt-signature-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-agid-jwt-signature-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

@doppi-header-audience-risposta-authorization-non-valida-rispetto-client
Scenario: L'erogatore genera un audience, nel token Authorization, differente da quello atteso e registrato sul client (atteso id identico nei due header)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudienceClient/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-authorization-non-valida-rispetto-client'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-errato'
And header GovWay-TestSuite-Test-Aud-Modi = 'http://client2'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-risposta-non-valido.json')

* def other_checks_risposta = 
"""
([
		{ name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'http://client2' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta

@doppi-header-audience-risposta-agid-jwt-signature-non-valida-rispetto-client
Scenario: L'erogatore genera un audience, nel token Agid-JWT-Signature, differente da quello atteso e registrato sul client (atteso id identico nei due header)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudienceClient/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-agid-jwt-signature-non-valida-rispetto-client'
And header GovWay-TestSuite-Test-Aud-Authorization = 'http://client2'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-agid-jwt-signature-risposta-non-valido.json')

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta

@doppi-header-audience-risposta-differente-audience-valida-rispetto-client
Scenario: L'erogatore genera un audience, nel token Agid-JWT-Signature, differente da quello presente nel token Authorization (sono attesi due valori differenti, nell'authorization quello del client)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudienceClient/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-differente-audience-valida-rispetto-client'
And header GovWay-TestSuite-Test-Aud-Modi = 'http://client2.integrity'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'http://client2.integrity' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta

@doppi-header-audience-risposta-differente-audience-non-valida-rispetto-client
Scenario: L'erogatore genera un audience, nel token Agid-JWT-Signature, differente da quello presente nel token Authorization (sono attesi due valori differenti, nell'authorization quello del client). Il valore inserito nel token Agid è errato.

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudienceClient/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-differente-audience-non-valida-rispetto-client'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-agid-jwt-signature-risposta-non-valido.json')

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta


@doppi-header-audience-risposta-valore-statico
Scenario: Il fruitore si attende nei token in risposta una valore statico di audience, rispetto al normale caso dinamico associato al client

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-valore-statico'
And header GovWay-TestSuite-Test-AudRequest = 'testsuite'
And header GovWay-TestSuite-Test-Aud = 'valore-statico'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta


@doppi-header-audience-risposta-valore-statico-non-valido
Scenario: Il fruitore si attende nei token in risposta una valore statico di audience, rispetto al normale caso dinamico associato al client. Il valore ritornato non è valido.

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-valore-statico-non-valido'
And header GovWay-TestSuite-Test-AudRequest = 'testsuite'
And header GovWay-TestSuite-Test-Aud = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-risposta-non-valido-header-duplicati.json')

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta

@doppi-header-audience-risposta-diversi-valori-statici
Scenario: Il fruitore si attende nei token in risposta valori statici di audience differenti nei 2 header

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-diversi-valori-statici'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-statico-authorization'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-statico-integrity'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico-authorization' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-statico-integrity' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta


@doppi-header-audience-risposta-diversi-valori-statici-authorization-non-valido
Scenario: Il fruitore si attende nei token in risposta valori statici di audience differenti nei 2 header. Il valore indicato nel token Authorization non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-diversi-valori-statici-authorization-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-errato'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-statico-integrity'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-risposta-non-valido.json')

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-statico-integrity' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta


@doppi-header-audience-risposta-diversi-valori-statici-agid-jwt-signature-non-valido
Scenario: Il fruitore si attende nei token in risposta valori statici di audience differenti nei 2 header. Il valore indicato nel token Agid-JWT-Signature non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-risposta-diversi-valori-statici-agid-jwt-signature-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-statico-authorization'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502
And match response == read('error-bodies/aud-token-agid-jwt-signature-risposta-non-valido.json')

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico-authorization' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Risposta') 
* match result contains deep other_checks_risposta


@doppi-header-audience-richiesta-stesso-valore
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-stesso-valore'
And header GovWay-TestSuite-Test-Aud-Authorization = 'testsuite-audience'
And header GovWay-TestSuite-Test-Aud-Modi = 'testsuite-audience'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta

@doppi-header-audience-richiesta-stesso-valore-authorization-non-valido
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header. Il valore indicato nel token Authorization non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-stesso-valore-authorization-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-errato'
And header GovWay-TestSuite-Test-Aud-Modi = 'testsuite-audience'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 400

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta

@doppi-header-audience-richiesta-stesso-valore-agid-jwt-signature-non-valido
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header. Il valore indicato nel token Agid-JWT-Signature non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-stesso-valore-agid-jwt-signature-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'testsuite-audience'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 400

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta


@doppi-header-audience-richiesta-differente-valore
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-differente-valore'
And header GovWay-TestSuite-Test-Aud-Authorization = 'testsuite-audience-authorization'
And header GovWay-TestSuite-Test-Aud-Modi = 'testsuite-audience-integrity'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience-authorization' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience-integrity' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta

@doppi-header-audience-richiesta-differente-valore-authorization-non-valido
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header. Il valore indicato nel token Authorization non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-differente-valore-authorization-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'valore-errato'
And header GovWay-TestSuite-Test-Aud-Modi = 'testsuite-audience-integrity'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 400

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience-integrity' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta

@doppi-header-audience-richiesta-differente-valore-agid-jwt-signature-non-valido
Scenario: Il fruitore genera nei token in richiesta lo stesso valore di audience nei 2 differenti header. Il valore indicato nel token Agid-JWT-Signature non è valido

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-audience-richiesta-differente-valore-agid-jwt-signature-non-valido'
And header GovWay-TestSuite-Test-Aud-Authorization = 'testsuite-audience-authorization'
And header GovWay-TestSuite-Test-Aud-Modi = 'valore-errato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 400

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience-authorization' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_traccia(tid,'Richiesta') 
* match result contains deep other_checks_richiesta


@doppi-header-differenti-id-authorization
Scenario: L'identificativo jti nei due header viene generato differente sia nella richiesta che nella risposta. Come id messaggio viene usato quello dell'Authorization

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAuthorization/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-differenti-id-authorization'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def request_jti_authorization = get client_authorization_token $.payload.jti
* def response_jti_authorization = get server_authorization_token $.payload.jti
* def request_jti_integrity = get client_integrity_token $.payload.jti
* def response_jti_integrity = get server_integrity_token $.payload.jti


* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: request_jti_authorization },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityMessageId', value: request_jti_integrity }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: response_jti_authorization },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityMessageId', value: response_jti_integrity }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


* def transazioni_checks_richiesta = 
"""
([
    { id_messaggio_richiesta: request_jti_authorization },
])
"""

* def transazioni_checks_risposta = 
"""
([
    { id_messaggio_risposta: response_jti_authorization },
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def tidmessaggiorichiesta = get_info_transazione(tid,'id_messaggio_richiesta') 
* match tidmessaggiorichiesta contains deep transazioni_checks_richiesta
* def tidmessaggiorisposta = get_info_transazione(tid,'id_messaggio_risposta') 
* match tidmessaggiorisposta contains deep transazioni_checks_risposta

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* def tidmessaggiorichiesta = get_info_transazione(tid,'id_messaggio_richiesta') 
* match tidmessaggiorichiesta contains deep transazioni_checks_richiesta
* def tidmessaggiorisposta = get_info_transazione(tid,'id_messaggio_risposta') 
* match tidmessaggiorisposta contains deep transazioni_checks_risposta



@doppi-header-differenti-id-agid-jwt-signature
Scenario: L'identificativo jti nei due header viene generato differente sia nella richiesta che nella risposta. Come id messaggio viene usato quello in Agid-JWT-Signature

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAgidJWTSignature/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-differenti-id-agid-jwt-signature'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def request_jti_authorization = get client_authorization_token $.payload.jti
* def response_jti_authorization = get server_authorization_token $.payload.jti
* def request_jti_integrity = get client_integrity_token $.payload.jti
* def response_jti_integrity = get server_integrity_token $.payload.jti


* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-AuthorizationMessageId', value: request_jti_authorization },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: request_jti_integrity },
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggio-AuthorizationMessageId', value: response_jti_authorization },
    { name: 'ProfiloSicurezzaMessaggio-MessageId', value: response_jti_integrity },
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


* def transazioni_checks_richiesta = 
"""
([
    { id_messaggio_richiesta: request_jti_integrity },
])
"""

* def transazioni_checks_risposta = 
"""
([
    { id_messaggio_risposta: response_jti_integrity },
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def tidmessaggiorichiesta = get_info_transazione(tid,'id_messaggio_richiesta') 
* match tidmessaggiorichiesta contains deep transazioni_checks_richiesta
* def tidmessaggiorisposta = get_info_transazione(tid,'id_messaggio_risposta') 
* match tidmessaggiorisposta contains deep transazioni_checks_risposta

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* def tidmessaggiorichiesta = get_info_transazione(tid,'id_messaggio_richiesta') 
* match tidmessaggiorichiesta contains deep transazioni_checks_richiesta
* def tidmessaggiorisposta = get_info_transazione(tid,'id_messaggio_risposta') 
* match tidmessaggiorisposta contains deep transazioni_checks_risposta

