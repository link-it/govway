Feature: Server proxy per il testing sicurezza messaggio

Background: 

    * def isTest =
    """
    function(id) {
        return karate.get("requestHeaders['GovWay-TestSuite-Test-Id'][0]") == id ||
               karate.get("requestHeaders['GovWay-TestSuite-Test-ID'][0]") == id ||
               karate.get("requestHeaders['govway-testsuite-test-id'][0]") == id
    }
    """

    * def karateCache = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.KarateCache')
    * def check_signature = read('check-signature.feature')
    * def check_client_token = read('check-client-token.feature')
    * def check_server_token = read('check-server-token.feature')
    * def check_server_token_oauth = read('check-server-token-oauth.feature')

    * def checkTokenAudit = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio/check-token.feature')
    * def checkTokenKid = read('classpath:org/openspcoop2/core/protocolli/modipa/testsuite/rest/sicurezza_messaggio/check-token-kid.feature')

    * def decodeToken = read('classpath:utils/decode-token.js')

Scenario: isTest('connettivita-base')
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS01/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('connettivita-base-default-trustore')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01DefaultTrustore/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01DefaultTrustore/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)
    

Scenario: isTest('connettivita-base-no-sbustamento')

    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01DefaultTrustoreNoSbustamento/v1')

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('connettivita-base-truststore-ca')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01TrustStoreCA/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('response-without-payload')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOP/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    # La signature non viene fatta su di una risposta vuota quindi non la controllo
    # Controllo qui la traccia della erogazione perchè non posso far viaggiare header
    # opzionali indietro visto che l'azione è one-way
    
    * def check_traccia = read("classpath:test/soap/sicurezza-messaggio/check-tracce/check-traccia.feature")
    * xml client_request = client_request

    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT' })



Scenario: isTest('disabled-security-on-action')
    * def c = request

    * match c/Envelope/Header/Security/BinarySecurityToken == "#notpresent"
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOP/v1')

    * match /Envelope/Header/Security/BinarySecurityToken == "#notpresent"

##
# Controllo che la sicurezza sia abilitata puntualmente su una operazione,
# mentre di default è disabilitata
#
#

Scenario: isTest('enabled-security-on-action') && bodyPath('/Envelope/Body/MRequestOp') != ''
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    
    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOPNoDefaultSecurity/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS01MultipleOPNoDefaultSecurity/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('enabled-security-on-action') && bodyPath('/Envelope/Body/MRequestOp1') != ''

    * def c = request

    * match c/Envelope/Header/Security/BinarySecurityToken == "#notpresent"
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01MultipleOPNoDefaultSecurity/v1')

    * match /Envelope/Header/Security/BinarySecurityToken == "#notpresent"


Scenario: isTest('riferimento-x509-SKIKey-IssuerSerial')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    
    # Testo la presenza del Subject Key Identifier nello header
    * match bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier') == "V8ojtQaElmusOPopR34itbvzPW8="
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01IssuerSerial/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01IssuerSerial/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })
    # Testo la presenza di IssuerSerial nello header
    * match /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/X509Data/X509IssuerSerial/X509IssuerName == "CN=ExampleCA,O=Example,L=Pisa,ST=Italy,C=IT"
    * match /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/X509Data/X509IssuerSerial/X509SerialNumber == "337913909459742394"

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)



Scenario: isTest('riferimento-x509-ThumbprintKey-SKIKey')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * match bodyPath("/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier") == "#present"
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01SKIKey/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01SKIKey/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })
    # Testo la presenza del Subject Key Identifier nello header
    * match /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier == "#present"

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('riferimento-x509-x509Key-ThumbprintKey')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    
    * match bodyPath("/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier") == "#present"
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01ThumbprintKey/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01ThumbprintKey/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })
    
    * match /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier == "#present"

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('riferimento-x509-IssuerSerial-x509Key')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    
    * match bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/X509Data/X509IssuerSerial/X509IssuerName') == "#present"
    * match bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/X509Data/X509IssuerSerial/X509SerialNumber') == "#present"
    
    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01X509KeyId/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01X509KeyId/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })
    
    # Testo la presenza della thumbprint sha-1 del certificato server
    * match /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/KeyIdentifier == "#present"

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('no-token-to-fruizione')

    * def responseStatus = 200
    * def response = read('classpath:test/soap/sicurezza-messaggio/response.xml')
    * def responseHeaders = { 'Content-type': "application/soap+xml" }


Scenario: isTest('manomissione-token-richiesta')

    * def c = request
    * set c /Envelope/Header/To = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/manomissione-token-richiesta.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-richiesta-no-disclosure')

    * def c = request
    * set c /Envelope/Header/To = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/interoperability-invalid-request.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('manomissione-token-risposta')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')
    * match responseStatus == 200
    
    * def c = response
    * set c /Envelope/Header/To = "tampered_content"

Scenario: isTest('low-ttl-fruizione')

    * java.lang.Thread.sleep(2000)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/ttl-scaduto-in-request.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('low-ttl-fruizione-no-disclosure')

    * java.lang.Thread.sleep(2000)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/interoperability-invalid-request.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'
    

Scenario: isTest('low-ttl-erogazione')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SOAPBlockingIDAS01LowTTL/v1')
    * match responseStatus == 200

    * java.lang.Thread.sleep(2000)

Scenario: isTest('low-iat-ttl-fruizione')

    # Lo iat accettato nell'erogazione e' fino a 3 secondi
    * java.lang.Thread.sleep(5000)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01LowIAT/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/iat-scaduto-in-request.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'
    
Scenario: isTest('low-iat-ttl-erogazione')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01LowIAT/v1')
    * match responseStatus == 200

    # Lo iat accettato nella fruizione e' fino a 3 secondi
    * java.lang.Thread.sleep(5000)

Scenario: isTest('applicativo-non-autorizzato')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/applicativo-non-autorizzato.xml')
    * match header GovWay-Transaction-ErrorType == 'Authorization'

Scenario: isTest('certificato-client-scaduto')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/certificato-client-scaduto.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('certificato-client-revocato')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCA/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/certificato-client-revocato.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('certificato-server-scaduto')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCACertificatoScaduto/v1')


Scenario: isTest('certificato-server-revocato')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCACertificatoRevocato/v1')


Scenario: isTest('keystore-default-fruizione')
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "SoapBlockingIDAS01KeystoreDefaultFruizione/v1", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01/v1", to: "SoapBlockingIDAS01KeystoreDefaultFruizione/v1" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('keystore-ridefinito-fruizione') || isTest('keystore-ridefinito-fruizione-applicativo-no-keystore')

    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "SoapBlockingIDAS01KeystoreRidefinitoFruizione/v1", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01/v1", to: "SoapBlockingIDAS01KeystoreRidefinitoFruizione/v1" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('keystore-ridefinito-fruizione-archivio')

    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "SoapBlockingIDAS01KeystoreRidefinitoFruizioneArchivio/v1", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01/v1", to: "SoapBlockingIDAS01KeystoreRidefinitoFruizioneArchivio/v1" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('keystore-definito-applicativo')

    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS01/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


######################
#       IDAS01 OCSP
######################

Scenario: isTest('connettivita-base-truststore-ca-ocsp')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01_OCSP", to: "testsuite" })

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCAOCSP/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS01TrustStoreCAOCSP/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01_OCSP" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('certificato-client-revocato-ocsp')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCAOCSP/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/certificato-client-revocato-ocsp.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'

Scenario: isTest('certificato-server-revocato-ocsp')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS01TrustStoreCACertificatoRevocatoOCSP/v1')
    
    
######################
#       IDAS02
######################

Scenario: isTest('connettivita-base-idas02') || isTest('connettivita-base-idas02-richiesta-con-header')

    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS02/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('riutilizzo-token-generato-auth-server')

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02TokenOAuth/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('connettivita-base-idas02-richiesta-con-header-e-trasformazione')

    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02ConAggiuntaHeader/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS02ConAggiuntaHeader/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('riutilizzo-token')

    * xml server_response = karateCache.get("Server-Response")
    * def response = server_response
    * def responseStatus = 200
    * def responseHeaders = { 'Content-type': "application/soap+xml" }

Scenario: isTest('check-authz-idas02')

    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS02CheckAuthz/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS02CheckAuthz/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


#####################################################
#                       IDAS03                      #
#####################################################

Scenario: isTest('connettivita-base-idas03')
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS03/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * def digests = bodyPath('/Envelope/Header/Security/Signature/SignedInfo/Reference/DigestValue')
    * def x_request_digests = /Envelope/Header/X-RequestDigest/Reference/DigestValue

    * match digests contains only x_request_digests
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('manomissione-token-richiesta-idas03')

    * def c = request
    * set c /Envelope/Header/To = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/manomissione-token-richiesta.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-risposta-idas03')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03/v1')
    * match responseStatus == 200
    
    * def c = response
    * set c /Envelope/Header/To = "tampered_content"


Scenario: isTest('manomissione-payload-richiesta')

    * def c = request
    * set c /Envelope/Body/MRequest/M/oId = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-payload-risposta')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03/v1')
    * def c = response
    * set c /Envelope/Body/MRequestResponse/return/c = "tampered_content"


Scenario: isTest('connettivita-base-idas03-no-digest-richiesta')

    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOp/v1')
    
    # Controllo nella risposta che non ci sia il digest della richiesta
    * match /Envelope/Header/X-RequestDigest/Reference/DigestValue == '#notpresent'

    * call check_server_token ({ from: "SoapBlockingIDAS03MultipleOp/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('response-without-payload-idas03')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOp/v1')

    # La signature non viene fatta su di una risposta vuota quindi non la controllo
    # Controllo qui la traccia della erogazione perchè non posso far viaggiare header
    # opzionali indietro visto che l'azione è one-way
    
    * def check_traccia = read("classpath:test/soap/sicurezza-messaggio/check-tracce/check-traccia.feature")
    * xml client_request = client_request

    * def body_reference = get client_request/Envelope/Body/@Id
    * def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

    * def checks_richiesta = 
    """
    ([
        { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
    ])
    """

    * def x509sub_client1 = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })


Scenario: isTest('response-without-payload-idas03-digest-richiesta')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03MultipleOpDigestRichiesta/v1')

    * def digests = bodyPath('/Envelope/Header/Security/Signature/SignedInfo/Reference/DigestValue')

    # La signature non viene fatta su di una risposta vuota quindi non la controllo
    # Controllo qui la traccia della erogazione perchè non posso far viaggiare header
    # opzionali indietro visto che l'azione è one-way
    
    * def check_traccia = read("classpath:test/soap/sicurezza-messaggio/check-tracce/check-traccia.feature")
    * xml client_request = client_request

    * def body_reference = get client_request/Envelope/Body/@Id
    * def request_signature = karate.xmlPath(client_request, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='#"+body_reference+"']/DigestValue")

    * def checks_richiesta = 
    """
    ([
        { name: 'ProfiloSicurezzaMessaggio-Digest', value: 'SHA256='+request_signature}
    ])
    """

    * def x509sub_client1 = 'CN=ExampleClient1, O=Example, L=Pisa, ST=Italy, C=IT'
    * def tid = responseHeaders['GovWay-Transaction-ID'][0]
    * call check_traccia ({tid: tid, tipo: 'Richiesta', body: client_request, x509sub: x509sub_client1, profilo_sicurezza: "IDAS0301", other_checks: checks_richiesta })


Scenario: isTest('no-informazioni-utente-at-erogazione')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/no-informazioni-utente-at-erogazione.xml')
    * match responseHeaders['GovWay-Transaction-ErrorType'][0] == "InteroperabilityInvalidRequest"


Scenario: isTest('informazioni-utente-header') || isTest('informazioni-utente-query') || isTest('informazioni-utente-mixed')
    
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    * match bodyPath('/Envelope/Header/Security/Assertion/Issuer') == "DemoSoggettoFruitore"
    * match bodyPath('/Envelope/Header/Security/Assertion/Subject/NameID') == "DemoSoggettoFruitore"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="User"]/AttributeValue') == "utente-token"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="IP-User"]/AttributeValue') == "ip-utente-token"

    * def idSignatureSAML = '#' + bodyPath('/Envelope/Header/Security/Assertion/@ID')
    * match bodyPath("/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='"+idSignatureSAML+"']") == "#present"

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS03InfoUtente/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('informazioni-utente-static')
    
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    * match bodyPath('/Envelope/Header/Security/Assertion/Issuer') == "DemoSoggettoFruitore"
    * match bodyPath('/Envelope/Header/Security/Assertion/Subject/NameID') == "codice-ente-static"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="User"]/AttributeValue') == "utente-token-static"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="IP-User"]/AttributeValue') == "ip-utente-token-static"

    * def idSignatureSAML = '#' + bodyPath('/Envelope/Header/Security/Assertion/@ID')
    * match bodyPath("/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='"+idSignatureSAML+"']") == "#present"

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS03InfoUtente/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('informazioni-utente-custom')
    
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })
    * match bodyPath('/Envelope/Header/Security/Assertion/Issuer') == "DemoSoggettoFruitore"
    * match bodyPath('/Envelope/Header/Security/Assertion/Subject/NameID') == "codice-ente-custom"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="User"]/AttributeValue') == "utente-token"
    * match bodyPath('/Envelope/Header/Security/Assertion/AttributeStatement/Attribute[@Name="IP-User"]/AttributeValue') == "ip-utente-token"

    * def idSignatureSAML = '#' + bodyPath('/Envelope/Header/Security/Assertion/@ID')
    * match bodyPath("/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='"+idSignatureSAML+"']") == "#present"

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03InfoUtente/v1')

    * call check_server_token ({ from: "SoapBlockingIDAS03InfoUtente/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)



Scenario: isTest('idas03-token-richiesta')
    
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03TokenRichiesta/v1')

    * match /Envelope/Header == ''


Scenario: isTest('idas03-token-risposta')
    

    * def c = request
    * match c/Envelope/Header == '#notpresent'

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03TokenRisposta/v1')
    * match responseStatus == 200

    * match /Envelope/Header/Security/Signature == "#present"
    * match /Envelope/Header/Security/Timestamp/Created == "#string"
    * match /Envelope/Header/Security/Timestamp/Expires == "#string"
    * match /Envelope/Header/To == "http://www.w3.org/2005/08/addressing/anonymous"
    * match /Envelope/Header/From/Address == "SoapBlockingIDAS03TokenRisposta/v1"
    * match /Envelope/Header/MessageID == "#uuid"
    * match /Envelope/Header/ReplyTo/Address == "http://www.w3.org/2005/08/addressing/anonymous"
    * match /Envelope/Header/RelatesTo == "#notpresent"

    * def body = response 
    * call check_signature [ {element: 'To'}, {element: 'From'}, {element: 'MessageID'}, {element: 'ReplyTo'} ]
    

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

    * def newHeaders = 
    """
    ({
        'GovWay-TestSuite-GovWay-Message-ID': responseHeaders['GovWay-Message-ID'][0],
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)

Scenario: isTest('idas03-token-azione-puntuale')
    
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03TokenAzionePuntuale/v1')

    * match /Envelope/Header == ''


Scenario: isTest('idas03-token-azione-puntuale-default')

    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS03TokenAzionePuntuale/v1')
    
    # Controllo nella risposta che non ci sia il digest della richiesta
    * match /Envelope/Header/X-RequestDigest/Reference/DigestValue == '#notpresent'

    * call check_server_token ({ from: "SoapBlockingIDAS03TokenAzionePuntuale/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key
    
    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)



#####################################################
#                     IDAS0302                      #
#####################################################

Scenario: isTest('connettivita-base-idas0302')
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * xml xml_client_request = client_request
    * def request_id = karate.xmlPath(xml_client_request, '/Envelope/Header/MessageID')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token ({ from: "SoapBlockingIDAS0302/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01" })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * def digests = bodyPath('/Envelope/Header/Security/Signature/SignedInfo/Reference/DigestValue')
    * def x_request_digests = /Envelope/Header/X-RequestDigest/Reference/DigestValue

    * match digests contains only x_request_digests

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)


Scenario: isTest('riutilizzo-token-generato-auth-server-idas0302')
    # Salvo la richiesta e la risposta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", to: "testsuite" })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * def request_token = decodeToken(requestHeaders['Authorization'][0])
    * def request_id = get request_token $.payload.jti

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302TokenOAuth/v1')

    * def tidMessaggio = responseHeaders['GovWay-Message-ID'][0]
    * match tidMessaggio == request_id

    * call check_server_token_oauth ({ from: "SoapBlockingIDAS0302TokenOAuth/v1", to: "DemoSoggettoFruitore/ApplicativoBlockingIDA01", requestId: request_id })

    * def keyRef = /Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI
    * def key = /Envelope/Header/Security/BinarySecurityToken/@Id
    * match keyRef == '#' + key

    * def digests = bodyPath('/Envelope/Header/Security/Signature/SignedInfo/Reference/DigestValue')

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)


Scenario: isTest('manomissione-token-richiesta-idas0302')

    * def c = request
    * set c /Envelope/Header/To = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/manomissione-token-richiesta.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-token-risposta-idas0302')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1')
    * match responseStatus == 200
    
    * def c = response
    * set c /Envelope/Header/To = "tampered_content"


Scenario: isTest('manomissione-payload-richiesta-idas0302')

    * def c = request
    * set c /Envelope/Body/MRequest/M/oId = "tampered_content"

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1')
    * match responseStatus == 500
    * match response == read('classpath:test/soap/sicurezza-messaggio/error-bodies/manomissione-payload-richiesta.xml')
    * match header GovWay-Transaction-ErrorType == 'InteroperabilityInvalidRequest'


Scenario: isTest('manomissione-payload-risposta-idas0302')

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/SoapBlockingIDAS0302/v1')
    * def c = response
    * set c /Envelope/Body/MRequestResponse/return/c = "tampered_content"


Scenario: isTest('riutilizzo-token-idas0302')

    * xml server_response = karateCache.get("Server-Response")
    * def response = server_response
    * def responseStatus = 200
    * def responseHeaders = { 'Content-type': "application/soap+xml" }
    
Scenario: isTest('pkcs11') || isTest('pkcs11-certificate') || isTest('pkcs11-keystore-fruizione')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/PKCS11TestSOAP/v1')

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)

Scenario: isTest('pkcs11-trustStore')
    
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/PKCS11TestSOAPtrustStore/v1')

    * xmlstring server_response = response
    * eval karateCache.add("Server-Response", server_response)    
    




########################
#       AUDIT REST     #
########################



Scenario: isTest('audit-rest-jwk-01') || 
		isTest('audit-rest-pdnd-02')

    * def tipoTest = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') ) {
      tipoTest = 'JWK'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02') ) {
      tipoTest = 'PDND'
    }
    """

    * def audExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') ) {
      audExpected = 'SoapBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02') ) {
      audExpected = 'SoapBlockingAuditRest02-'+tipoTest+'/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * def issExpected = 'N.D.'
    * def kidExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01')) {
      kidExpected = 'KID-ApplicativoBlockingIDA01'
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
      issExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02')) {
      kidExpected = 'KID-ApplicativoBlockingJWK'
      clientIdExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
      issExpected = 'DemoSoggettoFruitore/KidOnly/ApplicativoBlockingJWK'
    }
    """

    * def subExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01')) {
      subExpected = 'ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02')) {
      subExpected = 'ApplicativoBlockingJWK-CredenzialePrincipal'
    }
    """


    * def purposeIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingIDA01'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02')) {
      purposeIdExpected = 'purposeId-ApplicativoBlockingJWK'
    }
    """


    * def dnonceExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01')) {
      dnonceExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02')) {
      dnonceExpected = '#number'
    }
    """

    * def digestExpected = '#notpresent'
    * eval
    """
    if (isTest('audit-rest-jwk-01')) {
      digestExpected = '#notpresent'
    }
    """
    * eval
    """
    if (isTest('audit-rest-pdnd-02')) {
      digestExpected = { alg: 'SHA256', value: '#string' }
    }
    """

    * def client_token_audit_match = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-jwk-01') || 
	isTest('audit-rest-pdnd-02') ) {
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

    * karate.proceed (govway_base_path + '/soap/in/DemoSoggettoErogatore/'+audExpected+"/idar01.oauth")
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Authorization-Token': requestHeaders['Authorization'][0],
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
      audExpected = 'SoapBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audExpectedAUDIT = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      audExpectedAUDIT = 'SoapBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-01')) {
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
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
               'x5t#S256': '#notpresent'
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


    * karate.log("Ret: ", requestHeaders)


    # Salvo la richiesta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    * call check_client_token ({ address: clientIdExpected, to: audExpected })

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key

    * call checkTokenAudit ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/soap/in//DemoSoggettoErogatore/'+audExpected+'/idar01.locale')
    
    * def newHeaders = 
    """
    ({
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
      audExpected = 'SoapBlockingAuditRest01-'+tipoTest+'/v1'
    }
    """

    * def audExpectedAUDIT = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      audExpectedAUDIT = 'SoapBlockingAuditRest01-'+tipoTest+'-AUDIT/v1'
    }
    """

    * def clientIdExpected = 'N.D.'
    * eval
    """
    if (isTest('audit-rest-x509-0301')) {
      clientIdExpected = 'DemoSoggettoFruitore/ApplicativoBlockingIDA01'
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
               'x5t#S256': '#notpresent'
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

    * karate.log("Ret: ", requestHeaders)

    # Salvo la richiesta per far controllare la traccia del token
    # alla feature chiamante
    * xmlstring client_request = bodyPath('/')
    * eval karateCache.add("Client-Request", client_request)

    # Siccome abbiamo un Riferimento X509 DirectReference, controllo che KeyInfo riferisca il BinarySecurityToken
    * def keyRef = bodyPath('/Envelope/Header/Security/Signature/KeyInfo/SecurityTokenReference/Reference/@URI')
    * def key = bodyPath('/Envelope/Header/Security/BinarySecurityToken/@Id')
    * match keyRef == '#' + key


    * call check_client_token ({ address: clientIdExpected, to: audExpected })

    * call checkTokenAudit ({token: requestHeaders['Agid-JWT-TrackingEvidence'][0], match_to: client_token_audit_match, kind: "AGID" })

    * karate.proceed (govway_base_path + '/soap/in//DemoSoggettoErogatore/'+audExpected+'/idar03.locale')
    
    * def newHeaders = 
    """
    ({
	'GovWay-TestSuite-GovWay-Client-Audit-Token': requestHeaders['Agid-JWT-TrackingEvidence'][0]
    })
    """
    * def responseHeaders = karate.merge(responseHeaders,newHeaders)





# catch all
#
#

Scenario:
    karate.fail("Nessuno scenario matchato")
