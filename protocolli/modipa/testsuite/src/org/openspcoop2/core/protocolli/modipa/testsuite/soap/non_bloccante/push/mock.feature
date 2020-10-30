Feature: Server di mock contattato dalla erogazione, svolge il ruolo del TestService di echo.

Background: 

    * def isTest = function(id) { return headerContains('GovWay-TestSuite-Test-Id', id) } 

    * def confHeaders = 
    """
    function() { 
        return {
            'Content-type': "application/soap+xml",
            'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]")
        }
    }
    """

    * configure responseHeaders = confHeaders


# GIRO OK
#
#
Scenario: isTest('test-ok-richiesta-client')

    # Controllo che l'erogazione del client abbia aggiornato lo header X-ReplyTo
    # con la url invocazione della fruizione del server    
    * match request/Envelope/Header/X-ReplyTo == govway_base_path + "/soap/out/DemoSoggettoErogatore/DemoSoggettoFruitore/SoapNonBlockingPushClient/v1"

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/client-request-response.xml')

Scenario: isTest('test-ok-risposta-server')

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/server-response-response.xml')


# ID Correlazione aggiunto dall'erogazione del server
#
#

Scenario: isTest('correlation-id-added-by-server')

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

# ID Correlazione aggiunto dalla fruizione server sfruttando
# l'id collaborazione
#

Scenario: isTest('iniezione-header-id-collaborazione')

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/server-response-response.xml')

Scenario: isTest('iniezione-header-id-collaborazione-query')

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/server-response-response.xml')


# ID Correlazione aggiunto dalla fruizione server sfruttando
# il riferimento id richiesta

Scenario: isTest('iniezione-header-riferimento-id-richiesta')

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/server-response-response.xml')


Scenario: isTest('iniezione-header-riferimento-id-richiesta-query')

    * def responseStatus = 200
    * def response = read('classpath:src/test/soap/non-bloccante/push/server-response-response.xml')


# Catch all
Scenario: 
    karate.fail("Nessuno scenario matcahto")

# 
# * def response = read('classpath:src/test/soap/non-bloccante/pull/richiesta-applicativa-response.xml')
# * def responseStatus = 200
