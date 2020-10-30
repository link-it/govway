Feature: Feature test connettività base https

Background:

* url govway_base_path + '/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/ApiDemoBlockingRest/v1'
* def check_traccia = read('classpath:utils/check-traccia-idac01.feature')

Scenario: Test di Echo REST

* def body = read("classpath:bodies/modipa-blocking-sample-request.json")
* def resp = read("classpath:test/risposte-default/rest/bloccante/response.json")

Given path 'resources', 1, 'M'
And request body
When method post
Then status 200
And match response == resp


Scenario: Test di Echo REST e verifica traccia sul DB

* def body = read("classpath:bodies/modipa-blocking-sample-request.json")
* def resp = read("classpath:test/risposte-default/rest/bloccante/response.json")

Given path 'resources', 1, 'M'
And request body
When method post
Then status 200
And match response == resp

* call check_traccia ({ fruizione_tid: responseHeaders['GovWay-Transaction-ID'][0], erogazione_tid: responseHeaders['GovWay-TestSuite-GovWay-Transaction-ID'][0] })
