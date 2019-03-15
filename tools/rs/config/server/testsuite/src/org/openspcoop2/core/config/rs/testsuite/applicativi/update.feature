Feature: Aggiornamento Applicativi

Background:

    * call read('classpath:crud_commons.feature')

    * def applicativo = read('applicativo.json') 
    * eval applicativo.nome = applicativo.nome + "_" + random()

    * def applicativo_update = read('applicativo_update.json')
    * eval applicativo_update.nome = applicativo.nome

@Update204
Scenario: Aggiornamento 204 OK

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And request applicativo_update
    When method put
    Then status 204

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@Update404
Scenario: Aggiornamento 404 

    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And request applicativo_update
    When method put
    Then status 404

@Update400
Scenario: Aggiornamento Ruolo Inesistente 400

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    * eval applicativo_update.ruoli = [ 'RuoloInesistente_' + random() ]

    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And request applicativo_update
    When method put
    Then status 400

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )
