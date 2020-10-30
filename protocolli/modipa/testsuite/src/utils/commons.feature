@ignore
Feature:

Scenario:

* def basic = read('classpath:basic-auth.js')
* def randomize = read('classpath:randomize.js')

* def pause = 
"""
function(t){ java.lang.Thread.sleep(t) }
"""

* def getDate =
"""
function() {
var now = java.time.ZonedDateTime.now();
var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
var formatDateTime = now.format(formatter);
return formatDateTime;
} 
"""	

* def getDateMinuteZero =
"""
function() {
var now = java.time.ZonedDateTime.now().withMinute(0).withSecond(0);
var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
var formatDateTime = now.format(formatter);
return formatDateTime;
} 
"""	


* def random = function() { return java.lang.System.currentTimeMillis() }