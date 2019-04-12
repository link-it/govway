Feature: Creazione Servizi

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione;

# BUG: Mi fa creare una api trasparente con profilo spcoop, devo aggiungere qualche vincolo di sorta?
#       SI, DEVO ANDARE A GUARDARE IL FORMATO\TIPO E VEDERE SE SONO AMMESSI DAL PROFILO.

@Create204
Scenario: Creazione Servizi 204

* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
* eval api_spcoop.referente = soggettoDefault
* call create ({ resourcePath: 'api', body: api_spcoop })
* call create_204 ({ resourcePath: api_spcoop_path + '/servizi', body: servizio,  key: servizio.nome })
* call delete ({ resourcePath: api_spcoop_path })

@Create409
Scenario: Creazione Servizi 409

* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
* eval api_spcoop.referente = soggettoDefault
* call create ({ resourcePath: 'api', body: api_spcoop })
* call create_409 ({ resourcePath: api_spcoop_path + '/servizi', body: servizio,  key: servizio.nome })
* call delete ({ resourcePath: api_spcoop_path })

@CreateTrasparente400
Scenario: Creazione Servizio Trasparente 400

* call create ({ resourcePath: 'api', body: api })
* call create_400 ({ resourcePath: api_path + '/servizi', body: servizio,  key: servizio.nome })
* call delete ({ resourcePath: api_path })

@CreateApiInesistente
Scenario: Creazione Servizi Api Inesistente

* call create_400 ({ resourcePath: api_spcoop_path + '/servizi', body: servizio,  key: servizio.nome })



