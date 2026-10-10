@ignore
Feature: Utilità per i test degli scambi di dati asincroni PDND (SOAP)

# Invocare sempre assegnando il risultato ('* def x = call ...'): una 'call' senza assegnazione condivide le variabili
# con il chiamante, che verrebbero riutilizzate dalle chiamate successive.

Background:
    * def getResponseHeader = read('classpath:utils/get-response-header.js')
    * def headersExtra = karate.get('headersExtra', {})
    * def conversationId = karate.get('conversationId', null)
    * def statusAtteso = karate.get('statusAtteso', 200)
    * def queryString = karate.get('queryString', '')
    * def ns = karate.get('ns', 'http://govway.org/pdnd/async/eservice')
    * def soapBody = function(azione, ns){ return '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><tns:' + azione + ' xmlns:tns="' + ns + '"><tns:subject>test</tns:subject></tns:' + azione + '></soap:Body></soap:Envelope>' }

@invoca
Scenario: invocazione SOAP 1.1 di un'azione tramite fruizione (path: 'out/<fruitore>/<erogatore>/<servizio>/v1')
    * def hdr = karate.merge({ 'SOAPAction': '"' + azione + '"' }, headersExtra, conversationId != null ? { 'govway-conversation-id': conversationId } : {})
    * configure headers = hdr
    Given url govway_base_path + '/soap/' + path + '/' + azione + queryString
    And request soapBody(azione, ns)
    And header Content-Type = 'text/xml; charset=UTF-8'
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')
    * def conversationIdRisposta = getResponseHeader('GovWay-Conversation-ID')
    * def errorType = getResponseHeader('GovWay-Transaction-ErrorType')

@erogazione
Scenario: invocazione SOAP 1.1 diretta di un'erogazione con un voucher (senza passare dalla fruizione)
    * def voucher = karate.get('voucher', null)
    * def hdr = karate.merge({ 'SOAPAction': '"' + azione + '"' }, headersExtra, voucher != null ? { 'Authorization': 'Bearer ' + voucher } : {})
    * configure headers = hdr
    Given url govway_base_path + '/soap/in/' + soggettoErogatore + '/' + servizio + '/v1/' + azione
    And request soapBody(azione, ns)
    And header Content-Type = 'text/xml; charset=UTF-8'
    When method post
    Then match responseStatus == statusAtteso
    * def tid = getResponseHeader('GovWay-Transaction-ID')
    * def errorType = getResponseHeader('GovWay-Transaction-ErrorType')
