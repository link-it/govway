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
Scenario Outline: Test connettività base <tipo-test> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar04-<tipo-test-minuscolo>-<username>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | keyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair | purposeId-ApplicativoBlockingKeyPair | KID-ApplicativoBlockingKeyPair | DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair |
| KeyPair | keypair | servizio che genera una risposta tramite keyPair. La validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| KeyPair | keypair | servizio che genera una risposta tramite keyPair. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |
| KeyPair | keypair | servizio che genera una risposta tramite keyPair. Anche la validazione dei certificati token è tramite jwk | keyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair | purposeId-ApplicativoBlockingKeyPair | KID-ApplicativoBlockingKeyPair | DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | keyPair | ApplicativoBlockingKeyPair | ApplicativoBlockingKeyPair | purposeId-ApplicativoBlockingKeyPair | KID-ApplicativoBlockingKeyPair | DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair |








@connettivita-base-kid-not-trusted
Scenario Outline: Test connettività base <tipo-test> (<descrizione>) con kid non presente nel truststore

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar04-<tipo-test-minuscolo>-<username>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 200
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | purposeId-ApplicativoBlockingIDA01ExampleClient2 | ExampleClient2 | http://client2 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | purposeId-ApplicativoBlockingIDA01ExampleClient2 | ExampleClient2 | http://client2 |





@manomissione-token-richiesta
Scenario Outline: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |




@manomissione-token-risposta
Scenario Outline: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/'+responseMsgErroreCheck)
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | responseMsgErroreCheck |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | invalid-token-signature-in-response.json |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | invalid-token-signature-in-response.json |




@manomissione-payload-richiesta
Scenario Outline: Il payload della richiesta viene modificato in modo da non far coincidere la firma e fare arrabbiare l'erogazione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |



@manomissione-payload-risposta
Scenario Outline: Il payload della risposta viene modificato in modo da non far coincidere la firma e fare arrabbiare la fruizione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/manomissione-token-risposta.json')

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |







@manomissione-payload-richiesta-vuota
Scenario Outline: Il payload della richiesta viene eliminato in modo da non far coincidere la firma e fare arrabbiare l'erogazione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta-vuota-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |





@manomissione-payload-risposta-vuota
Scenario Outline: Il payload della risposta viene eliminato in modo da non far coincidere la firma e fare arrabbiare la fruizione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>-PayloadVuoto/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta-vuota-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/manomissione-token-risposta-vuota.json')

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |







@manomissione-header-http-firmati-richiesta
Scenario Outline: Lo header da firmare IDAR04TestHeader viene manomesso nella richiesta (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'
When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |



@manomissione-header-http-firmati-risposta
Scenario Outline: Lo header da firmare IDAR03TestHeader viene manomesso nella risposta (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/manomissione-header-http-firmati-risposta-idar04.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |






@assenza-header-integrity-richiesta
Scenario Outline: Il proxy rimuove lo header integrity per far arrabbiare l'erogazione (<tipo-test>; <descrizione>)

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-integrity-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |




@assenza-header-integrity-risposta
Scenario Outline: Il proxy rimuove lo header integrity per far arrabbiare la fruizione (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-integrity-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/assenza-header-integrity-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |








@assenza-header-digest-richiesta
Scenario Outline: Il proxy rimuove lo header Digest per far arrabbiare l'erogazione (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-richiesta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |




@assenza-header-digest-risposta
Scenario Outline: Il proxy rimuove lo header Digest per far arrabbiare la fruizione (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-risposta-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/assenza-header-digest-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |




@response-without-payload
Scenario Outline: Test di un endpoint che non ha il payload nella risposta (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def url_invocazione = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 201
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def other_checks_risposta = 
"""
([])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@response-without-payload-tampered-header
Scenario Outline: Modifco uno degli header firmati in una risposta senza payload (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def url_invocazione = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar04-tampered-header-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 502
And match response == read('error-bodies/manomissione-header-http-firmati-risposta-idar04.json')

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | 
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 |





@response-without-payload-digest-richiesta
Scenario Outline: Test di un endpoint che non ha il payload nella risposta (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def url_invocazione = govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idar04-digest-richiesta-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 201
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")

* def request_digest = get client_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |







@idar04-token-richiesta
Scenario Outline: Giro ok idar04 con il token abilitato solo sulla richiesta per una specifica azione, globalmente è richiesta/risposta. (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'requestOnly', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar04-token-richiesta-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |







@idar04-token-risposta
Scenario Outline: Giro ok idar04 con il token abilitato solo sulla risposta per una specifica azione, globalmente è richiesta/risposta. (<tipo-test>; <descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1'
And path 'responseOnly', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar04-token-risposta-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04-<tipo-test>/v1'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def response_digest = get server_token $.payload.signed_headers..digest

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
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdResponseExpected }
])
"""

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:tidMessaggio })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* def tidMessaggio = responseHeaders['GovWay-TestSuite-GovWay-Message-ID'][0]
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:tidMessaggio })

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |








@audience-differenti-ok
Scenario Outline: Test con audience differenti <tipo-test> (<descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'differentAudience', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audience-differenti-ok-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'Different-RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar04testheader', value: 'TestHeaderRequest' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_richiesta, profilo_interazione: 'crud', traceMessageId:client_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_token.payload.jti

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@audience-differenti-ko-auth-claim-deny
Scenario Outline: Test con audience nell'authorazione token diverso da quello atteso <tipo-test> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'differentAudience', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audience-differenti-ko-auth-claim-deny-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'Wrong-RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-authorization-not-authorized-by-auth-claims.json')
And match header Authorization == '#notpresent'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@audience-differenti-ko
Scenario Outline: Test con audience nell'authorazione token diverso da quello atteso <tipo-test> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audience-differenti-ko-idar04-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'Wrong-RestBlockingIDAR04-<tipo-test>/v1'

When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-authorization-not-authorized.json')
And match header Authorization == '#notpresent'

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |



