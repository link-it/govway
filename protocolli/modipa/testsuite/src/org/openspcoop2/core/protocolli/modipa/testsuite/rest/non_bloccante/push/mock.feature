Feature: Feature di mock per i test non bloccante push

Background:

    * def isTest =
    """
    function(id) {
        return karate.get("karate.request.header('GovWay-TestSuite-Test-Id')") == id ||
               karate.get("karate.request.header('GovWay-TestSuite-Test-ID')") == id ||
               karate.get("karate.request.header('govway-testsuite-test-id')") == id
    }
    """

    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

# GIRO OK
#
#

Scenario: isTest('test-ok-richiesta-client')

    # Verifico che l'erogazione del server abbia aggiornato lo header X-Reply-To con la url invocazione della fruizione del server
    # verso l'erogazione del client.

    * match karate.request.header('X-ReplyTo') == govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"
    * def responseHeaders = ({ 'X-Correlation-ID': task_id, 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })

    * def responseStatus = 202
    * def response = { 'outcome': 'accepted' }

Scenario: isTest('test-ok-risposta-server')

    * match karate.request.header('GovWay-Conversation-ID') == task_id
    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/non-bloccante/push/server-response-response.json')
    

# CorrelationID Aggiunto dall'erogazione server
#
# Invio solo il messaggio di ack senza il correlation ID che verrà aggiunto dall'erogazione server
#

Scenario: isTest('correlation-id-added-by-server')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 202
    * def response = { 'outcome': 'accepted' }


# CorrelationID aggiunto dalla fruizione server sfruttando l'id collaborazione
#
#

Scenario: isTest('iniezione-header-id-collaborazione')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/non-bloccante/push/server-response-response.json')

Scenario: isTest('iniezione-header-id-collaborazione-query')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/non-bloccante/push/server-response-response.json')

# CorrelationID aggiunto dalla fruizione server sfruttando il riferimento id richiesta
#
#

Scenario: isTest('iniezione-header-riferimento-id-richiesta')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/non-bloccante/push/server-response-response.json')


Scenario: isTest('iniezione-header-riferimento-id-richiesta-query')
    
    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': karate.request.header('GovWay-Transaction-ID') })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/non-bloccante/push/server-response-response.json')
