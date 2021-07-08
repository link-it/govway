Feature: Lettura Applicativi Server

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

@Get200
Scenario Outline: Applicativi Server Get 200 OK

    * def applicativo = read('<nome>') 
		* eval randomize(applicativo, ["nome"])

    * call get_200 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }

Examples:
|nome|
|applicativo.json|
|applicativo_proprieta.json|

