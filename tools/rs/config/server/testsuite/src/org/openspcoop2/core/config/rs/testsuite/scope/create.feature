Feature: Creazione Scope

Background: Background name

* call read('classpath:crud_commons.feature')

* def scope = read('scope.json')
* eval randomize(scope, ["nome"])

* def scope_descrizione4000 = read('scope_descrizione4000.json')
* eval randomize(scope_descrizione4000, ["nome"])

@CreateScope204
Scenario: Creazione Scope 204 OK

    * call create_201 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }

@Create409
Scenario: Creazione Scope 409 Conflitto

    * call create_409 { resourcePath: 'scope', body: '#(scope)', key: '#(scope.nome)' }

@CreateDescrizione4000
Scenario: Creazione scope descrizione 4000 204 OK

    * call create { resourcePath: 'scope', body: '#(scope_descrizione4000)' }

    Given url configUrl
    And path 'scope', scope_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == scope_descrizione4000.descrizione

    * call delete ( { resourcePath: 'scope' + '/' + scope_descrizione4000.nome } )
