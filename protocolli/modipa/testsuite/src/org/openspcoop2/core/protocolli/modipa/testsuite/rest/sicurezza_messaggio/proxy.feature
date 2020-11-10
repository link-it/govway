Feature: Server proxy per il testing sicurezza messaggio

Background: 

    * def isTest = function(id) { return karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id } 
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
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient2',
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
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01ExampleClient2',
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

    * def responseHeaders =  ({ 'Authorization': requestHeaders['GovWay-TestSuite-Server-Token'][0] })
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

    * set requestHeaders['IDAR03TestHeader'][0] = 'tampered_content'
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

    * set requestHeaders['IDAR03TestHeader'][0] = 'tampered_header'
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

    * set requestHeaders['IDAR03TestHeader'][0] = 'tampered_content'
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

    * def responseHeaders =  ({ 'Agid-JWT-Signature': requestHeaders['GovWay-TestSuite-Server-Token'][0], 'Digest': requestHeaders['GovWay-TestSuite-Digest'][0] })
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

# catch all
#
#

#Scenario:
#    karate.fail("Nessuno scenario matchato")
