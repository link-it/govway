Feature: Server proxy contattato dalla fruizione

Background:
* def url_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPull/v1"
* def url_no_validazione = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPullNoValidazione/v1"
* def url_helper_headers = govway_base_path + "/soap/in/DemoSoggettoErogatore/NonBlockingSoapPullHelperHeadersNoValidazione/v1"


* configure responseHeaders = { 'Content-type': "application/soap+xml" }

# NO-CORRELATION-IN-REQUEST
#
#

Scenario: methodIs('post') && headerContains('GovWay-TestSuite-Test-Id', 'no-correlation-in-request-fruizione-validazione')

* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseStatus = 200


Scenario: methodIs('post') && headerContains('GovWay-TestSuite-Test-Id', 'no-correlation-in-request-fruizione')

* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-applicativa-no-correlation-response.xml')
* def responseStatus = 200


# GENERAZIONE HEADER SOAP X-Correlation-ID da parte dell'erogazione
#
#

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'generazione-header-correlazione')

* karate.proceed(url_validazione)

* match /Envelope/Header/X-Correlation-ID == responseHeaders['GovWay-Transaction-ID'][0]
* match responseHeaders['GovWay-Transaction-ID'][0] == responseHeaders['GovWay-Conversation-ID'][0]


# GENERAZIONE HEADER GovWay-Conversation-ID DA PARTE DELL'EROGAZIONE
#
#

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'generazione-header-conversation-id-richiesta')

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'generazione-header-conversation-id-stato')

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608_NOT_READY'

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'generazione-header-conversation-id-stato-ready')

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'generazione-header-conversation-id-risposta')

* karate.proceed(url_validazione)
* match header GovWay-Conversation-ID == 'd2f49459-1624-4710-b80c-15e33d64b608'


# INIEZIONE HEADER SOAP A PARTIRE DAGLI HEADER HTTP E PARAMETRI QUERY DI COLLABORAZIONE
#
#   Come aiuto allo sviluppatore, la fruizione può arricchire il messaggio soap con lo header
#   di collaborazione prendendolo da alcuni campi della richiesta originaria.
#
Scenario: headerContains('GovWay-TestSuite-Test-Id', 'iniezione-header-soap')

* match bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'
* def responseStatus = 200
* def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-stato-ready-response.xml')

Scenario: headerContains('GovWay-TestSuite-Test-Id', 'iniezione-header-soap-risposta')

* match bodyPath('/Envelope/Header/X-Correlation-ID') == 'd2f49459-1624-4710-b80c-15e33d64b608'
* def responseStatus = 200
* def response = read('classpath:src/test/soap/non-bloccante/pull/recupero-risposta-response.xml')


# Catch all
#
#

Scenario: methodIs('post')
* karate.proceed(url_validazione)



