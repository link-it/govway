Feature: Testing feature idac02 REST

Background:

* def check_traccia = read('classpath:utils/check-traccia-idac02.feature')


@autenticazione-client
Scenario: IDAC02 Autenticazione Client

* def body = read("classpath:bodies/modipa-blocking-sample-request.json")
* def resp = read("classpath:test/risposte-default/rest/bloccante/response.json")

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingRestIDAC02/v1'
And path 'resources', 1, 'M'
And request body
When method post
Then status 200
And match response == resp

* call check_traccia ({ fruizione_tid: responseHeaders['GovWay-Transaction-ID'][0], erogazione_tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })


@autenticazione-client-assente
Scenario: IDAC02 Autenticazione Client Assente

* def body = read("classpath:bodies/modipa-blocking-sample-request.json")

Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingRestIDAC02BadAuth/v1'
And path 'resources', 1, 'M'
And request body
When method post
Then status 401

# Siccome lato erogazione non arriva lo header del Tid dell'erogazione,
# controlliamo solo quello della fruizione.

* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC02' }
]
"""
* def result = get_traccia(responseHeaders['GovWay-Transaction-ID'][0], 'Richiesta') 
* match result contains deep traccia_to_match

* def traccia_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC02' }
]
"""
* def result = get_traccia(responseHeaders['GovWay-Transaction-ID'][0], 'Risposta') 
* match result contains deep traccia_to_match