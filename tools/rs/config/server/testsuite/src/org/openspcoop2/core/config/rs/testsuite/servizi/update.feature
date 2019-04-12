Feature: Update Servizi

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval api.referente = soggettoDefault
* eval randomize(api, ["nome"])

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def servizio2 = read('servizio_update.json')
* eval randomize(servizio2, ["nome"])

* def api_path = api.nome + '/' + api.versione

@Update204
Scenario: Update Servizi 204
    * call create ( { resourcePath: "api", body: api } )
    * call update_204 ( { resourcePath: 'api/' + api_path + '/servizi', body: servizio, body_update: servizio2, key: servizio.nome, delete_key: servizio2.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@Update404
Scenario: Update Servizi 404

    * call create ( { resourcePath: "api", body: api } )
    * call update_404 ( { resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome } )
    * call delete ( { resourcePath: "api/" + api_path })
