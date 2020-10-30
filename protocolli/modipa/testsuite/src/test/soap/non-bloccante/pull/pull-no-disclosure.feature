Feature: Test Profilo Non Bloccante Pull senza disclosure di informazioni

Background:

* def url_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/NonBlockingSoapPullProxy/v1"
* def url_no_validazione = govway_base_path + "/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/NonBlockingSoapPullNoValidazioneProxy/v1"

* def result = callonce read('classpath:utils/jmx-disable-error-disclosure.feature')

* configure headers = { 'Content-Type': 'application/soap+xml', 'action': url_validazione }
* def problem = read("error-bodies/invalid-response.xml")

* def check_traccia_richiesta = read('./check-tracce/richiesta.feature')
* def check_traccia_richiesta_stato = read('./check-tracce/richiesta-stato.feature')
* def check_traccia_richiesta_stato_no_cid = read('./check-tracce/richiesta-stato-no-cid.feature')
* def check_traccia_risposta = read('./check-tracce/risposta.feature')
* def check_traccia_risposta_no_cid = read('./check-tracce/risposta-no-cid.feature')
* def check_id_collaborazione = read('./check-tracce/id-collaborazione.feature')

# Qui facciamo arrabbiare una volta l'erogazione e una volta la fruizione
# Questo è l'unico caso della testsuite in cui generiamo un'errore di validazione
# incrociato con un errore modIPA, per testare che l'integrazione fra i due funzioni.

@no-correlation-in-request-response
Scenario: Richiesta applicativa senza X-Correlation-ID nella risposta

    Given url url_no_validazione
    And request read("richiesta-applicativa.xml")
    And header GovWay-TestSuite-Test-Id = 'no-correlation-in-request-fruizione'
    When method post
    Then status 500
    And match response == problem
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'


    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })

# In questo caso l'erogazione aggiunge automaticamente lo header
# quindi ha senso testare solo la fruizione.
@no-correlation-in-request-response-validazione
Scenario: Richiesta applicativa senza X-Correlation-ID nella risposta con validazione sintattica body

    Given url url_validazione
    And request read("richiesta-applicativa.xml")
    And header GovWay-TestSuite-Test-Id = 'no-correlation-in-request-fruizione-validazione'
    When method post
    Then status 500
    And match response == problem
    And match header GovWay-Transaction-ErrorType == 'InvalidResponse'

    * call check_traccia_richiesta ({tid: responseHeaders['GovWay-Transaction-ID'][0]})
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })


@no-correlation-in-soap-header-fruizione
Scenario: Testa che la fruizione si arrabbi se nelle richieste non è presente lo header soap X-Corrleation-ID

    Given url url_no_validazione
    And request read("richiesta-stato-no-correlation.xml")
    When method post
    Then status 500
    And match response == read('error-bodies/no-correlation-id-in-request-status-no-disclosure.xml')
    And match header GovWay-Transaction-ErrorType == 'BadRequest'
    

    Given url url_no_validazione
    And request read("recupero-risposta-no-correlation.xml")
    When method post
    Then status 500
    And match response == read('error-bodies/no-correlation-id-in-request-status-no-disclosure.xml')
    And match header GovWay-Transaction-ErrorType == 'BadRequest'


@no-correlation-in-soap-header-erogazione
Scenario: Testa che l'erogazione si arrabbi se non è presente lo header soap X-Corrleation-ID nella

    * def url_erogazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPullNoValidazione/v1"

    Given url url_erogazione
    And request read("richiesta-stato-no-correlation.xml")
    And header GovWay-TestSuite-Test-Id = 'no-correlation-in-soap-header-erogazione'
    When method post
    Then status 500
    And match response == read('error-bodies/no-correlation-id-in-request-status-erogazione-no-disclosure.xml')
    And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

    
    # Invece l'erogazione segna la transazione ma non scrive il correlation ID
    * call check_traccia_richiesta_stato_no_cid ({tid: responseHeaders['GovWay-Transaction-ID'][0] })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })

    Given url url_erogazione
    And request read("recupero-risposta-no-correlation.xml")
    And header GovWay-TestSuite-Test-Id = 'no-correlation-in-soap-header-erogazione'
    When method post
    Then status 500
    And match response == read('error-bodies/no-correlation-id-in-request-status-erogazione-no-disclosure.xml')
    And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

    
    * call check_traccia_risposta_no_cid ({tid: responseHeaders['GovWay-Transaction-ID'][0]  })
    * call check_id_collaborazione ({tid: responseHeaders['GovWay-Transaction-ID'][0], id_collaborazione: null })





