Feature: Delete Azioni

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def azione = read('azione.json')
* eval randomize(azione, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def servizi_path = api_path + '/servizi'
* def servizio_path = servizi_path + '/' + servizio.nome
* def azioni_path = servizio_path + '/azioni'
* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })


@Delete204
Scenario: Azioni Delete 204

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call delete_204 ({ resourcePath: azioni_path, body: azione,  key: azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })

@Delete404
Scenario: Azioni Delete 404

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call delete_404 ({ resourcePath: azioni_path, body: azione,  key: azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })
