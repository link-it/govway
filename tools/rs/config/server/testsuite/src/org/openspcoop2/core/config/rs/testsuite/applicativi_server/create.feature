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


@Create204_connettore_ApiKey
Scenario: ApplicativiServer Creazione 204 OK

    * def applicativo = read('applicativo_connettore_apikey.json') 
    * eval randomize(applicativo, ["nome"])

    # full
    * call create_201 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }
    
    # senza header
    * remove applicativo.connettore.autenticazione_apikey.api_key_header
    * remove applicativo.connettore.autenticazione_apikey.app_id_header
    * call create_201 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }
    
    # senza app id
    * remove applicativo.connettore.autenticazione_apikey.app_id
    * call create_201 { resourcePath: 'applicativi-server', body: '#(applicativo)', key: '#(applicativo.nome)' }
    
    
