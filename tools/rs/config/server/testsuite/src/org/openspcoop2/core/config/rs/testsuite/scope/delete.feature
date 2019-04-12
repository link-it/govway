Feature: Eliminazione Scope

Background:

* call read('classpath:crud_commons.feature')

* def scope = read('scope.json') 
* eval randomize(scope, ["nome"])

@Delete204
Scenario: Eliminazione Scope 204 OK

    * call delete_204 { resourcePath: 'scope', body: '#(scope)', key:'#(scope.nome)'}

@Delete404
Scenario: Eliminazione Scope 404

    * call delete_404 { resourcePath: 'scope', key:'#(scope.nome)'}
