Feature: Testing Sicurezza Messaggio ModiPA IDAR01 Senza Disclosure sugli errori

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')
    

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
And match response == read('error-bodies/interoperability-invalid-request.xml')
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
And match response == read('error-bodies/invalid-response.xml')
And match header GovWay-Transaction-ErrorType == 'InvalidResponse'

@manomissione-token-richiesta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare l'erogazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-richiesta-no-disclosure'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/interoperability-invalid-request.xml')


@manomissione-token-risposta
Scenario: Il payload del token di richiesta viene manomesso in modo da non far corrispondere più la firma e far arrabbiare la fruizione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'manomissione-token-risposta'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/invalid-response.xml')
And match header GovWay-Transaction-ErrorType == 'InvalidResponse'


@low-ttl-fruizione
Scenario: Il TTL del token della fruizione (richiesta) viene superato e l'erogazione si arrabbia

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS01LowTTL/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'low-ttl-fruizione-no-disclosure'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read('error-bodies/interoperability-invalid-request.xml')


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
And match response == read('error-bodies/invalid-response.xml')
And match header GovWay-Transaction-ErrorType == 'InvalidResponse'
