Feature: Testing Sicurezza SUAP

Background:
    * def basic = read('classpath:utils/basic-auth.js')
    
    
@ERROR_400_001
Scenario: ERROR_400_001 - incorrect request input: uno o più parametri e/o la forma del body dell’operation non rispettano la sintassi definita nell’IDL OpenAPI.
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap-invalid.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_400_001'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
When method post
Then status 400
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_400_001.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'  
    
@ERROR_401_001
Scenario: ERROR_401_001 - PDND token not found: token di autorizzazione della PDND non presente nella richiesta.
Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_001.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent' 

@ERROR_401_002
Scenario: ERROR_401_002 - Invalid PDND token: token di autorizzazione della PDND non valido.
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_002'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_002.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'   

@ERROR_401_002_noBearerPrefix
Scenario: ERROR_401_002 - Invalid PDND token: token di autorizzazione della PDND senza prefisso bearer.
Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.jYW04zLDHfR1v7xdrW3lCGZrMIsVe0vWCfVkN2DRns2c3MN-mcp_-RE6TN9umSBYoNV-mnb31wFf8iun3fB6aDS6m_OXAiURVEKrPFNGlR38JSHUtsFzqTOj-wFrJZN4RwvZnNGSMvK3wzzUriZqmiNLsG8lktlEn6KA4kYVaM61_NpmPHWAjGExWv7cjHYupcjMSmR8uMTwN5UuAwgW6FRstCJEfoxwb0WKiyoaSlDuIiHZJ0cyGhhEmmAPiCwtPAwGeaL1yZMcp0p82cpTQ5Qb-7CtRov3N4DcOHgWYk6LomPR5j5cCkePAz87duqyzSMpCB0mCOuE3CU2VMtGeQ'
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_002_noBearerPrefix'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_002.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'  

@ERROR_401_002_audience
Scenario: ERROR_401_002 - Invalid PDND token: token di autorizzazione della PDND non valido. (caso audience non valido)
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_002_audience'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite-differente'
And header simulazioneintegrity-audience = 'testsuite'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_002.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'   
    
@ERROR_401_003
Scenario: ERROR_401_003 - AgID-JWT-Signature token not found: la richiesta non contiene l’header AgID-JWT-Signature.
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_003'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_003.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent' 
    
@ERROR_401_004
Scenario: ERROR_401_004 - invalid AgID-JWT-Signature token: token nell’header AgID-JWT-Signature non valido.
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_004'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_004.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent' 

@ERROR_401_004_audience
Scenario: ERROR_401_004 - invalid AgID-JWT-Signature token: token nell’header AgID-JWT-Signature non valido. (caso audience non valido)
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'notify'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_401_004_audience'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite-differente'
When method post
Then status 401
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_401_004.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'     
    
@ERROR_404_001
Scenario: ERROR_404_001 - resource not found: risorsa richiesta non esistente.
Given url govway_base_path + "/rest/in/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'inesistente'
And request read('request-suap.json')
When method post
Then status 404
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_404_001.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent' 

@ERROR_428_001
Scenario: ERROR_428_001 - hash not found: l'header http 'If-Match', richiesto obbligatoriamente nell’IDL OpenAPI, non presente.
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path 'instance'
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_428_001'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
When method get
Then status 428
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_428_001.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent' 

@ERROR_500_007
Scenario Outline: ERROR_500_007 - response processing error: validazione risposta fallita (<Descrizione>)
Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ModiTestSUAP/v1"
And path '<risorsa>'
And request read('request-suap.json')
And header Authorization = call basic ({ username: 'ApplicativoBlockingIDA01', password: 'ApplicativoBlockingIDA01' })
And header GovWay-TestSuite-Test-ID = 'suap-ERROR_500_007'
And header simulazionepdnd-username = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-password = 'ApplicativoBlockingIDA01'
And header simulazionepdnd-purposeId = 'purposeId-ApplicativoBlockingIDA01'
And header simulazionepdnd-audience = 'testsuite'
And header simulazioneintegrity-audience = 'testsuite'
And header simulazionebackend = '<tipo>'
And header govway-testsuite-expected-diagnostico = '<diag>'
When method post
Then status 500
And match response == read('classpath:test/rest/sicurezza-messaggio/error-bodies/suap_ERROR_500_007.json')
And match header Content-Type == 'application/json'
And match header Authorization == '#notpresent'
And match header Agid-JWT-Signature == '#notpresent'   

Examples:
| Descrizione | tipo | risorsa | diag |
| connectionRefused | connectionRefused | cancel | Connection refused |
| non viene fornito un content-type nella risposta | default | notify | is not allowed for body content  |
| connectionTimeout | connectionTimeout | cancel | Connect timed out |
| readTimeout | readTimeout | cancel | Read timed out |
| htmlResponse | htmlResponse | cancel | errore HTTP 500 |
| multipartResponse | multipartResponse | cancel | errore HTTP 500 |
| emptyResponse | emptyResponse | cancel | /ping |
| emptyResponseWithContentType | emptyResponseWithContentType | cancel | /ping |


