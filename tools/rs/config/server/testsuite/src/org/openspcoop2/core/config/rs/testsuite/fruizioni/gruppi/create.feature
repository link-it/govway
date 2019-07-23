Feature: Create Gruppi Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def erogatore = read('../soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('../fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione
* def fruizione_petstore_path = 'fruizioni/' + fruizione_key

* def gruppo_petstore = read ('gruppo_petstore.json')
* def gruppo_eredita = read('gruppo_petstore_eredita.json')

@Create204
Scenario: Create Gruppi Fruizioni 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call create_201 ( { resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})
    
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@CreateEredita204
Scenario: Create Gruppi Fruizioni Eredita 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_petstore })
    
    * eval gruppo_eredita.configurazione.nome = gruppo_petstore.nome
    * call create_201 ( { resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_eredita, key: gruppo_eredita.nome })
    
    * call delete ({ resourcePath: fruizione_petstore_path + '/gruppi/' + gruppo_petstore.nome})
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Create409
Scenario: Create Gruppi Fruizioni 409

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call create_409 ( { resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome})
    
    * call delete ({ resourcePath: fruizione_petstore_path  })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
