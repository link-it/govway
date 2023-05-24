Feature: Testing Sicurezza Messaggio ModiPA IDAR04 -> ID_INTEGRITY_REST_02 (Custom)

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_kid = read('check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_oauth = read('check-tracce/check-traccia-kid-solo-oauth.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    * def encode_base64 = read('classpath:utils/encode-base64.js')
    
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

    * def integration_header = karate.readAsString('integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);


@connettivita-base
Scenario Outline: Test connettività base con l'utilizzo di un custom header <tipo-test> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR04Custom-<tipo-test>/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar04-custom-header-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header IDAR04TestHeader = "TestHeaderRequest"
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = 'RestBlockingIDAR04Custom-<tipo-test>/v1'
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore'

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def clientIdResponseExpected = 'ExampleServer<tipo-test>'
* def subResponseExpected = 'RestBlockingIDAR04Custom-<tipo-test>/v1'
* def issResponseExpected = 'DemoSoggettoErogatore'

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: issResponseExpected },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdResponseExpected }
])
"""

* def kidRequest = '<kid>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', profilo_interazione: 'crud', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_token, kid: kidRequest, profilo_sicurezza: 'IDAR0401', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia_kid ({ tid: tid, tipo: 'Risposta', token: server_token, kid: 'KID-ExampleServer', profilo_sicurezza: 'IDAR0401', profilo_interazione: 'crud', other_checks: other_checks_risposta })

Examples:
| tipo-test | tipo-test-minuscolo | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| PDND | pdnd | servizio che genera una risposta tramite jwk. La validazione dei certificati token è basata su PDND | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |


