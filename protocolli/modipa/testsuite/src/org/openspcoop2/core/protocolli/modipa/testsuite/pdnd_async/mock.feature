Feature: Mock dei backend per i test degli scambi di dati asincroni PDND

# I backend restituiscono nella risposta gli header di integrazione ricevuti da GovWay
# (identificativo dell'interazione, URL di callback, numero di entità) per consentirne la verifica nei test.
# Il codice HTTP della risposta può essere forzato dal test tramite l'header 'govway-testsuite-pdnd-async-status'.

Background:

    * def getHeader =
    """
    function(name) {
        var headerArray = (karate.get("requestHeaders['" + name + "']") ||
               karate.get("requestHeaders['" + name.toLowerCase() + "']"))
        if (headerArray == null)
        	return null;
        return headerArray[0];
    }
    """
    * def getStatus =
    """
    function(defaultStatus) {
        var s = getHeader('govway-testsuite-pdnd-async-status');
        return s != null ? parseInt(s) : defaultStatus;
    }
    """
    * def received =
    """
    function() {
        return {
            conversationId: getHeader('GovWay-Conversation-ID'),
            urlCallback: getHeader('GovWay-PDND-Url-Callback'),
            entityNumber: getHeader('GovWay-PDND-Entity-Number'),
            transactionId: getHeader('GovWay-Transaction-ID')
        };
    }
    """
    * def soapFault =
    """
    function() {
        return '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><soap:Fault>' +
            '<faultcode>soap:Server</faultcode><faultstring>Errore simulato dal backend di test</faultstring>' +
            '</soap:Fault></soap:Body></soap:Envelope>';
    }
    """
    * def soapResponse =
    """
    function(ns, operation, info) {
        return '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body>' +
            '<tns:' + operation + 'Response xmlns:tns="' + ns + '">' +
            '<tns:conversationId>' + (info.conversationId != null ? info.conversationId : '') + '</tns:conversationId>' +
            '<tns:urlCallback>' + (info.urlCallback != null ? info.urlCallback.replace(/&/g, '&amp;') : '') + '</tns:urlCallback>' +
            '<tns:entityNumber>' + (info.entityNumber != null ? info.entityNumber : '') + '</tns:entityNumber>' +
            '</tns:' + operation + 'Response></soap:Body></soap:Envelope>';
    }
    """

# ------------------------------------------------------
# - BACKEND DELL'E-SERVICE (soggetto erogatore) - REST -
# ------------------------------------------------------

Scenario: pathMatches('/pdnd-async/eservice/{servizio}/requests') && methodIs('post')
    * def responseStatus = getStatus(202)
    * def response = { outcome: 'ACCEPTED', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/eservice/{servizio}/results') && methodIs('get')
    * def responseStatus = getStatus(200)
    * def response = { total: 2, offset: '#(paramValue("offset"))', items: [ { id: 1, value: 'a' }, { id: 2, value: 'b' } ], received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/eservice/{servizio}/results/confirmation') && methodIs('post')
    * def responseStatus = getStatus(200)
    * def response = { outcome: 'CONFIRMED', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/eservice/{servizio}/catalog') && methodIs('get')
    * def responseStatus = getStatus(200)
    * def response = { items: [ 'A01', 'A02' ], received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/eservice/{servizio}/health') && methodIs('get')
    * def responseStatus = getStatus(200)
    * def response = { status: 'UP', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

# ----------------------------------------------------------
# - BACKEND DELL'API DI CALLBACK (soggetto fruitore) - REST -
# ----------------------------------------------------------

Scenario: pathMatches('/pdnd-async/callback/{servizio}/notifications') && methodIs('post')
    * def responseStatus = getStatus(200)
    * def response = { outcome: 'ACK', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/callback/{servizio}/notifications/errors') && methodIs('post')
    * def responseStatus = getStatus(200)
    * def response = { outcome: 'ACK', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

Scenario: pathMatches('/pdnd-async/callback/{servizio}/health') && methodIs('get')
    * def responseStatus = getStatus(200)
    * def response = { status: 'UP', received: '#(received())' }
    * def responseHeaders = { 'Content-Type': 'application/json' }

# --------------------------------------------------------------------------------------------------
# - URL DI CALLBACK VERSO IL MOCK: la fruizione dell'API di callback invoca direttamente il mock, che -
# - restituisce il path ricevuto (verifica delle keyword ${context:pdndAsyncUrlCallback[Original]})  -
# --------------------------------------------------------------------------------------------------

Scenario: requestUri.indexOf('pdnd-async/url-callback/') >= 0 && methodIs('post')
    # percorso ricevuto dopo '/pdnd-async/url-callback' (es. '/<servizio>/notifications') e query string;
    # per SOAP il percorso viene restituito nell'elemento 'urlCallback' della risposta
    * def idx = requestUri.indexOf('pdnd-async/url-callback/') + 'pdnd-async/url-callback'.length
    # calcolato in un'unica espressione: un'assegnazione JavaScript ('* if (...) percorso = ...') creerebbe una variabile globale
    # del motore JavaScript che nel mock resta valorizzata tra le richieste successive
    * def q = requestUri.indexOf('?')
    * def percorso = q > 0 ? requestUri.substring(idx, q) : requestUri.substring(idx)
    * def info = received()
    * def responseStatus = 200
    * def soap = getHeader('SOAPAction') != null
    * def response = soap ? soapResponse('http://govway.org/pdnd/async/callback', 'callbackInvocation', karate.merge(info, { urlCallback: percorso })) : ({ outcome: 'ACK', percorso: percorso, tenant: paramValue('tenant'), received: info })
    * def responseHeaders = soap ? { 'Content-Type': 'text/xml; charset=UTF-8' } : { 'Content-Type': 'application/json' }

# -------------------------
# - BACKEND SOAP (1.1)     -
# -------------------------

Scenario: pathMatches('/pdnd-async/eservice/{servizio}') && methodIs('post')
    * def soapAction = getHeader('SOAPAction') != null ? getHeader('SOAPAction').replace(/"/g, '') : 'unknown'
    * def responseStatus = getStatus(200)
    # con un codice di errore forzato dal test viene restituito un SOAP Fault
    * def response = responseStatus >= 500 ? soapFault() : soapResponse('http://govway.org/pdnd/async/eservice', soapAction, received())
    * def responseHeaders = { 'Content-Type': 'text/xml; charset=UTF-8' }

Scenario: pathMatches('/pdnd-async/callback/{servizio}') && methodIs('post')
    * def soapAction = getHeader('SOAPAction') != null ? getHeader('SOAPAction').replace(/"/g, '') : 'unknown'
    * def responseStatus = getStatus(200)
    # con un codice di errore forzato dal test viene restituito un SOAP Fault
    * def response = responseStatus >= 500 ? soapFault() : soapResponse('http://govway.org/pdnd/async/callback', soapAction, received())
    * def responseHeaders = { 'Content-Type': 'text/xml; charset=UTF-8' }

Scenario:
    * def responseStatus = 404
    * def response = { error: 'risorsa non gestita dal mock', path: '#(requestUri)' }
