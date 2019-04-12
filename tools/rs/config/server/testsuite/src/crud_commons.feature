@ignore
Feature:

Scenario:

* def basic = read('classpath:basic-auth.js')
* def randomize = read('classpath:randomize.js')
* def govwayConfAuth = call basic basicCred
* def query_params = ({})

* def create = read('classpath:create_stub.feature')
* def delete = read('classpath:delete_stub.feature')
* def get_200 = read('classpath:get_200.feature')
* def get_404 = read('classpath:get_404.feature')
* def findall_200 = read('classpath:findall_200.feature')
* def create_204 = read('classpath:create_204.feature')
* def create_400 = read('classpath:create_400.feature')
* def create_409 = read('classpath:create_409.feature')
* def delete_204 = read('classpath:delete_204.feature')
* def delete_404 = read('classpath:delete_404.feature')
* def update_204 = read('classpath:update_204.feature')
* def update_404 = read('classpath:update_404.feature')


* def random = function() { return java.lang.System.currentTimeMillis() }