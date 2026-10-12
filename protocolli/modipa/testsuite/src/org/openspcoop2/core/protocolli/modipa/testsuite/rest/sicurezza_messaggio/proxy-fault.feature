Feature: Server proxy per il testing della sicurezza messaggio con messaggi classificati come fault

Background: 

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def setRequestHeader = read('classpath:utils/set-request-header.js')
    * def decodeToken = read('classpath:utils/decode-token.js')

    * def testId = function(){ return getRequestHeader('GovWay-TestSuite-Test-ID') }
    * def isTestPrefix = function(prefix){ var t = testId(); return t != null && t.indexOf(prefix) == 0 }
    * def isIdar01 = function(){ var t = testId(); return t != null && t.indexOf('-idar01') == (t.length - '-idar01'.length) }
    # erogazione con autorizzazione puntuale per applicativi (variante 'authz')
    * def isAuthz = function(){ var t = testId(); return t != null && t.indexOf('-authz-') >= 0 }
    # tipo di contenuto (json o xml) indicato nell'identificativo del test
    * def tipo = function(){ var t = testId(); return (t != null && t.indexOf('-xml-') >= 0) ? 'xml' : 'json' }

    # Erogazione da invocare in base al profilo e alla variante indicati nell'identificativo del test
    * def erogazioneUrl =
    """
    function() {
        var servizio = isIdar01() ? 'RestBlockingIDAR01Fault' : 'RestBlockingIDAR03Fault';
        if (isAuthz()) servizio = servizio + 'Authz';
        return govway_base_path + '/rest/in/DemoSoggettoErogatore/' + servizio + '/v1';
    }
    """

    * def getResponseHeader =
    """
    function(name) {
        var headerArray = (karate.get("responseHeaders['" + name + "']") ||
               karate.get("responseHeaders['" + name.toLowerCase() + "']"));
        if (headerArray == null) return null;
        return headerArray[0];
    }
    """

    * def signedHeader =
    """
    function(token, name) {
        var signed = token.payload.signed_headers;
        for (var i = 0; i < signed.length; i++) {
            if (signed[i][name] != null) return signed[i][name];
        }
        return null;
    }
    """


# Tutte le richieste che transitano dal proxy sono prodotte da una fruizione con sicurezza messaggio IDAR01 (header Authorization)
# o IDAR03 (header Agid-JWT-Signature): se la fruizione non ha generato il token, la richiesta non viene inoltrata all'erogazione
# e viene restituito un errore che evidenzia il punto di fallimento.
Scenario: (isIdar01() && getRequestHeader('Authorization') == null) || (!isIdar01() && getRequestHeader('Agid-JWT-Signature') == null)

    * def responseStatus = 418
    * def responseHeaders = { 'Content-Type': 'application/problem+json' }
    * def response = { type: 'https://govway.org/testsuite/modipa/fault/proxy', title: 'TokenNonGenerato', status: 418, detail: "La fruizione non ha generato il token di sicurezza ModI nella richiesta inoltrata" }


Scenario: isTestPrefix('fault-richiesta-problem-') && isIdar01()

    * def contentType = getRequestHeader('Content-Type')
    * match contentType contains 'application/problem+'
    * def authorization = getRequestHeader('Authorization')
    * match authorization contains 'Bearer '

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 200
    * def responseToken = getResponseHeader('Authorization')
    * match responseToken == '#notnull'


Scenario: isTestPrefix('fault-richiesta-problem-')

    * def contentType = getRequestHeader('Content-Type')
    * match contentType contains 'application/problem+'
    * def request_token = decodeToken(getRequestHeader('Agid-JWT-Signature'), "AGID")
    * def signedContentType = signedHeader(request_token, 'content-type')
    * match signedContentType == contentType
    * def signedDigest = signedHeader(request_token, 'digest')
    * def digest = getRequestHeader('Digest')
    * match signedDigest == digest

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 200
    * def responseToken = getResponseHeader('Agid-JWT-Signature')
    * match responseToken == '#notnull'


# La richiesta prodotta dalla fruizione (application/json o application/xml) viene inoltrata all'erogazione
# con 'Content-Type: application/problem+json' o 'Content-Type: application/problem+xml'
# - IDAR01: il token non firma il Content-Type e la richiesta deve essere gestita normalmente
# - IDAR03: il token firma il content-type originale e deve essere rilevata la differenza
Scenario: isTestPrefix('fault-content-type-riscritto-') && isIdar01()

    * setRequestHeader('Content-Type', 'application/problem+' + tipo())

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 200


Scenario: isTestPrefix('fault-content-type-riscritto-')

    * def request_token = decodeToken(getRequestHeader('Agid-JWT-Signature'), "AGID")
    * def signedContentType = signedHeader(request_token, 'content-type')
    * def contentTypeFirmato = 'application/' + tipo()
    * match signedContentType contains contentTypeFirmato
    * setRequestHeader('Content-Type', 'application/problem+' + tipo())

    * karate.proceed (erogazioneUrl())

    # il tipo di errore è restituito dall'erogazione e non viene propagato dalla fruizione al client
    * match responseStatus == 400
    * def errorType = getResponseHeader('GovWay-Transaction-ErrorType')
    * match errorType == 'InteroperabilityInvalidRequest'


Scenario: isTestPrefix('fault-risposta-problem-')

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 500
    * def responseToken = getResponseHeader(isIdar01() ? 'Authorization' : 'Agid-JWT-Signature')
    * match responseToken == null


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
