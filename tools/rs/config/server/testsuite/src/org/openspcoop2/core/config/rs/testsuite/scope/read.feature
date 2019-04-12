Feature: Lettura Scope

Background:

* call read('classpath:crud_commons.feature')

* def scope = read('scope.json') 
* eval randomize( scope, ["nome"] )

@FindAll200
Scenario: Scope FindAll 200 OK
    
    * call findall_200 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }

@Get200
Scenario: Scope Get 200 OK

    * call get_200 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }

@Get404
Scenario: Scope Get 404

    * call get_404 { resourcePath: '#("scope/" + scope.nome)' }
