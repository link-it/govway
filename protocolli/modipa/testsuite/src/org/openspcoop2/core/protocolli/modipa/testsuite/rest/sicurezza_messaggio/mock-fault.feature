Feature: Server mock per il testing della sicurezza messaggio con messaggi classificati come fault

Background:

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')

    * def testId = function(){ return getRequestHeader('GovWay-TestSuite-Test-ID') }
    * def isTestPrefix = function(prefix){ var t = testId(); return t != null && t.indexOf(prefix) == 0 }
    * def isIdar01 = function(){ var t = testId(); return t != null && t.indexOf('-idar01') == (t.length - '-idar01'.length) }

    * def confHeaders = 
    """
    function() { 
        return {
            'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]"),
            'Connection': 'close'
        }
    }
    """

    * configure responseHeaders = confHeaders


Scenario: isTestPrefix('fault-richiesta-problem-')

    * def contentType = getRequestHeader('Content-Type')
    * match contentType contains 'application/problem+'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTestPrefix('fault-content-type-riscritto-') && isIdar01()

    * def contentType = getRequestHeader('Content-Type')
    * def t = testId()
    * def atteso = t.indexOf('-xml-') >= 0 ? 'application/problem+xml' : 'application/problem+json'
    * match contentType contains atteso
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


# Le seguenti richieste non devono mai arrivare al backend: si registra l'invocazione affinché il test possa rilevarla
Scenario: isTestPrefix('fault-erogazione-') || isTestPrefix('fault-content-type-riscritto-')

    * def id = testId()
    * eval karateCache.add('fault-backend-' + id, 'raggiunto')
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTestPrefix('fault-risposta-problem-json-')

    * def responseStatus = 500
    * def responseHeaders = { 'Content-Type': 'application/problem+json' }
    * def response = read('classpath:test/rest/sicurezza-messaggio/fault/response-problem.json')


Scenario: isTestPrefix('fault-risposta-problem-xml-')

    * def responseStatus = 500
    * def responseHeaders = { 'Content-Type': 'application/problem+xml' }
    * def response = karate.readAsString('classpath:test/rest/sicurezza-messaggio/fault/response-problem.xml')


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
