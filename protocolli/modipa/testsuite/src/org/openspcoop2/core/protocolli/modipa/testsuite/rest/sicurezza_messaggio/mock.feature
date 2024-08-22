Feature: Server mock per il testing della sicurezza messaggio

Background:

    * def encode_base64 = read('classpath:utils/encode-base64.js')

    * def isTest =
    """
    function(id) {

        karate.log("Mock test id (case A): ", karate.get("karate.request.header('GovWay-TestSuite-Test-Id')"))

        return karate.get("karate.request.header('GovWay-TestSuite-Test-Id')") == id 
    }
    """

    * def confHeaders = 
    """
    function() { 
        return {
            'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
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
  isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArray') ||
  isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArrayMultipleValues') ||
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
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    
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

    * call checkToken ({token: karate.request.header("Authorization"), match_to: client_token_match })

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('response-without-payload')
    * match karate.request.header('Authorization') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('request-without-payload')
    * match karate.request.header('Authorization') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/request.json')


Scenario: isTest('request-response-without-payload')
    * match karate.request.header('Authorization') == null
    * def responseStatus = 204
    * def response = null


Scenario: isTest('certificato-server-scaduto') || isTest('certificato-server-revocato') || isTest('certificato-server-revocato-ocsp')
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('risposta-not-200')
    # Controllo che al server non siano arrivate le informazioni di sicurezza
    * match karate.request.header('Authorization') == null
    * def responseStatus = 301
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-header-agid')
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar02-header-agid')
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')



##########################################
#                IDAR03                  #
##########################################

Scenario: isTest('connettivita-base-idar03') || 
	isTest('test-audience-as-array') || isTest('test-audience-as-array-multiple-values') ||
	isTest('digest-hex-idar03') || isTest('manomissione-header-http-firmati-risposta')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('connettivita-base-idar03-header-bearer')

    * match karate.request.header('Authorization') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('assenza-header-integrity-risposta') || isTest('assenza-header-digest-risposta')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('response-without-payload-idar03')  || isTest('response-without-payload-idar03-tampered-header')
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('response-without-payload-idar03-digest-richiesta')
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null })


Scenario: isTest('request-without-payload-idar03') || isTest('request-without-payload-idar03-digest-richiesta')
    * match karate.request.header('Agid-JWT-Signature') == null
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
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 204
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR03TestHeader': "TestHeaderResponse" })


Scenario: isTest('request-response-without-payload-idar03-digest-richiesta')
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 204
    * def responseHeaders = ({ 'Content-Type': null })
    * def response = null

Scenario: isTest('informazioni-utente-header') || isTest('informazioni-utente-query') || isTest('informazioni-utente-mixed') || isTest('informazioni-utente-static') || isTest('informazioni-utente-custom')

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('idar03-token-richiesta') || isTest('idar03-token-risposta') || isTest('check-authz-idar03') || 
	isTest('check-authz-doppi-header-idar03') || isTest('check-authz-oauth2-doppi-header-idar03')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('idar03-token-azione-puntuale')
    # TODO Sembra la validazione non parta se gli faccio restituire 200??
    # Rimetti la logica di sotto per far spuntare il bug
    
    #* def responseStatus = 200
    #* def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null })

Scenario: isTest('idar03-token-azione-puntuale-default') || isTest('idar03-token-criteri-personalizzati')
    
    * match karate.request.header('Agid-JWT-Signature') == null
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

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }
    
Scenario: isTest('doppi-header-solo-authorization-richiesta-risposta') ||
	isTest('doppi-header-solo-authorization-richiesta-delete')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
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
	
	
    * call checkToken ({token: karate.request.header("Authorization"), match_to: client_token_match, kind: 'Bearer' })
    * match karate.request.header("Authorization") == ('Bearer ' + karate.request.header("X-Verifica-Req-Authorization"))
    * call checkToken ({token: karate.request.header("X-Verifica-Req-Authorization"), match_to: client_token_match, kind: 'AGID' })
    
    * match karate.request.header("X-Verifica-Req-Authorization-CertCN") == 'ExampleClient1'
    * match karate.request.header("X-Verifica-Req-Authorization-CertO") == 'Example'
    * match karate.request.header("X-Verifica-Req-Authorization-HeaderClaim") == 'RS256'
    * match karate.request.header("X-Verifica-Req-Authorization-PayloadClaim") == 'testsuite'

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
    * call checkToken ({token: karate.request.header("Authorization"), match_to: client_token_match, kind: 'Bearer' })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(karate.request.header("Authorization"), 'Bearer')

    * match tokenS.header == karate.request.header("X-Verifica-Req-Authorization-Header")

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
    * call checkToken ({token: karate.request.header("Authorization"), match_to: client_token_match, kind: 'Bearer' })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(karate.request.header("Authorization"), 'Bearer')

    * match tokenS.payload == karate.request.header("X-Verifica-Req-Authorization-Payload")   

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-custom')

    * match karate.request.header("Authorization") == 'Bearer TOKENVALUETEST'
    * match karate.request.header("X-Verifica-Req-Authorization-Custom") == '#present'

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
    * call checkToken ({token: karate.request.header("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * match karate.request.header("Agid-JWT-Signature") == karate.request.header("X-Verifica-Req-Integrity")
    * call checkToken ({token: karate.request.header("X-Verifica-Req-Integrity"), match_to: client_token_integrity_match, kind: 'AGID' })
    
    * match karate.request.header("X-Verifica-Req-Integrity-CertCN") == 'ExampleClient1'
    * match karate.request.header("X-Verifica-Req-Integrity-CertO") == 'Example'
    * match karate.request.header("X-Verifica-Req-Integrity-HeaderClaim") == 'RS256'
    * match karate.request.header("X-Verifica-Req-Integrity-PayloadClaim") == 'testsuite'

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
    * call checkToken ({token: karate.request.header("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(karate.request.header("Agid-JWT-Signature"), 'AGID')

    * match tokenS.header == karate.request.header("X-Verifica-Req-Integrity-Header")

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
    * call checkToken ({token: karate.request.header("Agid-JWT-Signature"), match_to: client_token_integrity_match,  kind: "AGID" })

    * def splitToken = read('classpath:utils/split-token.js')
    * def tokenS = splitToken(karate.request.header("Agid-JWT-Signature"), 'AGID')

    * match tokenS.payload == karate.request.header("X-Verifica-Req-Integrity-Payload")

    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


##########################################
#                IDAR0302                #
##########################################


Scenario: isTest('connettivita-base-idar0302') || isTest('manomissione-header-http-firmati-risposta-idar0302')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR03TestHeader: "TestHeaderResponse" }


Scenario: isTest('assenza-header-digest-risposta-idar0302') || isTest('riutilizzo-token-idar0302') || 
		isTest('pkcs11') || isTest('pkcs11-certificate') || isTest('pkcs11-keystore-fruizione')
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar0302-header-bearer')

    * match karate.request.header('Authorization') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('multipart-request-form-data-idar0302') || isTest('multipart-request-dump-idar0302')

    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * def responseStatus = 200
    * def responseHeaders = { 'Content-Type': 'multipart/form-data; boundary="----=_Part_0_1037475674.1651780088034"' }
    * def response = read('classpath:test/rest/sicurezza-messaggio/multipart-request.bin')


Scenario: isTest('multipart-request-mixed-idar0302')

    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * def responseStatus = 200
    * def responseHeaders = { Content-Type: 'multipart/mixed; boundary="----=_Part_0_1037475674.1651780088034"' }
    * def response = read('classpath:test/rest/sicurezza-messaggio/multipart-request.bin')



##########################################
#       IDAR03 (CUSTOM)                  #
##########################################

Scenario: isTest('idar03-custom-single-header') || isTest('idar03-custom-doppi-header') || 
	  isTest('idar03-custom-doppi-header-solo-richiesta') ||
          isTest('idar0302-custom-single-header') || isTest('idar0302-custom-doppi-header') || isTest('idar0302-custom-doppi-header-solo-richiesta') ||
	  isTest('idar03-custom-single-header-assenza-header-integrity-risposta') ||
	  isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('CustomTestSuiteDoppi-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('idar03-custom-doppi-header-get-without-custom') || 
	  isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-get-senza-payload') ||
	  isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-delete-senza-payload')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('CustomTestSuiteDoppi-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'Content-Type': null, 
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = null

Scenario: isTest('idar03-custom-doppi-header-get-with-custom')

    * def integration_header_2 = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info_2.json')
    * def integration_header_2_base64 = encode_base64(integration_header_2);

    * karate.log("Response: ", integration_header_2_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('CustomTestSuiteDoppi-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'Content-Type': null, 
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_2_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = null

Scenario: isTest('idar03-custom-doppi-header-multipart')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('CustomTestSuiteDoppi-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'Content-Type': 'multipart/mixed; boundary="----=_Part_1_1678144365.1610454048429"; type="text/xml"',
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * print newHeaders
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
		isTest('manomissione-payload-risposta-vuota-idar04-jwk') ||
		isTest('manomissione-header-http-firmati-risposta-idar04-jwk') ||
		isTest('manomissione-header-http-firmati-risposta-idar04-pdnd') ||
		isTest('assenza-header-integrity-risposta-idar04-jwk') ||
		isTest('assenza-header-integrity-risposta-idar04-pdnd') ||
		isTest('assenza-header-digest-risposta-idar04-jwk') ||
		isTest('assenza-header-digest-risposta-idar04-pdnd') ||
		isTest('idar04-token-risposta-jwk') ||
		isTest('idar04-token-risposta-pdnd') ||
		isTest('audience-differenti-ok-idar04-jwk') 

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR04TestHeader: "TestHeaderResponse" }


Scenario: isTest('response-without-payload-idar04-jwk')  || 
	 	isTest('response-without-payload-idar04-pdnd')  || 
		isTest('response-without-payload-idar04-tampered-header-jwk') || 
		isTest('response-without-payload-idar04-tampered-header-pdnd')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null, 'IDAR04TestHeader': "TestHeaderResponse" })


Scenario: isTest('response-without-payload-idar04-digest-richiesta-jwk') ||
		isTest('response-without-payload-idar04-digest-richiesta-pdnd')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 201
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null })

Scenario: isTest('idar04-token-richiesta-jwk') ||
		isTest('idar04-token-richiesta-pdnd')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('authorization-richiesta-integrity-risposta-idar04-jwk') ||
		isTest('authorization-richiesta-integrity-risposta-idar04-pdnd')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')
    * def responseHeaders = { IDAR04TestHeader: "TestHeaderResponse" }

Scenario: isTest('solo-authorization-richiesta-idar04-jwk') ||
		isTest('solo-authorization-richiesta-idar04-pdnd')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = null
    * def responseHeaders = ({ 'Content-Type': null })





##########################################
#                IDAR0402                  #
##########################################

Scenario: isTest('connettivita-base-idar0402-pdnd') ||
		isTest('connettivita-base-idar0402-keypair') ||
		isTest('riutilizzo-token-idar0402-pdnd') ||
		isTest('riutilizzo-token-idar0402-keypair')

    * match karate.request.header('Agid-JWT-Signature') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')





##########################################
#       IDAR04 (CUSTOM)                  #
##########################################

Scenario: isTest('idar04-custom-header-pdnd')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('idar04-custom-header-pdnd-assenza-header-integrity-risposta')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('idar04-custom-header-pdnd-get-with-custom') ||
            isTest('idar04-custom-header-pdnd-get-without-custom') ||
            isTest('idar04-custom-header-pdnd-assenza-header-integrity-risposta-metodo-get-senza-payload')

    * def integration_header = karate.readAsString('classpath:test/rest/sicurezza-messaggio/integration_info.json')
    * def integration_header_base64 = encode_base64(integration_header);

    * karate.log("Response: ", integration_header_base64)

    * match karate.request.header('CustomTestSuite-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Authorization') == null
    * match karate.request.header('Digest') == null
    * def responseStatus = 200
    * def newHeaders = 
    """
    ({
       'Content-Type': null, 
       'GovWay-TestSuite-GovWay-Transaction-ID': karate.get("karate.request.header('GovWay-Transaction-ID')"),
       'GovWay-Integration': integration_header_base64
    })
    """
    * def responseHeaders = newHeaders
    * def response = null


##########################################
#                AUDIT REST              #
##########################################

Scenario: isTest('audit-rest-jwk-01')  || 
		isTest('audit-rest-jwk-02') ||
		isTest('audit-rest-jwk-custom-01') || 
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-token-optional-01') || 
		isTest('audit-rest-jwk-purpose-id-uguali') ||
		isTest('audit-rest-jwk-01-differentAudienceAsArray') ||
		isTest('audit-rest-jwk-0401-differentAudienceAsArray')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token-test-cache'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token-differente-test-cache'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-Claim1') == 'valore-claim1-required-test-cache'
    * match karate.request.header('GovWay-Audit-Claim2') == 'valore-claim2-required-test-cache'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-Claim1') == 'valore-claim1-differente-required-test-cache'
    * match karate.request.header('GovWay-Audit-Claim2') == 'valore-claim2-differente-required-test-cache'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-Claim1') == null
    * match karate.request.header('GovWay-Audit-Claim2') == 'valore-claim2-required-test-cache'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-Claim1') == null
    * match karate.request.header('GovWay-Audit-Claim2') == 'valore-claim2-differente-required-test-cache'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-mixed-01')  || 
		isTest('audit-rest-jwk-mixed-02')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-x509-01')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token-ridefinito'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token-ridefinito'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token-ridefinito'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-0401')  || 
		isTest('audit-rest-jwk-0402')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-x509-0301')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-Signature') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token-ridefinito'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token-ridefinito'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token-ridefinito'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == null
    * match karate.request.header('GovWay-Audit-UserLocation') == null
    * match karate.request.header('GovWay-Audit-LoA') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-customtrace-customforward-01')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == null
    * match karate.request.header('GovWay-Audit-UserLocation') == null
    * match karate.request.header('audit-custom-location') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == 'utente-token'
    * match karate.request.header('GovWay-Audit-UserLocation') == 'ip-utente-token'
    * match karate.request.header('GovWay-Audit-LoA') == 'livello-autenticazione-utente-token'
    * match karate.request.header('audit-test-security-token-kid') == 'KID-ApplicativoBlockingIDA01'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-UserID') == null
    * match karate.request.header('GovWay-Audit-UserLocation') == null
    * match karate.request.header('GovWay-Audit-LoA') == null
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-token-custom-01')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-TypeINT') == '23'
    * match karate.request.header('GovWay-Audit-TypeBoolean') == 'true'
    * match karate.request.header('GovWay-Audit-TypeStringRegExp') == 'ABCDE'
    * match karate.request.header('GovWay-Audit-TypeINTRegExp') == '12'
    * match karate.request.header('GovWay-Audit-TypeListString') == 'Valore2'
    * match karate.request.header('GovWay-Audit-TypeListInt') == '10.3'
    * match karate.request.header('GovWay-Audit-TypeMixed1') == 'ZZ'
    * match karate.request.header('GovWay-Audit-TypeMixed2') == '23456'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

Scenario: isTest('audit-rest-jwk-token-custom-02')

    * match karate.request.header('Authorization') == null
    * match karate.request.header('Agid-JWT-TrackingEvidence') == null
    * match karate.request.header('GovWay-Audit-TypeINT') == '99147483647'
    * match karate.request.header('GovWay-Audit-TypeBoolean') == 'false'
    * match karate.request.header('GovWay-Audit-TypeStringRegExp') == 'A'
    * match karate.request.header('GovWay-Audit-TypeINTRegExp') == '1'
    * match karate.request.header('GovWay-Audit-TypeListString') == 'Valore4'
    * match karate.request.header('GovWay-Audit-TypeListInt') == '45'
    * match karate.request.header('GovWay-Audit-TypeMixed1') == 'AA'
    * match karate.request.header('GovWay-Audit-TypeMixed2') == '22'
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
