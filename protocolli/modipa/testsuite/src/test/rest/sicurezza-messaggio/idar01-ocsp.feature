Feature: Testing Sicurezza Messaggio ModiPA IDAR01 con OCSP

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    * def check_traccia = read('check-tracce/check-traccia_ocsp.feature')
    * def check_traccia_sub_iss_client_notpresent = read('check-tracce/check-traccia_sub_iss_clientid_notpresent.feature')
    * def decode_token = read('classpath:utils/decode-token.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }


@connettivita-base-truststore-ca-ocsp
Scenario: Test connettività base con erogazione e fruizione che usano il trustore delle CA con OCSP

* def url_invocazione = govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCAOCSP/v1"

Given url url_invocazione
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'connettivita-base-truststore-ca-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01_OCSP', password: 'ApplicativoBlockingIDA01_OCSP' })
When method post
Then status 200
And match response == read('response.json')
And match header Authorization == '#notpresent'


* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Token'][0])
* def server_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Server-Token'][0])

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({ tid: tid, tipo: 'Richiesta', token: client_token, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })
* call check_traccia ({ tid: tid, tipo: 'Risposta', token: server_token, x509sub: 'CN=Client-test.esempio.it, O=Esempio, C=it' })


@certificato-client-revocato-ocsp
Scenario: Viene utilizzato un applicativo con il certificato revocato, facendo arrabbiare l'erogazione (OCSP)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCAOCSP/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-client-revocato-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01CertificatoRevocato_OCSP', password: 'ApplicativoBlockingIDA01CertificatoRevocato_OCSP' })
When method post
Then status 400


@certificato-server-revocato-ocsp
Scenario: Per l'erogazione viene utilizzato un certificato server revocato, facendo arrabbiare la fruizione (OCSP)

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCAOCSP/v1"
And path 'resources', 1, 'M'
And request read('request.json')
And header GovWay-TestSuite-Test-ID = 'certificato-server-revocato-ocsp'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01_OCSP', password: 'ApplicativoBlockingIDA01_OCSP' })
When method post
Then status 502
And match response == read('error-bodies/certificato-server-revocato-ocsp.json')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'
