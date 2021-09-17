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


# Metto i test che non prevedono un payload nella richiesta (GET/DELETE) in cima perchè se eseguito dopo falliscono, per via di qualche bug di karate, se viene eseguita tutta la feature complessiva
# FIX BUG KARATE

@doppi-header-authorization-richiesta-integrity-risposta
Scenario: Test con presenza dell'header Authorization nella richiesta e Agid-JWT-Signature nella risposta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'letturaSoloIntegrityRisposta'
And request null
And header GovWay-TestSuite-Test-ID = 'doppi-header-authorization-richiesta-integrity-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
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


@doppi-header-solo-authorization-richiesta
Scenario: Test con presenza dell'header Authorization e dell'header Agid-JWT-Signature solo nella richiesta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'lettura'
And request null
And header GovWay-TestSuite-Test-ID = 'doppi-header-solo-authorization-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method get
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Client-Integrity-Token == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
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



@doppi-header-solo-authorization-richiesta-risposta
Scenario: Test con presenza solo dell'header Authorization nella richiesta e nella risposta poichè si tratta di una DELETE

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'eliminazione'
And request null
And header GovWay-TestSuite-Test-ID = 'doppi-header-solo-authorization-richiesta-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method DELETE
Then status 204
And match response == ''
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Client-Integrity-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Integrity-Token == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")

* def other_checks_richiesta = 
"""
"""

* def other_checks_risposta = 
"""
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_authorization_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_risposta, profilo_interazione: 'crud' })



@doppi-header-solo-authorization-richiesta-delete
Scenario: Test con presenza solo dell'header Authorization nella richiesta poichè si tratta di una DELETE

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1"
And path 'soloAuthorizationRichiesta'
And request null
And header GovWay-TestSuite-Test-ID = 'doppi-header-solo-authorization-richiesta-delete'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method DELETE
Then status 204
And match response == ''
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'
And match header GovWay-TestSuite-GovWay-Client-Integrity-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Authorization-Token == '#notpresent'
And match header GovWay-TestSuite-GovWay-Server-Integrity-Token == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")

* def other_checks_richiesta = 
"""
"""

* def other_checks_risposta = 
"""
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0301', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
