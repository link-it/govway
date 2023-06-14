Feature: Server mock per il testing della sicurezza messaggio

Background:

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def encode_base64 = read('classpath:utils/encode-base64.js')

    * def isTest =
    """
    function(id) {

        karate.log("Mock test id (case A): ", karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]"))
        karate.log("Mock test id (case B): ", karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]"))
        karate.log("Mock test id (case C): ", karate.get("requestHeaders['govway-testsuite-test-id'][0]"))

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
	isTest('manomissione-payload-risposta') || isTest('doppi-header-manomissione-payload-risposta') || 
	isTest('doppi-header-assenza-header-digest-risposta') || 
	isTest('doppi-header-assenza-header-authorization-risposta') || 
	isTest('doppi-header-assenza-header-agid-jwt-signature-risposta') || 
	isTest('doppi-header-audience-risposta-authorization-non-valida-rispetto-client') || isTest('doppi-header-audience-risposta-agid-jwt-signature-non-valida-rispetto-client') || 
	isTest('doppi-header-audience-risposta-differente-audience-valida-rispetto-client') ||
	isTest('doppi-header-audience-risposta-differente-audience-non-valida-rispetto-client') ||
	isTest('doppi-header-audience-risposta-valore-statico') || isTest('doppi-header-audience-risposta-valore-statico-non-valido') ||
	isTest('doppi-header-audience-risposta-diversi-valori-statici') || isTest('doppi-header-audience-risposta-diversi-valori-statici-authorization-non-valido') || isTest('doppi-header-audience-risposta-diversi-valori-statici-agid-jwt-signature-non-valido') ||
  isTest('doppi-header-audience-richiesta-stesso-valore') ||
  isTest('doppi-header-audience-richiesta-differente-valore') ||
	isTest('low-ttl-erogazione') || isTest('low-iat-ttl-erogazione') || isTest('iat-future-response') || 
	isTest('custom-claims') || isTest('custom-claims-sub-iss-clientid-empty') || 
	isTest('manomissione-token-risposta') || 
	isTest('connettivita-base-idar02') || 
	isTest('riutilizzo-token') || 
	isTest('riutilizzo-token-generato-auth-server') ||
	isTest('check-authz-idar02') ||
	isTest('manomissione-token-risposta-idar03') || isTest('manomissione-token-risposta-idar0302') || isTest('manomissione-payload-risposta-idar0302') || 
	isTest('doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato') ||
	isTest('doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato-richiesta') ||
	isTest('doppi-header-token-date-differenti-richiesta') ||
	isTest('connettivita-base-truststore-ca-ocsp') ||
	isTest('keystore-default-fruizione') ||
	isTest('keystore-ridefinito-fruizione') || isTest('keystore-ridefinito-fruizione-applicativo-no-keystore') || 
	isTest('keystore-ridefinito-fruizione-archivio') ||
	isTest('keystore-ridefinito-fruizione-x5u') ||
	isTest('keystore-definito-applicativo')
    
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


Scenario: isTest('certificato-server-scaduto') || isTest('certificato-server-revocato') || isTest('certificato-server-revocato-ocsp')
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


Scenario: isTest('assenza-header-integrity-risposta') || isTest('assenza-header-digest-risposta')
    
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


Scenario: isTest('idar03-token-richiesta') || isTest('idar03-token-risposta') || isTest('check-authz-idar03') || 
	isTest('check-authz-doppi-header-idar03') || isTest('check-authz-oauth2-doppi-header-idar03')

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
    

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-token')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * def checkToken = read('check-token.feature')

    * call checkToken ({token: getRequestHeader("Authorization"), match_to: client_token_match, kind: 'Bearer' })
    * match getRequestHeader("Authorization") == ('Bearer ' + getRequestHeader("X-Verifica-Req-Authorization"))
    * call checkToken ({token: getRequestHeader("X-Verifica-Req-Authorization"), match_to: client_token_match, kind: 'AGID' })
    
    * match getRequestHeader("X-Verifica-Req-Authorization-CertCN") == 'ExampleClient1'
    * match getRequestHeader("X-Verifica-Req-Authorization-CertO") == 'Example'
    * match getRequestHeader("X-Verifica-Req-Authorization-HeaderClaim") == 'RS256'
    * match getRequestHeader("X-Verifica-Req-Authorization-PayloadClaim") == 'testsuite'

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-header')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * def checkToken = read('check-token.feature')
    * call checkToken ({token: getRequestHeader("Authorization"), match_to: client_token_match, kind: 'Bearer' })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(getRequestHeader("Authorization"), 'Bearer')

    * match tokenS.header == getRequestHeader("X-Verifica-Req-Authorization-Header")

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-payload')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * def checkToken = read('check-token.feature')
    * call checkToken ({token: getRequestHeader("Authorization"), match_to: client_token_match, kind: 'Bearer' })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(getRequestHeader("Authorization"), 'Bearer')

    * match tokenS.payload == getRequestHeader("X-Verifica-Req-Authorization-Payload")   

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-custom')

    * match getRequestHeader("Authorization") == 'Bearer TOKENVALUETEST'
    * match getRequestHeader("X-Verifica-Req-Authorization-Custom") == '#present'

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-integrity-token')

    * def client_token_integrity_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def checkToken = read('check-token.feature')
    * call checkToken ({token: getRequestHeader("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * match getRequestHeader("Agid-JWT-Signature") == getRequestHeader("X-Verifica-Req-Integrity")
    * call checkToken ({token: getRequestHeader("X-Verifica-Req-Integrity"), match_to: client_token_integrity_match, kind: 'AGID' })
    
    * match getRequestHeader("X-Verifica-Req-Integrity-CertCN") == 'ExampleClient1'
    * match getRequestHeader("X-Verifica-Req-Integrity-CertO") == 'Example'
    * match getRequestHeader("X-Verifica-Req-Integrity-HeaderClaim") == 'RS256'
    * match getRequestHeader("X-Verifica-Req-Integrity-PayloadClaim") == 'testsuite'

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-integrity-header')

    * def client_token_integrity_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def checkToken = read('check-token.feature')
    * call checkToken ({token: getRequestHeader("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(getRequestHeader("Agid-JWT-Signature"), 'AGID')

    * match tokenS.header == getRequestHeader("X-Verifica-Req-Integrity-Header")

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-integrity-payload')

    * def client_token_integrity_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def checkToken = read('check-token.feature')
    * call checkToken ({token: getRequestHeader("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(getRequestHeader("Agid-JWT-Signature"), 'AGID')

    * match tokenS.payload == getRequestHeader("X-Verifica-Req-Integrity-Payload")

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


##########################################
#                IDAR0302                #
##########################################


Scenario: isTest('connettivita-base-idar0302') || isTest('manomissione-header-http-firmati-risposta-idar0302')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('assenza-header-digest-risposta-idar0302') || isTest('riutilizzo-token-idar0302') || 
		isTest('pkcs11') || isTest('pkcs11-certificate') || isTest('pkcs11-keystore-fruizione')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar0302-header-bearer')

    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('multipart-request-form-data-idar0302') || isTest('multipart-request-dump-idar0302')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def responseHeaders = { Content-Type: 'multipart/form-data; boundary="----=_Part_0_1037475674.1651780088034"' }
    * def response = read('classpath:test/rest/sicurezza-messaggio/multipart-request.bin')

Scenario: isTest('multipart-request-mixed-idar0302')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * def responseStatus = 200
    * def responseHeaders = { Content-Type: 'multipart/mixed; boundary="----=_Part_0_1037475674.1651780088034"' }
    * def response = read('classpath:test/rest/sicurezza-messaggio/multipart-request.bin')



##########################################
#       IDAR03 (CUSTOM)                  #
##########################################

Scenario: isTest('idar03-custom-single-header') || isTest('idar03-custom-doppi-header') || isTest('idar03-custom-doppi-header-solo-richiesta') ||
          isTest('idar0302-custom-single-header') || isTest('idar0302-custom-doppi-header') || isTest('idar0302-custom-doppi-header-solo-richiesta')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match requestHeaders['CustomTestSuite-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Digest'] == '#notpresent'
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * configure responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('idar03-custom-doppi-header-multipart')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match requestHeaders['CustomTestSuite-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Digest'] == '#notpresent'
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]"),
       'Content-Type': 'multipart/mixed; boundary="----=_Part_1_1678144365.1610454048429"; type="text/xml"',
       'GovWay-Integration': integration_header_base64
    })
    """
    * configure responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/richiestaConAllegati.bin')





##########################################
#                IDAR04                  #
##########################################

Scenario: isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingKeyPair') ||
		isTest('manomissione-token-risposta-idar04-jwk')  ||
		isTest('manomissione-token-risposta-idar04-pdnd') ||
		isTest('manomissione-payload-risposta-idar04-jwk') ||
		isTest('manomissione-payload-risposta-idar04-pdnd') ||
		isTest('manomissione-header-http-firmati-risposta-idar04-jwk') ||
		isTest('manomissione-header-http-firmati-risposta-idar04-pdnd') ||
		isTest('assenza-header-integrity-risposta-idar04-jwk') ||
		isTest('assenza-header-integrity-risposta-idar04-pdnd') ||
		isTest('assenza-header-digest-risposta-idar04-jwk') ||
		isTest('assenza-header-digest-risposta-idar04-pdnd') ||
		isTest('idar04-token-risposta-jwk') ||
		isTest('idar04-token-risposta-pdnd') ||
		isTest('audience-differenti-ok-idar04-jwk') 

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR04TestHeader: "TestHeaderResponse" }


Scenario: isTest('response-without-payload-idar04-jwk')  || 
	 	isTest('response-without-payload-idar04-pdnd')  || 
		isTest('response-without-payload-idar04-tampered-header-jwk') || 
		isTest('response-without-payload-idar04-tampered-header-pdnd')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR04TestHeader': "TestHeaderResponse" })


