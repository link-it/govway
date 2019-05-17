@ignore
Feature:

Scenario:

* def basic = read('classpath:basic-auth.js')
* def randomize = read('classpath:randomize.js')
* def govwayConfAuth = call basic configCred
* def govwayMonitorCred = call basic monitorCred
* def query_params = ({})

* def create = read('classpath:create_stub.feature')
* def delete = read('classpath:delete_stub.feature')
* def put = read('classpath:put_stub.feature')
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

* def pause = 
"""
function(t){ java.lang.Thread.sleep(t) }
"""

* def getDate =
"""
function() {
var now = java.time.LocalDateTime.now();
var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm+02:00");
var formatDateTime = now.format(formatter);
return formatDateTime;
} 
"""	

* def getDateMinuteZero =
"""
function() {
var now = java.time.LocalDateTime.now().withMinute(0);
var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm+02:00");
var formatDateTime = now.format(formatter);
return formatDateTime;
} 
"""	


* def random = function() { return java.lang.System.currentTimeMillis() }
