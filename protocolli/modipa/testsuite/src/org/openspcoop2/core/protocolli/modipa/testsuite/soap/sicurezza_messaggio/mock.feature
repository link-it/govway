Feature: Server mock per il testing della sicurezza messaggio

Background:

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def check_signature = read('check-signature.feature')

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


Scenario: isTest('connettivita-base') || isTest('connettivita-base-default-trustore') || isTest('connettivita-base-truststore-ca') || isTest('connettivita-base-truststore-ca-ocsp') || 
		isTest('riferimento-x509-SKIKey-IssuerSerial') || isTest('riferimento-x509-ThumbprintKey-SKIKey') || 
		isTest('riferimento-x509-x509Key-ThumbprintKey') ||  isTest('riferimento-x509-IssuerSerial-x509Key') || 
		isTest('manomissione-token-risposta') || 
		isTest('low-ttl-erogazione') ||	isTest('low-iat-ttl-erogazione') || 
		isTest('connettivita-base-idas02') || isTest('check-authz-idas02') ||
		isTest('keystore-default-fruizione') ||
		isTest('keystore-ridefinito-fruizione') || isTest('keystore-ridefinito-fruizione-applicativo-no-keystore') || 
		isTest('keystore-ridefinito-fruizione-archivio') ||
		isTest('keystore-definito-applicativo') 
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('riutilizzo-token-generato-auth-server')
    
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('connettivita-base-idas02-richiesta-con-header')
    
    * match bodyPath('/Envelope/Header') == '<soap:Header><t:elementoTest xmlns:t="http://test">PROVA</t:elementoTest></soap:Header>'
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/responseConHeader.xml')

Scenario: isTest('connettivita-base-idas02-richiesta-con-header-e-trasformazione')
    
    * match bodyPath('/Envelope/Header') == '<soap:Header><t:elementoTest xmlns:t="http://test">PROVA</t:elementoTest><t2:elementoTest2 xmlns:t2="http://test">PROVA2</t2:elementoTest2></soap:Header>'
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/responseConHeader.xml')

Scenario: isTest('connettivita-base-no-sbustamento')
    
    * match bodyPath('/Envelope/Header/Security/Signature') == "#present"
    * match bodyPath('/Envelope/Header/Security/Timestamp/Created') == "#string"
    * match bodyPath('/Envelope/Header/Security/Timestamp/Expires') == "#string"
    * match bodyPath('/Envelope/Header/To') == "testsuite"
    * match bodyPath('/Envelope/Header/From/Address') == "DemoSoggettoFruitore/ApplicativoBlockingIDA01NoSbustamento"
    * match bodyPath('/Envelope/Header/MessageID') == "#uuid"

    * def body = bodyPath('/')
    * call check_signature [ {element: 'To'}, {element: 'From'}, {element: 'MessageID'}, {element: 'ReplyTo'} ]

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTest('response-without-payload')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = ''


Scenario: isTest('disabled-security-on-action')
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response-op.xml')



Scenario: isTest('enabled-security-on-action') && bodyPath('/Envelope/Body/MRequestOp') != ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response-op.xml')


Scenario: isTest('enabled-security-on-action') && bodyPath('/Envelope/Body/MRequestOp1') != ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response-op.xml')

Scenario: isTest('certificato-server-scaduto') || isTest('certificato-server-revocato') || isTest('certificato-server-revocato-ocsp')
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


#####################################################
#                       IDAS03                      #
#####################################################

Scenario: isTest('connettivita-base-idas03') 

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTest('manomissione-token-risposta-idas03')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('manomissione-payload-risposta')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTest('connettivita-base-idas03-no-digest-richiesta')
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response-op.xml')


Scenario: isTest('response-without-payload-idas03') || isTest('response-without-payload-idas03-digest-richiesta')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = ''


Scenario: isTest('informazioni-utente-header') || isTest('informazioni-utente-query')  || isTest('informazioni-utente-mixed') || isTest('informazioni-utente-static') || isTest('informazioni-utente-custom')

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('idas03-token-richiesta')
    
    * def c = request
    * match c/Envelope/Header == ''

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('idas03-token-risposta')
    
    * def c = request
    * match c/Envelope/Header == '#notpresent'

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTest('idas03-token-azione-puntuale') || isTest('idas03-token-azione-puntuale-default')
    
    * def c = request
    * match c/Envelope/Header == ''
    
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response-op.xml')


#####################################################
#                     IDAS0302                      #
#####################################################

Scenario: isTest('connettivita-base-idas0302') ||
		isTest('riutilizzo-token-generato-auth-server-idas0302')

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


Scenario: isTest('manomissione-token-risposta-idas0302')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')

Scenario: isTest('manomissione-payload-risposta-idas0302') ||
	isTest('pkcs11') || isTest('pkcs11-certificate') || isTest('pkcs11-trustStore') || isTest('pkcs11-keystore-fruizione')
    
    * match bodyPath('/Envelope/Header') == ''
    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')


# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
