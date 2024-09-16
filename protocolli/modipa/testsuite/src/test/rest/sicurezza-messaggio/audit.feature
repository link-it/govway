Feature: Testing Sicurezza Messaggio ModiPA Audit

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_kid = read('check-tracce/check-traccia-kid.feature')
    * def check_traccia_kid_solo_audit = read('check-tracce/check-traccia-kid-solo-audit.feature')
    * def check_traccia_kid_solo_oauth = read('check-tracce/check-traccia-kid-solo-oauth.feature')
    * def check_traccia_kid_solo_oauth_pa_error = read('check-tracce/check-traccia-kid-solo-oauth-pa-error.feature')
    * def check_traccia_kid_no_audience = read('check-tracce/check-traccia-kid-no-audience.feature')
    * def decode_token = read('classpath:utils/decode-token.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_info_transazione = read('classpath:utils/get_info_transazione.js')
    
    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }

    * def get_diagnostici_create_token_audit = read('classpath:utils/get_diagnostici_create_token_audit.js')
    * def get_diagnostici_create_token_authorization = read('classpath:utils/get_diagnostici_create_token_authorization.js')
    * def get_diagnostici_create_token_integrity = read('classpath:utils/get_diagnostici_create_token_integrity.js')


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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_integrity_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_integrity_token.payload.jti


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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
And path 'idar01', 'oauth-no-filtro-duplicati'
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
And path 'idar01', 'oauth-no-filtro-duplicati'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-per-test-manomissione"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header old-authorization = authorization_token_giro_ok
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








@audit-non-fornito-erogazione
Scenario Outline: il token audit non viene fornito; <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

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
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-non-presente-erogazione.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-audit-non-fornito-erogazione-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-audit-non-fornito-erogazione-02 | RestBlockingAuditRest02 | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |








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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
And header govWay-audit-User = "utente-token"
And header GovWay-AUDIT-USERLocation = "ip-utente-token"
And header govWay-Audit-loa = "livello-autenticazione-utente-token"
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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti  })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Linee Guida ModI Optional', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti  })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Linee Guida ModI Optional', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_fruizione, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_erogazione, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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
And header GovWay-Audit-User = "utente-token-modificato-tempo-scaduto"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header old-audit = audit_token_giro_ok
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
And header GovWay-Audit-User = "utente-token-differente-test-iat"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header old-audit = audit_token_giro_ok
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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


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






@informazioni-utente-token-optional-fornito
Scenario Outline: Giro Ok con informazioni utente passate negli header http (token configurato come opzionale, ma fornito) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

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
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-token-optional-01 | RestBlockingAuditRest01TokenAuditOptional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@informazioni-utente-token-optional-non-fornito-erogazione
Scenario Outline: Giro Ok con informazioni utente passate negli header http (token configurato come opzionale, nel proxy viene eliminato prima di inoltrarlo all'erogazione) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'idar01', 'oauth-noaudit'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")

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
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-token-optional-non-fornito-erogazione-01-noaudit | RestBlockingAuditRest01TokenAuditOptional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk. Nell'API il token di audit non viene richiesto per l'operazione oauth-noaudit | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-token-optional-non-fornito-erogazione-01-optionalaudit | RestBlockingAuditRest01TokenAuditOptional | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk. Nell'API il token di audit è configurato come opzionale | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |







@purpose-id-uguale
Scenario Outline: Il token di audit e il token di authorization utilizzano uno stesso purposeId; test propedeutico per i successivi. Erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingAuditRest01CustomAuditBuild/v1"
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
And header GovWay-Audit-PurposeId = '<purposeId>'
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

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
# Viene usato l'id del client_audit_token perchè non e' un vero audit, ma un integrity custom che produce un token di audit
* match tidMessaggio == client_audit_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK-RecuperoInfoClient | jwk-purpose-id-uguali | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |






@purpose-id-differenti
Scenario Outline: Il token di audit e il token di authorization utilizzano un purposeId differente che fa arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingAuditRest01CustomAuditBuild/v1"
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
And header GovWay-Audit-PurposeId = '<purposeId>-differente'
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/purpose-id-differenti.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK-RecuperoInfoClient | jwk-purpose-id-differenti | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@purpose-id-non-presente-audit
Scenario Outline: Il token di audit non presenta il purposeId differente e fa arrabbiare l'erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingAuditRest01CustomAuditBuild/v1"
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
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/purpose-id-non-presente-audit.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK-RecuperoInfoClient | jwk-purpose-id-non-presente-audit | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@informazioni-utente-token-custom-valido
Scenario Outline: Giro Ok con informazioni utente passate negli header http (token configurato come custom) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-TypeINT = "<TypeINT>"
And header GovWay-Audit-TypeBoolean = "<TypeBoolean>"
And header GovWay-Audit-TypeStringRegExp = "<TypeStringRegExp>"
And header GovWay-Audit-TypeINTRegExp = "<TypeINTRegExp>"
And header GovWay-Audit-TypeListString = "<TypeListString>"
And header GovWay-Audit-TypeListInt = "<TypeListInt>"
And header GovWay-Audit-TypeMixed1 = "<TypeMixed1>"
And header GovWay-Audit-TypeMixed2 = "<TypeMixed2>"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'

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
    { name: 'ProfiloSicurezzaMessaggioAudit-Subject', value: client_audit_token.payload.sub },
    { name: 'ProfiloSicurezzaMessaggioAudit-ClientId', value: client_audit_token.payload.client_id },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeINT', value: '<TypeINT>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeBoolean', value: '<TypeBoolean>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeStringRegexp', value: '<TypeStringRegExp>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeINTRegexp', value: '<TypeINTRegExp>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeListString', value: '<TypeListString>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeListInt', value: '<TypeListInt>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeMixed1', value: '<TypeMixed1>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeMixed2', value: '<TypeMixed2>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri di validazione custom', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri di validazione custom', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | TypeINT | TypeBoolean | TypeStringRegExp | TypeINTRegExp | TypeListString | TypeListInt | TypeMixed1 | TypeMixed2 |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 23456 |
| JWK | jwk-token-custom-02 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 99147483647 | false | A | 1 | Valore4 | 45 | AA | 22 |



@informazioni-utente-token-custom-nonvalido-fruizione
Scenario Outline: Giro Ko sulla fruizione, con informazioni utente passate negli header http (token configurato come custom) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-TypeINT = "<TypeINT>"
And header GovWay-Audit-TypeBoolean = "<TypeBoolean>"
And header GovWay-Audit-TypeStringRegExp = "<TypeStringRegExp>"
And header GovWay-Audit-TypeINTRegExp = "<TypeINTRegExp>"
And header GovWay-Audit-TypeListString = "<TypeListString>"
And header GovWay-Audit-TypeListInt = "<TypeListInt>"
And header GovWay-Audit-TypeMixed1 = "<TypeMixed1>"
And header GovWay-Audit-TypeMixed2 = "<TypeMixed2>"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<errore>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'

Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | TypeINT | TypeBoolean | TypeStringRegExp | TypeINTRegExp | TypeListString | TypeListInt | TypeMixed1 | TypeMixed2 | errore |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeInt errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | errore23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-fruizione-typeINT-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeStringRegExp errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE3a | 12 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-fruizione-typeStringRegExp-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeIntRegExp errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 0 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-fruizione-typeINTRegExp-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeIntRegExp errato2 | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | -12 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-fruizione-typeINTRegExp-nonvalido2.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeListString errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | ValoreInesistente2 | 10.3 | ZZ | 23456 | audit-custom-fruizione-typeListString-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeListInt errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 123 | ZZ | 23456 | audit-custom-fruizione-typeListInt-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed1 errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | Z | 23456 | audit-custom-fruizione-typeMixed1-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed2 errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 2 | audit-custom-fruizione-typeMixed2min-nonvalido.json |
| JWK | jwk-token-custom-01 | RestBlockingAuditRest01TokenAuditCustom | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed2 errato 2 | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 234567 | audit-custom-fruizione-typeMixed2max-nonvalido.json |




@informazioni-utente-token-custom-nonvalido-erogazione
Scenario Outline: Giro Ko sull'erogazione, con informazioni utente passate negli header http (token configurato come custom) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-TypeINT = "<TypeINT>"
And header GovWay-Audit-TypeBoolean = "<TypeBoolean>"
And header GovWay-Audit-TypeStringRegExp = "<TypeStringRegExp>"
And header GovWay-Audit-TypeINTRegExp = "<TypeINTRegExp>"
And header GovWay-Audit-TypeListString = "<TypeListString>"
And header GovWay-Audit-TypeListInt = "<TypeListInt>"
And header GovWay-Audit-TypeMixed1 = "<TypeMixed1>"
And header GovWay-Audit-TypeMixed2 = "<TypeMixed2>"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<errore>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'

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
    { name: 'ProfiloSicurezzaMessaggioAudit-Subject', value: client_audit_token.payload.sub },
    { name: 'ProfiloSicurezzaMessaggioAudit-ClientId', value: client_audit_token.payload.client_id },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeINT', value: '<TypeINT>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeBoolean', value: '<TypeBoolean>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeStringRegexp', value: '<TypeStringRegExp>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeINTRegexp', value: '<TypeINTRegExp>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeListString', value: '<TypeListString>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeListInt', value: '<TypeListInt>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeMixed1', value: '<TypeMixed1>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-typeMixed2', value: '<TypeMixed2>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri di validazione custom (senza validazione, per TEST)', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth_pa_error ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri di validazione custom', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | TypeINT | TypeBoolean | TypeStringRegExp | TypeINTRegExp | TypeListString | TypeListInt | TypeMixed1 | TypeMixed2 | errore |
| JWK | jwk-token-custom-validazione-fallita-01 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeStringRegExp errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE3a | 12 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-erogazione-typeStringRegExp-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-02 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeIntRegExp errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 0 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-erogazione-typeINTRegExp-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-03 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeIntRegExp errato2 | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | -12 | Valore2 | 10.3 | ZZ | 23456 | audit-custom-erogazione-typeINTRegExp-nonvalido2.json |
| JWK | jwk-token-custom-validazione-fallita-04 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeListString errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | ValoreInesistente2 | 10.3 | ZZ | 23456 | audit-custom-erogazione-typeListString-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-05 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeListInt errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 123 | ZZ | 23456 | audit-custom-erogazione-typeListInt-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-06 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed1 errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | Z | 23456 | audit-custom-erogazione-typeMixed1-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-07 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed2 errato | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 2 | audit-custom-erogazione-typeMixed2min-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-08 | RestBlockingAuditRest01TokenAuditCustomSenzaValidazione | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim typeMixed2 errato 2 | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 23 | true | ABCDE | 12 | Valore2 | 10.3 | ZZ | 234567 | audit-custom-erogazione-typeMixed2max-nonvalido.json |





@informazioni-utente-token-custom-nonvalido-erogazione-typestring
Scenario Outline: Giro Ko sull'erogazione, con tipo spedito come primitivo e atteso come stringa, con informazioni utente passate negli header http (token configurato come custom) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Type = "<Type>"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<errore>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'

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
    { name: 'ProfiloSicurezzaMessaggioAudit-Subject', value: client_audit_token.payload.sub },
    { name: 'ProfiloSicurezzaMessaggioAudit-ClientId', value: client_audit_token.payload.client_id },
    { name: 'ProfiloSicurezzaMessaggioAudit-type', value: '<Type>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri custom; type not string', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth_pa_error ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri custom; type string', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | Type | errore |
| JWK | jwk-token-custom-validazione-fallita-typenotstring-01 | RestBlockingAuditRest01TokenAuditCustomTypeNotString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato primitivo atteso stringa | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 22 | audit-custom-erogazione-typeCustomNotString-long-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typenotstring-02 | RestBlockingAuditRest01TokenAuditCustomTypeNotString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato primitivo atteso stringa | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 2.3 | audit-custom-erogazione-typeCustomNotString-double-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typenotstring-03 | RestBlockingAuditRest01TokenAuditCustomTypeNotString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato primitivo atteso stringa | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | true | audit-custom-erogazione-typeCustomNotString-boolean-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typenotstring-04 | RestBlockingAuditRest01TokenAuditCustomTypeNotString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato primitivo atteso stringa | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | false | audit-custom-erogazione-typeCustomNotString-boolean-nonvalido2.json |





@informazioni-utente-token-custom-nonvalido-erogazione-typenotstring
Scenario Outline: Giro Ko sull'erogazione, con tipo spedito come stringa e atteso come tipo primitivo, con informazioni utente passate negli header http (token configurato come custom) <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path 'custom'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Type = "<Type>"
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/<errore>')
And match header Authorization == '#notpresent'
And match header Agid-JWT-TrackingEvidence == '#notpresent'

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
    { name: 'ProfiloSicurezzaMessaggioAudit-Subject', value: client_audit_token.payload.sub },
    { name: 'ProfiloSicurezzaMessaggioAudit-ClientId', value: client_audit_token.payload.client_id },
    { name: 'ProfiloSicurezzaMessaggioAudit-type', value: '<Type>' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: audExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri custom; type string', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth_pa_error ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, profilo_audit_schema: 'Criteri custom; type not string', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId | Type | errore |
| JWK | jwk-token-custom-validazione-fallita-typestring-01 | RestBlockingAuditRest01TokenAuditCustomTypeString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato stringa atteso primitivo | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 22 | audit-custom-erogazione-typeCustomString-long-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typestring-02 | RestBlockingAuditRest01TokenAuditCustomTypeString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato stringa atteso primitivo | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | 2.3 | audit-custom-erogazione-typeCustomString-double-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typestring-03 | RestBlockingAuditRest01TokenAuditCustomTypeString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato stringa atteso primitivo | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | true | audit-custom-erogazione-typeCustomString-boolean-nonvalido.json |
| JWK | jwk-token-custom-validazione-fallita-typestring-04 | RestBlockingAuditRest01TokenAuditCustomTypeString | AUDIT_REST_01 | IDAR01 | viene generato un token audit custom con un claim type errato come tipo, generato stringa atteso primitivo | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 | false | audit-custom-erogazione-typeCustomString-boolean-nonvalido2.json |






@token-verifica-cache
Scenario Outline: Il token di audit risulta riutilizzato grazie alla cache; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]



# Verifiche incrociate

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01-verifica-cache | RestBlockingAuditRest01 | idar01 | oauth | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-02-verifica-cache | RestBlockingAuditRest02 | idar01 | oauth-no-filtro-duplicati | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |





@token-verifica-cache-integrity
Scenario Outline: Il token di audit risulta riutilizzato grazie alla cache mentre il token di integrity è univoco; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })


# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]



# Verifiche incrociate

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


# Verifiche incrociate INTEGRITY

* match integrity_token_primo_giro != integrity_token_secondo_giro
* match integrity_token_primo_giro != integrity_token_terzo_giro

* match integrity_token_diverso_primo_giro != integrity_token_diverso_secondo_giro

* match integrity_token_primo_giro != integrity_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01-verifica-cache-integrity | RestBlockingAuditRest01 | idar04 | oauth | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-02-verifica-cache-integrity | RestBlockingAuditRest02 | idar04 | oauth-no-filtro-duplicati | AUDIT_REST_02 | IDAR02 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |







@token-verifica-cache-locale
Scenario Outline: Sia il token di audit che quello authorization risulta riutilizzato grazie alla cache; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]



# Verifiche incrociate AUDIT

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


# Verifiche incrociate ID_AUTH

* match authorization_token_primo_giro == authorization_token_secondo_giro
* match authorization_token_primo_giro == authorization_token_terzo_giro

* match authorization_token_diverso_primo_giro == authorization_token_diverso_secondo_giro

* match authorization_token_primo_giro == authorization_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| X509-TestCache | jwk-01-verifica-cache-locale | RestBlockingAuditRest01 | idar01 | locale | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |






@token-verifica-cache-locale-id-auth-filtro-duplicati
Scenario Outline: Solo il token di audit risulta riutilizzato grazie alla cache; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]



# Verifiche incrociate AUDIT

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


# Verifiche incrociate ID_AUTH

* match authorization_token_primo_giro != authorization_token_secondo_giro
* match authorization_token_primo_giro != authorization_token_terzo_giro

* match authorization_token_diverso_primo_giro != authorization_token_diverso_secondo_giro

* match authorization_token_primo_giro != authorization_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| X509-TestCache | jwk-01-verifica-cache-locale-id-auth-filtro-duplicati | RestBlockingAuditRest01 | idar01 | locale-con-filtro-duplicati | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@token-verifica-cache-locale-integrity
Scenario Outline: Sia il token di audit che quello authorization risulta riutilizzato grazie alla cache, mentre il token di integrity è sempre univoco; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo'

* def authorization_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token-differente-test-cache"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]

* def result = get_diagnostici_create_token_authorization(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'ID_AUTH\' della richiesta effettuata con successo (in cache)'

* def authorization_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def result = get_diagnostici_create_token_integrity(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'INTEGRITY\' della richiesta effettuata con successo'

* def integrity_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0]



# Verifiche incrociate AUDIT

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


# Verifiche incrociate ID_AUTH

* match authorization_token_primo_giro == authorization_token_secondo_giro
* match authorization_token_primo_giro == authorization_token_terzo_giro

* match authorization_token_diverso_primo_giro == authorization_token_diverso_secondo_giro

* match authorization_token_primo_giro == authorization_token_diverso_primo_giro


# Verifiche incrociate INTEGRITY

* match integrity_token_primo_giro != integrity_token_secondo_giro
* match integrity_token_primo_giro != integrity_token_terzo_giro

* match integrity_token_diverso_primo_giro != integrity_token_diverso_secondo_giro

* match integrity_token_primo_giro != integrity_token_diverso_primo_giro



Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| X509-TestCache | jwk-01-verifica-cache-locale-integrity | RestBlockingAuditRest01 | idar03 | locale | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |








@token-verifica-cache-elemento-not-cacheable
Scenario Outline: Il token di audit non risulta riutilizzato poichè l'elemento è dichiarato not cacheable; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim1 = "valore-claim1-required-test-cache"
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim1 = "valore-claim1-required-test-cache"
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim1 = "valore-claim1-differente-required-test-cache"
And header GovWay-Audit-Claim2 = "valore-claim2-differente-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim1 = "valore-claim1-required-test-cache"
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim1 = "valore-claim1-differente-required-test-cache"
And header GovWay-Audit-Claim2 = "valore-claim2-differente-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]



# Verifiche incrociate

* match audit_token_primo_giro != audit_token_secondo_giro
* match audit_token_primo_giro != audit_token_terzo_giro

* match audit_token_diverso_primo_giro != audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01-verifica-cache-elemento-not-cacheable | RestBlockingAuditRest01TokenAuditClaimNotCacheable | idar01 | oauth | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |
| JWK | jwk-01-verifica-cache-elemento-optional-not-cacheable | RestBlockingAuditRest01TokenAuditClaimOptionalNotCacheable | idar01 | oauth | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |






@token-verifica-cache-elemento-optional-not-cacheable-non-usato
Scenario Outline: Il token di audit risulta riutilizzato poichè l'elemento opzionale dichiarato not cacheable non viene usato; erogazione <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> (<descrizione>)

# Prima giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim2 = "valore-claim2-differente-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo'

* def audit_token_diverso_primo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# terzo giro

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente1'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim2 = "valore-claim2-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_terzo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]


* java.lang.Thread.sleep(1000)


# Secondo giro, invocazione con parametri diversi (nell'idUser)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>/v1"
And path '<path1>', '<path2>'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-utente2'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-Claim2 = "valore-claim2-differente-required-test-cache"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* def result = get_diagnostici_create_token_audit(tid) 
* match result[0].MESSAGGIO == 'Creazione security token ModI \'AUDIT\' della richiesta effettuata con successo (in cache)'

* def audit_token_diverso_secondo_giro = responseHeaders['GovWay-TestSuite-GovWay-Client-Audit-Token'][0]



# Verifiche incrociate

* match audit_token_primo_giro == audit_token_secondo_giro
* match audit_token_primo_giro == audit_token_terzo_giro

* match audit_token_diverso_primo_giro == audit_token_diverso_secondo_giro

* match audit_token_primo_giro != audit_token_diverso_primo_giro


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | path1 | path2 | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato | RestBlockingAuditRest01TokenAuditClaimOptionalNotCacheable | idar01 | oauth | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |





@negoziazioneViaTokenPolicySecurityOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore JWK definito nella token policy

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaAuditViaTokenPolicy/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'FruizioneAuditViaTokenPolicy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'



@negoziazioneViaTokenPolicySecurityConIntegrityOk
Scenario: Test negoziazione ok tramite l'utilizzo di un keystore JWK definito nella token policy, anche con integrity

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/DemoNegoziazioneTokenSicurezzaIntegrityAuditViaTokenPolicy/v1"
And path 'test'
And request read('request.json')
And header govway-testsuite-role = 'undefined'
And header tiponegoziazionetest = 'FruizioneIntegrityAuditViaTokenPolicy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
When method post
Then status 200
And match response == read('request.json')
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'




@audit-as-array
Scenario Outline: Giro Ok con informazioni utente passate negli header http <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> e audit spedito come array (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>-AudienceAsArray/v1"
And path 'idar01', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-differentAudienceAsArray'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>-AudienceAsArray/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And header GovWay-Audit-User = "utente-token"
And header GovWay-Audit-UserLocation = "ip-utente-token"
And header GovWay-Audit-LoA = "livello-autenticazione-utente-token"
And header GovWay-TestSuite-Test-Audience = "[altro,testsuite-audience]"
And header GovWay-TestSuite-Test-AudienceAudit = "[testsuite-audience-audit,altro]"
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

* def other_checks_richiesta_audit = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: '["testsuite-audience-audit","altro"]' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_audit, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_authorization_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_audit, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_authorization_token.payload.jti


Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-01 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR01 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | pkcs12 | ApplicativoBlockingIDA01 | ApplicativoBlockingIDA01 | purposeId-ApplicativoBlockingIDA01 | KID-ApplicativoBlockingIDA01 | DemoSoggettoFruitore/ApplicativoBlockingIDA01 |




@integrity-audit-as-array
Scenario Outline: Giro Ok con informazioni utente passate nella query url <tipo-test> pattern:<sicurezzaPattern> audit:<auditPattern> e audit spedito come array (<descrizione>)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/<nome-api-impl>-<tipo-test>-AudienceAsArray/v1"
And path 'idar04', 'oauth'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'audit-rest-<tipo-test-minuscolo>-differentAudienceAsArray'
And header Authorization = call basic ({ username: '<username>', password: '<password>' })
And header simulazionepdnd-username = '<username>'
And header simulazionepdnd-password = '<password>'
And header simulazionepdnd-purposeId = '<purposeId>'
And header simulazionepdnd-audience = '<nome-api-impl>-<tipo-test>-AudienceAsArray/v1'
And header simulazionepdnd-digest-mode = 'proxy'
And param govway_audit_user = "utente-token"
And param govway_audit_user_Location = "ip-utente-token"
And param govway_audit_loa = "livello-autenticazione-utente-token"
And header GovWay-TestSuite-Test-Audience = "[altro,testsuite-audience]"
And header GovWay-TestSuite-Test-AudienceAudit = "[testsuite-audience-audit,altro]"
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
    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["altro","testsuite-audience"]' },
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

* def other_checks_richiesta_audit = 
"""
([
    { name: 'GenerazioneTokenIDAuth', value: 'Authorization OAuth' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: issExpected },
    { name: 'ProfiloSicurezzaMessaggioAudit-userID', value: 'utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-userLocation', value: 'ip-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-LoA', value: 'livello-autenticazione-utente-token' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Audience', value: '["testsuite-audience-audit","altro"]' },
    { name: 'ProfiloSicurezzaMessaggioAudit-Issuer', value: client_audit_token.payload.iss },
    { name: 'ProfiloSicurezzaMessaggioAudit-Kid', value: kidRequest }
])
"""

* def auditPattern = '<auditPattern>'

* def sicurezzaPattern = '<sicurezzaPattern>'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_no_audience ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_audit, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_kid_solo_oauth ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_authorization_richiesta, profilo_interazione: 'crud', token_auth: 'Authorization OAuth', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_no_audience ({ tid: tid, tipo: 'Richiesta', token: client_integrity_token, kid: kidRequest, profilo_sicurezza: sicurezzaPattern, other_checks: other_checks_integrity_richiesta, profilo_interazione: 'crud', traceMessageId:client_integrity_token.payload.jti })
* call check_traccia_kid_solo_audit ({ tid: tid, tipo: 'Richiesta', token: client_audit_token, profilo_sicurezza: sicurezzaPattern, profilo_audit: auditPattern, other_checks: other_checks_richiesta_audit, profilo_interazione: 'crud' })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_integrity_token.payload.jti



Examples:
| tipo-test | tipo-test-minuscolo | nome-api-impl | auditPattern | sicurezzaPattern | descrizione | tipo-keystore-client | username | password | purposeId | kid | clientId |
| JWK | jwk-0401 | RestBlockingAuditRest01 | AUDIT_REST_01 | IDAR0401 | servizio che genera una risposta tramite jwk. Anche la validazione dei certificati token è tramite jwk | jwk | ApplicativoBlockingJWK | ApplicativoBlockingJWK | purposeId-ApplicativoBlockingJWK | KID-ApplicativoBlockingJWK | DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK |
