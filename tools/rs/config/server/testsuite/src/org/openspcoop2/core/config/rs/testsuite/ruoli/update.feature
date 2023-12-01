Feature: Update Ruoli


Background:

    * call read('classpath:crud_commons.feature')

    * def ruolo = read('ruolo.json')
    * eval randomize(ruolo, ["nome"])

    * def ruolo_update = read('ruolo2.json')
    * eval randomize(ruolo_update, ["nome"])

    * def ruolo_descrizione4000 = read('ruolo_descrizione4000.json')
    * eval randomize(ruolo_descrizione4000, ["nome"])


@Update204
Scenario: Update Ruoli 204 OK

    * call update_204 { resourcePath: 'ruoli',  body: '#(ruolo)',  body_update: '#(ruolo_update)',  key: '#(ruolo.nome)',  delete_key: '#(ruolo_update.nome)' }

@Update404
Scenario: Update Ruoli 404 

    * call update_404 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }


@UpdateDescrizione4000
Scenario: Aggiornamento Ruolo descrizione 4000 204 OK

    * call create { resourcePath: 'ruoli', body: '#(ruolo_descrizione4000)' }


    Given url configUrl
    And path 'ruoli', ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == ruolo_descrizione4000.descrizione

    # UPDATE 1

    * eval descr4000=ruolo_descrizione4000.descrizione
    * eval ruolo_descrizione4000.descrizione='descrModificata'

    Given url configUrl
    And path 'ruoli/' + ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request ruolo_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'ruoli', ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == 'descrModificata'

    # UPDATE 2

    * remove ruolo_descrizione4000.descrizione

    Given url configUrl
    And path 'ruoli/' + ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request ruolo_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'ruoli', ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'

    # UPDATE 3

    * eval ruolo_descrizione4000.descrizione=descr4000

    Given url configUrl
    And path 'ruoli/' + ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request ruolo_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'ruoli', ruolo_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descr4000

    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo_descrizione4000.nome } )


