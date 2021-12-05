Feature: Server di mock contattato dalla erogazione, svolge il ruolo del TestService di echo.

Background: 

    * configure responseHeaders = { 'Content-type': "application/soap+xml" }

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """


# Scenario giro ok, richiesta stato non pronta
Scenario: methodIs('post') && bodyPath('/Envelope/Body/MProcessingStatus') != null && bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608_NOT_READY'

* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-stato-not-ready-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def responseStatus = 200


Scenario: methodIs('post') && bodyPath('/Envelope/Body/MProcessingStatus') != null && bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'

* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-stato-ready-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def responseStatus = 200


Scenario: methodIs('post') && bodyPath('/Envelope/Body/MResponse') != null && bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'

* def response = read('classpath:test/soap/non-bloccante/pull/recupero-risposta-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def responseStatus = 200


Scenario: methodIs('post') && isTest("no-correlation-in-request-erogazione-validazione")

* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def responseStatus = 200


Scenario: methodIs('post') && isTest("no-correlation-in-request-erogazione")

* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })

* def responseStatus = 200

Scenario: isTest("generazione-header-correlazione")

* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def responseStatus = 200


# Catch all
Scenario: 

* def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
* def response = read('classpath:test/soap/non-bloccante/pull/richiesta-applicativa-response.xml')
* def responseStatus = 200
