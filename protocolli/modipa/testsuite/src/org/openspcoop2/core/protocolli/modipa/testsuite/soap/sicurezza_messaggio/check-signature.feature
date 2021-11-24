Feature: Testa che gli elementi dello header abbiano la rispettiva firma nella ws-security

Scenario:

* def header_path = '/Envelope/Header/'+element+'/@Id'
* print 'check-signature.feature: value of body is', body
* def idSignature = '#' + karate.xmlPath(body,header_path)
* match karate.xmlPath(body, "/Envelope/Header/Security/Signature/SignedInfo/Reference[@URI='"+idSignature+"']") == "#present"