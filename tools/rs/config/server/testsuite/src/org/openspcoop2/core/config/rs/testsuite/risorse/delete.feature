Feature: Delete Azioni

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def risorsa = read('risorsa.json')
* eval randomize(risorsa, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def risorse_path = api_path + '/risorse/'

@Delete204
Scenario: Delete Azioni 204

    * call create ({ resourcePath: 'api', body: api })
    * call delete_204 ({ resourcePath: risorse_path, body: risorsa,  key: risorsa.nome })
    * call delete ({ resourcePath: api_path })

@Delete404
Scenario: Delete Azioni 404

    * call create ({ resourcePath: 'api', body: api })
    * call delete_404 ({ resourcePath: risorse_path, key: risorsa.nome })
    * call delete ({ resourcePath: api_path })
