Feature: Creazione Scope

Background: Background name

* call read('classpath:crud_commons.feature')

* def scope = read('scope.json')
* eval randomize(scope, ["nome"])

@CreateScope204
Scenario: Creazione Scope 204 OK

    * call create_204 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }

@Create409
Scenario: Creazione Scope 409 Conflitto

    * call create_409 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }
