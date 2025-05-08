Feature: Testing Sicurezza Messaggio ModiPA IDAR01

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_sub_iss_client_notpresent = read('check-tracce/check-traccia_sub_iss_clientid_notpresent.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def get_diagnostico = read('classpath:utils/get_diagnostico.js')



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti




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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

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
And path 'resources', 1, 'M2CacheTest'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-iat-ttl-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/iat-scaduto-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@iat-future-request
Scenario: L'elemento iat del token della fruizione (richiesta) contiene una data futura e l'erogazione si arrabbia. Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequest.sh

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
Scenario: L'elemento iat del token dell'erogazione (risposta) contiene una data futura e la fruizione si arrabbia. Il token utilizzato dal proxy server viene generato tramite il tool jwt_generator/generateResponse.sh

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M3CacheTest'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'iat-future-response'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/iat-in-the-future-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@nbf-future-request
Scenario: L'elemento nbf del token della fruizione (richiesta) contiene una data futura (di 8 secondi; tolleranza di GovWay 5) e l'erogazione si arrabbia. Il token viene generato tramite una trasformazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01nbfFuture/v1'
And path 'none'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'nbf-future-request'
And header GovWay-TestSuite-NbfFutureConfigValue =  '8' 
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 400
And match response == read('error-bodies/nbf-future-in-request.json')

@low-nbf-future-request
Scenario: L'elemento nbf del token della fruizione (richiesta) contiene una data futura (di 2 secondi; tolleranza di GovWay 5) e l'erogazione non si arrabbia. Il token viene generato tramite una trasformazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01nbfFuture/v1'
And path 'none'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-nbf-future-request'
And header GovWay-TestSuite-NbfFutureConfigValue =  '2' 
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')

@nbf-future-response
Scenario: L'elemento nbf del token dell'erogazione (risposta) contiene una data futura (di 8 secondi; tolleranza di GovWay 5) e la fruizione si arrabbia. Il token viene generato tramite una trasformazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01nbfFuture/v1'
And path 'response'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'nbf-future-response'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/nbf-future-in-response.json')

@low-nbf-future-response
Scenario: L'elemento nbf del token dell'erogazione (risposta) contiene una data futura (di 2 secondi; tolleranza di GovWay 5) e la fruizione non si arrabbia. Il token viene generato tramite una trasformazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01nbfFuture/v1'
And path 'response'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'low-nbf-future-response'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')



@date-non-presenti-request
Scenario: Non sono presenti date e l'erogazione si arrabbia. Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'date-non-presenti-request'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 400
And match response == read('error-bodies/date-non-presenti-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



@scadenza-non-presente-request
Scenario: Non è presente la scadenza e l'erogazione si arrabbia. Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaScadenza.sh

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'scadenza-non-presente-request'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJpYXQiOiA3NjU3Mjg3Mjk3LAogICJuYmYiOiAxNjU3Mjg3Mjk3LAogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.nIdiOX-nTla2tKRVoAp-zSoPMIuoUgaOXGohBUXyjmSknaGPXGyInuvW43XNMZvz2WBOV2_2sab4V2iZbSQ_hMtHpcASbj-jIRlcKIdf4OWVRa9cYAaERitplPiC-8x2GUXKF6QKecpR29uJuyGWN80aQZ2iqP6g_Vqiwvv7JdTV1SQC_rRw4qlHwuhBRndEODPL3xlWlvA5If87103vRa6kGWeNkdxc6rsVN79xcaAQ2Jrvi5lGLWsU2-HnURcCBuBFda8JCzUJ7y7oeH7t6WuP1nAScO5LmeNSGx10RA3Pr7Nu3iPoVZDq8DJuqphAV3Sb-Y_w-zqXHk_VM4CkIw'
When method post
Then status 400
And match response == read('error-bodies/scadenza-non-presente-request.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



@date-non-presenti-request-pdnd-policy
Scenario: Non sono presenti date e l'erogazione si arrabbia. Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh

# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'default'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'date-non-presenti-request-pdnd-policy'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 401
And match response == read('error-bodies/token-non-valido.json')
And match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

* def tiderogazione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token rejected; the % claim is required but missing.") 
* match result[0].MESSAGGIO contains "'exp' (expiration time)"



@date-non-presenti-request-pdnd-policy-exp-not-required
Scenario: Non sono presenti date e l'erogazione si arrabbia (la configurazione accetta token senza exp). Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh

# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })


Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'date-non-presenti-request-pdnd-policy-exp-not-required'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 401
And match response == read('error-bodies/token-non-valido.json')
And match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

* def tiderogazione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token rejected; the % claim is required but missing.") 
* match result[0].MESSAGGIO contains "'nbf' (not before) "



@date-non-presenti-request-pdnd-policy-exp-nbf-not-required
Scenario: Non sono presenti date e l'erogazione si arrabbia (la configurazione accetta token senza exp). Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh

# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })


Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNbfNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'date-non-presenti-request-pdnd-policy-exp-nbf-not-required'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 401
And match response == read('error-bodies/token-non-valido.json')
And match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

* def tiderogazione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token rejected; the % claim is required but missing.") 
* match result[0].MESSAGGIO contains "'iat' (issued at) "



@date-non-presenti-request-pdnd-policy-exp-nbf-iat-not-required
Scenario: Non sono presenti date e l'erogazione si arrabbia (la configurazione accetta token senza exp). Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh


# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNbfIatNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'date-non-presenti-request-pdnd-policy-exp-nbf-iat-not-required'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 200
And match response == read('request.json')



@dati-pdnd-producerId-non-presente
Scenario: Non sono presenti date e l'erogazione si arrabbia (la configurazione accetta token senza date ma non è presente il producerId). Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDate.sh


# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/in/DemoSoggettoErogatoreConIdEnte/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNbfIatNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'dati-pdnd-producerId-non-presente'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIgp9Cg.VCZ6oymrpGnGvvlH2UJzuS8B9dLKkG_oc8F_pUfAtWYG3b9Ks5mHy4dll4VHL9zRqk2Tx961H_NQVSxfx25BjrtwlhRMoEkTBTpCB3O5kh4NHXWiKOwuET1iTpwBBr3B0TIBbs_vmJBnd3gjM-VPHiPVqct3rsrWTN6emZvILiwrzHAI3yYiSyT0f-QLwwaEiATlCbvuY5S42FL6zeq3DCDTqzoNPGVvPmQwW2jgGltp65rP3bpdNnoUwLIgoI3gB7BMWaEueQ-oZQI0-7jGgvFMT01PzHbmb5uwx9xCPC4stjGcSnC-bhNBqiP-khj70ODbidb2gmw6MWrEuLVMWA'
When method post
Then status 400
And match response == read('error-bodies/authorization-senza-producer-id.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def tiderogazione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token non contiene il claim") 
* match result[0].MESSAGGIO contains "'producerId'"



@dati-pdnd-producerId-differente
Scenario: Non sono presenti date e l'erogazione si arrabbia (la configurazione accetta token senza date ma non è presente il producerId). Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDateConValoriPdndProducerIdDifferente.sh


# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/in/DemoSoggettoErogatoreConIdEnte/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNbfIatNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'dati-pdnd-producerId-differente'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIiwKICAicHJvZHVjZXJJZCI6ICJlcnJvcjcwZC04YzRjLTRmMGQtOWQ4YS0xNjI4NDNjMTAzMzMiLAogICJjb25zdW1lcklkIjogImhoaGUwNzBkLThjNGMtNGYwZC05ZDhhLTE2Mjg0M2MxMDQ0NCIsCiAgImVTZXJ2aWNlSWQiOiAiZ2dnZTA3MGQtOGM0Yy00ZjBkLTlkOGEtMTYyODQzYzEwNTU1IiwKICAiZGVzY3JpcHRvcklkIjogImZmZmUwNzBkLThjNGMtNGYwZC05ZDhhLTE2Mjg0M2MxMDY2NiIKfQo.qn14x1NSnourpqmZL9k9VTvFVh6Te0MEW06vy4LF-ciO76QtNgU02ISNV8CoYub8KQO_236hq9FF8Nkfkufgq0xUY14M28k70-VVAu7sdDAI03GYkPxiPfnu2_6lWlBmPOXaBgUSiNqBSExwWneVK6J-HweXp-i9mtTCHvR8Bqfl2pvFMqcWlPmyvN2-6Ka-XF28-JhxXmDNXiIov4PiDJB85R5s8Lati3iJ1454lpBMjtirhSZllEWYzUEtyKYGkfHmxG_n6EveAN7Iu7FupCXG9-lI9-FB3NiKe72UR1fTuxi3NsVK8f9QqjBOuer1ZLlGECf5jl-FkZ8Qqt1aTg'
When method post
Then status 400
And match response == read('error-bodies/authorization-con-producer-id-non-valido.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

* def tiderogazione = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostico(tiderogazione, "Token contenente un claim % non valido") 
* match result[0].MESSAGGIO contains "'producerId'"




@dati-pdnd-producerId
Scenario: Nella richiesta sono presenti tutti i dati richiesti e il procuderId corrisponde a quello registrato sul soggetto. Il token sotto indicato viene generato tramite il tool jwt_generator/generateRequestSenzaDateConValoriPdnd.sh


# Svuoto la cache per evitare che venga generato lo stesso errore
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/in/DemoSoggettoErogatoreConIdEnte/DemoAutorizzazioneTokenRestTrustPDNDxcaKidInToken/v1'
And path 'expNbfIatNotRequired'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'dati-pdnd-producerId'
And header Authorization =  'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVDbGllbnQxIiwieDVjIjpbIk1JSURYakNDQWthZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJTTVFzd0NRWURWUVFHRXdKSlZERU9NQXdHQTFVRUNCTUZTWFJoYkhreERUQUxCZ05WQkFjVEJGQnBjMkV4RURBT0JnTlZCQW9UQjBWNFlXMXdiR1V4RWpBUUJnTlZCQU1UQ1VWNFlXMXdiR1ZEUVRBZUZ3MHhPVEEzTURreE1ESTJNREJhRncwME1EQTNNekF4TURJMk1EQmFNRmN4Q3pBSkJnTlZCQVlUQWtsVU1RNHdEQVlEVlFRSUV3VkpkR0ZzZVRFTk1Bc0dBMVVFQnhNRVVHbHpZVEVRTUE0R0ExVUVDaE1IUlhoaGJYQnNaVEVYTUJVR0ExVUVBeE1PUlhoaGJYQnNaVU5zYVdWdWRERXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEd2hpZXNoNWpLNElKbEFtOTJURXZsc1BuNi80dlp2QUNDTFBoa3drK3BhcUZ1Q3dhYWQ3Sm9kQWdvdjZLR0lwR0JzTlBUWWNnT1V0NG1ucTVjTEZHN294aFVSZVNtNGpVcTE3YkdxVWJQRFlYNVlBczJTZ1dCcGQ0aXNUQWk2Q1BsNTZLcW9GdDUxbDFBK3Z0aVpjZUprNUxPMVd4Qko3SkZNYUVoOHkyK3VvcFJyeEhoVGFBVUNubkNqWnlBSlRZT1RXQW44SGFhaWpHQzk3Q0xZUnJaSks2NDRBbE9HOEFUQUNUVnpGZkJsekZXbzRDUE9CNHA3dVErenYxV0FLbWNhNmkyMnVHcVV1MVBTRSttS1BaUFZMK3ZZUTFtdEQxN0hpR1FVWHlyWVNuR3E5NHB3WGx1Wk5vMUxWN09Nb0syRW1PYXJYTzc3TVFzc1VESGh0ai9BZ01CQUFHak9qQTRNQWtHQTFVZEV3UUNNQUF3SFFZRFZSME9CQllFRkZmS0k3VUdoSlpyckRqNktVZCtJclc3OHoxdk1Bd0dBMVVkRHdRRkF3TUgvNEF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUZaR1lrcjlDNVNqM3JRT0k1a2dueDdxTFZrOGhqKyt1TUJJRXVoQW50ZTlielo0cEcxQmFsUjRvUG5JakV4Z3p1WmxQeE05MEdPT0VEUTdKOWliS051aTkwQUFTbzJUQ2VKOTUvN3J3SzNUbnJ5TDZ5Q1orVUdORU95OElDeEo2Q3NkMlBhYzgvdnJaQjMwTnpibk5HajRBSHRwR0VvdzBvc2NZdzVORWU4TzlWeUMzdGZaTlBZSFo0ZmFsQTcvMFN1Z215WThIUjAvUjJWeXZvTWk3b3k3c2w2V2N3UjZuNWNHMXh1Y0RUaDFWb2NpVTlickt2WlhHOGhvdkJMblJidzlSWDRCOENYZWk4c1o2aWlEMTREWkQ5RVF4S2IyM3lXUUJscG5GWGU1UFVNVE5wTEpXNGlnbktJMm9Ja0dQeEJ5TWVJSUg4TEtQKzc3OUJNNFNPST0iXX0.ewogICJqdGkiOiAiYzI1ZDU2M2QtZmVjMi0xMWVjLTk1ZmQtMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogInRlc3RzdWl0ZSIsCiAgImNsaWVudF9pZCI6ICJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRnJ1aXRvcmUiLAogICJzdWIiOiAiQXBwbGljYXRpdm9CbG9ja2luZ0lEQTAxIiwKICAicHJvZHVjZXJJZCI6ICJhY2RlMDcwZC04YzRjLTRmMGQtOWQ4YS0xNjI4NDNjMTAzMzMiLAogICJjb25zdW1lcklkIjogImhoaGUwNzBkLThjNGMtNGYwZC05ZDhhLTE2Mjg0M2MxMDQ0NCIsCiAgImVTZXJ2aWNlSWQiOiAiZ2dnZTA3MGQtOGM0Yy00ZjBkLTlkOGEtMTYyODQzYzEwNTU1IiwKICAiZGVzY3JpcHRvcklkIjogImZmZmUwNzBkLThjNGMtNGYwZC05ZDhhLTE2Mjg0M2MxMDY2NiIKfQo.cwJDaFE1IXssQMgO032tiO04T_GVH3jvsIV7GK9iuSAp2G-TVa39QGDUVpuKtB9AGF2uqKqDjcLtFCAMOPgmmPYFysq_KAhR_W_Y4QoGv78z5jyvADsIld1x356naFLn6GFV_GBAs-Xm8QiYT9r_5w6PcdSsZXwpH9TXwFltxRi99P9d2cPRLJkD_VITYx9ShPR-_1kZ1SWohY2Nz_yesRFOZsDCjMASLnpVr_CSrKtlLaH3tVcygu9PGaanRrBaguxZOl1-cjRuMu-Eh-Vsb0OwYjEQ8Fxb6c-EIjAH3pA2xdR_e6jAK8UUc3NMPmz5HVDoqbbT-_noqHA3dEyy6A'
When method post
Then status 200
And match response == read('request.json')




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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

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
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud' })
* call check_traccia_sub_iss_client_notpresent ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



@keystore-ridefinito-fruizione
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato contiene un keystore modi, ma non verrà utilizzato.

# Svuoto la cache per evitare che venga generato lo stesso token in questo test e nel test successivo 'keystore-ridefinito-fruizione-applicativo-no-keystore'
* call reset_cache_token ({ })

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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


@keystore-ridefinito-fruizione-applicativo-no-keystore
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato non contiene un keystore modi

# Svuoto la cache per evitare che venga generato lo stesso token in questo test e nel test precedente 'keystore-ridefinito-fruizione'
* call reset_cache_token ({ })

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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient4, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti



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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_interazione: 'bloccante', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti




@autenticazione-token-applicativo-fruitore
Scenario: Test connettività in cui il servizio applicativo fruitore viene identificato tramite una token policy interna

# NOTA: il token viene prodotto tramite una trasformazione presente nella risorsa generaToken che poi cortocircuita sulla fruizione che richiede un token interno

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01AutenticazioneApplicativoTramiteToken/v1"
And path 'generaToken'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'autenticazione-token-applicativo-fruitore'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


