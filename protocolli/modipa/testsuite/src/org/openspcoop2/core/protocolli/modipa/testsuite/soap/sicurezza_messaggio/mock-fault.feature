Feature: Server mock per il testing della sicurezza messaggio con messaggi contenenti un SOAPFault

Background:

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def isTestPrefix = function(prefix){ var t = getRequestHeader('GovWay-TestSuite-Test-ID'); return t != null && t.indexOf(prefix) == 0 }
    * def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')

    * def confHeaders =
    """
    function() {
        return {
            'Content-type': "application/soap+xml",
            'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]")
        }
    }
    """

    * configure responseHeaders = confHeaders


Scenario: isTestPrefix('fault-soap-richiesta-fault-')

    * match bodyPath('/Envelope/Body/Fault/Reason/Text') == 'Richiesta applicativa contenente un SOAPFault'
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


# La seguente richiesta non deve mai arrivare al backend: si registra l'invocazione affinché il test possa rilevarla
Scenario: isTestPrefix('fault-soap-erogazione-fault-senza-sicurezza-')

    * def testId = getRequestHeader('GovWay-TestSuite-Test-ID')
    * eval karateCache.add('fault-backend-' + testId, 'raggiunto')
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTestPrefix('fault-soap-risposta-fault-')

    * def responseStatus = 500
    * def response = read('classpath:test/soap/sicurezza-messaggio/fault/response-fault.xml')


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
