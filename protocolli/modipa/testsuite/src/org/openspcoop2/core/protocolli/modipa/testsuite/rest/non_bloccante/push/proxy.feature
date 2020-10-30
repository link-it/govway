Feature: Feature di proxy per i test non bloccante push

Background:

    * def url_erogazione_server_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServer/v1"
    * def url_erogazione_client_validazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"

    * def url_erogazione_server_no_validazione = "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"
    * def url_erogazione_client_no_validazione = "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

    * def url_erogazione_client_helper_collaborazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientHelperCollaborazioneNoValidazione/v1"
    * def url_erogazione_client_helper_riferimento = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientHelperRiferimentoNoValidazione/v1"

    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

    * def isTest = function(id) { return headerContains('GovWay-TestSuite-Test-Id', id) } 

# GIRO OK
#
#

Scenario: isTest('test-ok-richiesta-client')

# Verifico che la fruizione abbia aggiornato lo header X-Reply-To con la url invocazione dell'erogazione lato client
* match requestHeaders['X-ReplyTo'][0] == govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"
* karate.proceed(url_erogazione_server_validazione)

Scenario: isTest('test-ok-risposta-server')

* karate.proceed(url_erogazione_client_validazione)

# CorrelationID Aggiunto dall'erogazione server
#
#

Scenario: isTest('correlation-id-added-by-server')

* karate.proceed(url_erogazione_server_validazione)
* match responseHeaders['X-Correlation-ID'][0] == responseHeaders['GovWay-Transaction-ID'][0]


# CorrelationID aggiunto dalla fruizione server sfruttando
# l'id collaborazione
#

Scenario: isTest('iniezione-header-id-collaborazione')


* match requestHeaders['X-Correlation-ID'][0] != null
* match requestHeaders['X-Correlation-ID'][0] == requestHeaders['GovWay-Conversation-ID'][0]
* karate.proceed(url_erogazione_client_helper_collaborazione)

Scenario: isTest('iniezione-header-id-collaborazione-query')

* match requestHeaders['X-Correlation-ID'][0] != null
* match requestHeaders['X-Correlation-ID'][0] == task_id
* karate.proceed(url_erogazione_client_helper_collaborazione)


# CorrelationID aggiunto dalla fruizione server sfruttando
# il riferimento id richiesta
#

Scenario: isTest('iniezione-header-riferimento-id-richiesta')

* match requestHeaders['X-Correlation-ID'][0] != null
* match requestHeaders['X-Correlation-ID'][0] == requestHeaders['GovWay-Relates-To'][0]
* karate.proceed(url_erogazione_client_helper_riferimento)

Scenario: isTest('iniezione-header-riferimento-id-richiesta-query')

* match requestHeaders['X-Correlation-ID'][0] != null
* match requestHeaders['X-Correlation-ID'][0] == task_id
* karate.proceed(url_erogazione_client_helper_riferimento)


# Assenza dello header x-correlation-id nella risposta alla fruizione del client
#
#

Scenario: isTest('no-correlation-id-in-client-request-response')

* def responseStatus = 202
* def response = { 'outcome': 'accepted' }

# Assenza dello header x-correlation-id nella richiesta del flusso di risposta
#
#

Scenario: isTest('no-correlation-id-in-server-response-request')

* karate.proceed(url_erogazione_client_no_validazione)

# CATCH ALL
#
#
Scenario:

    karate.fail("Nessuno scenario matchato sul proxy")