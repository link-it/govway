Feature: Testing Sicurezza Messaggio ModiPA IDAR01

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_sub_iss_client_notpresent = read('check-tracce/check-traccia_sub_iss_clientid_notpresent.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }



@enabled-security-on-action
Scenario: Sicurezza abilitata puntualmente su una sola azione

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUDNoDefaultSecurity/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'enabled-security-on-action'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method put
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })



@connettivita-base
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })


@connettivita-base-default-trustore
Scenario: Test connettività base con trustore e keystore di default

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01DefaultTrustore/v1"

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-default-trustore'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })


@connettivita-base-no-sbustamento
Scenario: Test connettività base senza sbustamento modipa

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01DefaultTrustore/v1"

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-no-sbustamento'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01NoSbustamento', password: 'ApplicativoBlockingIDA01NoSbustamento' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notnull'

* def server_token_match =
"""
({
    header: { kid: 'ExampleServer'},
    payload: {
        aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01NoSbustamento',
        client_id: 'RestBlockingIDAR01DefaultTrustoreNoSbustamento/v1',
        iss: 'DemoSoggettoErogatore',
        sub: 'RestBlockingIDAR01DefaultTrustoreNoSbustamento/v1'
    }
})
"""

* def checkToken = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio/check-token.feature')
* call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })



@connettivita-base-truststore-ca
Scenario: Test connettività base con erogazione e fruizione che usano il trustore delle CA

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1"

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-truststore-ca'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })


@response-without-payload
Scenario: Test di un endpoint che non ha il payload nella risposta

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1"

Given url url_invocazione
And path 'resources', 'object'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 201
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })


@disabled-security-on-action
Scenario: Sicurezza disabilitata puntualmente su una sola azione

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'disabled-security-on-action'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method put
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'




@riferimento-x509-x5u-x5t
Scenario: Riferimento X509 con x5u nella fruizione e x5t nell'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01X5U-X5T/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x5u-x5t'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })


Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01X5U-X5T/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x5u-x5t-client2'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })



@riferimento-x509-x5t-x5u
Scenario: Riferimento X509 con x5t nella fruizione e x5u nell'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01X5T-X5U/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x5t-x5u'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

@riferimento-x509-x5cx5t-x5cx5t
Scenario: Riferimento X509 di default in erogazione e fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01X5CX5T-X5CX5T/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x5cx5t-x5cx5t'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

@no-token-to-erogazione
Scenario: All'erogazione non arriva nessun token e questa deve arrabbiarsi

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1'
And path 'resources', 1, 'M'
And request read('request.json')
When method post
Then status 400
And match response == read('error-bodies/no-header-authorization.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


@no-token-fruizione
Scenario: Nella risposta alla fruizione non arriva nessun token e questa deve arrabbiarsi

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'no-token-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/no-header-authorization-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'



@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400
And match response == read('error-bodies/signature-verification-failed-request.json')


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/invalid-token-signature-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@low-ttl-fruizione
Scenario: Il TTL del token della fruizione (richiesta) viene superato e l'erogazione si arrabbia

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01LowTTL/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-ttl-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@low-ttl-erogazione
Scenario: Il ttl del token dell'erogazione (risposta) viene superato e la fruizione si arrabbia

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-ttl-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/ttl-scaduto-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@low-iat-ttl-fruizione
Scenario: L'elemento iat del token della fruizione (richiesta) è troppo vecchio per l'erogazione la quale si arrabbia

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-iat-ttl-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400
And match response == read('error-bodies/iat-scaduto-in-request.json')

@low-iat-ttl-erogazione
Scenario: L'elemento iat del token dell'erogazione (risposta) è troppo vecchio per la fruizione la quale si arrabbia

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-iat-ttl-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/iat-scaduto-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@iat-future-request
Scenario: L'elemento iat del token della fruizione (richiesta) contiene una data futura e l'erogazione si arrabbia. Il token sotto indicato viene generato tramite il tool jwt_generator

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-iat-future-request'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJpYXQiOiA3NjU3Mjg3Mjk3LAogICJuYmYiOiAxNjU3Mjg3Mjk3LAogICJleHAiOiA3NjU3Mjg3NTk3LAogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.qk9_xcsci2vKfo_oO9eoP7XpzBU5RKZF9GbWW_CBV9smmt8ZcSmJaOfEsqlW39-O0mBrxxULI5OzixFLECso54WsXJBKzx0IvalNUfBiMHB5KBw9cYWUut9bsVqvWDVy69-WKl_dsLSqYtotE34wflTiOG2RlHlx-kWqF7Adh-E3lvZqkvJl3lAkr6KkMtSyMsxlI2EZmK8UygC8klbqdvHaQUXzfGNJJBrxEAYjB0sJKNi53bevEFY5P4cAOf0UDgCeczOKPRUdOsCwTtrcsTJcDr_DxJ9B2A-3gBtqeAibYa8XTaEMasaP51fPFNyVNf6vqa9ZgA-6t2sAqrTMew'
When method post
Then status 400
And match response == read('error-bodies/iat-in-the-future-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


@iat-future-response
Scenario: L'elemento iat del token dell'erogazione (risposta) contiene una data futura e la fruizione si arrabbia. Il token utilizzato dal proxy server viene generato tramite il tool jwt_generator

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'iat-future-response'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/iat-in-the-future-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@applicativo-non-autorizzato
Scenario: Viene utilizzato l'identificativo di un applicativo non autorizzato dalla erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01AutenticazionePuntuale/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'applicativo-non-autorizzato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 502


@certificato-client-scaduto
Scenario: Viene utilizzato un applicativo con il certificato scaduto, con l'erogazione che si arrabbia

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-client-scaduto'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoScaduto', password: 'ApplicativoBlockingIDA01CertificatoScaduto' })
When method post
Then status 502


@certificato-client-revocato
Scenario: Viene utilizzato un applicativo con il certificato revocato, facendo arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-client-revocato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoRevocato', password: 'ApplicativoBlockingIDA01CertificatoRevocato' })
When method post
Then status 502


@certificato-server-scaduto
Scenario: Per l'erogazione viene utilizzato un certificato server scaduto, facendo arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-server-scaduto'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/certificato-server-scaduto.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@certificato-server-revocato
Scenario: Per l'erogazione viene utilizzato un certificato server revocato, facendo arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-server-revocato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/certificato-server-revocato.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@risposta-not-200
Scenario: Test senza validazione OpenAPI per controllare che secondo specifica ModiPA lo status code delle post sia 200

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'risposta-not-200'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/risposta-not-200.json')


@connettivita-base-header-agid
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01HeaderAgid/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-header-agid'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Agid-JWT-Signature == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })


@applicativo-senza-sicurezza
Scenario: Alla fruizione viene presentato un applicativo senza la sicurezza messaggio abilitata

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingNoModipa', password: 'ApplicativoBlockingNoModipa' })
When method post
Then status 400
And match response == read('error-bodies/applicativo-senza-sicurezza.json')
And match header GovWay-Transaction-ErrorType == 'BadRequest'


@applicativo-senza-x5u
Scenario: Alla fruizione viene presentato un applicativo che non ha la url x5u del certificato configurata

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01X5U-X5T/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x5u-x5t'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2NoX5U', password: 'ApplicativoBlockingIDA01ExampleClient2NoX5U' })
When method post
Then status 400
And match response == read('error-bodies/applicativo-senza-x5u.json')
And match header GovWay-Transaction-ErrorType == 'BadRequest'


@custom-claims
Scenario: Sicurezza che prevede token in cui sono stati ridefiniti dei claims

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUDClaimCustom/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'custom-claims'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method put
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })

@custom-claims-sub-iss-clientid-empty
Scenario: Sicurezza che prevede token in cui sono stati ridefiniti dei claims, e non vengono generati i claims sub, iss e client_id

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01CRUDClaimCustomSubIssNotGenerate/v1"

Given url url_invocazione
And path 'resources', 'object', 1
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'custom-claims-sub-iss-clientid-empty'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method put
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })



@keystore-default-fruizione
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore di base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreDefaultFruizione/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-default-fruizione'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })



@keystore-ridefinito-fruizione
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato contiene un keystore modi, ma non verrà utilizzato.

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreRidefinitoFruizione/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })


@keystore-ridefinito-fruizione-applicativo-no-keystore
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato non contiene un keystore modi

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreRidefinitoFruizione/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione-applicativo-no-keystore'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClientToken1', password: 'ApplicativoBlockingIDA01ExampleClientToken1' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })



@keystore-ridefinito-fruizione-archivio
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito tramite archivio. L'applicativo identificato contiene un keystore modi, ma non verrà utilizzato.

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreRidefinitoFruizioneArchivio/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione-archivio'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient4, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient4, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })



@keystore-ridefinito-fruizione-x5u
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. Il certificato viene inviato come informazione x5u

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreRidefinitoFruizioneX5U/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione-x5u'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })


@keystore-definito-applicativo
Scenario: Test connettività base in cui il keystore è definito nell'applicativo. A differenza delle altre configurazioni, in questa è stata salvata la proprietà che imposta il kestore definito nell'applicativo, mentre nelle altre funziona in ugual maniera per retrocompatibilita'

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01KeystoreApplicativo/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'keystore-definito-applicativo'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })

