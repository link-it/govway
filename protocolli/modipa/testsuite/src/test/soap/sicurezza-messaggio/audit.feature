Feature: Testing Sicurezza Messaggio ModiPA Audit

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_kid = read('classpath:test/rest/sicurezza-messaggio/check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_audit = read('classpath:test/rest/sicurezza-messaggio/check-tracce/check-traccia-kid-solo-audit.feature')
    * def check_traccia_kid_solo_oauth = read('classpath:test/rest/sicurezza-messaggio/check-tracce/check-traccia-kid-solo-oauth.feature')
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

Given url govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1/"
And path 'idar01.oauth'
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
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
And match response == read("response.xml")
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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'bloccante', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'bloccante', token_auth: 'Authorization OAuth' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01 | SoapBlockingAuditRest01 | AUDIT_REST_01 | IDAS01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| PDND | pdnd-02 | SoapBlockingAuditRest02 | AUDIT_REST_02 | IDAS02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |







@informazioni-utente-header-x509
Scenario Outline: Giro Ok con informazioni utente statiche; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01.locale'
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read("response.xml")
And match header Authorization == '#notpresent'

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")

* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")


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
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | x509 | 
| X509 | x509-01 | SoapBlockingAuditRest01 | AUDIT_REST_01 | IDAS01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | ExampleClient1 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT |








@informazioni-utente-header-x509-integrity
Scenario Outline: Giro Ok con informazioni utente statiche; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar03.locale'
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
When method post
Then status 200
And match response == read("response.xml")
And match header Authorization == '#notpresent'

* def client_audit_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0], "AGID")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
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
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: checks_richiesta })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: checks_richiesta })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'bloccante' })


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | x509 | 
| X509 | x509-0301 | SoapBlockingAuditRest01 | AUDIT_REST_01 | IDAS0301 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | ExampleClient1 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT |






@negoziazioneViaTokenPolicySecurityOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore JWK definito nella token policy

Given url govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaAuditViaTokenPolicySOAP/v1"
And path 'test'
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'FruizioneAuditViaTokenPolicy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read("request.xml")
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurityConIntegrityOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore JWK definito nella token policy, anche con integrity (fallisce poichè il kid nell'audit non è presente nel truststore)

Given url govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaIntegrityAuditViaTokenPolicySOAP/v1"
And path 'test'
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'FruizioneIntegrityAuditViaTokenPolicy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 500
And match response == read('error-bodies/audit_via_token_policy_kid_non_valido.xml')

