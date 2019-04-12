Feature: Creazione Ruolo

Background: Background name

* call read('classpath:crud_commons.feature')

* def ruolo = read('ruolo.json')
* eval randomize(ruolo, ["nome"])

@CreateRuolo204
Scenario: Creazione Ruolo 204 OK

    * call create_204 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }

@Create409
Scenario: Creazione Ruolo 409 Conflitto

    * call create_409 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }
