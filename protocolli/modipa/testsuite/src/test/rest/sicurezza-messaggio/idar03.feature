Feature: Testing Sicurezza Messaggio ModiPA IDAR03

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    
    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

	  * def formatDate =
		"""
		function(time) {
			var TokenUtils = Java.type("org.openspcoop2.pdd.core.token.parser.TokenUtils");
		  var date = TokenUtils.parseTimeInSecond(time);
		  var DateUtils = Java.type("org.openspcoop2.utils.date.DateUtils");
		  return DateUtils.getSimpleDateFormatMs().format(date);
		} 
		"""


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



@digest-hex
Scenario: Test digest encoding hex

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03-DigestHex/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'digest-hex-idar03'
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




@assenza-header-integrity-richiesta
Scenario: Il proxy rimuove lo header integrity per far arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-integrity-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@assenza-header-integrity-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-integrity-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-integrity-risposta.json')
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
And path 'resources', 1, 'M2CacheTest'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

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



@doppi-header-solo-integrity-risposta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature nella richiesta e solo Agid-JWT-Signature nella risposta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'soloIntegrityRisposta'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-solo-integrity-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Authorization-Token == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


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



@doppi-header-token-date-differenti-richiesta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella richiesta

* def client_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg3MTIsIm5iZiI6MTYzMTU0ODcxMiwiZXhwIjoyMjYyMjY4NzEyLCJqdGkiOiI3MjA2NDg0ZC0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSJ9.Bozl5SddjPJ6BBXyEYtPOp-FlNv5Dmj55JcL3IsyN-NTgsjj1QzcP__laNqARK4h3XS9JeshFDt3DgPrxc3_DHMnZUG7U6fnkdq_ivO5hJpYEkJ7zuwvTW-lShbezSYjj51d-zya7aVk9sC49X5jn46rPM6peJbJdn27riVjEzhEkMNsNXizIQn7yeCYaEGXWs1FFIfU78AznF69DW4WNOMxJY-Cda2cSyTuARYNBR7BcyjQvL3q3WCXIwd13LXEbRbCjKZrtSAOFuslaIipcEs_g0e932Nq2KRdfiKAdBizL67cfgxiJGzJgiQv-yC9O-QgwZWpgIZD1csz2Mgu8A'
* def client_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg4OTYsIm5iZiI6MTYzMTU0ODg5NiwiZXhwIjoyMjYyMjY4ODk2LCJqdGkiOiJkZjY5NWQyYS0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTAzMWY3MDIzOTEzOTQ2ZmU0ZjEwYzI0NmQwYzhmNDFiMmNhYzVlZDNkNTgzNGMwZjQ3MDM1YWQzMDkyMTZlNjYifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb247IGNoYXJzZXQ9VVRGLTgifV19.VlGhzz5q6UWPhM3xxhjKcZdmAAbSOFwYTB_MQ1YmAzcNJ7ZHunmo9VrT7lYcOBmTknDY1ZyZ7_oT3sqtkEIRybxGIfJwMge0zqm4gz0OMXQORrM5EXsALR9--tILyNgVsOQpzzL2X5a4o-M6B9mKvpF2OczSOzPYkd0goW4jEEzy2sMHyTdsygaWGzhbJE7W9k1VjUVT_M3OcfWm6mL5JgHf--VJf2n2C8_K-seuU719DswlXGnTtVD2O4mpZ_cS21kuHZVkuvPP0Z36K2DbjWkFTq-pDPV0JCG0SZs0v-mSRR8d9_xtfyBrsLZ6j4J-85epKhou19pNjLKA2p80Zg'
* def request_digest = 'SHA-256=031f7023913946fe4f10c246d0c8f41b2cac5ed3d5834c0f47035ad309216e66'

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-richiesta'
And header Authorization = client_authorization_token
And header Agid-JWT-Signature = client_integrity_token
And header Digest = request_digest
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#present'
And match header Agid-JWT-Signature == '#present'

* def client_authorization_token_decoded = decode_token(client_authorization_token, "Bearer")
* def client_integrity_token_decoded = decode_token(client_integrity_token, "AGID")
* def request_digest = get client_integrity_token_decoded $.payload.signed_headers..digest
* def request_integrity_iat = get client_integrity_token_decoded $.payload.iat
* def request_integrity_nbf = get client_integrity_token_decoded $.payload.nbf
* def request_integrity_exp = get client_integrity_token_decoded $.payload.exp
* def request_integrity_iat_format = formatDate(request_integrity_iat)
* def request_integrity_nbf_format = formatDate(request_integrity_nbf)
* def request_integrity_exp_format = formatDate(request_integrity_exp)

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityIssuedAt', value: request_integrity_iat_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityNotBefore', value: request_integrity_nbf_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityExpiration', value: request_integrity_exp_format }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token_decoded, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


@doppi-header-token-date-differenti-risposta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella risposta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def response_digest = get server_integrity_token $.payload.signed_headers..digest
* def response_integrity_iat = get server_integrity_token $.payload.iat
* def response_integrity_nbf = get server_integrity_token $.payload.nbf
* def response_integrity_exp = get server_integrity_token $.payload.exp
* def response_integrity_iat_format = formatDate(response_integrity_iat)
* def response_integrity_nbf_format = formatDate(response_integrity_nbf)
* def response_integrity_exp_format = formatDate(response_integrity_exp)

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityIssuedAt', value: response_integrity_iat_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityNotBefore', value: response_integrity_nbf_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityExpiration', value: response_integrity_exp_format }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

@doppi-header-token-date-differenti-richiesta-authorization-scaduta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella richiesta. Il token 'Authorization e' scaduto'

* def client_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDkwMDgsIm5iZiI6MTYzMTU0OTAwOCwiZXhwIjoxNjMxNTQ5MzA4LCJqdGkiOiIyMjJmYzg5My0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSJ9.NiLBU95q-1bKElffheJTnOeA6-EqDbKRN79KDw0_7YJp3RSjZC9sSzY6GCDEdt9AjH2WOZj5rYLpW35ER7T_jmzTcjYlCPrCtkD-dZLX7hmSSyz7xO7XFrDmmzDrHPn7xHv8RSvG0y-uoHTul1JdmuH4ljSoQQVs8VG8x7CWWG8ayqbWC43FhD-mgdyzHlk2TFTg7C4t7tj0BJP6E-kzymBbJ5Jip4YRSPUrGJWGlZx8l-xHIP_mF_O3iuG9hfGHw4is6HIruMwjtJqouPOUwjWGqbRxwsk3T3VSS_20XHoV6HFeSfPXTONU5E9l80g70ylMv_h3thIf66OhtoHrug'
* def client_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg4OTYsIm5iZiI6MTYzMTU0ODg5NiwiZXhwIjoyMjYyMjY4ODk2LCJqdGkiOiJkZjY5NWQyYS0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTAzMWY3MDIzOTEzOTQ2ZmU0ZjEwYzI0NmQwYzhmNDFiMmNhYzVlZDNkNTgzNGMwZjQ3MDM1YWQzMDkyMTZlNjYifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb247IGNoYXJzZXQ9VVRGLTgifV19.VlGhzz5q6UWPhM3xxhjKcZdmAAbSOFwYTB_MQ1YmAzcNJ7ZHunmo9VrT7lYcOBmTknDY1ZyZ7_oT3sqtkEIRybxGIfJwMge0zqm4gz0OMXQORrM5EXsALR9--tILyNgVsOQpzzL2X5a4o-M6B9mKvpF2OczSOzPYkd0goW4jEEzy2sMHyTdsygaWGzhbJE7W9k1VjUVT_M3OcfWm6mL5JgHf--VJf2n2C8_K-seuU719DswlXGnTtVD2O4mpZ_cS21kuHZVkuvPP0Z36K2DbjWkFTq-pDPV0JCG0SZs0v-mSRR8d9_xtfyBrsLZ6j4J-85epKhou19pNjLKA2p80Zg'
* def request_digest = 'SHA-256=031f7023913946fe4f10c246d0c8f41b2cac5ed3d5834c0f47035ad309216e66'

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-richiesta-authorization-scaduta'
And header Authorization = client_authorization_token
And header Agid-JWT-Signature = client_integrity_token
And header Digest = request_digest
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/ttl-scaduto-in-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def client_authorization_token_decoded = decode_token(client_authorization_token, "Bearer")
* def client_integrity_token_decoded = decode_token(client_integrity_token, "AGID")
* def request_digest = get client_integrity_token_decoded $.payload.signed_headers..digest
* def request_integrity_iat = get client_integrity_token_decoded $.payload.iat
* def request_integrity_nbf = get client_integrity_token_decoded $.payload.nbf
* def request_integrity_exp = get client_integrity_token_decoded $.payload.exp
* def request_integrity_iat_format = formatDate(request_integrity_iat)
* def request_integrity_nbf_format = formatDate(request_integrity_nbf)
* def request_integrity_exp_format = formatDate(request_integrity_exp)

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityIssuedAt', value: request_integrity_iat_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityNotBefore', value: request_integrity_nbf_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityExpiration', value: request_integrity_exp_format }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token_decoded, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


@doppi-header-token-date-differenti-richiesta-integrity-scaduta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella richiesta. Il token 'Agid-JWT-Signature e' scaduto'

* def client_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg3MTIsIm5iZiI6MTYzMTU0ODcxMiwiZXhwIjoyMjYyMjY4NzEyLCJqdGkiOiI3MjA2NDg0ZC0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSJ9.Bozl5SddjPJ6BBXyEYtPOp-FlNv5Dmj55JcL3IsyN-NTgsjj1QzcP__laNqARK4h3XS9JeshFDt3DgPrxc3_DHMnZUG7U6fnkdq_ivO5hJpYEkJ7zuwvTW-lShbezSYjj51d-zya7aVk9sC49X5jn46rPM6peJbJdn27riVjEzhEkMNsNXizIQn7yeCYaEGXWs1FFIfU78AznF69DW4WNOMxJY-Cda2cSyTuARYNBR7BcyjQvL3q3WCXIwd13LXEbRbCjKZrtSAOFuslaIipcEs_g0e932Nq2KRdfiKAdBizL67cfgxiJGzJgiQv-yC9O-QgwZWpgIZD1csz2Mgu8A'
* def client_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDkwMDgsIm5iZiI6MTYzMTU0OTAwOCwiZXhwIjoxNjMxNTQ5MzA4LCJqdGkiOiIyMjJmYzg5My0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTAzMWY3MDIzOTEzOTQ2ZmU0ZjEwYzI0NmQwYzhmNDFiMmNhYzVlZDNkNTgzNGMwZjQ3MDM1YWQzMDkyMTZlNjYifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb247IGNoYXJzZXQ9VVRGLTgifV19.15qMTUpS4WrQgD2aJ3w_6oMPF6hSeEGWUqeNLG14iURwda7ba_ZpPKh1UqmsTxnKpWs4I5MJViQlYUBl33OG53Oe3aXM8rAse5J3jhYNG0rgCzNqM036ocj4vsZpFbZAuhlQDwPd2OSabfcSH2dHsVZW_XddLpoT7bm9fmh19Utv1lTLwtrO9NkxFPXs0Ptu8zCku-AeUar1POdDVS8DoGbYTLxIQ8eWGBSOLZoozMeX5Wx3tHwxmzUwk4h4255Y-3zhlDwTfHiObeW8YdtrOgA5zT3RIWah9NsQ6388gbReiVSCc9n8hq7c_itJlmehnkKFaweK3nv2gy5D0FAv2A'
* def request_digest = 'SHA-256=031f7023913946fe4f10c246d0c8f41b2cac5ed3d5834c0f47035ad309216e66'

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-richiesta-integrity-scaduta'
And header Authorization = client_authorization_token
And header Agid-JWT-Signature = client_integrity_token
And header Digest = request_digest
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/ttl-scaduto-in-agid-jwt-signature-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def client_authorization_token_decoded = decode_token(client_authorization_token, "Bearer")
* def client_integrity_token_decoded = decode_token(client_integrity_token, "AGID")
* def request_digest = get client_integrity_token_decoded $.payload.signed_headers..digest
* def request_integrity_iat = get client_integrity_token_decoded $.payload.iat
* def request_integrity_nbf = get client_integrity_token_decoded $.payload.nbf
* def request_integrity_exp = get client_integrity_token_decoded $.payload.exp
* def request_integrity_iat_format = formatDate(request_integrity_iat)
* def request_integrity_nbf_format = formatDate(request_integrity_nbf)
* def request_integrity_exp_format = formatDate(request_integrity_exp)

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityIssuedAt', value: request_integrity_iat_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityNotBefore', value: request_integrity_nbf_format },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityExpiration', value: request_integrity_exp_format }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token_decoded, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


@doppi-header-token-date-differenti-risposta-authorization-scaduta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella risposta. Il token 'Authorization e' scaduto'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-risposta-authorization-scaduta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/ttl-scaduto-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@doppi-header-token-date-differenti-risposta-integrity-scaduta
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella risposta. Il token 'Agid-JWT-Signature e' scaduto'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiTtl2040/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-risposta-integrity-scaduta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/ttl-scaduto-in-agid-jwt-signature-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@doppi-header-token-date-differenti-richiesta-iat-oldest
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella richiesta. I token presentano un claim 'iat' troppo vecchio

* def client_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg3MTIsIm5iZiI6MTYzMTU0ODcxMiwiZXhwIjoyMjYyMjY4NzEyLCJqdGkiOiI3MjA2NDg0ZC0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSJ9.Bozl5SddjPJ6BBXyEYtPOp-FlNv5Dmj55JcL3IsyN-NTgsjj1QzcP__laNqARK4h3XS9JeshFDt3DgPrxc3_DHMnZUG7U6fnkdq_ivO5hJpYEkJ7zuwvTW-lShbezSYjj51d-zya7aVk9sC49X5jn46rPM6peJbJdn27riVjEzhEkMNsNXizIQn7yeCYaEGXWs1FFIfU78AznF69DW4WNOMxJY-Cda2cSyTuARYNBR7BcyjQvL3q3WCXIwd13LXEbRbCjKZrtSAOFuslaIipcEs_g0e932Nq2KRdfiKAdBizL67cfgxiJGzJgiQv-yC9O-QgwZWpgIZD1csz2Mgu8A'
* def client_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.eyJpYXQiOjE2MzE1NDg4OTYsIm5iZiI6MTYzMTU0ODg5NiwiZXhwIjoyMjYyMjY4ODk2LCJqdGkiOiJkZjY5NWQyYS0xNGFiLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJ0ZXN0c3VpdGUiLCJjbGllbnRfaWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJpc3MiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZSIsInN1YiI6IkFwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTAzMWY3MDIzOTEzOTQ2ZmU0ZjEwYzI0NmQwYzhmNDFiMmNhYzVlZDNkNTgzNGMwZjQ3MDM1YWQzMDkyMTZlNjYifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb247IGNoYXJzZXQ9VVRGLTgifV19.VlGhzz5q6UWPhM3xxhjKcZdmAAbSOFwYTB_MQ1YmAzcNJ7ZHunmo9VrT7lYcOBmTknDY1ZyZ7_oT3sqtkEIRybxGIfJwMge0zqm4gz0OMXQORrM5EXsALR9--tILyNgVsOQpzzL2X5a4o-M6B9mKvpF2OczSOzPYkd0goW4jEEzy2sMHyTdsygaWGzhbJE7W9k1VjUVT_M3OcfWm6mL5JgHf--VJf2n2C8_K-seuU719DswlXGnTtVD2O4mpZ_cS21kuHZVkuvPP0Z36K2DbjWkFTq-pDPV0JCG0SZs0v-mSRR8d9_xtfyBrsLZ6j4J-85epKhou19pNjLKA2p80Zg'
* def request_digest = 'SHA-256=031f7023913946fe4f10c246d0c8f41b2cac5ed3d5834c0f47035ad309216e66'

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-richiesta-iat-oldest'
And header Authorization = client_authorization_token
And header Agid-JWT-Signature = client_integrity_token
And header Digest = request_digest
When method post
Then status 400
And match response.type == 'https://govway.org/handling-errors/400/InteroperabilityInvalidRequest.html'
And match response.title == 'InteroperabilityInvalidRequest'
And match response.status == 400
And match response.detail contains '[Header \'Agid-JWT-Signature\'\] Token creato da troppo tempo'
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

@doppi-header-token-date-differenti-risposta-iat-oldest
Scenario: Test con presenza sia dell'header Authorization che Agid-JWT-Signature che presentano date differenti nella risposta. I token presentano un claim 'iat' troppo vecchio

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-token-date-differenti-risposta-iat-oldest'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response.type == 'https://govway.org/handling-errors/502/InteroperabilityInvalidResponse.html'
And match response.title == 'InteroperabilityInvalidResponse'
And match response.status == 502
And match response.detail contains '[Header \'Agid-JWT-Signature\'\] Token creato da troppo tempo'
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'



@integrity-criteri-autorizzativi
Scenario: IDAR03 - Test con criteri autorizzativi per contenuto (es. security token)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CheckAuthz/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'check-authz-idar03'
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



@doppi-header-criteri-autorizzativi
Scenario: IDAR03 - Test con criteri autorizzativi per contenuto (es. security token) e presenza sia dell'header Authorization che Agid-JWT-Signature

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'check-authz-doppi-header-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

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



@doppi-header-scenario-oauth2-criteri-autorizzativi
Scenario: IDAR03 - Test con criteri autorizzativi per contenuto (es. security token) e presenza sia dell'header Authorization che Agid-JWT-Signature. L'erogazione valida il token authorization anche tramite policy Token.

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'check-authz-oauth2-doppi-header-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

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



@doppi-header-security-token-trasformazione-authorization-token
Scenario: Test con presenza dell'header Authorization, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'authorization token

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'authorization', 'token'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-authorization-token'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")

* def request_digest = get client_authorization_token $.payload.signed_headers..digest
* def response_digest = get server_authorization_token $.payload.signed_headers..digest

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


@doppi-header-security-token-trasformazione-authorization-header
Scenario: Test con presenza dell'header Authorization, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'authorization token (solo l'header)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'authorization', 'header'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-authorization-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")

* def request_digest = get client_authorization_token $.payload.signed_headers..digest
* def response_digest = get server_authorization_token $.payload.signed_headers..digest

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


@doppi-header-security-token-trasformazione-authorization-payload
Scenario: Test con presenza dell'header Authorization, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'authorization token (solo il payload)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'authorization', 'payload'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-authorization-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")

* def request_digest = get client_authorization_token $.payload.signed_headers..digest
* def response_digest = get server_authorization_token $.payload.signed_headers..digest

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



@doppi-header-security-token-trasformazione-authorization-custom
Scenario: Test con presenza dell'header Authorization, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'authorization token (solo il payload) e anche un token custom indicato in X-Security-Token

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'authorization', 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-authorization-custom'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header X-Security-Token = 'TEST;Bearer;TOKENVALUETEST'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")

* def request_digest = get client_authorization_token $.payload.signed_headers..digest
* def response_digest = get server_authorization_token $.payload.signed_headers..digest

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




@doppi-header-security-token-trasformazione-integrity-token
Scenario: Test con presenza dell'header Agid-JWT-Signature, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'integrity token

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'integrity', 'token'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-integrity-token'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


@doppi-header-security-token-trasformazione-integrity-header
Scenario: Test con presenza dell'header Agid-JWT-Signature, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'integrity token (solo l'header)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'integrity', 'header'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-integrity-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


@doppi-header-security-token-trasformazione-integrity-payload
Scenario: Test con presenza dell'header Agid-JWT-Signature, dove viene verificato anche l'utilizzo del security token nelle trasformazioni per inoltrare l'integrity token (solo il payload)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1"
And path 'integrity', 'payload'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-idar03-security-token-trasformazione-integrity-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


