Feature: Server proxy per il testing della sicurezza messaggio con messaggi contenenti un SOAPFault

Background:

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def check_client_token = read('check-client-token.feature')
    * def check_signature = read('check-signature.feature')

    * def hasWSSecurity =
    """
    function(xml) {
        if (xml == null) return false;
        var s = karate.prettyXml(xml);
        return s.indexOf('oasis-200401-wss-wssecurity-secext-1.0.xsd') >= 0 && s.indexOf(':Security') >= 0;
    }
    """

    # L'azione deve essere presente nella url poiché non è identificabile dal contenuto (SOAPFault)
    * def isTestPrefix =
    """
    function(prefix) {
        var t = karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") || karate.get("requestHeaders['govway-testsuite-test-id'][0]");
        return t != null && t.indexOf(prefix) == 0;
    }
    """
    * def servizio =
    """
    function() {
        var t = karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") || karate.get("requestHeaders['govway-testsuite-test-id'][0]");
        var servizio = (t != null && t.indexOf('-idas01') == (t.length - '-idas01'.length)) ? 'SoapBlockingIDAS01Fault' : 'SoapBlockingIDAS03Fault';
        // erogazione con autorizzazione puntuale per applicativi (variante 'authz')
        if (t != null && t.indexOf('-authz-') >= 0) servizio = servizio + 'Authz';
        return servizio;
    }
    """

    * def erogazioneUrl =
    """
    function() {
        var base = govway_base_path + '/soap/in/DemoSoggettoErogatore/' + servizio() + '/v1';
        var uri = karate.get('requestUri');
        if (uri != null && uri.indexOf('MRequest') >= 0) return base;
        return base + '/MRequest';
    }
    """


# Tutte le richieste che transitano dal proxy sono prodotte da una fruizione con sicurezza messaggio IDAS01 o IDAS03:
# se la fruizione non ha applicato la sicurezza, la richiesta non viene inoltrata all'erogazione
# e viene restituito un errore che evidenzia il punto di fallimento.
Scenario: !hasWSSecurity(request)

    * karate.log("La fruizione non ha generato l'header WS-Security nella richiesta inoltrata")
    * def responseStatus = 500
    * def responseHeaders = { 'Content-Type': 'application/soap+xml' }
    * def response =
    """
    <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
        <soap:Body>
            <soap:Fault>
                <soap:Code>
                    <soap:Value>soap:Receiver</soap:Value>
                </soap:Code>
                <soap:Reason>
                    <soap:Text xml:lang="it">La fruizione non ha generato l'header WS-Security nella richiesta inoltrata</soap:Text>
                </soap:Reason>
            </soap:Fault>
        </soap:Body>
    </soap:Envelope>
    """


Scenario: isTestPrefix('fault-soap-richiesta-fault-')

    * match bodyPath('/Envelope/Body/Fault') == '#present'
    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 200
    * def rispostaFirmata = hasWSSecurity(response)
    * match rispostaFirmata == true


Scenario: isTestPrefix('fault-soap-risposta-fault-')

    * karate.proceed (erogazioneUrl())

    * match responseStatus == 500
    * match /Envelope/Body/Fault == '#present'
    * def rispostaFirmata = hasWSSecurity(response)
    * match rispostaFirmata == false


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
