Feature: Testing Sicurezza Messaggio ModiPA Audit

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_kid = read('check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_audit = read('check-tracce/check-traccia-kid-solo-audit.feature')
    * def check_traccia_kid_solo_oauth = read('check-tracce/check-traccia-kid-solo-oauth.feature')
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



@informazioni-utente-header
Scenario Outline: Giro Ok con informazioni utente passate negli header http <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |




@informazioni-utente-query
Scenario Outline: Giro Ok con informazioni utente passate nella query url <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar04', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And param govway_audit_user = "utente-token"
And param govway_audit_user_Location = "ip-utente-token"
And param govway_audit_loa = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def other_checks_integrity_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: 'DemoSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected }
])
"""

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-0401 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR0401 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |
| JWK | jwk-0402 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR0402 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |






@informazioni-utente-header-x509
Scenario Outline: Giro Ok con informazioni utente statiche; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'locale'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore/<username>'

* def other_checks_authorization_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' }
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>-AUDIT/v1'
* def x509Expected = '<x509>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Subject', value: x509Expected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Issuer', value: 'CN=ExampleCA, O=Example, L=Pisa, ST=Italy, C=IT' }
])
"""

* def kidRequest = '<kid>'

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | x509 | 
| X509 | x509-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | ExampleClient1 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT |








@informazioni-utente-header-x509-integrity
Scenario Outline: Giro Ok con informazioni utente statiche; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar03', 'locale'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = 'DemoSoggettoFruitore/<username>'

* def other_checks_authorization_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' }
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>-AUDIT/v1'
* def x509Expected = '<x509>'

* def other_checks_integrity_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' },
    { name: 'ProfiloSicurezzaMessaggio-Subject', value: subExpected },
    { name: 'ProfiloSicurezzaMessaggio-Issuer', value: 'DemoSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-ClientId', value: clientIdExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Subject', value: x509Expected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Issuer', value: 'CN=ExampleCA, O=Example, L=Pisa, ST=Italy, C=IT' }
])
"""

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization ModI' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token-ridefinito' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Subject', value: x509Expected },
    { name: 'ProfiloSicurezzaMessaggioAudit-X509-Issuer', value: 'CN=ExampleCA, O=Example, L=Pisa, ST=Italy, C=IT' }
])
"""

* def kidRequest = '<kid>'

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | x509 | 
| X509 | x509-0301 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR0301 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | ExampleClient1 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT |








@informazioni-utente-mixed-senza-loa
Scenario Outline: Giro Ok con informazioni utente passate negli header http e nella query, senza il livello di autenticazione, <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And param govway_audit_user_Location = "ip-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-mixed-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-mixed-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |









@manomissione-token-audit
Scenario Outline: Il token di audit viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-audit-signature-in-request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-manomissione-firma-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-manomissione-firma-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |





@manomissione-digest-token-audit-in-token-authorization
Scenario Outline: Il token di authorization viene manomesso in modo da non far corrispondere più al digest del token di audit e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima faccio un giro ok e mi tengo l'audit

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-jwk-02'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def authorization_token_giro_ok = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


# Creo una nuova richiesta modificando i valori di audit (altrimenti viene usato quello della cache) e fornendo il precedente authorization da utilizzare nel proxy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header Old-Authorization = authorization_token_giro_ok
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-audit-digest-in-authorization-request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-manomissione-digest-authorization-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |







@eliminazione-digest-value-in-token-authorization
Scenario Outline: Il token di authorization viene manomesso in modo da non essere presente il digest value e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete-value'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-senza-value.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-eliminazione-digest-value-authorization-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |




@eliminazione-digest-alg-in-token-authorization
Scenario Outline: Il token di authorization viene manomesso in modo da non essere presente il digest algorithm e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete-algorithm'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-senza-alg.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-eliminazione-digest-alg-authorization-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |




@eliminazione-digest-in-token-authorization
Scenario Outline: Il token di authorization viene manomesso in modo da non essere presente il digest e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-non-presente.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-eliminazione-digest-authorization-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |






@connettivita-base-kid-not-trusted
Scenario Outline: Il token di authorization viene manomesso in modo da non essere presente il digest e far arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-kid-not-trusted-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | purposeId-ApplicativoBlockingIDA01ExampleClient2 | ExampleClient2 | http://client2 |
| JWK | jwk-kid-not-trusted-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01ExampleClient2 | ApplicativoBlockingIDA01ExampleClient2 | purposeId-ApplicativoBlockingIDA01ExampleClient2 | ExampleClient2 | http://client2 |





@audit-user-non-fornito
Scenario Outline: l'informazione sull'audit user non viene fornito; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-non-presente.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audit-user-non-fornito-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-audit-user-non-fornito-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |





@audit-user-location-non-fornito
Scenario Outline: l'informazione sull'audit user location non viene fornito; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-location-non-presente.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audit-user-non-fornito-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-audit-user-non-fornito-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |







@audit-user-non-fornito-erogazione
Scenario Outline: l'informazione sull'audit user non viene fornito; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-non-presente-erogazione.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audit-user-non-fornito-erogazione-01 | RestBlockingAuditRest01Optional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-audit-user-non-fornito-erogazione-02 | RestBlockingAuditRest02Optional | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |





@audit-user-location-non-fornito-erogazione
Scenario Outline: l'informazione sull'audit user location non viene fornito; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-location-non-presente-erogazione.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audit-user-location-non-fornito-erogazione-01 | RestBlockingAuditRest01Optional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-audit-user-location-non-fornito-erogazione-02 | RestBlockingAuditRest02Optional | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |








@informazioni-utente-custom
Scenario Outline: Giro Ok con informazioni utente passate negli header http e parametri della url custom <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>-Custom/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header CUSTOM-Audit-User = "utente-token"
And param custom_audit_user_Location = "ip-utente-token"
And header CUSTOM-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-custom-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-custom-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |





@informazioni-utente-header-no-trace-no-forward-default
Scenario Outline: Giro Ok con informazioni utente passate negli header http, le informazioni non vengono ne tracciate ne inoltrate al backend (configurazione di default) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Linee Guida ModI Optional', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Linee Guida ModI Optional', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-notrace-noforward-default-01 | RestBlockingAuditRest01Optional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-notrace-noforward-default-02 | RestBlockingAuditRest02Optional | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |






@informazioni-utente-header-custom-trace-custom-forward
Scenario Outline: Giro Ok con informazioni utente passate negli header http <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta_fruizione = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def other_checks_richiesta_erogazione = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_fruizione, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_erogazione, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK-CustomTrace-CustomForward | jwk-customtrace-customforward-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@audit-audience-errato
Scenario Outline: l'informazione sull'audience presente nel token risulta errata; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-audience-non-valida-erogazione.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audience-errato-01 | RestBlockingAuditRest01Optional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |



@audit-audience-errato-richiesto-differente-erogazione
Scenario Outline: l'informazione sull'audience presente nel token risulta errata (l'erogazione è stata configurata per attendersi un audience differente per l'audit); <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-audience-non-valida-erogazione.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audience-errato-atteso-differente-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |








@token-audit-scaduto
Scenario Outline: Il token di audit risulta scaduto sull'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima faccio un giro ok e mi tengo l'audit

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>-TTLShort/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-jwk-01'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def audit_token_giro_ok = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* java.lang.Thread.sleep(10000)


# Creo una nuova richiesta modificando i valori di audit (altrimenti viene usato quello della cache) e fornendo il precedente authorization da utilizzare nel proxy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header Old-Audit = audit_token_giro_ok
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/expired-audit-token.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-token-audit-scaduto-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |






@token-audit-iat-oldest
Scenario Outline: Il token di audit risulta generato da troppo tempo sull'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima faccio un giro ok e mi tengo l'audit

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-jwk-01'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def audit_token_giro_ok = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* java.lang.Thread.sleep(5000)


# Creo una nuova richiesta modificando i valori di audit (altrimenti viene usato quello della cache) e fornendo il precedente authorization da utilizzare nel proxy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header Old-Audit = audit_token_giro_ok
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-audit-iat-oldest.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-token-audit-iat-oldest-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@informazioni-utente-criteri-autorizzativi-ok
Scenario Outline: Giro Ok con criteri autorizzativi per contenuto (es. security token) utilizzato anche come forward header <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def clientIdExpected = '<clientId>'
* def subExpected = '<username>'
* def issExpected = '<clientId>'

* def other_checks_authorization_richiesta = 
"""
([
])
"""

* def audExpected = '<nome-api-impl>-<tipo-test>/v1'

* def kidRequest = '<kid>'

* def other_checks_richiesta = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-criteri-autorizzativi-ok-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@informazioni-utente-criteri-autorizzativi-ko
Scenario Outline: Giro Ko con criteri autorizzativi per contenuto (es. security token) utilizzato anche come forward header <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'delete'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 403
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/authorization-deny.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-criteri-autorizzativi-ko-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |

