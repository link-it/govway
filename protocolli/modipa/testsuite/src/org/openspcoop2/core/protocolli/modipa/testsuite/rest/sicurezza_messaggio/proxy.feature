Feature: Server proxy per il testing sicurezza messaggio

Background: 

    * def getRequestHeader = read('classpath:utils/get-request-header.js')
    * def setRequestHeader = read('classpath:utils/set-request-header.js')
    * def get_traccia = read('classpath:utils/get_traccia.js')
    * def get_diagnostici = read('classpath:utils/get_diagnostici.js')

    * def isTest =
    """
    function(id) {

        karate.log("Proxy test id (case A): ", karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]"))
        karate.log("Proxy test id (case B): ", karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]"))
        karate.log("Proxy test id (case C): ", karate.get("requestHeaders['govway-testsuite-test-id'][0]"))

        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def checkToken = read('check-token.feature')
    * def checkTokenKid = read('check-token-kid.feature')
    * def decodeToken = read('classpath:utils/decode-token.js')
    * def encode_base64 = read('classpath:utils/encode-base64.js')

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

    * def tamper_token_audit = 
    """
    function(tok) {
        var components = tok.split('.')
        components[2] = components[2] + 'tamper'
        return components[0] + '.' + components[1] + '.' + components[2]
    }
    """
   
Scenario: isTest('connettivita-base')

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match })

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
        
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01CRUDNoDefaultSecurity/v1' 

    * karate.proceed (url_invocazione_erogazione)
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

Scenario: isTest('low-iat-ttl-fruizione')

		# Lo iat accettato nell'erogazione e' fino a 3 secondi
    * java.lang.Thread.sleep(5000)

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/iat-scaduto-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('low-iat-ttl-erogazione')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1')
    * match responseStatus == 200

    # Lo iat accettato nella fruizione e' fino a 3 secondi
    * java.lang.Thread.sleep(5000)

Scenario: isTest('iat-future-response')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01LowIAT/v1')
    * match responseStatus == 200

    # Viene iniettatto un token con iat nel futuro. Il token utilizzato dal proxy server viene generato tramite il tool jwt_generator
    * set responseHeaders['Authorization'][0] = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il19.ewogICJpYXQiOiA4NjU3Mjg5NTk3LAogICJuYmYiOiAxNjU3Mjg5NTk3LAogICJleHAiOiA4NjU3Mjg5ODk3LAogICJqdGkiOiAiMWNlMDc4ZWItZmVjOC0xMWVjLTg0NDctMDI0MmM3ZWFlNDg2IiwKICAiYXVkIjogIkRlbW9Tb2dnZXR0b0ZydWl0b3JlL0FwcGxpY2F0aXZvQmxvY2tpbmdJREEwMSIsCiAgImNsaWVudF9pZCI6ICJSZXN0QmxvY2tpbmdJREFSMDFMb3dJQVQvdjEiLAogICJpc3MiOiAiRGVtb1NvZ2dldHRvRXJvZ2F0b3JlIiwKICAic3ViIjogIlJlc3RCbG9ja2luZ0lEQVIwMUxvd0lBVC92MSIKfQo.ETJP9ETOnY76uRFMbz7CTx5bAHdrXrjUoC9KpRT97PIhZUzho0Ws0Z-bbfTQhqBPt9yPjTs6iyn6kK3e2fJYPuo9IoZX98-cciBTayBy3p19IpCkgqlfiXturvChEkwKmm--NJuloSjLF0WjzBic3RRAIuoioAqe9SD-ePQiZ7jNgvQbpFkqPWTSGJBZvlOHAAEzqyprp6qdTG9v0tQwATKQ72WNenFdQPRkS6mOQBXn6HNaGOw55iG-oGvPzUVr527tQHt9r2Tr_8YpNYAR6s_54sxZ_s6cluglr4MS__xw2BX2JfhqovlE1UfuyzIsBddWBbEXobtWRrwcCGLl0w'

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

Scenario: isTest('keystore-default-fruizione')

    * def client_token_keystore_fruizione_match = 
    """
    ({
        header: { 
            kid: 'ExampleServer',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'RestBlockingIDAR01KeystoreDefaultFruizione/v1',
            iss: 'DemoSoggettoFruitore',
            sub: 'RestBlockingIDAR01KeystoreDefaultFruizione/v1'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_keystore_fruizione_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'RestBlockingIDAR01KeystoreDefaultFruizione/v1',
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


Scenario: isTest('keystore-ridefinito-fruizione') || isTest('keystore-ridefinito-fruizione-applicativo-no-keystore')

    * def client_token_keystore_ridefinito_fruizione_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient5',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'RestBlockingIDAR01KeystoreRidefinitoFruizione/v1',
            iss: 'DemoSoggettoFruitore',
            sub: 'RestBlockingIDAR01KeystoreRidefinitoFruizione/v1'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_keystore_ridefinito_fruizione_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'RestBlockingIDAR01KeystoreRidefinitoFruizione/v1',
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

Scenario: isTest('keystore-ridefinito-fruizione-archivio')

    * def client_token_keystore_ridefinito_fruizione_archivio_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient4',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneArchivio/v1',
            iss: 'DemoSoggettoFruitore',
            sub: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneArchivio/v1'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_keystore_ridefinito_fruizione_archivio_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01/v1')
    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneArchivio/v1',
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

Scenario: isTest('keystore-ridefinito-fruizione-x5u')

    * def client_token_keystore_ridefinito_fruizione_match = 
    """
    ({
        header: { 
            kid: 'ExampleClient1',
            x5c: '#notpresent',
            x5u: 'http://localhost:8080/ExampleClient1.crt',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneX5U/v1',
            iss: 'DemoSoggettoFruitore',
            sub: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneX5U/v1'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_keystore_ridefinito_fruizione_match })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01X5T/v1')
    

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
            aud: 'RestBlockingIDAR01KeystoreRidefinitoFruizioneX5U/v1',
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

Scenario: isTest('keystore-definito-applicativo')

    * def client_token_keystore_ridefinito_applicativo_match = 
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

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_keystore_ridefinito_applicativo_match })

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


##############################
#           IDAR01 OCSP
##############################

Scenario: isTest('connettivita-base-truststore-ca-ocsp')

    * def client_token_match_ocsp = 
    """
    ({
        header: { 
            kid: 'testclient',
            x5c: '#present',
            x5u: '#notpresent',
            'x5t#S256': '#notpresent'
        },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01_OCSP',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01_OCSP'
        }
    })
    """

    * call checkToken ({token: requestHeaders.Authorization[0], match_to: client_token_match_ocsp })

    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCAOCSP/v1'
    * karate.proceed (url_invocazione_erogazione)
    

    * def server_token_match_ocsp =
    """
    ({
        header: { kid: 'testclient'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01_OCSP',
            client_id: 'RestBlockingIDAR01TrustStoreCAOCSP/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR01TrustStoreCAOCSP/v1'
        }
    })
    """
    * call checkToken ({token: responseHeaders.Authorization[0], match_to: server_token_match_ocsp  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders.Authorization[0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('certificato-client-revocato-ocsp')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCAOCSP/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/certificato-client-revocato-ocsp.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('certificato-server-revocato-ocsp')

    * karate.proceed (govway_base_path + "/rest/in/DemoSoggettoErogatore/RestBlockingIDAR01TrustStoreCACertificatoRevocatoOCSP/v1")



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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02/v1')    

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id


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

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02RiutilizzoToken/v1')    
    
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



Scenario: isTest('riutilizzo-token-generato-auth-server')

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02TokenOAuth/v1')    
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders.Authorization[0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('riutilizzo-token-risposta-generato-auth-server')

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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0],'AGID')
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02HeaderAgid/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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


Scenario: isTest('check-authz-idar02')

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

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR02CheckAuthz/v1')    

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR02CheckAuthz/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR02CheckAuthz/v1',
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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id
    
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


Scenario: isTest('test-audience-as-array')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: ['testsuite'],
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

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03-AudienceAsArray/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: ['testsuite-server-response'],
            client_id: 'RestBlockingIDAR03-AudienceAsArray/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03-AudienceAsArray/v1',
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


Scenario: isTest('test-audience-as-array-multiple-values')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: ['testsuite2','testsuite','testsuite3'],
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

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03-AudienceAsArray/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: ['testsuite-server-response'],
            client_id: 'RestBlockingIDAR03-AudienceAsArray/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03-AudienceAsArray/v1',
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


Scenario: isTest('test-audience-as-array-invalid')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: ['testsuite-invalid'],
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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03-AudienceAsArray/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('test-audience-as-array-multiple-values-invalid')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: ['testsuite2','testsuite-invalid','testsuite3'],
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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03-AudienceAsArray/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('digest-hex-idar03')

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

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03-DigestHex/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03-DigestHex/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03-DigestHex/v1',
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



Scenario: isTest('assenza-header-integrity-richiesta')

    * remove requestHeaders['Agid-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('assenza-header-integrity-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03/v1')
    * remove responseHeaders['Agid-JWT-Signature']




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


Scenario: isTest('informazioni-utente-header') || 
		isTest('informazioni-utente-query') || 
		isTest('informazioni-utente-mixed')

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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderBearer/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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
        'GovWay-TestSuite-GovWay-Message-ID': responseHeaders['GovWay-Message-ID'][0],
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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id


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

Scenario: isTest('doppi-header-solo-integrity-risposta')

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
    
		* match responseHeaders['Authorization'] == '#notpresent' 
    
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

    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def response_digest = get server_token_integrity $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]


    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('doppi-header-authorization-richiesta-integrity-risposta')

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

		* match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match requestHeaders['Digest'] == '#notpresent' 

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * match responseHeaders['Authorization'] == '#notpresent' 
    
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
            ]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def response_digest = get server_token_integrity $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]


    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)
    
Scenario: isTest('doppi-header-solo-authorization-richiesta')

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

		* match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match requestHeaders['Digest'] == '#notpresent' 

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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
            ]
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
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)    
    
Scenario: isTest('doppi-header-solo-authorization-richiesta-risposta')

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

		* match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match requestHeaders['Digest'] == '#notpresent' 

    * request ''

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

		* match responseHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match responseHeaders['Digest'] == '#notpresent' 

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)    
    
Scenario: isTest('doppi-header-solo-authorization-richiesta-delete')

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

		* match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match requestHeaders['Digest'] == '#notpresent' 

		* request ''

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati/v1')
    
		* match responseHeaders['Authorization'] == '#notpresent'
		* match responseHeaders['Agid-JWT-Signature'] == '#notpresent' 
		* match responseHeaders['Digest'] == '#notpresent' 

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)  
    
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


Scenario: isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArray')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-authorization']
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-integrity'],
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati-AudienceAsArray/v1')

	* def other_checks_richiesta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["testsuite-audience-authorization"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["testsuite-audience-integrity"]' }
	])
	"""

	* def tid = responseHeaders['GovWay-Transaction-ID'][0]
	* def result = get_traccia(tid,'Richiesta') 
	* match result contains deep other_checks_richiesta

	* def other_checks_risposta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["testsuite-server-response-authorization","altro-valore"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["testsuite-server-response-integrity","altro-valore"]' }
	])
	"""

	* def result = get_traccia(tid,'Risposta') 
	* match result contains deep other_checks_risposta	



Scenario: isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArrayMultipleValues')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-authorization','altro']
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['altro','altro2','testsuite-audience-integrity'],
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati-AudienceAsArray/v1')

	* def other_checks_richiesta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["testsuite-audience-authorization","altro"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["altro","altro2","testsuite-audience-integrity"]' }
	])
	"""

	* def tid = responseHeaders['GovWay-Transaction-ID'][0]
	* def result = get_traccia(tid,'Richiesta') 
	* match result contains deep other_checks_richiesta

	* def other_checks_risposta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["testsuite-server-response-authorization","altro-valore"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["testsuite-server-response-integrity","altro-valore"]' }
	])
	"""

	* def result = get_traccia(tid,'Risposta') 
	* match result contains deep other_checks_risposta	


Scenario: isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArrayInvalid')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-authorization-invalid']
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-integrity'],
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati-AudienceAsArray/v1')

	* def other_checks_richiesta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["testsuite-audience-authorization-invalid"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["testsuite-audience-integrity"]' }
	])
	"""

	* def tid = responseHeaders['GovWay-Transaction-ID'][0]
	* def result = get_traccia(tid,'Richiesta') 
	* match result contains deep other_checks_richiesta	

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/aud-token-richiesta-non-valido.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('doppi-header-audience-richiesta-differente-valore-audienceAsArrayIntegrityInvalid')

    * def client_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['altro','testsuite-audience-authorization']
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer"  })

    * def client_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleClient2'},
        payload: {
            aud: ['testsuite-audience-integrity-invalid','altro'],
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """
    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID"  })
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicati-AudienceAsArray/v1')

	* def other_checks_richiesta = 
	"""
	([
	    { name: 'ProfiloSicurezzaMessaggio-Audience', value: '["altro","testsuite-audience-authorization"]' },
	    { name: 'ProfiloSicurezzaMessaggio-IntegrityAudience', value: '["testsuite-audience-integrity-invalid","altro"]' }
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

    * def request_id = get client_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAuthorization/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

    * def request_id = get client_token_integrity $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiDifferenteIDconDefaultAgidJWTSignature/v1')
        
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

Scenario: isTest('doppi-header-token-date-differenti-risposta') ||
					isTest('doppi-header-token-date-differenti-risposta-iat-oldest')

    * match requestHeaders['Authorization'] == '#present'
    * match requestHeaders['Agid-JWT-Signature'] == '#present'
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

		* def server_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkzMTQsIm5iZiI6MTYzMTU0OTMxNCwiZXhwIjoyMjYyMjY5MzE0LCJqdGkiOiJkOGU2MTNjYy0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJpc3MiOiJEZW1vU29nZ2V0dG9Fcm9nYXRvcmUiLCJzdWIiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEifQ.z6nx5Vhs2AP9z4hALBppYb0we4jaZ55zdLYpE7qpvCPhdVQusT1SiiX94IDF0OVWety54R6-LkiYsmFrtn3x3iYLdNrSjCRoKVYGhGADYXF4hDxKY_rZ5jySHak5-We81_jUm49pIED_TnroHIQdMmflD6UWrfPscLkPfwjHtt6PHZt5GCdYgRRg1P9KmswIwaya6Pcqv_euqdjvCFZ3pL-7lkRVIlF5Buezb9VXz9Y7fOYY0UupGJBe13SGdRILY1Ghet0KopyXUFgGXYhklfoqFeojeKOIDj1uNpbu3Gyz9JWomuCScfpj39bHM2igblTWUY-VRyri-q-VEJXy5g' 
		* def server_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkzNzYsIm5iZiI6MTYzMTU0OTM3NiwiZXhwIjoyMjYyMjY5Mzc2LCJqdGkiOiJmZDk1YjRlMi0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJyZXF1ZXN0X2RpZ2VzdCI6IlNIQS0yNTY9MDMxZjcwMjM5MTM5NDZmZTRmMTBjMjQ2ZDBjOGY0MWIyY2FjNWVkM2Q1ODM0YzBmNDcwMzVhZDMwOTIxNmU2NiIsImlzcyI6IkRlbW9Tb2dnZXR0b0Vyb2dhdG9yZSIsInN1YiI6IlJlc3RCbG9ja2luZ0lEQVIwM0hlYWRlckR1cGxpY2F0aS92MSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTFhYjAwYjY0MjdmZjc2ZDBkMWZlM2VmOWViYTczMDM1YzNiNDhmZmQ0MmQyYzVhZmRkYmQ4NzA2ZTFhOTk2N2YifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb24ifV19.AGDg1mY6dpawvq2X65F12glKoGAqzBKHYWLbsvPYanOfBf9RNWXZ4AUKm95MjQqAdBLRkjeY1xrWaGe_u2tLdZuE-fOqxn8mdnvgv7Bdt9x1TiOGVRtsh9SfP6eFGg5w04BNXaFB6wSz-0TJburhDWB8xbAAw86xyYDefiTy4e1FMlMznJu9NzZprLd_NjxoGnHbiHpkrugSwfMRFbukX6RPtofPk_MGsPqO7Ma8CCibwAihUetvaP9rK2586grENaqu4hd0MFpu6Il5ec8OJjBu7_1G_2CaAKybchPAFOImZZsV8EAvo-1mnFVXdnGlj-ipNcBPdyvEW_KrG7UB9Q'
		* def server_digest = 'SHA-256=1ab00b6427ff76d0d1fe3ef9eba73035c3b48ffd42d2c5afddbd8706e1a9967f'

    * def newHeaders = 
    """
    ({
        'Authorization': server_authorization_token,
        'Agid-JWT-Signature': server_integrity_token,
        'Digest': server_digest,
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': server_authorization_token,
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': server_integrity_token
    })
    """
    * def responseHeaders = ({ 'Content-Type': 'application/json' })
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)
    
Scenario: isTest('doppi-header-token-date-differenti-risposta-authorization-scaduta')

    * match requestHeaders['Authorization'] == '#present'
    * match requestHeaders['Agid-JWT-Signature'] == '#present'
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

		* def server_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkwMDgsIm5iZiI6MTYzMTU0OTAwOCwiZXhwIjoxNjMxNTQ5MzA4LCJqdGkiOiIyMjhkNzkxMi0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJpc3MiOiJEZW1vU29nZ2V0dG9Fcm9nYXRvcmUiLCJzdWIiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEifQ.V-Q-r2aQCQjIlFVOrP-F07PWDrz1twf2rejJD1EMVjw4Hg3DU5w-FUEBcoz393va24_Pl2VQYt61ofrofEMVgNy0ZQzkAp-uqLPRWcpfmwEsWEY1wCKmAfWKpLyg-1BtBzGQVj0gHbvBFTHdm0tKDaSDXZNeEm7s7Dsv2tV3MEzSM__eKJvsU1vV2uD_pF5jx57Kb-jh_k5xKXF51stt1JIUYb4kYGVzGL9H7VbzW0NYIVeSLzVzu5L1ZmXdnhXAbvehXHArVfEL-DzXMoEC8DOnmcJqPUS84dsOivOt4_FlWMQybOi2HfBOaBWVuvkr4fCTDH45vL7ZS7pM5H7SeQ'
		* def server_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkzNzYsIm5iZiI6MTYzMTU0OTM3NiwiZXhwIjoyMjYyMjY5Mzc2LCJqdGkiOiJmZDk1YjRlMi0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJyZXF1ZXN0X2RpZ2VzdCI6IlNIQS0yNTY9MDMxZjcwMjM5MTM5NDZmZTRmMTBjMjQ2ZDBjOGY0MWIyY2FjNWVkM2Q1ODM0YzBmNDcwMzVhZDMwOTIxNmU2NiIsImlzcyI6IkRlbW9Tb2dnZXR0b0Vyb2dhdG9yZSIsInN1YiI6IlJlc3RCbG9ja2luZ0lEQVIwM0hlYWRlckR1cGxpY2F0aS92MSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTFhYjAwYjY0MjdmZjc2ZDBkMWZlM2VmOWViYTczMDM1YzNiNDhmZmQ0MmQyYzVhZmRkYmQ4NzA2ZTFhOTk2N2YifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb24ifV19.AGDg1mY6dpawvq2X65F12glKoGAqzBKHYWLbsvPYanOfBf9RNWXZ4AUKm95MjQqAdBLRkjeY1xrWaGe_u2tLdZuE-fOqxn8mdnvgv7Bdt9x1TiOGVRtsh9SfP6eFGg5w04BNXaFB6wSz-0TJburhDWB8xbAAw86xyYDefiTy4e1FMlMznJu9NzZprLd_NjxoGnHbiHpkrugSwfMRFbukX6RPtofPk_MGsPqO7Ma8CCibwAihUetvaP9rK2586grENaqu4hd0MFpu6Il5ec8OJjBu7_1G_2CaAKybchPAFOImZZsV8EAvo-1mnFVXdnGlj-ipNcBPdyvEW_KrG7UB9Q'
		* def server_digest = 'SHA-256=1ab00b6427ff76d0d1fe3ef9eba73035c3b48ffd42d2c5afddbd8706e1a9967f'

    * def newHeaders = 
    """
    ({
        'Authorization': server_authorization_token,
        'Agid-JWT-Signature': server_integrity_token,
        'Digest': server_digest,
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': server_authorization_token,
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': server_integrity_token
    })
    """
    * def responseHeaders = ({ 'Content-Type': 'application/json' })
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('doppi-header-token-date-differenti-risposta-integrity-scaduta')

    * match requestHeaders['Authorization'] == '#present'
    * match requestHeaders['Agid-JWT-Signature'] == '#present'
    
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')

		* def server_authorization_token = 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkzMTQsIm5iZiI6MTYzMTU0OTMxNCwiZXhwIjoyMjYyMjY5MzE0LCJqdGkiOiJkOGU2MTNjYy0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJpc3MiOiJEZW1vU29nZ2V0dG9Fcm9nYXRvcmUiLCJzdWIiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEifQ.z6nx5Vhs2AP9z4hALBppYb0we4jaZ55zdLYpE7qpvCPhdVQusT1SiiX94IDF0OVWety54R6-LkiYsmFrtn3x3iYLdNrSjCRoKVYGhGADYXF4hDxKY_rZ5jySHak5-We81_jUm49pIED_TnroHIQdMmflD6UWrfPscLkPfwjHtt6PHZt5GCdYgRRg1P9KmswIwaya6Pcqv_euqdjvCFZ3pL-7lkRVIlF5Buezb9VXz9Y7fOYY0UupGJBe13SGdRILY1Ghet0KopyXUFgGXYhklfoqFeojeKOIDj1uNpbu3Gyz9JWomuCScfpj39bHM2igblTWUY-VRyri-q-VEJXy5g' 
		* def server_integrity_token = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkV4YW1wbGVTZXJ2ZXIiLCJ4NWMiOlsiTUlJRFpEQ0NBa3lnQXdJQkFnSUlCTENDNlFvQzdyb3dEUVlKS29aSWh2Y05BUUVMQlFBd1VqRUxNQWtHQTFVRUJoTUNTVlF4RGpBTUJnTlZCQWdUQlVsMFlXeDVNUTB3Q3dZRFZRUUhFd1JRYVhOaE1SQXdEZ1lEVlFRS0V3ZEZlR0Z0Y0d4bE1SSXdFQVlEVlFRREV3bEZlR0Z0Y0d4bFEwRXdIaGNOTWpBeE1EQTRNVFF5TmpBd1doY05Nemt4TURBNE1UUXlOakF3V2pCV01Rc3dDUVlEVlFRR0V3SkpWREVPTUF3R0ExVUVDQk1GU1hSaGJIa3hEVEFMQmdOVkJBY1RCRkJwYzJFeEVEQU9CZ05WQkFvVEIwVjRZVzF3YkdVeEZqQVVCZ05WQkFNVERVVjRZVzF3YkdWVFpYSjJaWEl3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRGc0cHQ0Q09HNVlHY0JPZDNZV04yOUVCM0tTelRrQ2RibWRnMUZaVzNaRWk4Rjgrd0JKa1B6bnlHLysyelZiYTVRM1VSc2xOS0JET0JteVM1OEVyUGNvQ2tqVXVyY1hjZ1FmYU1zRmdiTENrSnU2enFFYzBLTHpFM1U3TkVzUU9uNjVwbThoNC9icEVPTzZ5SG5lUDFnVzlVUWprZUp6cTJFdFBUT3EwV0diNjdWVzBNZlM0Um4vVkxrSlN5a3hodGxNNlNYNDhUUHRqdnQwZ2FUUzkrRjhuZTViZURXU0ZaQTQxVWNEdGZoYzVrc29Pald1ZERITm0vZVh1di8rSGthVWVwbEdpa3NIQ3FmcUVOSEtoNUhObDlDMnVYOTh5bmsrREtEUVdMbUVvam1qUmd1YzZFZk1IOHdrbXVDaGM2T0t1K2dVRTc2cVFSUDRFQ05Ub0VCQWdNQkFBR2pPakE0TUFrR0ExVWRFd1FDTUFBd0hRWURWUjBPQkJZRUZNVHl5L2kycHg1WnNuWTFWSnlGcmp1TkpRMUhNQXdHQTFVZER3UUZBd01ILzRBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNRnNEOFZjbktzektoMmRPTmp0K1UvUmVSNlAyR3JDb095VVliNFFXYm5nd1cvaENiWlErWVRCeHpLOE9lYjAzZUJRZmNva0RFaWhQUUZueXA2Z1NYYXRYNHlPalRGQ3JtSWJCcDhLZCt5UWRYbHdpV00yazd1c1IxQmpLL2lPN0VMejhnTmtlZjFFcGlFSkx1Qi9PUUx4YkZDenBHZHdqYjBHUGNjTjBwc0c2bWh4NzFwRXRjUEFEekZ3WkhmTzIrbk9XeHdwRnZNai9nTWFTQnY3QkxnbkxDMnNMdGdWNFc2UWFDTXROOExqbWRUODliTHRhYzZ1WGp1WkRhaDVMM2ppUi9oYUN3R09pR2c3L29OWDhKTXkydklVOExwT2dsZEpyS3hPdFN2NzMyL3ZveHArSlVraWhndldmRkN5Sk5zUE9FSjFqQ2x2cVlkdG13TWUyc1E9Il0sIng1dCNTMjU2IjoicE5QVF9RcGVBeGNoRXllUnIzUDM5Wi1kV3h4dmpMS0lPOFQzbTV3d3pSOCJ9.eyJpYXQiOjE2MzE1NDkwMDgsIm5iZiI6MTYzMTU0OTAwOCwiZXhwIjoxNjMxNTQ5MzA4LCJqdGkiOiIyMjhkNzkxMi0xNGFjLTExZWMtYTY5Yi01MjU0MDAzNjM2YTQiLCJhdWQiOiJEZW1vU29nZ2V0dG9GcnVpdG9yZS9BcHBsaWNhdGl2b0Jsb2NraW5nSURBMDEiLCJjbGllbnRfaWQiOiJSZXN0QmxvY2tpbmdJREFSMDNIZWFkZXJEdXBsaWNhdGkvdjEiLCJyZXF1ZXN0X2RpZ2VzdCI6IlNIQS0yNTY9MDMxZjcwMjM5MTM5NDZmZTRmMTBjMjQ2ZDBjOGY0MWIyY2FjNWVkM2Q1ODM0YzBmNDcwMzVhZDMwOTIxNmU2NiIsImlzcyI6IkRlbW9Tb2dnZXR0b0Vyb2dhdG9yZSIsInN1YiI6IlJlc3RCbG9ja2luZ0lEQVIwM0hlYWRlckR1cGxpY2F0aS92MSIsInNpZ25lZF9oZWFkZXJzIjpbeyJkaWdlc3QiOiJTSEEtMjU2PTFhYjAwYjY0MjdmZjc2ZDBkMWZlM2VmOWViYTczMDM1YzNiNDhmZmQ0MmQyYzVhZmRkYmQ4NzA2ZTFhOTk2N2YifSx7ImNvbnRlbnQtdHlwZSI6ImFwcGxpY2F0aW9uL2pzb24ifV19.D0VyrlnnASlTcQT4pyqWeJEqplfpnX5sxTblFzuX7fX1yjn9oCErHYABjdH1R0DdKT8o5a-x6nWSboKyn3E1_fzb4ewgIXEvYDm2O9run9FtwfrXmz3DlN9viEhJ-lnl1oFfJd2_Z35gVosI5f9sMEp4_OUU_Q0B_Tj4aFzDkc-dxgapkGcMtC7-QycU9rQC0MegxqPA0WvlRuJkcn6hhayesJheAjfC7Ysi2zjcPf1BqdlVRUUT10A1MWLIHkgCdRRcoxVwzEFeTXqmzYu8Un360BU0G3eRvMUizixyPYP2g4KB9QwCvONxsEeuJT_xvx_YePBKTXy80ntzLgHVzQ'
		* def server_digest = 'SHA-256=1ab00b6427ff76d0d1fe3ef9eba73035c3b48ffd42d2c5afddbd8706e1a9967f'

    * def newHeaders = 
    """
    ({
        'Authorization': server_authorization_token,
        'Agid-JWT-Signature': server_integrity_token,
        'Digest': server_digest,
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': server_authorization_token,
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': server_integrity_token
    })
    """
    * def responseHeaders = ({ 'Content-Type': 'application/json' })
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('check-authz-idar03')

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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CheckAuthz/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CheckAuthz/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CheckAuthz/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
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

Scenario: isTest('check-authz-doppi-header-idar03')

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

    * def request_id = get client_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1'
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
            client_id: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthz/v1',
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

Scenario: isTest('check-authz-oauth2-doppi-header-idar03')

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            test_cn: 'ExampleClient1',
	    test_property: 'IDAR0301'
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

    * def request_id = get client_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id


    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1'
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
            client_id: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03HeaderDuplicatiCheckAuthzOAuth2/v1',
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


Scenario: isTest('doppi-header-idar03-security-token-trasformazione-authorization-token') ||
	isTest('doppi-header-idar03-security-token-trasformazione-authorization-header') ||
	isTest('doppi-header-idar03-security-token-trasformazione-authorization-payload') ||
	isTest('doppi-header-idar03-security-token-trasformazione-authorization-custom')

    * def client_token_authorization_match = 
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
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    
    * def request_digest = get client_token_authorization $.payload.signed_headers..digest
    * match requestHeaders['Digest'][0] == request_digest[0]

    * def request_id = get client_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id


    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03SecurityTokenTrasformazione/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03SecurityTokenTrasformazione/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })
    
    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    
    * def response_digest = get server_token_authorization $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]


    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('doppi-header-idar03-security-token-trasformazione-integrity-token') ||
	isTest('doppi-header-idar03-security-token-trasformazione-integrity-header') ||
	isTest('doppi-header-idar03-security-token-trasformazione-integrity-payload')

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

    * def client_token_integrity = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def request_digest = get client_token_integrity $.payload.signed_headers..digest
    * match requestHeaders['Digest'][0] == request_digest[0]

    * def request_id = get client_token_integrity $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03SecurityTokenTrasformazione/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    
    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03SecurityTokenTrasformazione/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03SecurityTokenTrasformazione/v1',
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkToken ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def response_digest = get server_token_integrity $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]


    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
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

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0],'AGID')
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0302HeaderBearer/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id
    
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
            custom_json_authorization: 'RGFuJ3MgVG9vbHMgYXJlIGNvb2whAuthorization',
            custom_env: 'EnvValue1', 
            custom_system: 'SystemValue1',  
            custom_java: 'JavaValue1'
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
            custom_env: 'EnvValue1', 
            custom_system: 'SystemValue1',  
            custom_java: 'JavaValue1', 
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

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (url_invocazione_erogazione)
 
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id
   
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
            custom_json_authorization: 'Risultato CAuthorization',
            custom_env: 'EnvValue1', 
            custom_system: 'SystemValue1',  
            custom_java: 'JavaValue1',
            custom_env_authorization: 'EnvValue1Authorization', 
            custom_system_authorization: 'SystemValue1Authorization',  
            custom_java_authorization: 'JavaValue1Authorization'
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
            custom_env: 'EnvValue1', 
            custom_system: 'SystemValue1',  
            custom_java: 'JavaValue1',
            custom_env_integrity: 'EnvValue1Integrity', 
            custom_system_integrity: 'SystemValue1Integrity',  
            custom_java_integrity: 'JavaValue1Integrity',
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



Scenario: isTest('pkcs11') || isTest('pkcs11-certificate') || isTest('pkcs11-keystore-fruizione')

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/PKCS11TestREST/v1'

    * karate.proceed (url_invocazione_erogazione)
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)
    
    
Scenario: isTest('pkcs11-trustStore')

    # Cambia questo
    * def url_invocazione_erogazione = govway_base_path + '/rest/in/DemoSoggettoErogatore/PKCS11TestRESTtrustStore/v1'

    * karate.proceed (url_invocazione_erogazione)
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



########################
#       IDAR03 (CUSTOM)#
########################

Scenario: isTest('idar03-custom-single-header') || isTest('idar0302-custom-single-header')

    * def client_token_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
	    integrationInfoString: 'valoreClientString',
            integrationInfoInt: 13,
            integrationInfoBoolean: true,
	    integrationInfoFloat: 24.2,
            integrationInfoList0: 'val1',
            integrationInfoList1: 'val2',
	    integrationInfoList: ['val1','val2'],
	    level2_integrationInfoString: 'valoreClientLeve2String',
            level2_integrationInfoInt: 99,
            level2_integrationInfoBoolean: false,
	    level2_integrationInfoFloat: 102.29,
            level2_integrationInfoList0: 33,
            level2_integrationInfoList1: 1234,
	    level2_integrationInfoList: [33,1234],
	    level3_integrationInfoString: 'valoreClientLeve3String',
            level3_integrationInfoInt: 12467,
            level3_integrationInfoBoolean: false,
	    level3_claims_integrationInfoString: 'valoreClientLeve3String',
            level3_claims_integrationInfoInt: 12467,
            level3_claims_integrationInfoBoolean: false,
	    complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            },
            complex_integrationInfoListTest1: [ '666' , '999' ],
            complex_integrationInfoListTest2: [ '666' , '999' ],
            optional_integrationInfoString: 'valoreClientString',
            optional_integrationInfoBoolean: true,
            optional_integrationInfoLong: 2147483747,
            optional_integrationInfoDouble: 1399.56,
            optional_integrationInfoListTest: [ '666' , '999' ],
            optional_complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            }
        }
    })
    """
    * call checkToken ({token: requestHeaders['CustomTestSuite-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def request_token = decodeToken(requestHeaders['CustomTestSuite-JWT-Signature'][0], "AGID")
    * karate.log("Ret: ", request_token)
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id    

    * match request_token.payload.signed_headers == "#notpresent"
    
    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['GovWay-Integration'] == '#notpresent'

    * def server_token_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03Custom/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03Custom/v1',
	    integrationInfoString: 'valoreClientString',
            integrationInfoInt: 13,
            integrationInfoBoolean: true,
	    integrationInfoFloat: 24.2,
            integrationInfoList0: 'val1',
            integrationInfoList1: 'val2',
	    integrationInfoList: ['val1','val2'],
	    level2_integrationInfoString: 'valoreClientLeve2String',
            level2_integrationInfoInt: 99,
            level2_integrationInfoBoolean: false,
	    level2_integrationInfoFloat: 102.29,
            level2_integrationInfoList0: 33,
            level2_integrationInfoList1: 1234,
	    level2_integrationInfoList: [33,1234],
	    level3_integrationInfoString: 'valoreClientLeve3String',
            level3_integrationInfoInt: 12467,
            level3_integrationInfoBoolean: false,
	    level3_claims_integrationInfoString: 'valoreClientLeve3String',
            level3_claims_integrationInfoInt: 12467,
            level3_claims_integrationInfoBoolean: false,
	    complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            },
            complex_integrationInfoListTest1: [ '666' , '999' ],
            complex_integrationInfoListTest2: [ '666' , '999' ],
            optional_integrationInfoString: 'valoreClientString',
            optional_integrationInfoBoolean: true,
            optional_integrationInfoLong: 2147483747,
            optional_integrationInfoDouble: 1399.56,
            optional_integrationInfoListTest: [ '666' , '999' ],
            optional_complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            }
        }
    })
    """
    * call checkToken ({token: responseHeaders['CustomTestSuite-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['CustomTestSuite-JWT-Signature'][0], "AGID")
    * match response_token.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Authorization'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match responseHeaders['GovWay-Integration'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['CustomTestSuite-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['CustomTestSuite-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar03-custom-single-header-assenza-header-integrity-richiesta')

    * remove requestHeaders['CustomTestSuite-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/idar03-custom-single-header-assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('idar03-custom-single-header-assenza-header-integrity-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03Custom/v1')
    * remove responseHeaders['CustomTestSuite-JWT-Signature']


Scenario: isTest('idar03-custom-doppi-header') || isTest('idar0302-custom-doppi-header') || 
		isTest('idar03-custom-doppi-header-get-with-custom')

    * def integrationInfoStringCheckValue =
    * eval
    """
    if (isTest('idar03-custom-doppi-header') || isTest('idar0302-custom-doppi-header')  ) {
    integrationInfoStringCheckValue = 'valoreClientString'
    }
    """
    * eval
    """
    if (isTest('idar03-custom-doppi-header-get-with-custom')  ) {
    integrationInfoStringCheckValue = 'valoreClientString2'
    }
    """


    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            integrationInfoString: integrationInfoStringCheckValue,
            level2_integrationInfoAuthorizationString: 'valoreClientLeve2String',
            integrationInfoAuthorizationBoolean: true,
            integrationInfoAuthorizationList0: 'val1',
            integrationInfoAuthorizationList: [ 'val1' , 'val2' ],
            optional_integrationInfoAuthorizationString: integrationInfoStringCheckValue,
            optional_integrationInfoAuthorizationListTest: [ '666' , '999' ],
            optional_complex_integrationInfoAuthorization: {
               clientClaimLeve3String: 'valoreClientLeve3String', 
               clientClaimLeve3Boolean: false, 
               clientClaimLeve3Int: 12467, 
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: 'valoreClientLeve4String', 
                  clientClaimLeve4ListInt: [ 33 , 1234 ], 
                  clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                  clientClaimLeve4ListString: [ 'val1' , 'val2']
               }
           }
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
            integrationInfoString: integrationInfoStringCheckValue,
            level2_integrationInfoIntegrityString: 'valoreClientLeve2String',
            complex_integrationInfoIntegrityListTest1: [ '666' , '999' ],
            complex_integrationInfoIntegrityListTest2: [ '666' , '999' ],
            complex_integrationInfoIntegrity: {
              clientClaimLeve3String: 'valoreClientLeve3String', 
              clientClaimLeve3Boolean: false, 
              clientClaimLeve3Int: 12467, 
              clientClaimLeve4Complex: {
                clientClaimLeve4String: 'valoreClientLeve4String', 
                clientClaimLeve4ListInt: [ 33 , 1234 ], 
                clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                clientClaimLeve4ListString: [ 'val1' , 'val2' ]
              }
            },
            optional_integrationInfoIntegrityBoolean: true,
            optional_integrationInfoIntegrityLong: 2.147483747E9,
            optional_integrationInfoIntegrityDouble: 1399.56
        }
    })
    """
    * call checkToken ({token: requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], "AGID")
    
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

    * match client_token_authorization.payload.signed_headers == "#notpresent"
    * match client_token_integrity.payload.signed_headers == "#notpresent"

    * def request_id = get client_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id



    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            integrationInfoString: integrationInfoStringCheckValue,
            level2_integrationInfoAuthorizationString: 'valoreClientLeve2String',
            integrationInfoAuthorizationBoolean: true,
            integrationInfoAuthorizationList0: 'val1',
            integrationInfoAuthorizationList: [ 'val1' , 'val2' ],
            optional_integrationInfoAuthorizationString: integrationInfoStringCheckValue,
            optional_integrationInfoAuthorizationListTest: [ '666' , '999' ],
            optional_complex_integrationInfoAuthorization: {
               clientClaimLeve3String: 'valoreClientLeve3String', 
               clientClaimLeve3Boolean: false, 
               clientClaimLeve3Int: 12467, 
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: 'valoreClientLeve4String', 
                  clientClaimLeve4ListInt: [ 33 , 1234 ], 
                  clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                  clientClaimLeve4ListString: [ 'val1' , 'val2']
               }
           }
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
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            integrationInfoString: integrationInfoStringCheckValue,
            level2_integrationInfoIntegrityString: 'valoreClientLeve2String',
            complex_integrationInfoIntegrityListTest1: [ '666' , '999' ],
            complex_integrationInfoIntegrityListTest2: [ '666' , '999' ],
            complex_integrationInfoIntegrity: {
              clientClaimLeve3String: 'valoreClientLeve3String', 
              clientClaimLeve3Boolean: false, 
              clientClaimLeve3Int: 12467, 
              clientClaimLeve4Complex: {
                clientClaimLeve4String: 'valoreClientLeve4String', 
                clientClaimLeve4ListInt: [ 33 , 1234 ], 
                clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                clientClaimLeve4ListString: [ 'val1' , 'val2' ]
              }
            },
            optional_integrationInfoIntegrityBoolean: true,
            optional_integrationInfoIntegrityLong: 2.147483747E9,
            optional_integrationInfoIntegrityDouble: 1399.56
        }
    })
    """
    * call checkToken ({token: responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    * def server_token_integrity = decodeToken(responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], "AGID")
    
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

    * match server_token_authorization.payload.signed_headers == "#notpresent"
    * match server_token_integrity.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('idar03-custom-doppi-header-solo-richiesta') || isTest('idar0302-custom-doppi-header-solo-richiesta')

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            integrationInfoString: 'valoreClientString',
            level2_integrationInfoAuthorizationString: 'valoreClientLeve2String',
            integrationInfoAuthorizationBoolean: true,
            integrationInfoAuthorizationList0: 'val1',
            integrationInfoAuthorizationList: [ 'val1' , 'val2' ],
            optional_integrationInfoAuthorizationString: 'valoreClientString',
            optional_integrationInfoAuthorizationListTest: [ '666' , '999' ],
            optional_complex_integrationInfoAuthorization: {
               clientClaimLeve3String: 'valoreClientLeve3String', 
               clientClaimLeve3Boolean: false, 
               clientClaimLeve3Int: 12467, 
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: 'valoreClientLeve4String', 
                  clientClaimLeve4ListInt: [ 33 , 1234 ], 
                  clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                  clientClaimLeve4ListString: [ 'val1' , 'val2']
               }
           }
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
            integrationInfoString: 'valoreClientString',
            level2_integrationInfoIntegrityString: 'valoreClientLeve2String',
            complex_integrationInfoIntegrityListTest1: [ '666' , '999' ],
            complex_integrationInfoIntegrityListTest2: [ '666' , '999' ],
            complex_integrationInfoIntegrity: {
              clientClaimLeve3String: 'valoreClientLeve3String', 
              clientClaimLeve3Boolean: false, 
              clientClaimLeve3Int: 12467, 
              clientClaimLeve4Complex: {
                clientClaimLeve4String: 'valoreClientLeve4String', 
                clientClaimLeve4ListInt: [ 33 , 1234 ], 
                clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                clientClaimLeve4ListString: [ 'val1' , 'val2' ]
              }
            },
            optional_integrationInfoIntegrityBoolean: true,
            optional_integrationInfoIntegrityLong: 2.147483747E9,
            optional_integrationInfoIntegrityDouble: 1399.56
        }
    })
    """
    * call checkToken ({token: requestHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0], "AGID")
    
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

    * match client_token_authorization.payload.signed_headers == "#notpresent"
    * match client_token_integrity.payload.signed_headers == "#notpresent"

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    

    
    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            integrationInfoString: 'valoreClientString'
        }
    })
    """
    * call checkToken ({token: responseHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_integrity = decodeToken(responseHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0], "AGID")
    
    * match server_token_integrity.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['Authorization'] == '#notpresent'
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['CustomTestSuiteDoppiSoloRichiesta-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar03-custom-doppi-header-assenza-header-integrity-richiesta')

    * remove requestHeaders['CustomTestSuiteDoppi-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/idar03-custom-doppi-header-assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    * remove responseHeaders['CustomTestSuiteDoppi-JWT-Signature']


Scenario: isTest('idar03-custom-doppi-header-assenza-header-integrity-richiesta-metodo-get-senza-payload') ||
		isTest('idar03-custom-doppi-header-assenza-header-integrity-richiesta-metodo-delete-senza-payload')

    * def requestUri = '/resources/22/M/customAlways'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/idar03-custom-doppi-header-assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-get-senza-payload') ||
		isTest('idar03-custom-doppi-header-assenza-header-integrity-risposta-metodo-delete-senza-payload')
    
    * def requestUri = '/resources/22/M'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')

Scenario: isTest('idar03-custom-doppi-header-get-without-custom')

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            integrationInfoString: 'valoreClientString',
            level2_integrationInfoAuthorizationString: 'valoreClientLeve2String',
            integrationInfoAuthorizationBoolean: true,
            integrationInfoAuthorizationList0: 'val1',
            integrationInfoAuthorizationList: [ 'val1' , 'val2' ],
            optional_integrationInfoAuthorizationString: 'valoreClientString',
            optional_integrationInfoAuthorizationListTest: [ '666' , '999' ],
            optional_complex_integrationInfoAuthorization: {
               clientClaimLeve3String: 'valoreClientLeve3String', 
               clientClaimLeve3Boolean: false, 
               clientClaimLeve3Int: 12467, 
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: 'valoreClientLeve4String', 
                  clientClaimLeve4ListInt: [ 33 , 1234 ], 
                  clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                  clientClaimLeve4ListString: [ 'val1' , 'val2']
               }
           }
        }
    })
    """
    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    
    * match requestHeaders['CustomTestSuiteDoppi-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    
    * match client_token_authorization.payload.signed_headers == "#notpresent"


    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicati/v1')
    


    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicati/v1',
            integrationInfoString: 'valoreClientString',
            level2_integrationInfoAuthorizationString: 'valoreClientLeve2String',
            integrationInfoAuthorizationBoolean: true,
            integrationInfoAuthorizationList0: 'val1',
            integrationInfoAuthorizationList: [ 'val1' , 'val2' ],
            optional_integrationInfoAuthorizationString: 'valoreClientString',
            optional_integrationInfoAuthorizationListTest: [ '666' , '999' ],
            optional_complex_integrationInfoAuthorization: {
               clientClaimLeve3String: 'valoreClientLeve3String', 
               clientClaimLeve3Boolean: false, 
               clientClaimLeve3Int: 12467, 
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: 'valoreClientLeve4String', 
                  clientClaimLeve4ListInt: [ 33 , 1234 ], 
                  clientClaimLeve4ListStringInt: [ '666' , '999' ], 
                  clientClaimLeve4ListString: [ 'val1' , 'val2']
               }
           }
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })
    
    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    
    * match server_token_authorization.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['CustomTestSuiteDoppi-JWT-Signature'] == '#notpresent'

    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('idar03-custom-doppi-header-multipart')

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: 'ExampleClient1' },
        payload: { 
            aud: 'testsuite',
            client_id: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            iss: 'DemoSoggettoFruitore',
            sub: 'ApplicativoBlockingIDA01',
            apiRest: true,
            digest: '#string',
            digestBody: 'hcVH9WYC4R+GjoSDXIWcn+ewY1B7+hgZ7w2EJVR//mk=',
            digestAllegato1: '2v8wtsBZO3c0V0i4IKvwFmffKeea5ZG/drRGiXl39Gc=',
            digestAllegato2: 'KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA='
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
            apiRest: true,
            digest: '#string',
            digestBody: 'hcVH9WYC4R+GjoSDXIWcn+ewY1B7+hgZ7w2EJVR//mk=',
            digestAllegato1: 'SHA-256=62d7686b95ca8df384ce021b205ebb1dca6e23842f41ea6930e93c2a4a0b05e3',
            digestAllegato2: 'SHA-256=9c287afbddcad7c3bceb4a49fc6d246be236f04639c4eff7269504b3d9a801e3'
        }
    })
    """
    * call checkToken ({token: requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def client_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def client_token_integrity = decodeToken(requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], "AGID")
    
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

    * match client_token_authorization.payload.signed_headers == "#notpresent"
    * match client_token_integrity.payload.signed_headers == "#notpresent"

    * setRequestHeader("Content-Type","multipart/mixed; boundary=----=_Part_1_1678144365.1610454048429; type=text/xml")
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1')
    


    * def server_token_authorization_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1',
            apiRestMultipart: true,
            response_digest: '#string',
            response_digestBody: 'hcVH9WYC4R+GjoSDXIWcn+ewY1B7+hgZ7w2EJVR//mk=',
            response_digestAllegato1: '2v8wtsBZO3c0V0i4IKvwFmffKeea5ZG/drRGiXl39Gc=',
            response_digestAllegato2: 'KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA='
        }
    })
    """
    * call checkToken ({token: responseHeaders['Authorization'][0], match_to: server_token_authorization_match, kind: "Bearer"  })
    
    # Il response digest del secondo token viene diverso poichè c'è una newLine differente tra il messaggio trattato la prima volta e la seconda volta, credo per via del mock
    * def server_token_integrity_match =
    """
    ({
        header: { kid: 'ExampleServer'},
        payload: {
            aud: 'DemoSoggettoFruitore/ApplicativoBlockingIDA01',
            client_id: 'RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1',
            iss: 'DemoSoggettoErogatore',
            sub: 'RestBlockingIDAR03CustomHeaderDuplicatiMultipart/v1',
            apiRestMultipart: true,
            response_digest: '#string',
            response_digestBody: 'hcVH9WYC4R+GjoSDXIWcn+ewY1B7+hgZ7w2EJVR//mk=',
            response_digestAllegato1: 'SHA-256=62d7686b95ca8df384ce021b205ebb1dca6e23842f41ea6930e93c2a4a0b05e3',
            response_digestAllegato2: 'SHA-256=9c287afbddcad7c3bceb4a49fc6d246be236f04639c4eff7269504b3d9a801e3'
        }
    })
    """
    * call checkToken ({token: responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], match_to: server_token_integrity_match, kind: "AGID"  })

    * def server_token_authorization = decodeToken(responseHeaders['Authorization'][0], "Bearer")
    * def server_token_integrity = decodeToken(responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0], "AGID")
    
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

    * match server_token_authorization.payload.signed_headers == "#notpresent"
    * match server_token_integrity.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['CustomTestSuiteDoppi-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Authorization-Token': responseHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['CustomTestSuiteDoppi-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)










########################
#       IDAR04         #
########################

Scenario: isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingKeyPair')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-jwk-ApplicativoBlockingKeyPair')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('connettivita-base-idar04-keypair-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingKeyPair')) {
      tipoTest = 'KeyPair'
    }
    """
    * eval
    """
    if (isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingKeyPair')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingIDA01') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingJWK') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingJWK')) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      subExpected = 'ApplicativoBlockingJWK'
    }
    """
    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-keypair-ApplicativoBlockingKeyPair') ||
		isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingKeyPair')) {
      kidExpected = 'KID-ApplicativoBlockingKeyPair'
      clientIdExpected = 'DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair'
      subExpected = 'ApplicativoBlockingKeyPair'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar04testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01ExampleClient2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01ExampleClient2')) {
      tipoTest = 'JWK'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('connettivita-base-idar04-jwk-ApplicativoBlockingIDA01ExampleClient2')) {
      kidExpected = 'ExampleClient2'
      clientIdExpected = 'http://client2'
      subExpected = 'ApplicativoBlockingIDA01ExampleClient2'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * match responseStatus == 401
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/kid-not-authorized.json')
    * match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * def result = get_diagnostici(tid) 

    * def errorExpected = 'Certificato, corrispondente al kid \''+kidExpected+'\', non trovato nel TrustStore dei certificati'
    * match result[0].MESSAGGIO contains errorExpected

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Transaction-ID': tid,
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

    * def responseStatus = 200



Scenario: isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01ExampleClient2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01ExampleClient2')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('connettivita-base-idar04-pdnd-ApplicativoBlockingIDA01ExampleClient2')) {
      kidExpected = 'ExampleClient2'
      clientIdExpected = 'http://client2'
      subExpected = 'ApplicativoBlockingIDA01ExampleClient2'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * match responseStatus == 401
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/kid-not-authorized.json')
    * match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * def result = get_diagnostici(tid) 

    * def errorExpected = 'Retrieve remote key \''+kidExpected+'\' failed: Retrieve external resource \'http://localhost:8080/govway/rest/in/DemoSoggettoErogatore/simulazione-api-pdnd/v1/keys/'+kidExpected+'\' failed: (http code: 500)'
    * match result[0].MESSAGGIO contains errorExpected

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Transaction-ID': tid,
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

    * def responseStatus = 200


Scenario: isTest('manomissione-token-richiesta-idar04-jwk')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-token-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * set requestHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(requestHeaders['Agid-JWT-Signature'][0])
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('manomissione-token-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-token-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * set requestHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(requestHeaders['Agid-JWT-Signature'][0])
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request-pdnd.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('manomissione-token-risposta-idar04-jwk')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-token-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * set responseHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(responseHeaders['Agid-JWT-Signature'][0])

Scenario: isTest('manomissione-token-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-token-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * set responseHeaders['Agid-JWT-Signature'][0] = tamper_token_agid(responseHeaders['Agid-JWT-Signature'][0])

Scenario: isTest('manomissione-payload-richiesta-idar04-jwk') ||
	isTest('manomissione-payload-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-payload-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('manomissione-payload-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def c = request
    * set c.nuovo_campo = "pippa"

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('manomissione-payload-risposta-idar04-jwk') ||
	isTest('manomissione-payload-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-payload-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('manomissione-payload-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 200

    * set response.nuovo_campo = "pippa"


Scenario: isTest('manomissione-payload-richiesta-vuota-idar04-jwk')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-payload-richiesta-vuota-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    # nota: sull'erogazione c'è un handler che riconosce che il messaggio è rimasto {} e lo imposta a null. Da karate non ci sono riuscito
    * remove request.a
    * remove request.b
    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'-PayloadVuoto/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta-vuota.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-payload-risposta-vuota-idar04-jwk')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-payload-risposta-vuota-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * karate.proceed(govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 200

    # nota: sulla fruizione c'è un handler che riconosce che il messaggio è rimasto {} e lo imposta a null. Da karate non ci sono riuscito
    * remove response.c



Scenario: isTest('manomissione-header-http-firmati-richiesta-idar04-jwk') ||
		isTest('manomissione-header-http-firmati-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-header-http-firmati-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('manomissione-header-http-firmati-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * setRequestHeader("IDAR04TestHeader","tampered_content")
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/manomissione-header-http-firmati-richiesta-idar04.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('manomissione-header-http-firmati-risposta-idar04-jwk') ||
		isTest('manomissione-header-http-firmati-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('manomissione-header-http-firmati-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('manomissione-header-http-firmati-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * match responseStatus == 200
    * set responseHeaders['IDAR04TestHeader'][0] = 'tampered_content'




Scenario: isTest('assenza-header-integrity-richiesta-idar04-jwk') ||
		isTest('assenza-header-integrity-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('assenza-header-integrity-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('assenza-header-integrity-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * remove requestHeaders['Agid-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('assenza-header-integrity-risposta-idar04-jwk') ||
		isTest('assenza-header-integrity-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('assenza-header-integrity-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('assenza-header-integrity-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * remove responseHeaders['Agid-JWT-Signature']







Scenario: isTest('assenza-header-digest-richiesta-idar04-jwk') ||
		isTest('assenza-header-digest-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('assenza-header-digest-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('assenza-header-digest-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * remove requestHeaders['Digest']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/assenza-header-digest-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('assenza-header-digest-risposta-idar04-jwk') ||
		isTest('assenza-header-digest-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('assenza-header-digest-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('assenza-header-digest-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    * remove responseHeaders['Digest']


Scenario: isTest('response-without-payload-idar04-jwk') ||
		isTest('response-without-payload-idar04-pdnd')

   * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('response-without-payload-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('response-without-payload-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('response-without-payload-idar04-jwk') ||
		isTest('response-without-payload-idar04-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { idar04testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

  
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('response-without-payload-idar04-tampered-header-jwk') ||
		isTest('response-without-payload-idar04-tampered-header-pdnd')

   * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('response-without-payload-idar04-tampered-header-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('response-without-payload-idar04-tampered-header-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('response-without-payload-idar04-tampered-header-jwk') ||
		isTest('response-without-payload-idar04-tampered-header-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { idar04testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null,
        'IDAR04TestHeader': 'tampered_header'
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)





Scenario: isTest('response-without-payload-idar04-digest-richiesta-jwk') ||
		isTest('response-without-payload-idar04-digest-richiesta-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('response-without-payload-idar04-digest-richiesta-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('response-without-payload-idar04-digest-richiesta-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('response-without-payload-idar04-digest-richiesta-jwk') ||
		isTest('response-without-payload-idar04-digest-richiesta-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')


    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'Content-Type': null
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('idar04-token-richiesta-jwk') ||
		isTest('idar04-token-richiesta-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('idar04-token-richiesta-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('idar04-token-richiesta-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('idar04-token-richiesta-jwk') ||
		isTest('idar04-token-richiesta-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')


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



Scenario: isTest('idar04-token-risposta-jwk') ||
		isTest('idar04-token-risposta-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('idar04-token-risposta-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('idar04-token-risposta-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['Digest'][0] == '#notpresent'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    #* def clientIdExpected = 'anonymous'
    * def clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar04testheader: 'TestHeaderResponse' }
            ]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })   

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Message-ID': responseHeaders['GovWay-Message-ID'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('authorization-richiesta-integrity-risposta-idar04-jwk') ||
		isTest('authorization-richiesta-integrity-risposta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('authorization-richiesta-integrity-risposta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('authorization-richiesta-integrity-risposta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('authorization-richiesta-integrity-risposta-idar04-jwk') ||
		isTest('authorization-richiesta-integrity-risposta-idar04-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_match, kind: "Bearer" })
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
    * match requestHeaders['Digest'] == '#notpresent'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    
    * match responseHeaders['Authorization'] == '#notpresent' 
    
    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar04testheader: 'TestHeaderResponse' }
            ]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def server_token_integrity = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    
    * def response_digest = get server_token_integrity $.payload.signed_headers..digest
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Server-Integrity-Token': responseHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('solo-authorization-richiesta-idar04-jwk') ||
		isTest('solo-authorization-richiesta-idar04-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('solo-authorization-richiesta-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('solo-authorization-richiesta-idar04-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('solo-authorization-richiesta-idar04-jwk') ||
		isTest('solo-authorization-richiesta-idar04-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_match, kind: "Bearer" })
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent' 
    * match requestHeaders['Digest'] == '#notpresent'

    * request ''

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    * match responseHeaders['Authorization'] == '#notpresent' 
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)  



Scenario: isTest('audience-differenti-ok-idar04-jwk') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audience-differenti-ok-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'
    * def authorizationAudExpected = 'Different-RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('audience-differenti-ok-idar04-jwk')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: authorizationAudExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')
    
    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' },
                { idar04testheader: 'TestHeaderResponse' }
            ],
            request_digest: request_digest[0]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)



Scenario: isTest('audience-differenti-ko-auth-claim-deny-idar04-jwk') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audience-differenti-ko-auth-claim-deny-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'
    * def authorizationAudExpected = 'Wrong-RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('audience-differenti-ko-auth-claim-deny-idar04-jwk')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: authorizationAudExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    * match responseStatus == 403
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-authorization-not-authorized-by-auth-claims.json')
    * match header GovWay-Transaction-ErrorType == 'AuthorizationTokenDeny'

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * def result = get_diagnostici(tid) 

    * def errorExpected = '(Token claim \'aud\' with unexpected value) La richiesta presenta un token non sufficiente per fruire del servizio richiesto'
    * match result[0].MESSAGGIO contains errorExpected




Scenario: isTest('audience-differenti-ko-idar04-jwk') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audience-differenti-ko-idar04-jwk')) {
      tipoTest = 'JWK'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04-'+tipoTest+'/v1'
    * def authorizationAudExpected = 'Wrong-RestBlockingIDAR04-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('audience-differenti-ko-idar04-jwk')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' },
                { idar04testheader: 'TestHeaderRequest' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: authorizationAudExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04-'+tipoTest+'/v1')

    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-authorization-not-authorized.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * def result = get_diagnostici(tid) 

    * def errorExpected = '[Header \'Authorization\'] Token contenente un claim \'aud\' non valido'
    * match result[0].MESSAGGIO contains errorExpected










########################
#       IDAR0402         #
########################

Scenario: isTest('connettivita-base-idar0402-pdnd')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar0402-pdnd')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR0402-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('connettivita-base-idar0402-pdnd')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01-CredenzialePrincipal'
    }
    """

    * def kidFruizioneExpected = 'N.D.'
    * def audFruizioneExpected = 'N.D.'
    * def clientIdFruizioneExpected = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar0402-pdnd')) {
      kidFruizioneExpected = 'de606068-01cb-49a5-824d-fb171b5d5ae4'
      audFruizioneExpected = 'RestBlockingIDAR0402-PDND/v1'
      clientIdFruizioneExpected = 'RestBlockingIDAR0402-PDND/v1'
    }
    """


    * def client_token_match = 
    """
    ({
        header: { kid: kidFruizioneExpected },
        payload: { 
            aud: audFruizioneExpected,
            client_id: clientIdFruizioneExpected,
            iss: 'DemoSoggettoFruitore',
            sub: audFruizioneExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0402-'+tipoTest+'/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('connettivita-base-idar0402-keypair')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar0402-keypair')) {
      tipoTest = 'KeyPair'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR0402-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('connettivita-base-idar0402-keypair')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01-CredenzialePrincipal'
    }
    """

    * def kidFruizioneExpected = 'N.D.'
    * def audFruizioneExpected = 'N.D.'
    * def clientIdFruizioneExpected = 'N.D.'
    * eval
    """
    if (isTest('connettivita-base-idar0402-keypair')) {
      kidFruizioneExpected = 'KID-ApplicativoBlockingKeyPair'
      audFruizioneExpected = 'RestBlockingIDAR0402-KeyPair/v1'
      clientIdFruizioneExpected = 'DemoSoggettoFruitore/KeyPair/ApplicativoBlockingKeyPair'
    }
    """


    * def client_token_match = 
    """
    ({
        header: { kid: kidFruizioneExpected },
        payload: { 
            aud: audFruizioneExpected,
            client_id: clientIdFruizioneExpected,
            iss: 'DemoSoggettoFruitore',
            sub: audFruizioneExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })

    * def request_token_authorization = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * def request_id = get request_token_authorization $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0402-'+tipoTest+'/v1')
    
    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def request_token = decodeToken(requestHeaders['Agid-JWT-Signature'][0], "AGID")
    * def request_digest = get request_token $.payload.signed_headers..digest

    * match requestHeaders['Digest'][0] == request_digest[0]

    * karate.log("Ret: ", responseHeaders)

    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json' }
            ]
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['Agid-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['Agid-JWT-Signature'][0], "AGID")
    * def response_digest = get response_token $.payload.signed_headers..digest
    
    * match responseHeaders['Digest'][0] == response_digest[0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)





Scenario: isTest('riutilizzo-token-idar0402-pdnd') ||
		isTest('riutilizzo-token-idar0402-keypair')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('riutilizzo-token-idar0402-pdnd')) {
      tipoTest = 'PDND'
    }
    """
    * eval
    """
    if (isTest('riutilizzo-token-idar0402-keypair')) {
      tipoTest = 'KeyPair'
    }
    """

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR0402-'+tipoTest+'/v1')
    
    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Agid-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Agid-Token': responseHeaders['Agid-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('riutilizzo-token-risposta-idar0402-pdnd') ||
		isTest('riutilizzo-token-risposta-idar0402-keypair')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('riutilizzo-token-risposta-idar0402-pdnd')) {
      tipoTest = 'PDND'
    }
    """
    * eval
    """
    if (isTest('riutilizzo-token-risposta-idar0402-keypair')) {
      tipoTest = 'KeyPair'
    }
    """

    * def responseHeaders =  ({ 'Agid-JWT-Signature': getRequestHeader("GovWay-TestSuite-Server-Token"), 'Digest': getRequestHeader("GovWay-TestSuite-Digest") })
    * def responseStatus = 200
    * def response = read('classpath:test/rest/sicurezza-messaggio/response.json')






########################
#       IDAR04 (CUSTOM)#
########################

Scenario: isTest('idar04-custom-header-pdnd') ||
		isTest('idar04-custom-header-pdnd-get-with-custom')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('idar04-custom-header-pdnd') ||
		isTest('idar04-custom-header-pdnd-get-with-custom')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04Custom-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('idar04-custom-header-pdnd') ||
		isTest('idar04-custom-header-pdnd-get-with-custom')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * def client_token_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    integrationInfoString: 'valoreClientString',
            integrationInfoInt: 13,
            integrationInfoBoolean: true,
	    integrationInfoFloat: 24.2,
            integrationInfoList0: 'val1',
            integrationInfoList1: 'val2',
	    integrationInfoList: ['val1','val2'],
	    level2_integrationInfoString: 'valoreClientLeve2String',
            level2_integrationInfoInt: 99,
            level2_integrationInfoBoolean: false,
	    level2_integrationInfoFloat: 102.29,
            level2_integrationInfoList0: 33,
            level2_integrationInfoList1: 1234,
	    level2_integrationInfoList: [33,1234],
	    level3_integrationInfoString: 'valoreClientLeve3String',
            level3_integrationInfoInt: 12467,
            level3_integrationInfoBoolean: false,
	    level3_claims_integrationInfoString: 'valoreClientLeve3String',
            level3_claims_integrationInfoInt: 12467,
            level3_claims_integrationInfoBoolean: false,
	    complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            },
            complex_integrationInfoListTest1: [ '666' , '999' ],
            complex_integrationInfoListTest2: [ '666' , '999' ],
            optional_integrationInfoString: 'valoreClientString',
            optional_integrationInfoBoolean: true,
            optional_integrationInfoLong: 2147483747,
            optional_integrationInfoDouble: 1399.56,
            optional_integrationInfoListTest: [ '666' , '999' ],
            optional_complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            }
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['CustomTestSuite-JWT-Signature'][0], match_to: client_token_match, kind: "AGID" })
   
    * def request_token = decodeToken(requestHeaders['CustomTestSuite-JWT-Signature'][0], "AGID")
    * karate.log("Ret: ", request_token)
    * match request_token.payload.signed_headers == "#notpresent"
    
    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#present'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['GovWay-Integration'] == '#notpresent'

    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-'+tipoTest+'/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id


    * karate.log("Ret: ", responseHeaders)


    * def server_token_match =
    """
    ({
        header: { kid: 'KID-ExampleServer'},
        payload: {
            aud: clientIdExpected,
            client_id: 'ExampleServer'+tipoTest,
            iss: 'DemoSoggettoErogatore',
            sub: audExpected,
	    integrationInfoString: 'valoreClientString',
            integrationInfoInt: 13,
            integrationInfoBoolean: true,
	    integrationInfoFloat: 24.2,
            integrationInfoList0: 'val1',
            integrationInfoList1: 'val2',
	    integrationInfoList: ['val1','val2'],
	    level2_integrationInfoString: 'valoreClientLeve2String',
            level2_integrationInfoInt: 99,
            level2_integrationInfoBoolean: false,
	    level2_integrationInfoFloat: 102.29,
            level2_integrationInfoList0: 33,
            level2_integrationInfoList1: 1234,
	    level2_integrationInfoList: [33,1234],
	    level3_integrationInfoString: 'valoreClientLeve3String',
            level3_integrationInfoInt: 12467,
            level3_integrationInfoBoolean: false,
	    level3_claims_integrationInfoString: 'valoreClientLeve3String',
            level3_claims_integrationInfoInt: 12467,
            level3_claims_integrationInfoBoolean: false,
	    complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            },
            complex_integrationInfoListTest1: [ '666' , '999' ],
            complex_integrationInfoListTest2: [ '666' , '999' ],
            optional_integrationInfoString: 'valoreClientString',
            optional_integrationInfoBoolean: true,
            optional_integrationInfoLong: 2147483747,
            optional_integrationInfoDouble: 1399.56,
            optional_integrationInfoListTest: [ '666' , '999' ],
            optional_complex_integrationInfo: {
               clientClaimLeve3String: 'valoreClientLeve3String',
               clientClaimLeve3Boolean: false,
               clientClaimLeve3Int: 12467,
               clientClaimLeve4Complex: {
                  clientClaimLeve4String: "valoreClientLeve4String",
                  clientClaimLeve4ListInt: [ 33, 1234 ],
                  clientClaimLeve4ListStringInt: [ '666', '999' ],
                  clientClaimLeve4ListString: [ 'val1', 'val2' ]
               }
            }
        }
    })
    """
    * call checkTokenKid ({token: responseHeaders['CustomTestSuite-JWT-Signature'][0], match_to: server_token_match, kind: "AGID"  })

    * def response_token = decodeToken(responseHeaders['CustomTestSuite-JWT-Signature'][0], "AGID")
    * match response_token.payload.signed_headers == "#notpresent"
    
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Authorization'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match responseHeaders['GovWay-Integration'] == '#notpresent'

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Token': requestHeaders['CustomTestSuite-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Server-Token': responseHeaders['CustomTestSuite-JWT-Signature'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('idar04-custom-header-pdnd-assenza-header-integrity-richiesta')

    * remove requestHeaders['CustomTestSuite-JWT-Signature']
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-PDND/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/idar03-custom-single-header-assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('idar04-custom-header-pdnd-assenza-header-integrity-risposta')
    
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-PDND/v1')
    * remove responseHeaders['CustomTestSuite-JWT-Signature']


Scenario: isTest('idar04-custom-header-pdnd-assenza-header-integrity-richiesta-metodo-get-senza-payload')

    * def requestUri = '/resources/22/M/customAlways'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-PDND/v1')
    
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/idar03-custom-single-header-assenza-header-integrity-richiesta.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('idar04-custom-header-pdnd-assenza-header-integrity-risposta-metodo-get-senza-payload')
    
    * def requestUri = '/resources/22/M'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-PDND/v1')


Scenario: isTest('idar04-custom-header-pdnd-get-without-custom')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('idar04-custom-header-pdnd-get-without-custom')) {
      tipoTest = 'PDND'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def subExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def audExpected = 'RestBlockingIDAR04Custom-'+tipoTest+'/v1'

    * eval
    """
    if (isTest('idar04-custom-header-pdnd-get-without-custom')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: 'purposeId-'+subExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * match requestHeaders['CustomTestSuite-JWT-Signature'] == '#notpresent'
       
    * match requestHeaders['Digest'] == '#notpresent'
    * match requestHeaders['Authorization'] == '#present'
    * match requestHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match requestHeaders['GovWay-Integration'] == '#notpresent'

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingIDAR04Custom-'+tipoTest+'/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * karate.log("Ret: ", responseHeaders)

    * match responseHeaders['CustomTestSuite-JWT-Signature'] == '#notpresent'
   
    * match responseHeaders['Digest'] == '#notpresent'
    * match responseHeaders['Authorization'] == '#notpresent'
    * match responseHeaders['Agid-JWT-Signature'] == '#notpresent'
    * match responseHeaders['GovWay-Integration'] == '#notpresent'

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)








########################
#       AUDIT REST     #
########################



Scenario: isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-02') ||
		isTest('audit-rest-jwk-mixed-01') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-01') || 
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') || 
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-02') ||
		isTest('audit-rest-jwk-mixed-01') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-01') || 
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2') ) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-customtrace-customforward-01') ) {
      tipoTest = 'JWK-CustomTrace-CustomForward'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') ||
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-notrace-noforward-default-01')) {
      audExpected = 'RestBlockingAuditRest01Optional-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2') ) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-notrace-noforward-default-02')) {
      audExpected = 'RestBlockingAuditRest02Optional-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-01')) {
      audExpected = 'RestBlockingAuditRest01TokenAuditOptional-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditClaimNotCacheable-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2') ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditClaimOptionalNotCacheable-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2')) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      issExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2')) {
      subExpected = 'ApplicativoBlockingJWK-CredenzialePrincipal'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2') ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingJWK'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')  ) {
      dnonceExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2')) {
      dnonceExpected = '#number'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-jwk-mixed-01') ||
		isTest('audit-rest-jwk-custom-01') ||
		isTest('audit-rest-jwk-notrace-noforward-default-01') ||
		isTest('audit-rest-jwk-customtrace-customforward-01') ||
		isTest('audit-rest-jwk-token-optional-01') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2')  ) {
      digestExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02') || 
		isTest('audit-rest-jwk-mixed-02') ||
		isTest('audit-rest-jwk-custom-02') ||
		isTest('audit-rest-jwk-notrace-noforward-default-02') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-utente2')) {
      digestExpected = { alg: 'SHA256', value: '#string' }
    }
    """

    * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
	isTest('audit-rest-jwk-02') ||
	isTest('audit-rest-jwk-custom-01') || 
	isTest('audit-rest-jwk-custom-02') ||
	isTest('audit-rest-jwk-notrace-noforward-default-01') ||
	isTest('audit-rest-jwk-notrace-noforward-default-02') ||
	isTest('audit-rest-jwk-customtrace-customforward-01') ||
	isTest('audit-rest-jwk-token-optional-01')  ) {
      userIdToken = 'utente-token'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
	isTest('audit-rest-jwk-02-verifica-cache-utente1')  ) {
      userIdToken = 'utente-token-test-cache'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
	isTest('audit-rest-jwk-02-verifica-cache-utente2')  ) {
      userIdToken = 'utente-token-differente-test-cache'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
	isTest('audit-rest-jwk-02') ||
	isTest('audit-rest-jwk-custom-01') || 
	isTest('audit-rest-jwk-custom-02') ||
	isTest('audit-rest-jwk-notrace-noforward-default-01') ||
	isTest('audit-rest-jwk-notrace-noforward-default-02') ||
	isTest('audit-rest-jwk-customtrace-customforward-01') ||
	isTest('audit-rest-jwk-token-optional-01') ||
	isTest('audit-rest-jwk-01-verifica-cache-utente1') ||
	isTest('audit-rest-jwk-02-verifica-cache-utente1') ||
	isTest('audit-rest-jwk-01-verifica-cache-utente2') ||
	isTest('audit-rest-jwk-02-verifica-cache-utente2')  ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente1') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    claim1: 'valore-claim1-required-test-cache', 
            claim2: 'valore-claim2-required-test-cache',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-not-cacheable-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-utente2') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    claim1: 'valore-claim1-differente-required-test-cache', 
            claim2: 'valore-claim2-differente-required-test-cache',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente1') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    claim1: '#notpresent', 
            claim2: 'valore-claim2-required-test-cache',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-elemento-optional-not-cacheable-non-usato-utente2') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    claim1: '#notpresent', 
            claim2: 'valore-claim2-differente-required-test-cache',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * eval
    """
    if (isTest('audit-rest-jwk-mixed-01') || 
	isTest('audit-rest-jwk-mixed-02')) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: 'utente-token', 
            userLocation: 'ip-utente-token', 
            LoA: '#notpresent',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2') ) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      issExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')) {
      subExpected = 'ApplicativoBlockingJWK-CredenzialePrincipal'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingJWK'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2')  ) {
      dnonceExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')) {
      dnonceExpected = '#number'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2')  ) {
      digestExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
		isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')) {
      digestExpected = { alg: 'SHA256', value: '#string' }
    }
    """

    * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
	isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1')  ) {
      userIdToken = 'utente-token-test-cache'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ||
	isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')  ) {
      userIdToken = 'utente-token-differente-test-cache'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-integrity-utente1') ||
	isTest('audit-rest-jwk-02-verifica-cache-integrity-utente1') ||
	isTest('audit-rest-jwk-01-verifica-cache-integrity-utente2') ||
	isTest('audit-rest-jwk-02-verifica-cache-integrity-utente2')  ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2') ) {
      tipoTest = 'X509'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audAuditExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2') ) {
      audAuditExpected = 'RestBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      kidExpected = 'ExampleClient1'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = '#notpresent'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      purposeIdExpected = '#notpresent'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      dnonceExpected = '#notpresent'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      digestExpected = '#notpresent'
    }
    """

    * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1')  ) {
      userIdToken = 'utente-token-test-cache'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
      userIdToken = 'utente-token-differente-test-cache'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-utente2') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-id-auth-filtro-duplicati-utente2')  ) {
    client_token_audit_match = ({
        header: { 
		kid: '#notpresent',
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audAuditExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    

    * def client_token_authorization_match = 
    """
    ({
        header: { 
		kid: kidExpected,
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkToken ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)





Scenario: isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2') ) {
      tipoTest = 'X509'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audAuditExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2') ) {
      audAuditExpected = 'RestBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      kidExpected = 'ExampleClient1'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = '#notpresent'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      purposeIdExpected = '#notpresent'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      dnonceExpected = '#notpresent'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
		isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      digestExpected = '#notpresent'
    }
    """

   * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1')  ) {
      userIdToken = 'utente-token-test-cache'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
      userIdToken = 'utente-token-differente-test-cache'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente1') ||
	  isTest('audit-rest-jwk-01-verifica-cache-locale-integrity-utente2')  ) {
    client_token_audit_match = ({
        header: { 
		kid: '#notpresent',
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audAuditExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """
    

    * def client_token_authorization_match = 
    """
    ({
        header: { 
		kid: kidExpected,
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkToken ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0],
        'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)






Scenario: isTest('audit-rest-jwk-0401') || 
		isTest('audit-rest-jwk-0402') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401') || 
		isTest('audit-rest-jwk-0402')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      issExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      subExpected = 'ApplicativoBlockingJWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      subExpected = 'ApplicativoBlockingIDA01-CredenzialePrincipal'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingJWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      dnonceExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      dnonceExpected = '#number'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      digestExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      digestExpected = { alg: 'SHA256', value: '#string' }
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401') || 
	isTest('audit-rest-jwk-0402')) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: 'utente-token', 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def audAuthorizationExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      audAuthorizationExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      audAuthorizationExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audAuthorizationExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * def subExpectedIntegrity = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401')) {
      subExpectedIntegrity = 'ApplicativoBlockingJWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-0402')) {
      subExpectedIntegrity = 'ApplicativoBlockingIDA01'
    }
    """


    * def client_token_integrity_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpectedIntegrity,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * karate.log("Ret (idTransazione: "+requestHeaders['GovWay-Transaction-ID'][0]+"): ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], id_transazione: requestHeaders['GovWay-Transaction-ID'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-Signature'][0], id_transazione: requestHeaders['GovWay-Transaction-ID'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], id_transazione: requestHeaders['GovWay-Transaction-ID'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
	'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('audit-rest-x509-01')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      tipoTest = 'X509'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audExpectedAUDIT = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      audExpectedAUDIT = 'RestBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      kidExpected = 'ExampleClient1'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-x509-02')) {
      subExpected = 'ApplicativoBlockingIDA01-CredenzialePrincipal'
    }
    """


    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
    client_token_audit_match = ({
        header: { 
		kid: '#notpresent',
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audExpectedAUDIT,
            client_id: '#notpresent',
            iss: '#notpresent',
            sub: '#notpresent',
	    userID: 'utente-token-ridefinito', 
            userLocation: 'ip-utente-token-ridefinito', 
            LoA: 'livello-autenticazione-utente-token-ridefinito',
	    dnonce: '#notpresent'
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: '#notpresent',
	    digest: '#notpresent'
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkToken ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)






Scenario: isTest('audit-rest-x509-0301')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      tipoTest = 'X509'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audExpectedAUDIT = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      audExpectedAUDIT = 'RestBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      kidExpected = 'ExampleClient1'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
    client_token_audit_match = ({
        header: { 
		kid: '#notpresent',
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audExpectedAUDIT,
            client_id: '#notpresent',
            iss: '#notpresent',
            sub: '#notpresent',
	    userID: 'utente-token-ridefinito', 
            userLocation: 'ip-utente-token-ridefinito', 
            LoA: 'livello-autenticazione-utente-token-ridefinito',
	    dnonce: '#notpresent'
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: '#notpresent',
	    digest: '#notpresent'
        }
    })
    """


    * def subExpectedIntegrity = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      subExpectedIntegrity = 'ApplicativoBlockingIDA01'
    }
    """

    * def client_token_integrity_match = 
    """
    ({
        header: { 
		kid: 'ExampleClient1',
	        x5c: '#present',
                x5u: '#notpresent',
               'x5t#S256': '#present'
	},
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpectedIntegrity,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """



    * karate.log("Ret: ", requestHeaders)

    * call checkToken ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * call checkToken ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
	'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)






Scenario: isTest('audit-rest-jwk-manomissione-firma-01') || 
		isTest('audit-rest-jwk-manomissione-firma-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-manomissione-firma-01') || 
		isTest('audit-rest-jwk-manomissione-firma-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-manomissione-firma-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-manomissione-firma-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * set requestHeaders['Agid-JWT-TrackingEvidence'][0] = tamper_token_audit(requestHeaders['Agid-JWT-TrackingEvidence'][0])
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-audit-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'




Scenario: isTest('audit-rest-jwk-manomissione-digest-authorization-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-manomissione-digest-authorization-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-manomissione-digest-authorization-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * def old_authz = requestHeaders['old-authorization'][0]
    * karate.log("old: ", old_authz);
    * set requestHeaders['Authorization'][0] = old_authz
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-audit-digest-in-authorization-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'






Scenario: isTest('audit-rest-jwk-eliminazione-digest-value-authorization-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-value-authorization-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-value-authorization-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-senza-value.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



Scenario: isTest('audit-rest-jwk-eliminazione-digest-alg-authorization-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-alg-authorization-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-alg-authorization-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-senza-alg.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



Scenario: isTest('audit-rest-jwk-eliminazione-digest-authorization-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-authorization-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-eliminazione-digest-authorization-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-digest-in-authorization-request-non-presente.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'




Scenario: isTest('audit-rest-jwk-kid-not-trusted-01') ||
		isTest('audit-rest-jwk-kid-not-trusted-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-kid-not-trusted-01') ||
		isTest('audit-rest-jwk-kid-not-trusted-02') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-kid-not-trusted-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-kid-not-trusted-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)

    * match responseStatus == 401
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/kid-not-authorized.json')
    * match header GovWay-Transaction-ErrorType == 'TokenAuthenticationFailed'

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * def result = get_diagnostici(tid) 

    * def errorExpected = 'Certificato, corrispondente al kid \'ExampleClient2\', non trovato nel TrustStore dei certificati'
    * match result[0].MESSAGGIO contains errorExpected

    * def responseStatus = 200


Scenario: isTest('audit-rest-jwk-audit-user-non-fornito-01') ||
		isTest('audit-rest-jwk-audit-user-non-fornito-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-01') ||
		isTest('audit-rest-jwk-audit-user-non-fornito-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-non-presente.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'




Scenario: isTest('audit-rest-jwk-audit-user-location-non-fornito-01') ||
		isTest('audit-rest-jwk-audit-user-location-non-fornito-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-location-non-fornito-01') ||
		isTest('audit-rest-jwk-audit-user-location-non-fornito-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-location-non-fornito-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audit-use-locationr-non-fornito-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-location-non-presente.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'






Scenario: isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-non-fornito-erogazione-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-non-presente-erogazione.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'




Scenario: isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audit-user-location-non-fornito-erogazione-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-user-location-non-presente-erogazione.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'





Scenario: isTest('audit-rest-jwk-audit-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-non-fornito-erogazione-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-non-fornito-erogazione-01') ||
		isTest('audit-rest-jwk-audit-non-fornito-erogazione-02')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audit-non-fornito-erogazione-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audit-non-fornito-erogazione-02')) {
      audExpected = 'RestBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * remove requestHeaders['Agid-JWT-TrackingEvidence']

    * karate.log("req: ", requestHeaders);

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-non-presente-erogazione.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'





Scenario: isTest('audit-rest-jwk-audience-errato-01') ||
	isTest('audit-rest-jwk-audience-errato-atteso-differente-01') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audience-errato-01')) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-audience-errato-atteso-differente-01')) {
      tipoTest = 'JWK-AudienceDifferente'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-audience-errato-01') ||
	isTest('audit-rest-jwk-audience-errato-atteso-differente-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/audit-audience-non-valida-erogazione.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'




Scenario: isTest('audit-rest-jwk-token-audit-scaduto-01') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-audit-scaduto-01')) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-audit-scaduto-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * def old_audit = requestHeaders['old-audit'][0]
    * karate.log("old: ", old_audit);
    * set requestHeaders['Agid-JWT-TrackingEvidence'][0] = old_audit

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/expired-audit-token.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('audit-rest-jwk-token-audit-iat-oldest-01') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-audit-iat-oldest-01')) {
      tipoTest = 'JWK-TTLShort'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-audit-iat-oldest-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * def old_audit = requestHeaders['old-audit'][0]
    * karate.log("old: ", old_audit);
    * set requestHeaders['Agid-JWT-TrackingEvidence'][0] = old_audit

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/token-audit-iat-oldest.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'



Scenario: isTest('audit-rest-jwk-criteri-autorizzativi-ok-01') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')) {
      dnonceExpected = '#notpresent'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01')) {
      digestExpected = '#notpresent'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ok-01') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: 'utente-token', 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+'RestBlockingAuditRest01-'+tipoTest+'-CheckAuthz/v1')
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('audit-rest-jwk-criteri-autorizzativi-ko-01') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ko-01')) {
      tipoTest = 'JWK-CheckAuthz'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-criteri-autorizzativi-ko-01')) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * karate.log("req: ", requestHeaders);

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 403
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/authorization-deny.json')
    * match header GovWay-Transaction-ErrorType == 'AuthorizationContentDeny'



Scenario: isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')) {
      audExpected = 'RestBlockingAuditRest01TokenAuditOptional-'+tipoTest+'/v1'
    }
    """

    * match requestHeaders['Agid-JWT-TrackingEvidence'] == "#notpresent"


    * def clientIdExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ||
		isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-optionalaudit')) {
      digestExpected = '#notpresent'
    }
    """


    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * def requestUri = isTest('audit-rest-jwk-token-optional-non-fornito-erogazione-01-noaudit') ? '/idar01/oauth-noaudit' : '/idar01/oauth'

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)








Scenario: isTest('audit-rest-jwk-purpose-id-uguali') ||
		isTest('audit-rest-jwk-purpose-id-differenti') ||
		isTest('audit-rest-jwk-purpose-id-non-presente-audit')

    * def tipoTest = 'N.D.'
    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-uguali') ||
		isTest('audit-rest-jwk-purpose-id-differenti') ||
		isTest('audit-rest-jwk-purpose-id-non-presente-audit')) {
      tipoTest = 'JWK-RecuperoInfoClient'
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
   
    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * def subExpected = 'N.D.'    
    * def purposeIdExpectedAuth = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-uguali') ||
		isTest('audit-rest-jwk-purpose-id-differenti') ||
		isTest('audit-rest-jwk-purpose-id-non-presente-audit')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      subExpected = 'ApplicativoBlockingIDA01-CredenzialePrincipal'
      purposeIdExpectedAuth = 'purposeId-ApplicativoBlockingIDA01'
    }
    """
        
    * def purposeIdExpectedAudit = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-uguali') ) {
      purposeIdExpectedAudit = 'purposeId-ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-differenti')) {
      purposeIdExpectedAudit = 'purposeId-ApplicativoBlockingIDA01-differente'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-non-presente-audit')) {
      purposeIdExpectedAudit = '#notpresent'
    }
    """

    * def dnonceExpected = '#notpresent'
   
    * def digestExpected = '#notpresent'
   
    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-purpose-id-uguali') ||
		isTest('audit-rest-jwk-purpose-id-differenti') ||
		isTest('audit-rest-jwk-purpose-id-non-presente-audit') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: 'utente-token', 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected,
	    purposeId: purposeIdExpectedAudit
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpectedAuth,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02') 

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02') ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditCustom-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def clientIdSubAuditExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02')) {
      clientIdSubAuditExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """

    * def typeINT = 'N.D.'
    * def typeBoolean = 'N.D.'
    * def typeStringRegexp = 'N.D.'
    * def typeINTRegexp = 'N.D.'
    * def typeListString = 'N.D.'
    * def typeListInt = 'N.D.'
    * def typeMixed1 = 'N.D.'
    * def typeMixed2 = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01')) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-02')) {
      typeINT = 99147483647
      typeBoolean = false
      typeStringRegexp = 'A'
      typeINTRegexp = 1
      typeListString = 'Valore4'
      typeListInt = 45
      typeMixed1 = 'AA'
      typeMixed2 = 22
    }
    """

    * def dnonceExpected = '#notpresent'
    * def digestExpected = '#notpresent'
    
    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-01') ||
		isTest('audit-rest-jwk-token-custom-02') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdSubAuditExpected,
            iss: '#notpresent',
            sub: clientIdSubAuditExpected,
	    typeINT: typeINT, 
            typeBoolean: typeBoolean, 
            typeStringRegexp: typeStringRegexp,
            typeINTRegexp: typeINTRegexp, 
            typeListString: typeListString, 
            typeListInt: typeListInt, 
            typeMixed1: typeMixed1, 
            typeMixed2: typeMixed2, 
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')  

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')   ) {
      tipoTest = 'JWK'
    }
    """

    * def forwardContextUrl = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')   ) {
      forwardContextUrl = 'RestBlockingAuditRest01TokenAuditCustom-'+tipoTest+'/v1'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')   ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditCustomSenzaValidazione-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def clientIdSubAuditExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')  ) {
      clientIdSubAuditExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')  ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """

    * def typeINT = 'N.D.'
    * def typeBoolean = 'N.D.'
    * def typeStringRegexp = 'N.D.'
    * def typeINTRegexp = 'N.D.'
    * def typeListString = 'N.D.'
    * def typeListInt = 'N.D.'
    * def typeMixed1 = 'N.D.'
    * def typeMixed2 = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE3a'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 0
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = -12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'ValoreInesistente2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 123
      typeMixed1 = 'ZZ'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'Z'
      typeMixed2 = 23456
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 2
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-08') ) {
      typeINT = 23
      typeBoolean = true
      typeStringRegexp = 'ABCDE'
      typeINTRegexp = 12
      typeListString = 'Valore2'
      typeListInt = 10.3
      typeMixed1 = 'ZZ'
      typeMixed2 = 234567
    }
    """


    * def dnonceExpected = '#notpresent'
    * def digestExpected = '#notpresent'
    
    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-04') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-05') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-06') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-07') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-08')   ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdSubAuditExpected,
            iss: '#notpresent',
            sub: clientIdSubAuditExpected,
	    typeINT: typeINT, 
            typeBoolean: typeBoolean, 
            typeStringRegexp: typeStringRegexp,
            typeINTRegexp: typeINTRegexp, 
            typeListString: typeListString, 
            typeListInt: typeListInt, 
            typeMixed1: typeMixed1, 
            typeMixed2: typeMixed2, 
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+forwardContextUrl)
    
    * def tid = responseHeaders['GovWay-Transaction-ID'][0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0],
        'GovWay-TestSuite-GovWay-Transaction-ID': tid
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)






Scenario: isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      tipoTest = 'JWK'
    }
    """

    * def forwardContextUrl = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      forwardContextUrl = 'RestBlockingAuditRest01TokenAuditCustomTypeString-'+tipoTest+'/v1'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditCustomTypeNotString-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def clientIdSubAuditExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      clientIdSubAuditExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """

    * def type = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ) {
      type = 22
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ) {
      type = 2.3
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ) {
      type = true
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04') ) {
      type = false
    }
    """


    * def dnonceExpected = '#notpresent'
    * def digestExpected = '#notpresent'
    
    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typenotstring-04')  ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdSubAuditExpected,
            iss: '#notpresent',
            sub: clientIdSubAuditExpected,
	    type: type, 
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+forwardContextUrl)
    
    * def tid = responseHeaders['GovWay-Transaction-ID'][0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0],
        'GovWay-TestSuite-GovWay-Transaction-ID': tid
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)




Scenario: isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      tipoTest = 'JWK'
    }
    """

    * def forwardContextUrl = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      forwardContextUrl = 'RestBlockingAuditRest01TokenAuditCustomTypeNotString-'+tipoTest+'/v1'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      audExpected = 'RestBlockingAuditRest01TokenAuditCustomTypeString-'+tipoTest+'/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """

    * def clientIdSubAuditExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      clientIdSubAuditExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """

    * def type = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ) {
      type = '22'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ) {
      type = '2.3'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ) {
      type = 'true'
    }
    """
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04') ) {
      type = 'false'
    }
    """


    * def dnonceExpected = '#notpresent'
    * def digestExpected = '#notpresent'
    
    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-01') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-02') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-03') ||
		isTest('audit-rest-jwk-token-custom-validazione-fallita-typestring-04')  ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdSubAuditExpected,
            iss: '#notpresent',
            sub: clientIdSubAuditExpected,
	    type: type, 
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+forwardContextUrl)
    
    * def tid = responseHeaders['GovWay-Transaction-ID'][0]

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0],
        'GovWay-TestSuite-GovWay-Transaction-ID': tid
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)






Scenario: isTest('audit-rest-jwk-01-differentAudienceAsArray')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'-AudienceAsArray/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray')  ) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray')  ) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      dnonceExpected = '#notpresent'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      digestExpected = '#notpresent'
    }
    """

    * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
      userIdToken = 'utente-token'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01-differentAudienceAsArray') ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: ["testsuite-audience-audit","altro"],
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingAuditRest01-'+tipoTest+'-DifferentAudienceAsArray/v1')
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('audit-rest-jwk-0401-differentAudienceAsArray')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray') ) {
      tipoTest = 'JWK'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray') ) {
      audExpected = 'RestBlockingAuditRest01-'+tipoTest+'-AudienceAsArray/v1'
    }
    """


    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      issExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
      subExpected = 'ApplicativoBlockingJWK'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray') ) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingJWK'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
      dnonceExpected = '#notpresent'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
      digestExpected = '#notpresent'
    }
    """

    * def userIdToken = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
      userIdToken = 'utente-token'
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-0401-differentAudienceAsArray')  ) {
    client_token_audit_match = ({
        header: { kid: kidExpected },
        payload: { 
            aud: ["testsuite-audience-audit","altro"],
            client_id: '#notpresent',
            iss: issExpected,
            sub: '#notpresent',
	    userID: userIdToken, 
            userLocation: 'ip-utente-token', 
            LoA: 'livello-autenticazione-utente-token',
	    dnonce: dnonceExpected
        }
    })
    }
    """

    * def client_token_integrity_match = 
    """
    ({
        header: { 
		kid: kidExpected,
	        x5c: '#notpresent',
                x5u: '#notpresent',
               'x5t#S256': '#notpresent'
	},
        payload: { 
            aud: ["altro","testsuite-audience"],
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
            signed_headers: [
                { digest: '#string' },
                { 'content-type': 'application\/json; charset=UTF-8' }
            ]
        }
    })
    """

    * def client_token_authorization_match = 
    """
    ({
        header: { kid: kidExpected },
        payload: { 
            aud: audExpected,
            client_id: clientIdExpected,
            iss: 'DemoSoggettoFruitore',
            sub: subExpected,
	    purposeId: purposeIdExpected,
	    digest: digestExpected
        }
    })
    """

    * karate.log("Ret: ", requestHeaders)

    * call checkTokenKid ({token: requestHeaders['Authorization'][0], match_to: client_token_authorization_match, kind: "Bearer" })

    * call checkToken ({token: requestHeaders['Agid-JWT-Signature'][0], match_to: client_token_integrity_match, kind: "AGID" })

    * call checkTokenKid ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/RestBlockingAuditRest01-'+tipoTest+'-DifferentAudienceAsArray/v1')
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
	'GovWay-TestSuite-GovWay-Client-Integrity-Token': requestHeaders['Agid-JWT-Signature'][0],
        'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)





Scenario: isTest('tengo-da-parte') 

    * def tamper_token_authorization_digest_value = 
    """
    function(tok, newPayload) {
        var components = tok.split('.');
	return components[0] + '.' + newPayload + '.' + components[2];
    }
    """

    * def tokenDecoded = decodeToken(requestHeaders['Authorization'][0], "Bearer")
    * eval
    """
    tokenDecoded.payload.digest.value = tokenDecoded.payload.digest.value+'tamper';
    """
    * string payload = tokenDecoded.payload;
    * karate.log("Payload: ", payload);
    * def payload_base64 = encode_base64(payload);
    * karate.log("Payload base64: ", payload_base64);

    * set requestHeaders['Authorization'][0] = tamper_token_authorization_digest_value(requestHeaders['Authorization'][0],payload_base64)
    * karate.proceed (govway_base_path + '/rest/in/DemoSoggettoErogatore/'+audExpected)
    * match responseStatus == 400
    * match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/invalid-token-signature-in-request.json')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'










# catch all
#
#

#Scenario:
#    karate.fail("Nessuno scenario matchato")
