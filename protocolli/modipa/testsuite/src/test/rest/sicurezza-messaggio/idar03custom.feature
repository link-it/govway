Feature: Testing Sicurezza Messaggio ModiPA IDAR03 (Custom)

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
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


@single-header
Scenario: Test connettività con singolo header custom

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-single-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta })



@doppi-header
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })




@doppi-header-solo-richiesta
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature solo nella richiesta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'AuthorizationOnlyRequest'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-solo-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppiSoloRichiesta-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppiSoloRichiesta-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })




@single-header-filtro-duplicati
Scenario: Test connettività con singolo header custom e filtro duplicati

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1"
And path 'resources', 1, 'M0302'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar0302-custom-single-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuite-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta })



@doppi-header-filtro-duplicati
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature e filtro duplicati

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M0302'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar0302-custom-doppi-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })




@doppi-header-solo-richiesta-filtro-duplicati
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature solo nella richiesta e filtro duplicati

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'AuthorizationOnlyRequest0302'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar0302-custom-doppi-header-solo-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppiSoloRichiesta-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppiSoloRichiesta-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })




@doppi-header-multipart
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature su una richiesta Multipart

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1"
And path 'resources', 1, 'M'
And request read("richiestaConAllegati.bin")
And header Content-Type = 'multipart/mixed; boundary=----=_Part_1_1678144365.1610454048429; type=text/xml'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-multipart'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header CustomTestSuite-JWT-Signature == '#notpresent'
#And match response == read('richiestaConAllegati.bin')
#Non e' possibile verificare l'uguaglianza in un multipart structure

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Custom-JWT-Signature', value: 'CustomTestSuiteDoppi-JWT-Signature' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud' })
