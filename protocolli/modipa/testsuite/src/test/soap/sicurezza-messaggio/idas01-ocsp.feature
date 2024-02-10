Feature: Testing Sicurezza Messaggio ModiPA IDAS01

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia_ocsp.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

@connettivita-base-truststore-ca-ocsp
Scenario: Test connettività base con erogazione e fruizione che usano il trustore delle CA con OCSP

* def body = read("request.xml")
* def resp = read("response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCAOCSP/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-truststore-ca-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01_OCSP', password: 'ApplicativoBlockingIDA01_OCSP' })
When method post
Then status 200
And match response == resp

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it', requestMessageId:client_request_id })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it', requestMessageId:client_request_id })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_request_id


@certificato-client-revocato-ocsp
Scenario: Viene utilizzato un applicativo con il certificato revocato, facendo arrabbiare l'erogazione (OCSP)

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCAOCSP/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-client-revocato-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoRevocato_OCSP', password: 'ApplicativoBlockingIDA01CertificatoRevocato_OCSP' })
When method post
Then status 500
And match response == read("error-bodies/certificato-client-revocato-ocsp.xml")


@certificato-server-revocato-ocsp
Scenario: Per l'erogazione viene utilizzato un certificato revocato, facendo arrabbiare la fruizione (OCSP)

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCAOCSP/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'certificato-server-revocato-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01_OCSP', password: 'ApplicativoBlockingIDA01_OCSP' })
When method post
Then status 500
And match response == read("error-bodies/certificato-server-revocato-ocsp.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'
