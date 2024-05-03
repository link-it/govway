Feature: Testing Sicurezza Messaggio ModiPA IDAS01

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

@connettivita-base
Scenario: Test connettività base

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@connettivita-base-default-trustore
Scenario: Test connettività base con trustore default

* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01DefaultTrustore/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-default-trustore'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@connettivita-base-no-sbustamento
Scenario: Test connettività base senza sbustamento modipa

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01DefaultTrustore/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-no-sbustamento'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01NoSbustamento', password: 'ApplicativoBlockingIDA01NoSbustamento' })
When method post
Then status 200

* match /Envelope/Header/Security/Signature == "#present"
* match /Envelope/Header/Security/Timestamp/Created == "#string"
* match /Envelope/Header/Security/Timestamp/Expires == "#string"
* match /Envelope/Header/To == "DemoSoggettoFruitore/ApplicativoBlockingIDA01NoSbustamento"
* match /Envelope/Header/From/Address == "SoapBlockingIDAS01DefaultTrustoreNoSbustamento/v1"
* match /Envelope/Header/MessageID == "#uuid"

* def body = response
* call check_signature [ {element: 'To'}, {element: 'From'}, {element: 'MessageID'}, {element: 'ReplyTo'}, {element: 'RelatesTo'} ]

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id




@connettivita-base-truststore-ca
Scenario: Test connettività base con erogazione e fruizione che usano il trustore delle CA

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-truststore-ca'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@response-without-payload
Scenario: Test di una azione che non ha il payload nella risposta

* def body = read("only-request.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOP/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'response-without-payload'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == ''

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@disabled-security-on-action
Scenario: Test risorsa non protetta in una API con IDAS01 abilitato di default

* def body = read("MRequestResponse.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOP/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'disabled-security-on-action'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response-op.xml')


@enabled-security-on-action
Scenario: Test risorsa protetta in una API con IDAS01 disabilitato di default

* def body = read("MRequestResponse.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOPNoDefaultSecurity/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'enabled-security-on-action'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response-op.xml')

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


# Voglio testare che sull'altra azione non sia abilitata la sicurezza

* def body = read("MRequestResponse1.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOPNoDefaultSecurity/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'enabled-security-on-action'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read('response-op.xml')


@riferimento-x509-SKIKey-IssuerSerial
Scenario: Test con Riferimento x509 SKI Key per la fruizione e IssuerSerial per l'erogazione

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01SKIKey/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-SKIKey-IssuerSerial'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp


* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@riferimento-x509-ThumbprintKey-SKIKey
Scenario: Test con Riferimento x509 Thumbprint Key per la fruizione e SKIKey per l'erogazione

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01ThumbprintKey/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-ThumbprintKey-SKIKey'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@riferimento-x509-x509Key-ThumbprintKey
Scenario: Test con Riferimento x509 Thumbprint Key per la fruizione e SKIKey per l'erogazione

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01X509KeyId/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-x509Key-ThumbprintKey'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@riferimento-x509-IssuerSerial-x509Key
Scenario: Test con Riferimento x509 Thumbprint Key per la fruizione e SKIKey per l'erogazione

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01IssuerSerial/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riferimento-x509-IssuerSerial-x509Key'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@no-token-to-erogazione
Scenario: All'erogazione non arriva nessun token e questa deve arrabbiarsi

* def body = read("request.xml")
* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
When method post
Then status 500
And match response == read('error-bodies/no-token-in-richiesta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


@no-token-to-fruizione
Scenario: Nella risposta alla fruizione non arriva nessun token e questa deve arrabbiarsi

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'no-token-to-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/no-token-in-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'

@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-richiesta.xml')


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@low-ttl-fruizione
Scenario: Il TTL del token della fruizione (richiesta) viene superato e l'erogazione si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01LowTTL/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'low-ttl-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/ttl-scaduto-in-request.xml')


@low-ttl-erogazione
Scenario: Il TTL del token della erogazione (risposta) viene superato e la fruizione si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'low-ttl-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/ttl-scaduto-in-response.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@low-iat-ttl-fruizione
Scenario: Il claim create del token della fruizione (richiesta) è troppo vecchio per l'erogazione la quale si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01LowIAT/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'low-iat-ttl-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/iat-scaduto-in-request.xml')


@low-iat-ttl-erogazione
Scenario: Il claim create del token dell'erogazione (risposta) è troppo vecchio per la fruizione la quale si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01LowIAT/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'low-iat-ttl-erogazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/iat-scaduto-in-response.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'



@applicativo-non-autorizzato
Scenario: Viene utilizzato l'identificativo di un applicativo non autorizzato dalla erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'applicativo-non-autorizzato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClient2', password: 'ApplicativoBlockingIDA01ExampleClient2' })
When method post
Then status 500
And match response == read('error-bodies/applicativo-non-autorizzato.xml')


@certificato-client-scaduto
Scenario: Viene utilizzato un applicativo con il certificato scaduto, con l'erogazione che si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-client-scaduto'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoScaduto', password: 'ApplicativoBlockingIDA01CertificatoScaduto' })
When method post
Then status 500
And match response == read("error-bodies/certificato-client-scaduto.xml")


@certificato-client-revocato
Scenario: Viene utilizzato un applicativo con il certificato revocato, facendo arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-client-revocato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoRevocato', password: 'ApplicativoBlockingIDA01CertificatoRevocato' })
When method post
Then status 500
And match response == read("error-bodies/certificato-client-revocato.xml")


@certificato-server-scaduto
Scenario: Per l'erogazione viene utilizzato un certificato scaduto, facendo arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-server-scaduto'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/certificato-server-scaduto.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@certificato-server-revocato
Scenario: Per l'erogazione viene utilizzato un certificato revocato, facendo arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-server-revocato'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/certificato-server-revocato.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@keystore-default-fruizione
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore di base

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01KeystoreDefaultFruizione/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'keystore-default-fruizione'
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@keystore-ridefinito-fruizione
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato contiene un keystore modi, ma non verrà utilizzato.

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01KeystoreRidefinitoFruizione/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@keystore-ridefinito-fruizione-applicativo-no-keystore
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito. L'applicativo identificato non contiene un keystore modi

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01KeystoreRidefinitoFruizione/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01ExampleClientToken1', password: 'ApplicativoBlockingIDA01ExampleClientToken1' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient5, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


@keystore-ridefinito-fruizione-archivio
Scenario: Test connettività base in cui il keystore è definito nella fruizione, come keystore ridefinito tramite archivio. L'applicativo identificato contiene un keystore modi, ma non verrà utilizzato.

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01KeystoreRidefinitoFruizioneArchivio/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'keystore-ridefinito-fruizione-archivio'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient4, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient4, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id



@keystore-definito-applicativo
Scenario: Test connettività base in cui il keystore è definito nell'applicativo. A differenza delle altre configurazioni, in questa è stata salvata la proprietà che imposta il kestore definito nell'applicativo, mentre nelle altre funziona in ugual maniera per retrocompatibilita'

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01KeystoreApplicativo/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'keystore-definito-applicativo'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = karate.response.header('GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tid = karate.response.header('GovWay-TestSuite-GovWay-Transaction-ID')
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT', requestMessageId:client_request_id })

* def tidMessaggio = karate.response.header('GovWay-Message-ID')
* match tidMessaggio == client_request_id


