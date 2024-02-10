Feature: Testing Sicurezza Messaggio ModiPA IDAR02

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_id_messaggio = read('check-tracce/check-traccia-id-messaggio.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@connettivita-base
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


@riutilizzo-token
Scenario: Riutilizzo dello stesso token, che deve far arrabiare erogazione e fruizione

# Prima facciamo un giro ok per far generare il token alla fruizione e alla erogazione
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02RiutilizzoToken/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0]
* def server_token_header = responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0]

# Contattiamo direttamente l'erogazione con il token che si ripete

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header Authorization = client_token_header
When method post
Then status 409
And match response == read('error-bodies/identificativo-token-riutilizzato.json')
And match header GovWay-Transaction-ErrorType == 'Conflict'


# Contattiamo la fruizione e le facciamo rispondere dal proxy con lo stesso token dell'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-risposta'
And header GovWay-TestSuite-Server-Token = server_token_header
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/identificativo-token-riutilizzato-in-risposta.json')
And match header GovWay-Transaction-ErrorType == 'ConflictResponse'


@connettivita-base-header-agid
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02HeaderAgid/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar02-header-agid'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Agid-JWT-Signature == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti






@riutilizzo-token-oauth2
Scenario: Riutilizzo dello stesso token, che deve far arrabiare erogazione e fruizione, token generato tramite authorization server

# Prima facciamo un giro ok per far generare il token alla fruizione e alla erogazione
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02TokenOAuth/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-generato-auth-server'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'RestBlockingIDAR02TokenOAuth/v1'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0]

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "Bearer")
* def jti_token = get client_token $.payload.jti


# Verifico che l'identificativo utilizzato sia quello di Authorization, per l'id della busta

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
# Implementato solo lato erogazione l'associazione del jti del token come id di messaggio per fare il filtro duplicato * call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jti_token })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jti_token })


# Contattiamo direttamente l'erogazione con il token che si ripete

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02TokenOAuth/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header Authorization = client_token_header
When method post
Then status 409
And match response == read('error-bodies/identificativo-token-riutilizzato.json')
And match header GovWay-Transaction-ErrorType == 'Conflict'





@connettivita-base-header-agid
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02HeaderAgid/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar02-header-agid'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Agid-JWT-Signature == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti









@authorization-criteri-autorizzativi
Scenario: IDAR02 - Test con criteri autorizzativi per contenuto (es. security token)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR02CheckAuthz/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'check-authz-idar02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR02', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti
