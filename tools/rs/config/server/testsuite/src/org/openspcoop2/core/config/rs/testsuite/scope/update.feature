Feature: Aggiornamento Scope

Background:

    * call read('classpath:crud_commons.feature')

    * def scope = read('scope.json') 
    * eval randomize( scope, ["nome"] )
    * print 'SCOPE RANDOMIZZATO: ', scope

    * def scope_update = read('scope_update.json')
    * eval randomize( scope_update, ["nome"] )

    * def scope_descrizione4000 = read('scope_descrizione4000.json')
    * eval randomize(scope_descrizione4000, ["nome"])

@Update204
Scenario: Aggiornamento Scope 204 OK

    * call update_204 { resourcePath: 'scope', body: '#(scope)', body_update: '#(scope_update)', key: '#(scope.nome)', delete_key: '#(scope_update.nome)' }

@Update404
Scenario: Aggiornamento Scope 404 

    * call update_404 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }



@UpdateDescrizione4000
Scenario: Aggiornamento scope descrizione 4000 204 OK

    * call create { resourcePath: 'scope', body: '#(scope_descrizione4000)' }


    Given url configUrl
    And path 'scope', scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == scope_descrizione4000.descrizione

    # UPDATE 1

    * eval descr4000=scope_descrizione4000.descrizione
    * eval scope_descrizione4000.descrizione='descrModificata'

    Given url configUrl
    And path 'scope/' + scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request scope_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'scope', scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == 'descrModificata'

    # UPDATE 2

    * remove scope_descrizione4000.descrizione

    Given url configUrl
    And path 'scope/' + scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request scope_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'scope', scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'

    # UPDATE 3

    * eval scope_descrizione4000.descrizione=descr4000

    Given url configUrl
    And path 'scope/' + scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request scope_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'scope', scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descr4000

    * call delete ( { resourcePath: 'scope' + '/' + scope_descrizione4000.nome } )




