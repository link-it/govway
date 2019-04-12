Feature: Aggiornamento Scope

Background:

    * call read('classpath:crud_commons.feature')

    * def scope = read('scope.json') 
    * eval randomize( scope, ["nome"] )
    * print 'SCOPE RANDOMIZZATO: ', scope

    * def scope_update = read('scope_update.json')
    * eval randomize( scope_update, ["nome"] )

@Update204
Scenario: Aggiornamento Scope 204 OK

    * call update_204 { resourcePath: 'scope', body: '#(scope)', body_update: '#(scope_update)', key: '#(scope.nome)', delete_key: '#(scope_update.nome)' }

@Update404
Scenario: Aggiornamento Scope 404 

    * call update_404 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }


