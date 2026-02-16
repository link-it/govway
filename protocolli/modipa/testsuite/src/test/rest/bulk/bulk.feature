Feature: Testing BULK

Background:


@head
Scenario: Test head

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULK/v1"
And path 'resource', 1
When method head
Then status 200
And match response == ''
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '1300'
And match header Accept-Range == 'bytes'


@get
Scenario: Test get

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULK/v1"
And path 'resource', 1
When method get
Then status 206
And match response == {"test":123}
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '#present'
And match header Content-Range == '0-12'


@head-no-bulk
Scenario: Test head senza bulk options; govway non preserva il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitato/v1"
And path 'resource', 1
When method head
Then status 200
And match response == ''
And match header Transfer-Encoding == 'chunked'
And match header Content-Length == '#notpresent'
And match header Accept-Range == 'bytes'



@get-no-bulk
Scenario: Test get senza bulk options; govway non preserva il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitato/v1"
And path 'resource', 1
When method get
Then status 206
And match response == {"test":123}
And match header Transfer-Encoding == 'chunked'
And match header Content-Length == '#notpresent'
And match header Content-Range == '0-12'



@head-no-bulk-force
Scenario: Test head senza bulk options; govway ricalcola il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitatoPropertyForce/v1"
And path 'resource', 1
When method head
Then status 200
And match response == ''
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '1300'
And match header Accept-Range == 'bytes'


@get-no-bulk-force
Scenario: Test get senza bulk options; govway ricalcola il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitatoPropertyForce/v1"
And path 'resource', 1
When method get
Then status 206
And match response == {"test":123}
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '#present'
And match header Content-Range == '0-12'


@head-no-bulk-preserve
Scenario: Test head senza bulk options; govway preserva il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitatoPropertyPreserve/v1"
And path 'resource', 1
When method head
Then status 200
And match response == ''
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '1300'
And match header Accept-Range == 'bytes'


@get-no-bulk-preserve
Scenario: Test get senza bulk options; govway preserva il content-length

Given url govway_base_path + "/rest/out/DemoSoggettoFruitore/DemoSoggettoErogatore/RestBULKNonAbilitatoPropertyPreserve/v1"
And path 'resource', 1
When method get
Then status 206
And match response == {"test":123}
And match header Transfer-Encoding == '#notpresent'
And match header Content-Length == '#present'
And match header Content-Range == '0-12'
