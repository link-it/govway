Feature: Server mock per il testing della sicurezza messaggio

Background:

    * def getRequestHeader = read('classpath:utils/get-request-header.js')

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

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


Scenario: isTest('connettivita-base') || isTest('connettivita-base-default-trustore') || isTest('connettivita-base-truststore-ca') || 
	isTest('disabled-security-on-action') || isTest('enabled-security-on-action') || 
	isTest('riferimento-x509-x5u-x5t') || isTest('riferimento-x509-x5u-x5t-client2') || isTest('riferimento-x509-x5t-x5u') || isTest('riferimento-x509-x5cx5t-x5cx5t') || 
	isTest('manomissione-payload-risposta') || isTest('doppi-header-manomissione-payload-risposta') || isTest('doppi-header-assenza-header-digest-risposta') || 
	isTest('doppi-header-assenza-header-authorization-risposta') || isTest('doppi-header-assenza-header-agid-jwt-signature-risposta') || 
	isTest('doppi-header-audience-risposta-authorization-non-valida-rispetto-client') || isTest('doppi-header-audience-risposta-agid-jwt-signature-non-valida-rispetto-client') || 
	isTest('doppi-header-audience-risposta-differente-audience-valida-rispetto-client') ||
	isTest('doppi-header-audience-risposta-differente-audience-non-valida-rispetto-client') ||
	isTest('doppi-header-audience-risposta-valore-statico') || isTest('doppi-header-audience-risposta-valore-statico-non-valido') ||
	isTest('doppi-header-audience-risposta-diversi-valori-statici') || isTest('doppi-header-audience-risposta-diversi-valori-statici-authorization-non-valido') || isTest('doppi-header-audience-risposta-diversi-valori-statici-agid-jwt-signature-non-valido') ||
  isTest('doppi-header-audience-richiesta-stesso-valore') ||
  isTest('doppi-header-audience-richiesta-differente-valore') ||
	isTest('low-ttl-erogazione') || isTest('low-iat-ttl-erogazione') || 
	isTest('custom-claims') || isTest('custom-claims-sub-iss-clientid-empty') || 
	isTest('manomissione-token-risposta') || 
	isTest('connettivita-base-idar02') || isTest('riutilizzo-token') || 
	isTest('manomissione-token-risposta-idar03') || isTest('manomissione-token-risposta-idar0302') || isTest('manomissione-payload-risposta-idar0302') || 
	isTest('doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato') ||
	isTest('doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato-richiesta') ||
	isTest('doppi-header-token-date-differenti-richiesta')
    
    # Controllo che al server non siano arrivate le informazioni di sicurezza
    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    
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
    * def checkToken = read('check-token.feature')

    * call checkToken ({token: getRequestHeader("Authorization"), match_to: client_token_match })

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


Scenario: isTest('risposta-not-200')
    # Controllo che al server non siano arrivate le informazioni di sicurezza
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 301
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-header-agid')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar02-header-agid')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')



##########################################
#                IDAR03                  #
##########################################

Scenario: isTest('connettivita-base-idar03') || isTest('digest-hex-idar03') || isTest('manomissione-header-http-firmati-risposta')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('connettivita-base-idar03-header-bearer')

    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('assenza-header-digest-risposta')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('response-without-payload-idar03')  || isTest('response-without-payload-idar03-tampered-header')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('response-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('request-without-payload-idar03') || isTest('request-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
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
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 204
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('request-response-without-payload-idar03-digest-richiesta')
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 204
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('informazioni-utente-header') || isTest('informazioni-utente-query') || isTest('informazioni-utente-mixed') || isTest('informazioni-utente-static') || isTest('informazioni-utente-custom')

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('idar03-token-richiesta') || isTest('idar03-token-risposta')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('idar03-token-azione-puntuale')
    # TODO Sembra la validazione non parta se gli faccio restituire 200??
    # Rimetti la logica di sotto per far spuntare il bug
    
    #* def responseStatus = 200
    #* def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })

Scenario: isTest('idar03-token-azione-puntuale-default') || isTest('idar03-token-criteri-personalizzati')
    
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
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

Scenario: isTest('doppi-header-idar03') || isTest('doppi-header-differenti-id-authorization') || isTest('doppi-header-differenti-id-agid-jwt-signature') ||
	isTest('doppi-header-solo-integrity-risposta') || 
	isTest('doppi-header-authorization-richiesta-integrity-risposta') ||
	isTest('doppi-header-solo-authorization-richiesta')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }
    
Scenario: isTest('doppi-header-solo-authorization-richiesta-risposta') ||
	isTest('doppi-header-solo-authorization-richiesta-delete')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 204
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }
    

##########################################
#                IDAR0302                #
##########################################


Scenario: isTest('connettivita-base-idar0302') || isTest('manomissione-header-http-firmati-risposta-idar0302')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('assenza-header-digest-risposta-idar0302') || isTest('riutilizzo-token-idar0302') || 
		isTest('pkcs11') || isTest('pkcs11-certificate')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar0302-header-bearer')

    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
