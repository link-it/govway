Feature: Feature di proxy per i test non bloccante push

Background:

    * def url_erogazione_server_validazione = govway_base_path + "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServer/v1"
    * def url_erogazione_client_validazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"

    * def url_erogazione_server_no_validazione = "/rest/in/DemoSoggettoErogatore/RestNonBlockingPushServerNoValidazione/v1"
    * def url_erogazione_client_no_validazione = "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientNoValidazione/v1"

    * def url_erogazione_client_helper_collaborazione = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientHelperCollaborazioneNoValidazione/v1"
    * def url_erogazione_client_helper_riferimento = govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClientHelperRiferimentoNoValidazione/v1"

    * def task_id = "fb382380-cf98-4f75-95eb-2a65ba45309e"

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

# GIRO OK
#
#

Scenario: isTest('test-ok-richiesta-client')

# Verifico che la fruizione abbia aggiornato lo header X-Reply-To con la url invocazione dell'erogazione lato client
* match requestHeaders['X-ReplyTo'][0] == govway_base_path + "/rest/in/DemoSoggettoFruitore/RestNonBlockingPushClient/v1"
* karate.proceed(url_erogazione_server_validazione)

Scenario: isTest('test-ok-risposta-server')

* karate.proceed(url_erogazione_client_validazione)

Scenario:

    karate.fail("Nessuno scenario matchato sul proxy")
