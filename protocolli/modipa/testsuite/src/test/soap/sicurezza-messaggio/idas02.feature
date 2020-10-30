Feature: Testing Sicurezza Messaggio ModiPA IDAS01

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

    * def client_x509_sub = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def server_x509_sub = 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT'

@connettivita-base
Scenario: Test connettività base

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02' })


@riutilizzo-token
Scenario: Il token viene riutilizzato, fruizione ed erogazione devono arrabbiarsi

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

# Ripeto la richiesta all'erogazione per farla arrabbiare

* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request client_request
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-richiesta.xml")
And match header GovWay-Transaction-ErrorType == 'Conflict'

# Faccio in modo che il proxy risponda alla fruizione con la precedente risposta dell'erogazione
# NOTA: Questo test non controlla in realtà che la fruizione si arrabbi perchè il token è stato riutilizzato,
#   infatti si arrabbia perchè "IdentificativoBusta presente nel RiferimentoMessaggio non è valido", infatti
# inviando nuovamente la stessa risposta, l'identificativo busta appartiene a quello della richiesta precedente.

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

# La richiesta normale fa generare un nuovo indentificativo, devo riusare la stessa richiesta
# e la stessa risposta. Ma non è possibile perchè ad ogni richiesta la fruizione imposta nello header
# un nuovo valore per il campo ReleaseTo che riferisce l'identificativo della precedente richiesta.

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-risposta.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'
