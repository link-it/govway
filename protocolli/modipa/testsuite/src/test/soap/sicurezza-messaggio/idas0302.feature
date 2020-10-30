Feature: Testing Sicurezza Messaggio ModiPA IDAS0302

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

    * def x509sub_client1 = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def x509sub_server = 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT'


@connettivita-base
Scenario: Test giro ok, controllo dell'elemento X-RequestDigest

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas0302'
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
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0302", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0302", other_checks: checks_risposta })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0302", other_checks: checks_richiesta })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: x509sub_server, profilo_sicurezza: "IDAS0302", other_checks: checks_risposta })


@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-richiesta.xml')


@manomissione-token-risposta
Scenario: Il payload del token di risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-token-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@manomissione-payload-richiesta
Scenario: Il payload della richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-richiesta-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-payload-richiesta.xml')


@manomissione-payload-risposta
Scenario: Il payload della risposta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-payload-risposta-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/manomissione-payload-risposta.xml')
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'


@riutilizzo-token
Scenario: Il token viene riutilizzato, fruizione ed erogazione devono arrabbiarsi

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

# Ripeto la richiesta all'erogazione per farla arrabbiare

* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

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

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1'

# La richiesta normale fa generare un nuovo indentificativo, devo riusare la stessa richiesta
# e la stessa risposta. Ma non è possibile perchè ad ogni richiesta la fruizione imposta nello header
# un nuovo valore per il campo ReleaseTo che riferisce l'identificativo della precedente richiesta.

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-idas0302'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-risposta.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'