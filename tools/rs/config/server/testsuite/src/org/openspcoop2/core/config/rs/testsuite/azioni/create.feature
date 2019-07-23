Feature: Creazione Azioni

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def azione = read('azione.json')
* eval randomize(azione, ["nome"])

* def api_path = api.nome + '/' + api.versione
* def servizio_path = api_path + '/servizi/' + servizio.nome
* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })

@Create204
Scenario: Azioni Creazione 204

* call create ({ resourcePath: 'api', body: api })
* call create ({ resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome })
* call create_201 ({ resourcePath: 'api/' + servizio_path + '/' + 'azioni', body: azione,  key: azione.nome })
* call delete ({ resourcePath: 'api/' + servizio_path })
* call delete ({ resourcePath: 'api/' + api_path })

@Create409
Scenario: Azioni Creazione 409

* call create ({ resourcePath: 'api', body: api })
* call create ({ resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome })
* call create_409 ({ resourcePath: 'api/' + servizio_path + '/' + 'azioni', body: azione,  key: azione.nome })
* call delete ({ resourcePath: 'api/' + api_path + '/servizi/' + servizio.nome })
* call delete ({ resourcePath: 'api/' + api_path })

