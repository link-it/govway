Feature: Testing Sicurezza Messaggio ModiPA IDAR payload vuoti

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

@request-without-payload
Scenario: Test di un endpoint che non ha il payload nella richiesta IDAR01

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-without-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method get
Then status 200
And match header Authorization == null
And match response == read('request.json')

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'))
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'))

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti


@request-response-without-payload
Scenario: Test di un endpoint che non ha il payload ne nella richiesta ne nella risposta IDAR01

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-response-without-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method delete
Then status 204
And match header Authorization == null

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'))
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'))

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti


@request-without-payload-idar03
Scenario: Test di un endpoint che non ha il payload nella richiesta IDAR03

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-without-payload-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method get
Then status 200
And match header Authorization == null
And match response == read('request.json')

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")

* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: [], profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: [], profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti


@request-without-payload-tampered-header
Scenario: Manomissione header firmato nel test di un endpoint che non ha il payload nella richiesta

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-without-payload-idar03-tampered-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method get
Then status 502


@request-response-without-payload-idar03
Scenario: Test di un endpoint che non ha il payload ne nella richiesta ne nella risposta IDAR03

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-response-without-payload-idar03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method delete
Then status 204
And match header Authorization == null

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")


* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud'})
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti})

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti


@request-without-payload-idar03-digest-richiesta
Scenario: Test di un endpoint che non ha il payload nella richiesta IDAR03 e Digest

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-without-payload-idar03-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method get
Then status 200
And match header Authorization == null
And match response == read('request.json')

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")

* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' }
])
"""

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: [], profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: [], profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: "IDAR0301", other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti



@request-response-without-payload-idar03-digest-richiesta
Scenario: Test di un endpoint che non ha il payload ne nella richiesta ne nella risposta IDAR03 e Digest

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And header GovWay-TestSuite-Test-ID = 'request-response-without-payload-idar03-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method delete
Then status 204
And match header Authorization == null

* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti
