Feature: Testing Sicurezza Messaggio ModiPA IDAS01

Background:
    * def basic = read('classpath:utils/basic-auth.js')

    * def result = callonce read('classpath:utils/jmx-enable-error-disclosure.feature')
    * configure afterFeature = function(){ karate.call('classpath:utils/jmx-disable-error-disclosure.feature'); }
    
    * def check_traccia = read('check-tracce/check-traccia.feature')
    * def check_traccia_id_messaggio = read('check-tracce/check-traccia-id-messaggio.feature')
    * def check_signature = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/soap/sicurezza_messaggio/check-signature.feature')

    * def check_traccia_oauth = read('check-tracce/check-traccia-oauth.feature')

    * def reset_cache_token = read('classpath:utils/reset-cache-token.feature')

    * def decode_token = read('classpath:utils/decode-token.js')

    * def client_x509_sub = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def server_x509_sub = 'CN=ExampleServer, O=Example, L=Pisa, ST=Italy, C=IT'

@connettivita-base
Scenario: Test connettività base

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_request_id


@connettivita-base-richiesta-con-header
Scenario: Test connettività base in cui la richiesta possiede un header SOAP

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request read("requestConHeader.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02-richiesta-con-header'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("responseConHeader.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_request_id


@connettivita-base-richiesta-con-header-e-trasformazione
Scenario: Test connettività base in cui la richiesta possiede un header SOAP e viene generato un nuovo header tramite trasformazione

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02ConAggiuntaHeader/v1'

Given url soap_url
And request read("requestConHeader.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02-richiesta-con-header-e-trasformazione'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("responseConHeader.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_request_id


@riutilizzo-token
Scenario: Il token viene riutilizzato, fruizione ed erogazione devono arrabbiarsi

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'connettivita-base-idas02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

# Ripeto la richiesta all'erogazione per farla arrabbiare

* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

Given url soap_url
And request client_request
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-richiesta.xml")
And match header GovWay-Transaction-ErrorType == 'Conflict'

# Faccio in modo che il proxy risponda alla fruizione con la precedente risposta dell'erogazione
# NOTA: Questo test non controlla in realtà che la fruizione si arrabbi perchè il token è stato riutilizzato,
#   infatti si arrabbia perchè "IdentificativoBusta presente nel RiferimentoMessaggio non è valido", infatti
# inviando nuovamente la stessa risposta, l'identificativo busta appartiene a quello della richiesta precedente.

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02/v1'

# La richiesta normale fa generare un nuovo indentificativo, devo riusare la stessa richiesta
# e la stessa risposta. Ma non è possibile perchè ad ogni richiesta la fruizione imposta nello header
# un nuovo valore per il campo ReleaseTo che riferisce l'identificativo della precedente richiesta.

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-risposta.xml")
And match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidResponse'








@riutilizzo-token-oauth2
Scenario: Il token viene riutilizzato, fruizione ed erogazione devono arrabbiarsi, token generato tramite authorization server

# Svuoto la cache per evitare che venga generato lo stesso token in questo test usato già in altri
* call reset_cache_token ({ })

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02TokenOAuth/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'riutilizzo-token-generato-auth-server'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'SoapBlockingIDAS02TokenOAuth/v1'
When method post
Then status 200
And match response == read("response.xml")

* def client_token_header = responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0]

* def client_token = decode_token(responseHeaders['GovWay-TestSuite-GovWay-Client-Authorization-Token'][0], "Bearer")
* def jti_token = get client_token $.payload.jti


# Verifico che l'identificativo utilizzato sia quello di Authorization, per l'id della busta

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jti_token })
* call check_traccia_oauth ({ tid: tid, tipo: 'Richiesta', client_request_id: jti_token, profilo_sicurezza: 'IDAS02' })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia_id_messaggio ({ tid: tid, tipo: 'Richiesta', id_messaggio: jti_token })
* call check_traccia_oauth ({ tid: tid, tipo: 'Richiesta', client_request_id: jti_token, profilo_sicurezza: 'IDAS02' })



# Ripeto la richiesta all'erogazione per farla arrabbiare

* def soap_url = govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02TokenOAuth/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header Authorization = client_token_header
When method post
Then status 500
And match response == read("error-bodies/identificativo-token-riutilizzato-in-richiesta.xml")
And match header GovWay-Transaction-ErrorType == 'Conflict'








@authorization-criteri-autorizzativi
Scenario: IDAS02 - Test con criteri autorizzativi per contenuto (es. security token)

* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/SoapBlockingIDAS02CheckAuthz/v1'

Given url soap_url
And request read("request.xml")
And header Content-Type = 'application/soap+xml'
And header action = soap_url
And header GovWay-TestSuite-Test-ID = 'check-authz-idas02'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
When method post
Then status 200
And match response == read("response.xml")

* def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
* xml client_request = karateCache.get("Client-Request")
* xml server_response = karateCache.get("Server-Response")

* def client_request_id = karate.xmlPath(client_request, '/Envelope/Header/MessageID')

* def tid = responseHeaders['GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tid = responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0]
* call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: client_x509_sub, profilo_sicurezza: 'IDAS02' })
* call check_traccia ({tid: tid, tipo: 'Risposta', body: server_response, x509sub: server_x509_sub, profilo_sicurezza: 'IDAS02', requestMessageId:client_request_id })

* def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
* match tidMessaggio == client_request_id
