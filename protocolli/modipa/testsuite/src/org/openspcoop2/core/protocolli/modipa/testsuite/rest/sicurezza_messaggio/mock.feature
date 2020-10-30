Feature: Server mock per il testing della sicurezza messaggio

Background:
    * def isTest = function(id) { return karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id } 
    * def checkToken = read('check-token.feature')

    * def confHeaders = 
    """
    function() { 
        return {
            'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]"),
            'Connection': 'close'
        }
    }
    """

    * configure responseHeaders = confHeaders


Scenario: isTest('connettivita-base') || isTest('connettivita-base-default-trustore') || isTest('connettivita-base-truststore-ca') || isTest('disabled-security-on-action') || isTest('enabled-security-on-action') || isTest('riferimento-x509-x5u-x5t') || isTest('riferimento-x509-x5t-x5u') || isTest('riferimento-x509-x5cx5t-x5cx5t') || isTest('manomissione-payload-risposta') || isTest('low-ttl-erogazione') || isTest('manomissione-token-risposta') || isTest('connettivita-base-idar02') || isTest('riutilizzo-token') || isTest('manomissione-token-risposta-idar03') || isTest('manomissione-token-risposta-idar0302') || isTest('manomissione-payload-risposta-idar0302')
    
    # Controllo che al server non siano arrivate le informazioni di sicurezza
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('applicativo-non-autorizzato')

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')



Scenario: isTest('connettivita-base-no-sbustamento')
    # Controllo che al backend siano arrivate le informazioni di sicurezza

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01NoSbustamento',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01NoSbustamento'
        }
    })
    """
    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('response-without-payload')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('request-without-payload')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/request.json')


Scenario: isTest('request-response-without-payload')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 204
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('certificato-server-scaduto') || isTest('certificato-server-revocato')
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


##########################################
#                IDAR03                  #
##########################################

Scenario: isTest('connettivita-base-idar03') || isTest('manomissione-header-http-firmati-risposta')

    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('assenza-header-digest-risposta')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('response-without-payload-idar03')  || isTest('response-without-payload-idar03-tampered-header')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('response-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('request-without-payload-idar03') || isTest('request-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200

    * def response =
    """
    ({
         "a" : {
            "a2": "RGFuJ3MgVG9vbHMgYXJlIGNvb2wh",
            "a1s": [ 1, 2 ]
          },
          "b": "Stringa di esempio"
    })
    """


Scenario: isTest('request-response-without-payload-idar03')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 204
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('request-response-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 204
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })



##########################################
#                IDAR0302                #
##########################################


Scenario: isTest('connettivita-base-idar0302') || isTest('manomissione-header-http-firmati-risposta-idar0302')

    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('assenza-header-digest-risposta-idar0302') || isTest('riutilizzo-token-idar0302')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")