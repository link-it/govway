Feature: Server proxy per il testing sicurezza messaggio

Background: 

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def setRequestHeader = read('classpath:utils/set-request-header.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def checkToken = read('check-token.feature')
    * def decodeToken = read('classpath:utils/decode-token.js')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * def tamper_token = 
    """
    function(tok) {
        var prefix = "Bearer"
        tok = tok.slice(prefix.length).trim()
        var components = tok.split('.')
        components[1] = components[1] + 'tamper'
        return 'Bearer ' + components[0] + '.' + components[1] + '.' + components[2]
    }
    """

    * def tamper_token_agid = 
    """
    function(tok) {
        var components = tok.split('.')
        components[1] = components[1] + 'tamper'
        return components[0] + '.' + components[1] + '.' + components[2]
    }
    """
   
Scenario: isTest('connettivita-base')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('connettivita-base-default-trustore')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01DefaultTrustore/v1'
    * karate.proceed (url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01DefaultTrustore/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01DefaultTrustore/v1'
        }
    })
    """

    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('connettivita-base-no-sbustamento')
    

    # Testo il token del client sulla feature di mock e il token del server sulla feature client (idar01.feature)
    
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01DefaultTrustoreNoSbustamento/v1'
    * karate.proceed (url_invocazione_erogazione)

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('connettivita-base-truststore-ca')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1'
    * karate.proceed (url_invocazione_erogazione)
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01TrustStoreCA/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01TrustStoreCA/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('response-without-payload')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01CRUD/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('request-without-payload')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01CRUD/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('request-response-without-payload')
    
    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01CRUD/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('disabled-security-on-action')

    * match requestHeaders.Authorization == "#notpresent"

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * match responseHeaders.Authorization == "#notpresent"


Scenario: isTest('enabled-security-on-action')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUDNoDefaultSecurity/v1' 

    * karate.proceed (url_invocazione_erogazione)
    
    # Cambia il sub
    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01CRUDNoDefaultSecurity/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01CRUDNoDefaultSecurity/v1'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('riferimento-x509-x5u-x5t')

    * def client_token_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient1',
            x5c: '#notpresent',
            x5u: 'http://localhost:8080/ExampleClient1.crt'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01X5T/v1')
    * match responseStatus == 200
    
    * def server_token_match =
    """
    ({
        header: { 
            kid: 'ExampleServer',
            x5c: '#notpresent',
            x5u: '#notpresent',
            'x5t#S256': '#present'
        },
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01X5T/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01X5T/v1'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('riferimento-x509-x5u-x5t-client2')

    * def client_token_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient2',
            x5c: '#notpresent',
            x5u: 'http://localhost:8080/ExampleClient2.crt'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'http://client2',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01ExampleClient2'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01X5T/v1')
    * match responseStatus == 200
    
    * def server_token_match =
    """
    ({
        header: { 
            kid: 'ExampleServer',
            x5c: '#notpresent',
            x5u: '#notpresent',
            'x5t#S256': '#present'
        },
        payload: {
            aud: 'http://client2',
            client_id: 'RestBlockingIDAR01X5T/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01X5T/v1'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('riferimento-x509-x5t-x5u')

    * def client_token_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient1',
            x5c: '#notpresent',
            x5u: '#notpresent',
            'x5t#S256': '#present'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01X5T-X5U/v1')
    * match responseStatus == 200
    
    * def server_token_match =
    """
    ({
        header: { 
            kid: 'ExampleServer',
            x5c: '#notpresent',
            x5u: 'http://localhost:8080/ExampleServer.crt'
        },
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01X5T-X5U/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01X5T-X5U/v1'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('riferimento-x509-x5cx5t-x5cx5t')

    * def client_token_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient1',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#present'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01X5CX5T-X5CX5T/v1')
    * match responseStatus == 200
    
    * def server_token_match =
    """
    ({
        header: { 
            kid: 'ExampleServer',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#present'
        },
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01X5CX5T-X5CX5T/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01X5CX5T-X5CX5T/v1'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('no-token-fruizione')

    # Rispondo direttamente senza passare dall'erogazione, non generando nessun token
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('low-ttl-fruizione')

    * java.lang.Thread.sleep(2000)

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/ttl-scaduto-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('low-ttl-fruizione-no-disclosure')

    * java.lang.Thread.sleep(2000)

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/interoperability-invalid-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('low-ttl-erogazione')
    
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowTTL/v1')
    * match responseStatus == 200
    * java.lang.Thread.sleep(2000)


Scenario: isTest('manomissione-token-richiesta')

    * set requestHeaders['Authorization'][0] = tamper_token(requestHeaders['Authorization'][0])
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-richiesta-no-disclosure')

    * set requestHeaders['Authorization'][0] = tamper_token(requestHeaders['Authorization'][0])
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/interoperability-invalid-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-risposta')

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1')
    
    * set responseHeaders['Authorization'][0] = tamper_token(responseHeaders['Authorization'][0])


Scenario: isTest('applicativo-non-autorizzato')

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01AutenticazionePuntuale/v1')
    * match responseStatus == 403
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/applicativo-example-client2-non-autorizzato.json')
    * match header GovWay-Transaction-ErrorType == 'Authorization'


Scenario: isTest('certificato-client-scaduto')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/certificato-client-scaduto.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('certificato-client-revocato')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCA/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/certificato-client-revocato.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('certificato-server-scaduto') 

    * karate.proceed (govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCACertificatoScaduto/v1")


Scenario: isTest('certificato-server-revocato')

    * karate.proceed (govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCACertificatoRevocato/v1")

Scenario: isTest('risposta-not-200')

    * karate.proceed(govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01NoValidazione/v1")
    * match responseStatus == 502
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/risposta-not-200.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityResponseManagementFailed'


Scenario: isTest('connettivita-base-header-agid')

    * def client_token_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient1',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01HeaderAgid/v1')
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR01HeaderAgid/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01HeaderAgid/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



##############################
#           IDAR02
##############################

Scenario: isTest('connettivita-base-idar02')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            jti: '#uuid'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match, kind: 'Bearer' })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02/v1')    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR02/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR02/v1',
            jti: '#uuid'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match, kind: 'Bearer'  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('riutilizzo-token')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02/v1')    
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('riutilizzo-token-risposta')

    * def responseHeaders =  ({ 'Authorization': getRequestHeader("GovWay-TestSuite-Server-Token") })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar02-header-agid')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            jti: '#uuid'
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02HeaderAgid/v1')

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR02HeaderAgid/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR02HeaderAgid/v1',
            jti: '#uuid'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



########################
#       IDAR03         #
########################

Scenario: isTest('connettivita-base-idar03')

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
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar03testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar03testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('manomissione-token-richiesta-idar03')

    * set requestHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(requestHeaders['Agid-JWT-Signature'][0])
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-risposta-idar03')

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    
    * set responseHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(responseHeaders['Agid-JWT-Signature'][0])


Scenario: isTest('manomissione-payload-richiesta')

    * def c = request
    * set c.nuovo_campo = "pippa"

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-payload-risposta')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * match responseStatus == 200

    * set response.nuovo_campo = "pippa"

Scenario: isTest('manomissione-header-http-firmati-richiesta')

    * setRequestHeader("IDAR03TestHeader","tampered_content")
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-header-http-firmati-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-header-http-firmati-risposta')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * match responseStatus == 200
    * set responseHeaders['IDAR03TestHeader'][0] = 'tampered_content'


Scenario: isTest('assenza-header-digest-richiesta')

    * remove requestHeaders['Digest']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-digest-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('assenza-header-digest-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * remove responseHeaders['Digest']


Scenario: isTest('response-without-payload-idar03')

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
                { 'content-type': 'application\/json; charset=UTF-8' },
            ]
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUD/v1',
            signed_headers: [
                { idar03testheader: 'TestHeaderResponse' }
            ]
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('response-without-payload-idar03-tampered-header')

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
                { 'content-type': 'application\/json; charset=UTF-8' },
            ]
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUD/v1',
            signed_headers: [
                { idar03testheader: 'TestHeaderResponse' }
            ]
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null,
        'IDAR03TestHeader': 'tampered_header'
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('request-without-payload-idar03')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUD/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('request-without-payload-idar03-tampered-header')

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
               { idar03testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1'

    * setRequestHeader("IDAR03TestHeader","tampered_header")
    * karate.proceed(url_invocazione_erogazione)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-header-http-firmati-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



Scenario: isTest('request-response-without-payload-idar03')


    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: '#notpresent'
        }
    })
    """
    
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUD/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUD/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUD/v1',
            signed_headers: [
                { idar03testheader: 'TestHeaderResponse' }
            ]
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('response-without-payload-idar03-digest-richiesta')

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
                { 'content-type': 'application\/json; charset=UTF-8' },
            ]
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            signed_headers: '#notpresent',
            request_digest: request_digest[0]
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('request-without-payload-idar03-digest-richiesta')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('request-response-without-payload-idar03-digest-richiesta')


    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            signed_headers: '#notpresent'
        }
    })
    """
    
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CRUDDigestRichiesta/v1'
    * karate.proceed(url_invocazione_erogazione)

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CRUDDigestRichiesta/v1',
            signed_headers: '#notpresent'
        }
    })
    """

    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('informazioni-utente-header') || isTest('informazioni-utente-query') || isTest('informazioni-utente-mixed')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'utente-token',
            user_ip: 'ip-utente-token',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },            
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03InfoUtente/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03InfoUtente/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('informazioni-utente-static')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'codice-ente-static',
            sub: 'utente-token-static',
            user_ip: 'ip-utente-token-static',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },            
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03InfoUtente/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03InfoUtente/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('informazioni-utente-custom')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'codice-ente-custom',
            sub: 'utente-token',
            user_ip: 'ip-utente-token',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },            
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03InfoUtente/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03InfoUtente/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('no-informazioni-utente-at-erogazione')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03InfoUtente/v1')
    * match responseStatus == 400
    * match responseHeaders['GovWay-Transaction-ErrorType'][0] == "InteroperabilityInvalidRequest"
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/no-token-ip-at-erogazione.json')


Scenario: isTest('connettivita-base-idar03-header-bearer')

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
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_match, kind: 'Bearer' })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderBearer/v1')
    
    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03HeaderBearer/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderBearer/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_match, kind: 'Bearer'})

    * def response_token = decodeToken(responseHeaders['Authorization'][0])
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Authorization'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar03-token-richiesta')

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
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03TokenRichiesta/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * match responseHeaders['Agid-JWT-Signature'] == "#notpresent"
    * match responseHeaders['Digest'] == "#notpresent"

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('idar03-token-risposta')

    
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Digest'][0] == '#notpresent'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03TokenRisposta/v1')
    
    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'anonymous',
            client_id: 'RestBlockingIDAR03TokenRisposta/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03TokenRisposta/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar03-token-azione-puntuale')

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
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03TokenAzionePuntuale/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * match responseHeaders['Agid-JWT-Signature'] == "#notpresent"
    * match responseHeaders['Digest'] == "#notpresent"

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar03-token-azione-puntuale-default')

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
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03TokenAzionePuntuale/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03TokenAzionePuntuale/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03TokenAzionePuntuale/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('idar03-token-criteri-personalizzati')
    
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Digest'][0] == '#notpresent'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03TokenCriteriPersonalizzati/v1')
    
    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'anonymous',
            client_id: 'RestBlockingIDAR03TokenCriteriPersonalizzati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03TokenCriteriPersonalizzati/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('doppi-header-idar03')

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

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
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def client_token_authorization_jti = get client_token_authorization $.payload.jti
    * def client_token_integrity_jti = get client_token_integrity $.payload.jti
    * match client_token_authorization_jti == client_token_integrity_jti

    * def client_token_authorization_iat = get client_token_authorization $.payload.iat
    * def client_token_integrity_iat = get client_token_integrity $.payload.iat
    * match client_token_authorization_iat == client_token_integrity_iat
    
    * def client_token_authorization_nbf = get client_token_authorization $.payload.nbf
    * def client_token_integrity_nbf = get client_token_integrity $.payload.nbf
    * match client_token_authorization_nbf == client_token_integrity_nbf
    
    * def client_token_authorization_exp = get client_token_authorization $.payload.exp
    * def client_token_integrity_exp = get client_token_integrity $.payload.exp
    * match client_token_authorization_exp == client_token_integrity_exp

    * def request_digest = get client_token_integrity $.payload.signed_headers..digest
    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    


    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03HeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicati/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })
    
    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03HeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicati/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def server_token_authorization_jti = get server_token_authorization $.payload.jti
    * def server_token_integrity_jti = get server_token_integrity $.payload.jti
    * match server_token_authorization_jti == server_token_integrity_jti

    * def server_token_authorization_iat = get server_token_authorization $.payload.iat
    * def server_token_integrity_iat = get server_token_integrity $.payload.iat
    * match server_token_authorization_iat == server_token_integrity_iat
    
    * def server_token_authorization_nbf = get server_token_authorization $.payload.nbf
    * def server_token_integrity_nbf = get server_token_integrity $.payload.nbf
    * match server_token_authorization_nbf == server_token_integrity_nbf
    
    * def server_token_authorization_exp = get server_token_authorization $.payload.exp
    * def server_token_integrity_exp = get server_token_integrity $.payload.exp
    * match server_token_authorization_exp == server_token_integrity_exp

    * def response_digest = get server_token_integrity $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]


    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('doppi-header-manomissione-payload-richiesta')

    * def c = request
    * set c.nuovo_campo = "pippa"

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-manomissione-payload-risposta')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    * match responseStatus == 200

    * set response.nuovo_campo = "pippa"

Scenario: isTest('doppi-header-assenza-header-digest-richiesta')

    * remove requestHeaders['Digest']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-digest-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-assenza-header-digest-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    * remove responseHeaders['Digest']

Scenario: isTest('doppi-header-assenza-header-authorization-richiesta')

    * remove requestHeaders['Authorization']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-authorization-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-assenza-header-authorization-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    * remove responseHeaders['Authorization']
    
Scenario: isTest('doppi-header-assenza-header-agid-jwt-signature-richiesta')

    * remove requestHeaders['Agid-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-agid-jwt-signature-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-assenza-header-agid-jwt-signature-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    * remove responseHeaders['Agid-JWT-Signature']
    
Scenario: isTest('doppi-header-audience-risposta-authorization-non-valida-rispetto-client')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudienceClient/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'http://client2',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'http://client2' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta
    
Scenario: isTest('doppi-header-audience-risposta-agid-jwt-signature-non-valida-rispetto-client')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudienceClient/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'http://client2'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
    		{ name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta
		
		
Scenario: isTest('doppi-header-audience-risposta-differente-audience-valida-rispetto-client')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDiversoAudienceClient/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'http://client2'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'http://client2.integrity',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'http://client2.integrity' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta	
		
		
Scenario: isTest('doppi-header-audience-risposta-differente-audience-non-valida-rispetto-client')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDiversoAudienceClient/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'http://client2'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'http://client2' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta		

Scenario: isTest('doppi-header-audience-risposta-valore-statico')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudience/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta	
		
Scenario: isTest('doppi-header-audience-risposta-valore-statico-non-valido')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseStessoAudience/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta		

Scenario: isTest('doppi-header-audience-risposta-diversi-valori-statici')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico-authorization'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico-integrity',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico-authorization' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-statico-integrity' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta	
		
		
Scenario: isTest('doppi-header-audience-risposta-diversi-valori-statici-authorization-non-valido')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico-integrity',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-statico-integrity' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta	
		
		
Scenario: isTest('doppi-header-audience-risposta-diversi-valori-statici-agid-jwt-signature-non-valido')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiResponseDifferenteAudience/v1')

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-statico-authorization'
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })

    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: requestHeaders['Digest'][0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

		* def other_checks_risposta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-statico-authorization' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Risposta') 
		* match result contains deep other_checks_risposta	


Scenario: isTest('doppi-header-audience-richiesta-stesso-valore')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	
		
		
Scenario: isTest('doppi-header-audience-richiesta-stesso-valore-authorization-non-valido')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'valore-errato'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-audience-richiesta-stesso-valore-agid-jwt-signature-non-valido')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestStessoAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-agid-jwt-signature-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-audience-richiesta-differente-valore')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience-authorization'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience-integrity',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience-authorization' },
   			{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience-integrity' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	
		
		
Scenario: isTest('doppi-header-audience-richiesta-differente-valore-authorization-non-valido')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'valore-errato'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience-integrity',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'valore-errato' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'testsuite-audience-integrity' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('doppi-header-audience-richiesta-differente-valore-agid-jwt-signature-non-valido')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'testsuite-audience-authorization'
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: 'valore-errato',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiRequestDifferenteAudience/v1')

		* def other_checks_richiesta = 
		"""
		([
		    { name: 'ProfiloSicurezzaMessaggio-Audience', value: 'testsuite-audience-authorization' },
    		{ name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: 'valore-errato' }
		])
		"""

		* def tid = responseHeaders['GovWay-Transaction-ID'][0]
		* def result = get_traccia(tid,'Richiesta') 
		* match result contains deep other_checks_richiesta	

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-agid-jwt-signature-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('doppi-header-differenti-id-authorization')

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def client_token_authorization_jti = get client_token_authorization $.payload.jti
    * def client_token_integrity_jti = get client_token_integrity $.payload.jti
    * match client_token_authorization_jti != client_token_integrity_jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAuthorization/v1')
    
    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def server_token_authorization_jti = get server_token_authorization $.payload.jti
    * def server_token_integrity_jti = get server_token_integrity $.payload.jti
    * match server_token_authorization_jti != server_token_integrity_jti

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('doppi-header-differenti-id-agid-jwt-signature')

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def client_token_authorization_jti = get client_token_authorization $.payload.jti
    * def client_token_integrity_jti = get client_token_integrity $.payload.jti
    * match client_token_authorization_jti != client_token_integrity_jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAgidJWTSignature/v1')
    
    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def server_token_authorization_jti = get server_token_authorization $.payload.jti
    * def server_token_integrity_jti = get server_token_integrity $.payload.jti
    * match server_token_authorization_jti != server_token_integrity_jti

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


##########################
#       IDAR0302         #
##########################

Scenario: isTest('connettivita-base-idar0302')

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
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar03testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR0302/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR0302/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar03testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('manomissione-token-richiesta-idar0302')

    * set requestHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(requestHeaders['Agid-JWT-Signature'][0])
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-risposta-idar0302')

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    
    * set responseHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(responseHeaders['Agid-JWT-Signature'][0])


Scenario: isTest('manomissione-payload-richiesta-idar0302')

    * def c = request
    * set c.nuovo_campo = "pippa"

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-payload-risposta-idar0302')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * match responseStatus == 200

    * set response.nuovo_campo = "pippa"


Scenario: isTest('manomissione-header-http-firmati-richiesta-idar0302')

    * setRequestHeader("IDAR03TestHeader","tampered_content")
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-header-http-firmati-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-header-http-firmati-risposta-idar0302')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * match responseStatus == 200
    * set responseHeaders['IDAR03TestHeader'][0] = 'tampered_content'


Scenario: isTest('assenza-header-digest-richiesta-idar0302')

    * remove requestHeaders['Digest']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-digest-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('assenza-header-digest-risposta-idar0302')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    * remove responseHeaders['Digest']

Scenario: isTest('riutilizzo-token-idar0302')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('riutilizzo-token-risposta-idar0302')

    * def responseHeaders =  ({ 'Agid-JWT-Signature': getRequestHeader("GovWay-TestSuite-Server-Token"), 'Digest': getRequestHeader("GovWay-TestSuite-Digest") })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')


Scenario: isTest('connettivita-base-idar0302-header-bearer')

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
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_match, kind: 'Bearer' })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderBearer/v1')
    
    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR0302HeaderBearer/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR0302HeaderBearer/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_match, kind: 'Bearer'})

    * def response_token = decodeToken(responseHeaders['Authorization'][0])
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Authorization'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('custom-claims')

    * def client_token_match_custom_claims = 
    """
    ({
        header: { kid: 'ExampleClient2' },
        payload: { 
            aud: 'testsuite',
            client_id: 'http://client2',
            iss: 'customIssSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01ExampleClient2',
            custom_api: 'customExampleAPIFruizione',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'custom-claims',
            custom_json: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2wh'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match_custom_claims, kind: 'Bearer' })

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUDClaimCustom/v1'

    * karate.proceed (url_invocazione_erogazione)
    
    # Check claims custom
    * def server_token_match_claims =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'customAud',
            client_id: 'customClientId',
            iss: 'customIssSoggettoErogatore',
            sub: 'customSubSoggettoErogatore',
            custom_api: 'customExampleAPIErogazione',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'custom-claims',
            custom_json: 'Risultato C'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match_claims, kind: 'Bearer'  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)
  
  
    
Scenario: isTest('custom-claims-sub-iss-clientid-empty')

    * def client_token_match_custom_claims2 = 
    """
    ({
        header: { kid: 'ExampleClient2' },
        payload: { 
            aud: 'testsuite',
            client_id: '#notpresent',
            iss: '#notpresent',
            sub: '#notpresent',
            custom_api: 'customExampleAPIFruizione',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'custom-claims-sub-iss-clientid-empty',
            custom_json: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2wh'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match_custom_claims2, kind: 'Bearer' })

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUDClaimCustomSubIssNotGenerate/v1'

    * karate.proceed (url_invocazione_erogazione)
    
    # Check claims custom
    * def server_token_match_claims2 =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'customAud',
            client_id: '#notpresent',
            iss: '#notpresent',
            sub: '#notpresent',
            custom_api: 'customExampleAPIErogazione',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'custom-claims-sub-iss-clientid-empty',
            custom_json: 'Risultato C'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match_claims2, kind: 'Bearer'  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato')

    * def client_token_match_custom_claims_authorization = 
    """
    ({
        header: { kid: 'ExampleClient2' },
        payload: { 
            aud: 'testsuite',
            client_id: 'http://client2',
            iss: 'customIssSoggettoFruitoreAuthorization',
            sub: 'ApplicativoBlockingIDA01ExampleClient2',
            user_ip: '10.112.32.21',
            custom_api: 'customExampleAPIFruizione',
            custom_api_ridefinito: 'customExampleAPIFruizioneAuthorization',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_json: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2wh',
            custom_fruitore_authorization: 'exampleFruitorePCustomAuthorization',
            custom_erogatore_authorization: 'exampleErogatorePCustomAuthorization',
            custom_applicativo_authorization: 'exampleClient2PCustomAuthorization',
            custom_hdr_authorization: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoAuthorization',
            custom_json_authorization: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2whAuthorization'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match_custom_claims_authorization, kind: 'Bearer' })

    * def client_token_match_custom_claims_integrity = 
    """
    ({
        header: { kid: 'ExampleClient2' },
        payload: { 
            aud: 'testsuite',
            client_id: 'http://client2',
            iss: 'customIssSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01ExampleClient2Integrity',
            user_ip: '10.112.32.21',
            custom_api: 'customExampleAPIFruizione',
            custom_api_ridefinito: 'customExampleAPIFruizioneIntegrity',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_json: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2wh',
            custom_fruitore_integrity: 'exampleFruitorePCustomIntegrity',
            custom_erogatore_integrity: 'exampleErogatorePCustomIntegrity',
            custom_applicativo_integrity: 'exampleClient2PCustomIntegrity',
            custom_hdr_integrity: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoIntegrity',
            custom_json_integrity: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2whIntegrity',
            signed_headers: [
                { digest: requestHeaders.Digest[0] },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

		* call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match_custom_claims_integrity, kind: "AGID" })

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderDuplicatiCorniceSicurezzaCustomClaim/v1'

    * karate.proceed (url_invocazione_erogazione)
    
    # Check claims custom
    * def server_token_match_claims_authorization =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'customAudAuthorization',
            client_id: 'customClientId',
            iss: 'customIssSoggettoErogatoreAuthorization',
            sub: 'customSubSoggettoErogatore',
            custom_ip: '10.112.32.21',
            custom_api: 'customExampleAPIErogazione',
            custom_api_ridefinito: 'customExampleAPIErogazioneAuthorization',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr_request: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_hdr_response: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_json: 'Risultato C',
            custom_ip_authorization: '10.112.32.21Authorization',
            custom_api_authorization: 'customExampleAPIErogazioneAuthorization',
            custom_fruitore_authorization: 'exampleFruitorePCustomAuthorization',
            custom_erogatore_authorization: 'exampleErogatorePCustomAuthorization',
            custom_applicativo_authorization: 'exampleClient2PCustomAuthorization',
            custom_hdr_request_authorization: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoAuthorization',
            custom_hdr_response_authorization: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoAuthorization',
            custom_json_authorization: 'Risultato CAuthorization'
        }
    })
    """
    
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match_claims_authorization, kind: 'Bearer'  })

		* def request_digest = requestHeaders.Digest[0]

    * def server_token_match_claims_integrity =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'customAud',
            client_id: 'customClientIdIntegrity',
            iss: 'customIssSoggettoErogatore',
            sub: 'customSubSoggettoErogatoreIntegrity',
            custom_ip: '10.112.32.21',
            custom_api: 'customExampleAPIErogazione',
            custom_api_ridefinito: 'customExampleAPIErogazioneIntegrity',
            custom_fruitore: 'exampleFruitorePCustom',
            custom_erogatore: 'exampleErogatorePCustom',
            custom_applicativo: 'exampleClient2PCustom',
            custom_hdr_request: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_hdr_response: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmato',
            custom_json: 'Risultato C',
            custom_ip_integrity: '10.112.32.21Integrity',
            custom_api_integrity: 'customExampleAPIErogazioneIntegrity',
            custom_fruitore_integrity: 'exampleFruitorePCustomIntegrity',
            custom_erogatore_integrity: 'exampleErogatorePCustomIntegrity',
            custom_applicativo_integrity: 'exampleClient2PCustomIntegrity',
            custom_hdr_request_integrity: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoIntegrity',
            custom_hdr_response_integrity: 'doppi-header-cornice-sicurezza-e-custom-claims-e-hdr-authorization-firmatoIntegrity',
            custom_json_integrity: 'Risultato CIntegrity',
            signed_headers: [
                { digest: responseHeaders.Digest[0] },
                { 'content-type': 'application\/json' },
                { authorization: responseHeaders.Authorization[0] }
            ],
            request_digest: request_digest
        }
    })
    """
    
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match_claims_integrity, kind: "AGID" })
    

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


# catch all
#
#

#Scenario:
#    karate.fail("Nessuno scenario matchato")
