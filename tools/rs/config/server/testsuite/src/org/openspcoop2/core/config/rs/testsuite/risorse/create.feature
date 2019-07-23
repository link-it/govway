Feature: Creazione Risorse

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def risorsa = read('risorsa.json')
* eval randomize(risorsa, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione;
* def risorse_path = api_path + '/risorse/'


@Create204
Scenario: Creazione Risorse 204

* call create ({ resourcePath: 'api', body: api })
* call create_201 ({ resourcePath: risorse_path, body: risorsa,  key: risorsa.nome })
* call delete ({ resourcePath: api_path })

@Create409
Scenario: Creazione Risorse 409

* call create ({ resourcePath: 'api', body: api })
* call create_409 ({ resourcePath: risorse_path, body: risorsa,  key: risorsa.nome })
* call delete ({ resourcePath: api_path })

@CreateSPCoop400
Scenario: Creazione Risorsa su Api Soap 400

* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
* call create ({ resourcePath: 'api', body: api_spcoop })
* call create_400 ({ resourcePath: api_spcoop_path + '/risorse', body: risorsa,  key: risorsa.nome })
* call delete ({ resourcePath: api_spcoop_path })

@CreateApiInesistente
Scenario: Creazione Risorsa Api Inesistente

* call create_400 ({ resourcePath: api_spcoop_path + '/risorse', body: risorsa,  key: risorsa.nome })

