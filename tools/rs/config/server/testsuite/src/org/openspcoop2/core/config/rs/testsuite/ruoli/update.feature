Feature: Update Ruoli


Background:

    * call read('classpath:crud_commons.feature')

    * def ruolo = read('ruolo.json')
    * eval randomize(ruolo, ["nome"])

    * def ruolo_update = read('ruolo2.json')
    * eval randomize(ruolo_update, ["nome"])


@Update204
Scenario: Update Ruoli 204 OK

    * call update_204 { resourcePath: 'ruoli',  body: '#(ruolo)',  body_update: '#(ruolo_update)',  key: '#(ruolo.nome)',  delete_key: '#(ruolo_update.nome)' }

@Update404
Scenario: Update Ruoli 404 

    * call update_404 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }


