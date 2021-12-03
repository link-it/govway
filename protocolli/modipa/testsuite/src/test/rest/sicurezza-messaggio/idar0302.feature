Feature: Testing Sicurezza Messaggio ModiPA IDAR0302 (Unicità messaggio)

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_info_utente = read('check-tracce/check-traccia-info-utente.feature')
    * def check_traccia_self_signed = read('check-tracce/check-traccia-self-signed.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@connettivita-base
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar03testheader', value: 'TestHeaderRequest' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-idar03testheader', value: 'TestHeaderResponse' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta })


@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/invalid-token-signature-in-response.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@manomissione-payload-richiesta
Scenario: Il payload della richiesta viene modificato in modo da non far coincidere la firma e fare arrabbiare l'erogazione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@manomissione-payload-risposta
Scenario: Il payload della risposta viene modificato in modo da non far coincidere la firma e fare arrabbiare la fruizione

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1'
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/manomissione-token-risposta.json')


@manomissione-header-http-firmati-richiesta
Scenario: Lo header da firmare IDAR03TestHeader viene manomesso nella richiesta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-richiesta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 502


@manomissione-header-http-firmati-risposta
Scenario: Lo header da firmare IDAR03TestHeader viene manomesso nella risposta

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'manomissione-header-http-firmati-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header IDAR03TestHeader = "TestHeaderRequest"
When method post
Then status 502
And match response == read('error-bodies/manomissione-header-http-firmati-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@assenza-header-digest-richiesta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare l'erogazione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-richiesta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502


@assenza-header-digest-risposta
Scenario: Il proxy rimuove lo header Digest per far arrabbiare la fruizione

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'assenza-header-digest-risposta-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/assenza-header-digest-risposta.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@riutilizzo-token
Scenario: Riutilizzo dello stesso token, che deve far arrabiare erogazione e fruizione

# Prima facciamo un giro ok per far generare il token alla fruizione e alla erogazione
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-idar0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null

* def client_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0]
* def server_token_header = responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0]

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0], "AGID")
* def request_digest = get client_token $.payload.signed_headers..digest

* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0], "AGID")
* def response_digest = get server_token $.payload.signed_headers..digest

# Contattiamo direttamente l'erogazione con il token che si ripete

Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header Agid-JWT-Signature = client_token_header
And header Digest = request_digest[0]
When method post
Then status 409
And match response == read('error-bodies/identificativo-token-riutilizzato.json')
And match header GovWay-Transaction-ErrorType == 'Conflict'


Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-risposta-idar0302'
And header GovWay-TestSuite-Server-Token = server_token_header
And header GovWay-TestSuite-Digest = response_digest[0]
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 502
And match response == read('error-bodies/identificativo-token-riutilizzato-in-risposta.json')
And match header GovWay-Transaction-ErrorType == 'ConflictResponse'


@connettivita-base-header-bearer
Scenario: Test connettività base

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderBearer/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idar0302-header-bearer'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])
* def request_digest = get client_token $.payload.signed_headers..digest
* def response_digest = get server_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
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
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta })


@doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato
Scenario: Sicurezza che prevede token in cui sono stati ridefiniti dei claims, viene usata la cornice di sicurezza e un header Authorization viene firmato dentro del header Agid-JWT-Signature (solo nella risposta)

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderDuplicatiCorniceSicurezzaCustomClaim/v1"

Given url url_invocazione
And path 'test0302conCorniceSicurezza'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
And param userIP = '10.112.32.21'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Authorization-Token'][0], "Bearer")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def request_jti_integrity = get client_integrity_token $.payload.jti

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'customIssSoggettoFruitore' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'ApplicativoBlockingIDA01ExampleClient2Integrity' },
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: '10.112.32.21' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json' },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-authorization', value: '#string' },
    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'customAud' }
])
"""

* eval server_integrity_token.payload.aud = 'customAudAuthorization'

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_info_utente ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_info_utente ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleClient2, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })



@doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato-richiesta
Scenario: Sicurezza che prevede token in cui sono stati ridefiniti dei claims, viene usata la cornice di sicurezza e un header Authorization viene firmato dentro del header Agid-JWT-Signature
# La configurazione della fruizione "punta" direttamente verso l'erogazione poiche' altrimenti al server 'proxy' di karate non arrivano gli header, probabilmente perche' si supera una qualche dimensione massima
# I controlli nelle tracce vengono comunque fatte, anche se solo sulla risposta, nel test precedente

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderDuplicatiCorniceSicurezzaCustomClaimFirmaAuthorizationRichiesta/v1"

Given url url_invocazione
And path 'test0302conCorniceSicurezza'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
And param userIP = '10.112.32.21'
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null



@pkcs11
Scenario: Test base PKCS11

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/PKCS11TestREST/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'pkcs11'
And header Authorization = call basic ({ username: 'PKCS11-Client1HSM', password: '123456' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
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
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient1HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServerHSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient1HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServerHSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })



@pkcs11-certificate
Scenario: Test base PKCS11 dove il client, oltre a essere definito tramite HSM, è stato caricato anche il certificato. 

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/PKCS11TestREST/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'pkcs11-certificate'
And header Authorization = call basic ({ username: 'PKCS11-Client2HSM', password: '123456' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == null
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
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
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServerHSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServerHSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })




@pkcs11-trustStore
Scenario: Test base PKCS11 che viene usato anche come trustStore

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/PKCS11TestRESTtrustStore/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'pkcs11-trustStore'
And header Authorization = call basic ({ username: 'PKCS11-Client3HSM', password: '123456' })
When method post
Then status 200
And match response == {"esito":"OK"}
And match header Authorization == null
And match header Agid-JWT-Signature == '#notpresent'

* def client_authorization_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def client_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Integrity-Token'][0], "AGID")
* def server_integrity_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Integrity-Token'][0], "AGID")

* def request_digest = get client_integrity_token $.payload.signed_headers..digest
* def response_digest = get server_integrity_token $.payload.signed_headers..digest

* def other_checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: request_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; charset=UTF-8' }
])
"""

* def other_checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-digest', value: response_digest[0] },
    { name: 'ProfiloSicurezzaMessaggioSignedHeader-content-type', value: 'application/json; prova="aa";charset=UTF-8' }
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient3HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_self_signed ({ tid: tid, tipo: 'Richiesta', token: client_authorization_token, x509sub: 'CN=ExampleModIClient3HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_richiesta, profilo_interazione: 'crud' })
* call check_traccia_self_signed ({ tid: tid, tipo: 'Risposta', token: server_integrity_token, x509sub: 'CN=ExampleServer2HSM, OU=Test, O=Test, L=Pisa, ST=Italy, C=IT', profilo_sicurezza: 'IDAR0302', other_checks: other_checks_risposta, profilo_interazione: 'crud' })


