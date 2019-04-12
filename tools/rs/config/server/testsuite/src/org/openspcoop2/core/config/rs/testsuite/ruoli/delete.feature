Feature: Eliminazione Ruoli

Background:

* call read('classpath:crud_commons.feature')

* def ruolo = read('ruolo.json') 
* eval randomize(ruolo, ["nome"])

@Delete204
Scenario: Eliminazione Ruoli 204 OK

    * call delete_204 { resourcePath: 'ruoli', body: '#(ruolo)', key:'#(ruolo.nome)'}

@Delete404
Scenario: Eliminazione Ruoli 404

    * call delete_404 { resourcePath: 'ruoli', key:'#(ruolo.nome)'}

