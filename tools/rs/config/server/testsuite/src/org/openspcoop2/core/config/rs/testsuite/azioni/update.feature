Feature: Update Servizi

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def azione = read('azione.json')
* eval randomize(azione, ["nome"])

* def azione_update = read('azione_update.json')
* eval randomize(azione_update, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def servizi_path = api_path + '/servizi'
* def servizio_path = servizi_path + '/' + servizio.nome
* def azioni_path = servizio_path + '/azioni'
* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })


@Update204
Scenario: Azioni Update 204
    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call update_204 ({ resourcePath: azioni_path, body: azione, body_update: azione_update, key: azione.nome, delete_key: azione_update.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })


@Update404
Scenario: Azioni Update 404

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call update_404 ({ resourcePath: azioni_path, body: azione, key: azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })
