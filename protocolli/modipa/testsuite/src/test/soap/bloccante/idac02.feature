Feature: Testing feature idac02 SOAP

Background:

* def check_traccia = read('classpath:utils/check-traccia-idac02.feature')


Scenario: IDAC02 Autenticazione Client

* def body = read("classpath:bodies/modipa-blocking-sample-request-soap.xml")
* def resp = read("classpath:test/risposte-default/soap/bloccante/response.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingSoapIDAC02/v1'

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
When method post
Then status 200
And match response == resp

* call check_traccia ({ fruizione_tid: responseHeaders['GovWay-Transaction-ID'][0], erogazione_tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })

Scenario: IDAC02 Autenticazione Client Assente

* def body = read("classpath:bodies/modipa-blocking-sample-request-soap.xml")
* def soap_url = govway_base_path + '/soap/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingSoapIDAC02BadAuth/v1'

* def code_error = 
 """
  <env:Code>
    <env:Value>env:Sender</env:Value>
    <env:Subcode>
        <env:Value xmlns:integration="http://govway.org/integration/fault">integration:AuthenticationRequired</env:Value>
    </env:Subcode>
  </env:Code>
"""

* def detail_error = 
"""
<env:Detail>
    <problem xmlns="urn:ietf:rfc:7807">
        <type>https://govway.org/handling-errors/401/AuthenticationRequired.html</type>
        <title>AuthenticationRequired</title>
        <status>401</status>
        <detail>Authentication required</detail>
        <govway_id>#string</govway_id>
    </problem>
</env:Detail>
"""

Given url soap_url
And request body
And header Content-Type = 'application/soap+xml'
And header action = soap_url
When method post
Then status 500
And match /Envelope/Body/Fault/Reason/Text == "Authentication required"
And match /Envelope/Body/Fault/Code == code_error
And match /Envelope/Body/Fault/Detail == detail_error


* def get_traccia = read('classpath:utils/get_traccia.js')
* def traccia_to_match = 
"""
[
    { name: 'ProfiloInterazione', value: 'bloccante' },
    { name: 'ProfiloSicurezzaCanale', value: 'IDAC02' }
]
"""

* def result = get_traccia(responseHeaders['GovWay-Transaction-ID'][0]) 
* match result contains deep traccia_to_match