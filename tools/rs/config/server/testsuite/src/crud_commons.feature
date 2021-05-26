@ignore
Feature:

Scenario:

* def basic = read('classpath:basic-auth.js')
* def randomize = read('classpath:randomize.js')
* def govwayConfAuth = call basic basicCred
* def query_params = ({})

* def create = read('classpath:create_stub.feature')
* def delete = read('classpath:delete_stub.feature')
* def put = read('classpath:put_stub.feature')
* def findall = read('classpath:findall_stub.feature')
* def get = read('classpath:get_stub.feature')
* def update = read('classpath:update_stub.feature')
* def get_200 = read('classpath:get_200.feature')
* def get_404 = read('classpath:get_404.feature')
* def findall_200 = read('classpath:findall_200.feature')
* def create_201 = read('classpath:create_201.feature')
* def create_201_apikey = read('classpath:create_201_apikey.feature')
* def create_201_multipleapikey = read('classpath:create_201_multipleapikey.feature')
* def create_400 = read('classpath:create_400.feature')
* def create_409 = read('classpath:create_409.feature')
* def delete_204 = read('classpath:delete_204.feature')
* def delete_404 = read('classpath:delete_404.feature')
* def update_204 = read('classpath:update_204.feature')
* def update_404 = read('classpath:update_404.feature')
* def update_400 = read('classpath:update_400.feature')


* def random = function() { return java.lang.System.currentTimeMillis() }
