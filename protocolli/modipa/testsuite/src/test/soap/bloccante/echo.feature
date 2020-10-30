Feature: Test di connettività base https

Background:

* def check_traccia = read('classpath:utils/check-traccia-idac01.feature')

Scenario: Test di Echo SOAP

* def body = read("classpath:bodies/modipa-blocking-sample-request-soap.xml")
* def resp = read("classpath:test/risposte-default/soap/bloccante/response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingSoap/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
When method post
Then status 200
And match response == resp


* call check_traccia ({ fruizione_tid: responseHeaders['GovWay-Transaction-ID'][0], erogazione_tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })    