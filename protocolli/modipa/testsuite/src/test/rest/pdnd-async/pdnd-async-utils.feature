@ignore
Feature: Utilità per i test degli scambi di dati asincroni PDND (REST)

# Scenari richiamabili tramite 'call read(...@tag) { ... }'.
# Variabili opzionali non indicate dal chiamante: headersExtra (header aggiuntivi), queryString, statusAtteso.
# Invocare sempre assegnando il risultato ('* def x = call ...'): una 'call' senza assegnazione condivide le variabili
# con il chiamante (es. 'risorsa' o gli header configurati), che verrebbero riutilizzate dalle chiamate successive.

Background:
    * def getResponseHeader = read('classpath:utils/get-response-header.js')
    * def headersExtra = karate.get('headersExtra', {})
    * def queryString = karate.get('queryString', '')
    * def conversationId = karate.get('conversationId', null)
    * def voucher = karate.get('voucher', null)
    * configure headers = headersExtra

@start
Scenario: start_interaction tramite la fruizione dell'e-service
    * def statusAtteso = karate.get('statusAtteso', 202)
    Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1/requests' + queryString
    And request { subject: 'Richiesta di test', entities: 10 }
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')
    * def conversationId = getResponseHeader('GovWay-Conversation-ID')

@callback
Scenario: callback_invocation tramite la fruizione dell'API di callback (backend dell'erogatore)
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def senzaEntityNumber = karate.get('senzaEntityNumber', false)
    * def entityNumber = senzaEntityNumber ? null : karate.get('entityNumber', 4)
    * def entityNumberQuery = karate.get('entityNumberQuery', false)
    * def risorsa = karate.get('risorsa', '/notifications')
    * def hdr = karate.merge(headersExtra, conversationId != null ? { 'govway-conversation-id': conversationId } : {})
    * if (!entityNumberQuery && entityNumber != null) hdr['govway-pdnd-entity-number'] = '' + entityNumber
    * def qs = (entityNumberQuery && entityNumber != null) ? '?govway_pdnd_entity_number=' + entityNumber : ''
    * configure headers = hdr
    Given url govway_base_path + '/rest/out/DemoSoggettoErogatore/DemoSoggettoFruitore/' + servizioCallback + '/v1' + risorsa + qs
    And request { status: 'READY', entities: 4 }
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')

@getResource
Scenario: get_resource tramite la fruizione dell'e-service
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def hdr = karate.merge(headersExtra, conversationId != null ? { 'govway-conversation-id': conversationId } : {})
    * configure headers = hdr
    Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1/results'
    And param offset = 0
    And param limit = 10
    When method get
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')
    * def conversationIdRisposta = getResponseHeader('GovWay-Conversation-ID')

@confirmation
Scenario: confirmation tramite la fruizione dell'e-service
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def risorsa = karate.get('risorsa', '/results/confirmation')
    * def hdr = karate.merge(headersExtra, conversationId != null ? { 'govway-conversation-id': conversationId } : {})
    * configure headers = hdr
    Given url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/' + servizio + '/v1' + risorsa
    And request {}
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')

@voucher
Scenario: voucher con claim arbitrari emesso dall'authorization server di test
    * configure headers = {}
    Given url govway_base_path + '/rest/in/DemoSoggettoErogatore/AuthorizationServerAsincroniDummy/v1/voucher'
    And form fields claims
    When method post
    Then status 200
    * def voucher = response.access_token

@erogazionePost
Scenario: invocazione diretta (POST) di un'erogazione con un voucher (senza passare dalla fruizione)
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def hdr = karate.merge(headersExtra, voucher != null ? { 'Authorization': 'Bearer ' + voucher } : {})
    * configure headers = hdr
    Given url govway_base_path + '/rest/in/' + soggettoErogatore + '/' + servizio + '/v1' + risorsa + queryString
    And request { test: true }
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')

@erogazioneGet
Scenario: invocazione diretta (GET) di un'erogazione con un voucher (senza passare dalla fruizione)
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def hdr = karate.merge(headersExtra, voucher != null ? { 'Authorization': 'Bearer ' + voucher } : {})
    * configure headers = hdr
    Given url govway_base_path + '/rest/in/' + soggettoErogatore + '/' + servizio + '/v1' + risorsa + queryString
    When method get
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')
