Feature: Server proxy contattato dalla fruizione

Background:
* def url_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPull/v1"
* def url_no_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPullNoValidazione/v1"
* def url_helper_headers = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPullHelperHeadersNoValidazione/v1"

    * def isTest =
    """
    function(id) {
        return karate.get("karate.request.header('GovWay-TestSuite-Test-Id')") == id
    }
    """

# marcodel non è la prima volta che commentare questa configure nei proxy soap fa funzionare i test non so perchè

# configure responseHeaders = { 'Content-type': "application/soap+xml" }

# NO-CORRELATION-IN-REQUEST
#
#

Scenario: methodIs('post') && isTest("no-correlation-in-request-fruizione-validazione")

* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseStatus = 200


Scenario: methodIs('post') && isTest("no-correlation-in-request-fruizione")

* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseStatus = 200


# GENERAZIONE HEADER SOAP X-Correlation-ID da parte dell'erogazione
#
#

Scenario: isTest("generazione-header-correlazione")

* karate.proceed(url_validazione)

* match /Envelope/Header/X-Correlation-ID == karate.response.header('GovWay-Transaction-ID')
* match karate.response.header('GovWay-Transaction-ID') == karate.response.header('GovWay-Conversation-ID')


# GENERAZIONE HEADER GovWay-Conversation-ID DA PARTE DELL'EROGAZIONE
#
#

Scenario: isTest("generazione-header-conversation-id-richiesta")

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'

Scenario: isTest("generazione-header-conversation-id-stato")

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608_NOT_READY'

Scenario: isTest("generazione-header-conversation-id-stato-ready")

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'

Scenario: isTest("generazione-header-conversation-id-risposta")

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'


# INIEZIONE HEADER SOAP A PARTIRE DAGLI HEADER HTTP E PARAMETRI QUERY DI COLLABORAZIONE
#
#   Come aiuto allo sviluppatore, la fruizione può arricchire il messaggio soap con lo header
#   di collaborazione prendendolo da alcuni campi della richiesta originaria.
#
Scenario: isTest("iniezione-header-soap")
# così i test funzionano ma è orribile, il prossimo test che viene aggiunto darà errore perchè la configure è globale


* configure responseHeaders = { 'Content-type': "application/soap+xml" }
* match bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'
* def responseStatus = 200
* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-stato-ready-response.xml')

Scenario: isTest("iniezione-header-soap-risposta")

* match bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'
* def responseStatus = 200
* def response = read('classpath:src/test/soap/non-bloccante/pull/recupero-risposta-response.xml')


# Catch all
#
#

Scenario: methodIs('post')
* karate.proceed(url_validazione)



