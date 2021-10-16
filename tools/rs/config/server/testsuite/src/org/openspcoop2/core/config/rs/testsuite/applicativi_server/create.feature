Feature: CreazioneApplicativiServer

Background:

* call read('classpath:crud_commons.feature')

@Create204
Scenario Outline: ApplicativiServer Creazione 204 OK

		* def applicativo = read('<nome>') 
		* eval randomize(applicativo, ["nome"])

    * call create_201 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }

Examples:
|nome|
|applicativo.json|
|applicativo_proprieta.json|

@Create409
Scenario: Applicativi Creazione 409 Conflitto

		* def applicativo = read('applicativo.json') 
    * call create_409 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }

