Feature: Update Risorse

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def risorsa = read('risorsa.json')
* eval randomize(risorsa, ["nome"])
* def risorsa_update = read('risorsa_update.json')
* eval randomize(risorsa_update, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def risorse_path = api_path + '/risorse/'

@Update204
Scenario: Update Risorse 204
    * call create ({ resourcePath: 'api', body: api })
    * call update_204 ({ resourcePath: risorse_path, body: risorsa, body_update: risorsa_update, key: risorsa.nome, delete_key: risorsa_update.nome })
    * call delete ({ resourcePath: api_path })


@Update404
Scenario: Update Risorse 404

    * call create ({ resourcePath: 'api', body: api })
    * call update_404 ({ resourcePath: risorse_path, body: risorsa, key: risorsa.nome })
    * call delete ({ resourcePath: api_path })
