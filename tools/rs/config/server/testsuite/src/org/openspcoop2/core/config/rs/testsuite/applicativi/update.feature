Feature: Aggiornamento Applicativi

Background:

    * call read('classpath:crud_commons.feature')

    * def applicativo = read('applicativo.json') 
    * eval randomize(applicativo, ["nome", "credenziali.username"]);

    * def applicativo_update = read('applicativo_update.json')
    * eval applicativo_update.nome = applicativo.nome

@Update204
Scenario: Applicativi Aggiornamento 204 OK

    * call update_204 { resourcePath: 'applicativi',  body: '#(applicativo)',  body_update: '#(applicativo_update)',  key: '#(applicativo.nome)',  delete_key: '#(applicativo_update.nome)' }

@Update404
Scenario: Applicativi Aggiornamento 404 

    * call update_404 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Update400
Scenario: Applicativi Aggiornamento Ruolo Inesistente 400

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    * eval applicativo_update.ruoli = [ 'RuoloInesistente_' + random() ]

    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And request applicativo_update
    When method put
    Then status 400

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )
