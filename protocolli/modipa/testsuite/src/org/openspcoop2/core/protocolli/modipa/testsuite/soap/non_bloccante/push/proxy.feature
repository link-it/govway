Feature: Server proxy contattato dalla fruizione

Background:

* configure responseHeaders = { 'Content-type': "application/soap+xml" }

* def url_erogazione_server_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/SoapNonBlockingPushServer/v1"
* def url_erogazione_client_validazione = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1"

* def url_erogazione_server_no_validazione = "/soap/in/DemoSoggettoErogatore/SoapNonBlockingPushServerNoValidazione/v1"
* def url_erogazione_client_no_validazione = "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientNoValidazione/v1"

* def url_erogazione_client_helper_collaborazione = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientHelperCollaborazione/v1"
* def url_erogazione_client_helper_riferimento = govway_base_path + "/soap/in/DemoSoggettoFruitore/SoapNonBlockingPushClientHelperRiferimento/v1"

* def isTest = function(id) { return headerContains('GovWay-TestSuite-Test-Id', id) } 

* def task_id = "8695a025-0931-4af4-9c76-26421374c7f2"

# GIRO OK
#
#

Scenario: isTest('test-ok-richiesta-client')

    # Controllo che la fruizione client abbia aggiornato lo header x-replyTo con la url
    # invocazione dell'erogazione lato client
    * match request/Envelope/Header/X-ReplyTo == url_erogazione_client_validazione 

    * karate.proceed(url_erogazione_server_validazione)

    * match responseHeaders['GovWay-Conversation-ID'][0] == task_id

Scenario: isTest('test-ok-risposta-server')

    * karate.proceed(url_erogazione_client_validazione)

    * match responseHeaders['GovWay-Conversation-ID'][0] == task_id


# ID Correlazione aggiunto dall'erogazione del server
#
#
Scenario: isTest('correlation-id-added-by-server')
    
    * karate.proceed(url_erogazione_server_validazione)

# ID Correlazione aggiunto dalla fruizione server sfruttando
# l'id collaborazione
#

Scenario: isTest('iniezione-header-id-collaborazione')

    * match request/Envelope/Header/X-Correlation-ID == requestHeaders['GovWay-Conversation-ID'][0]
    * karate.proceed(url_erogazione_client_helper_collaborazione)

Scenario: isTest('iniezione-header-id-collaborazione-query')

    * match request/Envelope/Header/X-Correlation-ID == task_id
    * karate.proceed(url_erogazione_client_helper_collaborazione)


# ID Correlazione aggiunto dalla fruizione server sfruttando
# il riferimento id richiesta
#

Scenario: isTest('iniezione-header-riferimento-id-richiesta')

* match request/Envelope/Header/X-Correlation-ID == requestHeaders['GovWay-Relates-To'][0]
* karate.proceed(url_erogazione_client_helper_riferimento)

Scenario: isTest('iniezione-header-riferimento-id-richiesta-query')

* match request/Envelope/Header/X-Correlation-ID == task_id
* karate.proceed(url_erogazione_client_helper_riferimento)


# Assenza dello header x-correlation-id nella risposta alla fruizione del client
#
#

Scenario: isTest('no-correlation-id-in-client-request-response')

* def responseStatus = 200
* def response = 
"""
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
    <soap:Header>
    </soap:Header>
    <soap:Body>
        <ns2:MRequestResponse xmlns:ns2="http://amministrazioneesempio.it/nomeinterfacciaservizio">
            <return>
                <outcome>ACCEPTED</outcome>
            </return>
        </ns2:MRequestResponse>
    </soap:Body>
</soap:Envelope>
"""

# Assenza dello header x-correlation-id nella richiesta del flusso di risposta
#
#

Scenario: isTest('no-correlation-id-in-server-response-request-no-disclosure')

* karate.proceed(url_erogazione_client_no_validazione)
* match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


# Catch all
#
#

Scenario:
    * karate.fail("Nessuno scenario matchato");



