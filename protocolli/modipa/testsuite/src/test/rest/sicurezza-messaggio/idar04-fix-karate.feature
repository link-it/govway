Feature: Testing Sicurezza Messaggio ModiPA IDAR04 -> ID_INTEGRITY_REST_02

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia_kid = read('check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_oauth = read('check-tracce/check-traccia-kid-solo-oauth.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    
    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def clean_remote_store_key = read('classpath:utils/remote_store_key.js')
    * def result = clean_remote_store_key('KID-ExampleServer')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingIDA01')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingJWK')
    * def result = clean_remote_store_key('de606068-01cb-49a5-824d-fb171b5d5ae4')
    * def result = clean_remote_store_key('KID-ApplicativoBlockingKeyPair')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


# Metto i test che non prevedono un payload nella richiesta (GET/DELETE) in cima perchè se eseguito dopo falliscono, per via di qualche bug di karate, se viene eseguita tutta la feature complessiva
# FIX BUG KARATE



@authorization-richiesta-integrity-risposta
Scenario Outline: Test con presenza dell'header Authorization nella richiesta e Agid-JWT-Signature nella risposta (<tipo-test> <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request null
And header GovWay-TestSuite-Test-ID = 'authorization-richiesta-integrity-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'
When method get
Then status 200
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Client-Integrity-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Authorization-Token == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
])
"""

* def clientIdResponseExpected = 'ExampleServer<tipo-test>'
* def subResponseExpected = 'RestBlockingIDAR04-<tipo-test>/v1'
* def issResponseExpected = 'DemoSoggettoErogatore'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderResponse' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdResponseExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@solo-authorization-richiesta
Scenario Outline: Test con presenza dell'header Authorization solo nella richiesta (<tipo-test> <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request null
And header GovWay-TestSuite-Test-ID = 'solo-authorization-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'
When method delete
Then status 200
And match response == ''
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Client-Integrity-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Integrity-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Authorization-Token == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")

* def other_checks_richiesta = 
"""
([
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |


