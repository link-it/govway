Feature: Create Gruppi Erogazioni

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

* def gruppo_petstore = read ('gruppo_petstore.json')
* def gruppo_eredita = read('gruppo_petstore_eredita.json')

@Create204
Scenario: Create Gruppi Erogazioni 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_204 ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@CreateEredita204
Scenario: Create Gruppi Erogazioni Eredita 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ({ resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore })
    
    * eval gruppo_eredita.configurazione.nome = gruppo_petstore.nome
    * call create_204 ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_eredita, key: gruppo_eredita.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path + '/gruppi/' + gruppo_petstore.nome})
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Create409
Scenario: Create Gruppi Erogazioni 409

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_409 ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
