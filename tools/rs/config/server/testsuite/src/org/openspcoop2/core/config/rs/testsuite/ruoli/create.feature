Feature: Creazione Ruolo

Background: Background name

* call read('classpath:crud_commons.feature')

* def ruolo = read('ruolo.json')
* eval randomize(ruolo, ["nome"])

* def ruolo_descrizione4000 = read('ruolo_descrizione4000.json')
* eval randomize(ruolo_descrizione4000, ["nome"])

@CreateRuolo204
Scenario: Creazione Ruolo 204 OK

    * call create_201 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }

@Create409
Scenario: Creazione Ruolo 409 Conflitto

    * call create_409 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }

@CreateDescrizione4000
Scenario: Creazione Ruolo descrizione 4000 204 OK

    * call create { resourcePath: 'ruoli', body: '#(ruolo_descrizione4000)' }

    Given url configUrl
    And path 'ruoli', ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == ruolo_descrizione4000.descrizione

    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo_descrizione4000.nome } )
