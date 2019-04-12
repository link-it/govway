Feature: Update Gruppi Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def erogazione_petstore = read('../erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key
* def erogazione_petstore_path = 'erogazioni/' + petstore_key

* def gruppo_petstore = read('gruppo_petstore.json')
* def gruppo_update = read('gruppo_nome.json')

@Update204
Scenario: Update Gruppi Erogazioni 204

    * call create ({ resourcePath: "api", body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * call update_204 ({ resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, body_update: gruppo_update, key: gruppo_petstore.nome, delete_key: gruppo_update.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Update404
Scenario: Update Gruppi Erogazioni 404

    * call create ({ resourcePath: "api", body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call update_404 ({ resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_update, key: gruppo_petstore.nome })
    
    * call delete ({ resourcePath:  erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

