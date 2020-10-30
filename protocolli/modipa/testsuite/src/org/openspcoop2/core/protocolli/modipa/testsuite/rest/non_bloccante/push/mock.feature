Feature: Feature di mock per i test non bloccante push

Background:

    * def isTest = function(id) { return headerContains('GovWay-TestSuite-Test-Id', id) } 
    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

# GIRO OK
#
#

Scenario: isTest('test-ok-richiesta-client')

    # Verifico che l'erogazione del server abbia aggiornato lo header X-Reply-To con la url invocazione della fruizione del server
    # verso l'erogazione del client.

    * match requestHeaders['X-ReplyTo'][0] == govway_base_path + "/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"
    * def responseHeaders = ({ 'X-Correlation-ID': task_id, 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })

    * def responseStatus = 202
    * def response = { 'outcome': 'accepted' }

Scenario: isTest('test-ok-risposta-server')

    * match requestHeaders['GovWay-Conversation-ID'][0] == task_id
    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 200
    * def response = read('classpath:src/test/rest/non-bloccante/push/server-response-response.json')
    

# CorrelationID Aggiunto dall'erogazione server
#
# Invio solo il messaggio di ack senza il correlation ID che verrà aggiunto dall'erogazione server
#

Scenario: isTest('correlation-id-added-by-server')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 202
    * def response = { 'outcome': 'accepted' }


# CorrelationID aggiunto dalla fruizione server sfruttando l'id collaborazione
#
#

Scenario: isTest('iniezione-header-id-collaborazione')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 200
    * def response = read('classpath:src/test/rest/non-bloccante/push/server-response-response.json')

Scenario: isTest('iniezione-header-id-collaborazione-query')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 200
    * def response = read('classpath:src/test/rest/non-bloccante/push/server-response-response.json')

# CorrelationID aggiunto dalla fruizione server sfruttando il riferimento id richiesta
#
#

Scenario: isTest('iniezione-header-riferimento-id-richiesta')

    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 200
    * def response = read('classpath:src/test/rest/non-bloccante/push/server-response-response.json')


Scenario: isTest('iniezione-header-riferimento-id-richiesta-query')
    
    * def responseHeaders = ({ 'GovWay-TestSuite-GovWay-Transaction-ID': requestHeaders['GovWay-Transaction-ID'][0] })
    * def responseStatus = 200
    * def response = read('classpath:src/test/rest/non-bloccante/push/server-response-response.json')