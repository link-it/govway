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

@CreateDescrizione4000
Scenario: Creazione ApplicativoServer descrizione 4000 204 OK

    * def applicativo_server_descrizione4000 = read('applicativo_descrizione4000.json') 

    * call create { resourcePath: 'applicativi-server', body: '#(applicativo_server_descrizione4000)' }

    Given url configUrl
    And path 'applicativi-server', applicativo_server_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == applicativo_server_descrizione4000.descrizione

    * call delete ( { resourcePath: 'applicativi-server' + '/' + applicativo_server_descrizione4000.nome } )

