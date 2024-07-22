Feature: Testing Sicurezza Messaggio ModiPA IDAR03 (Custom)

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    * def encode_base64 = read('classpath:utils/encode-base64.js')

    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')
    
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

    * def integration_header_2 = karate.readAsString('integration_info_2.json')
    * def integration_header_2_base64 = encode_base64(integration_header_2);


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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null


* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti






@single-header-assenza-header-integrity-richiesta
Scenario: Il proxy rimuove lo header integrity per far arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-single-header-assenza-header-integrity-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 400


@single-header-assenza-header-integrity-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-single-header-assenza-header-integrity-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 502
And match response == read('error-bodies/idar03-custom-single-header-assenza-header-integrity-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'




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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Authorization-Token'), "Bearer")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti




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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null
And match header CustomTestSuiteDoppiSoloRichiesta-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti




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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null
And match header CustomTestSuiteDoppiSoloRichiesta-JWT-Signature == null


* def client_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Token'), "AGID")
* def server_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, requestMessageId:client_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, requestMessageId:client_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_token.payload.jti



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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null
And match header CustomTestSuiteDoppiSoloRichiesta-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Authorization-Token'), "Bearer")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti




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
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null
And match header CustomTestSuiteDoppiSoloRichiesta-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti




@assenza-header-integrity-richiesta
Scenario: Il proxy rimuove lo header integrity per far arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 400


@assenza-header-integrity-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 502
And match response == read('error-bodies/idar03-custom-doppi-header-assenza-header-integrity-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'



@assenza-header-integrity-richiesta-metodo-get-senza-payload
Scenario: la risorsa GET non genera un token di integrita custom per default

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-richiesta-metodo-get-senza-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method get
Then status 400


@assenza-header-integrity-richiesta-metodo-delete-senza-payload
Scenario: la risorsa DELETE non genera un token di integrita custom per default

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-richiesta-metodo-delete-senza-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method delete
Then status 400


@assenza-header-integrity-risposta-metodo-get-senza-payload
Scenario: la risorsa GET è configurata per generare un token di integrita custom sempre, mentre nella risposta non arriva

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M', 'customAlways'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-get-senza-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method get
Then status 502
And match response == read('error-bodies/idar03-custom-doppi-header-assenza-header-integrity-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@assenza-header-integrity-risposta-metodo-delete-senza-payload
Scenario: la risorsa DELETE è configurata per generare un token di integrita custom sempre, mentre nella risposta non arriva

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M', 'customAlways'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-get-senza-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method delete
Then status 502
And match response == read('error-bodies/idar03-custom-doppi-header-assenza-header-integrity-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'




@doppi-header-get-with-custom
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature per una risorsa GET senza payload

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M', 'customAlways'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-get-with-custom'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_2_base64
When method get
Then status 200
And match response == ''
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Authorization-Token'), "Bearer")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti






@doppi-header-get-without-custom
Scenario: Test con presenza solo dell'header Authorization, poichè il Custom-JWT-Signature per una risorsa GET senza payload non viene prodotto

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1"
And path 'resources', 1, 'M', 'testOk'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-get-without-custom'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method get
Then status 200
And match response == ''
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def server_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Authorization-Token'), "Bearer")

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti





@doppi-header-multipart
Scenario: Test con presenza sia dell'header Authorization che Custom-JWT-Signature su una richiesta Multipart

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1"
And path 'resources', 1, 'M'
And request read("richiestaConAllegati.bin")
And header Content-Type = 'multipart/mixed; boundary="----=_Part_1_1678144365.1610454048429"; type="text/xml"'
And header GovWay-TestSuite-Test-ID = 'idar03-custom-doppi-header-multipart'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-Integration = integration_header_base64
When method post
Then status 200
And match header Authorization == null
And match header Agid-JWT-Signature == null
And match header CustomTestSuite-JWT-Signature == null
And match header CustomTestSuiteDoppi-JWT-Signature == null
And match header CustomTestSuiteDoppiSoloRichiesta-JWT-Signature == null
#And match response == read('richiestaConAllegati.bin')
#Non e' possibile verificare l'uguaglianza in un multipart structure

* def client_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Authorization-Token'), "Bearer")
* def client_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Client-Integrity-Token'), "AGID")
* def server_authorization_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Authorization-Token'), "Bearer")
* def server_integrity_token = decode_token(karate.response.header('GovWay-TestSuite-GovWay-Server-Integrity-Token'), "AGID")

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

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', profilo_interazione: 'crud', other_checks: other_checks_risposta, profilo_interazione: 'crud', requestMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_authorization_token.payload.jti
