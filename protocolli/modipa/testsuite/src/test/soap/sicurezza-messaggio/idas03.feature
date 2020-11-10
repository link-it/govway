Feature: Testing Sicurezza Messaggio ModiPA IDAS03

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

    * def x509sub_client1 = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def x509sub_server = 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT'


@connettivita-base
Scenario: Test giro ok, controllo del X-RequestDigest

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-idas03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-richiesta.xml')


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta-idas03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@manomissione-payload-richiesta
Scenario: Il payload della richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-payload-richiesta.xml')


@manomissione-payload-risposta
Scenario: Il payload della risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-payload-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@connettivita-base-no-digest-richiesta
Scenario: Test connettività base senza i digest della richiesta

* def body = read("MRequestResponse.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOp/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas03-no-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response-op.xml')


* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID

* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@response-without-payload
Scenario: Test di una azione che non ha il payload nella risposta
* def body = read("only-request.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOp/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idas03'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == ''

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })


@response-without-payload-digest-richiesta
Scenario: Test di una azione che non ha il payload nella risposta

* def body = read("only-request.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOpDigestRichiesta/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'response-without-payload-idas03-digest-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == ''

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
])
"""

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })


@informazioni-utente-header
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-header'
And header GovWay-CS-User = "utente-token"
And header GovWay-CS-IPUser = "ip-utente-token"
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token'},
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@informazioni-utente-query
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-query'
And param govway_cs_user = "utente-token"
And param govway_cs_ipuser = "ip-utente-token"
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token'},
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@informazioni-utente-mixed
Scenario: Giro Ok IDAR03 con informazioni utente passate negli header http

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-query'
And header GovWay-CS-User = "utente-token"
And param govway_cs_ipuser = "ip-utente-token"
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'DemoSoggettoFruitore'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token'},
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@informazioni-utente-static
Scenario: Giro Ok IDAR03 con informazioni utente statiche

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtenteStatic/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-static'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'codice-ente-static'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token-static'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token-static'},
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@informazioni-utente-custom
Scenario: Giro Ok IDAR03 con informazioni utente in header custom

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtenteCustom/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-custom'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header CodiceEnteCustom = 'codice-ente-custom'
And header UserIDUtenteCustom = "utente-token"
And header IndirizzoIPUtenteCustom = "ip-utente-token"
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-Ente', value: 'codice-ente-custom'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-User', value: 'utente-token'},
    { name: 'ProfiloSicurezzaMessaggio-CorniceSicurezza-UserIP', value: 'ip-utente-token'},
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@no-informazioni-utente-at-erogazione
Scenario: All'erogazione non arriva la security con le informazioni utente

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'no-informazioni-utente-at-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/no-informazioni-utente-at-erogazione.xml")


@no-informazioni-utente-at-fruizione
Scenario: Alla fruizione non passo gli header per l'informazione utente, facendola arrabbiare

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'informazioni-utente-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/no-informazioni-utente-at-fruizione.xml")
And match header GovWay-Transaction-ErrorType == "BadRequest"


@idas03-token-richiesta
Scenario: Giro ok idas03 con il token soltanto nella richiesta

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03TokenRichiesta/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'idas03-token-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

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


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })



@idas03-token-risposta
Scenario: Giro ok idar03 con il token soltanto nella risposta

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03TokenRisposta/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'idas03-token-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')

* xml server_response = karateCache.get("Server-Response")


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")


* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })


@idas03-token-azione-puntuale
Scenario: Giro ok idar03 con il token abilitato solo sulla richiesta per una specifica azione, globalmente è richiesta/risposta.

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03TokenAzionePuntuale/v1'

Given url soap_url
And request read("MRequestResponse.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'idas03-token-azione-puntuale'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response-op.xml")

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


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })


# Faccio una richiesta su un'azione di default per dimostrare che il flusso normale
# con il token sia in richiesta che risposta venga rispettato

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS03TokenAzionePuntuale/v1'

Given url soap_url
And request read("MRequestResponse1.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'idas03-token-azione-puntuale-default'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response-op.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def body_reference = get client_request/Envelope/Body/@Id
* def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")
* def request_id = get client_request/Envelope/Header/MessageID


* def body_reference = get server_response/Envelope/Body/@Id
* def response_signature = karate.xmlPath(server_response, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

* def checks_richiesta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
])
"""

* def checks_risposta = 
"""
([
    { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+response_signature},
    { name: 'ProfiloSicurezzaMessaggio-RelatesTo', value: request_id}
])
"""


* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0301", other_checks: checks_risposta })