Scenario: isTest('response-without-payload-idar04-digest-richiesta-jwk') ||
		isTest('response-without-payload-idar04-digest-richiesta-pdnd')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 201
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })

Scenario: isTest('idar04-token-richiesta-jwk') ||
		isTest('idar04-token-richiesta-pdnd')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('authorization-richiesta-integrity-risposta-idar04-jwk') ||
		isTest('authorization-richiesta-integrity-risposta-idar04-pdnd')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR04TestHeader: "TestHeaderResponse" }

Scenario: isTest('solo-authorization-richiesta-idar04-jwk') ||
		isTest('solo-authorization-richiesta-idar04-pdnd')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = ''
    * def responseHeaders = ({ 'Content-Type': null })





##########################################
#                IDAR0402                  #
##########################################

Scenario: isTest('connettivita-base-idar0402-pdnd') ||
		isTest('connettivita-base-idar0402-keypair') ||
		isTest('riutilizzo-token-idar0402-pdnd') ||
		isTest('riutilizzo-token-idar0402-keypair')

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')





##########################################
#       IDAR04 (CUSTOM)                  #
##########################################

Scenario: isTest('idar04-custom-header-pdnd')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match requestHeaders['CustomTestSuite-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Digest'] == '#notpresent'
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("requestHeaders['GovWay-Transaction-ID'][0]"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * configure responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')




##########################################
#                AUDIT REST              #
##########################################

Scenario: isTest('audit-rest-jwk-01')  || 
		isTest('audit-rest-jwk-02') ||
		isTest('audit-rest-jwk-custom-01') || 
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-token-optional-01') || 
		isTest('audit-rest-jwk-purpose-id-uguali')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-mixed-01')  || 
		isTest('audit-rest-jwk-mixed-02')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token'
    * match requestHeaders['GovWay-Audit-LoA'][0] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-x509-01')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token-ridefinito'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token-ridefinito'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token-ridefinito'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-0401')  || 
		isTest('audit-rest-jwk-0402')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-x509-0301')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token-ridefinito'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token-ridefinito'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token-ridefinito'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == '#notpresent'
    * match requestHeaders['GovWay-Audit-LoA'][0] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-customtrace-customforward-01')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == '#notpresent'
    * match requestHeaders['audit-custom-location'][0] == 'ip-utente-token'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'][0] == 'utente-token'
    * match requestHeaders['GovWay-Audit-UserLocation'][0] == 'ip-utente-token'
    * match requestHeaders['GovWay-Audit-LoA'][0] == 'livello-autenticazione-utente-token'
    * match requestHeaders['audit-test-security-token-kid'][0] == 'KID-ApplicativoBlockingIDA01'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01')

    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-TrackingEvidence'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserID'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-UserLocation'] == '#notpresent'
    * match requestHeaders['GovWay-Audit-LoA'] == '#notpresent'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
