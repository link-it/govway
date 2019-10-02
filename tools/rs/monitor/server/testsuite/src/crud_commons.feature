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

* def pause = 
"""
function(t){ java.lang.Thread.sleep(t) }
"""

* def getDate =
"""
function() {
var now = java.time.LocalDateTime.now();
var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+02:00");
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


* def sommaRisultatiGraficoJSON = 
"""
function(array){ 
	var sum = 0;
	for(var i = 0; i < array.length; i++) {
	 	sum = sum + parseInt(array[i].totale);
	}
	
	return sum;
}
"""