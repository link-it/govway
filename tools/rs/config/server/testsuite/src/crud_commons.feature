@ignore
Feature:

Scenario:

* def basic = read('classpath:basic-auth.js')
* def govwayConfAuth = call basic basicCred

* def create = read('classpath:create_stub.feature')
* def delete = read('classpath:delete_stub.feature')

# TODO: Sostituire con una uuid()
* def random = function() { return java.lang.System.currentTimeMillis() }