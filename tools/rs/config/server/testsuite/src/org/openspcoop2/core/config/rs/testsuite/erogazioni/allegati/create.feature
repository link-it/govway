Feature: Create Allegati Erogazioni

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

* def allegato = read('allegato.json')
* def allegatoSSem = read('allegato_specificasemiformale.json')
* def allegatoSLivSer = read('allegato_specificaLivelloServizio.json')
* def allegatoSSec = read('allegato_specificaSicurezza.json')

@Create204
Scenario: Allegati Erogazioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_201 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })


@Create409
Scenario: Allegati Erogazioni Create 409

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_409 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Create204SpecificaSemiformale
Scenario: Allegati SpecificaSemiformale Erogazioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_201 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegatoSSem, key: allegatoSSem.allegato.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Create204SpecificaLivelloServizio
Scenario: Allegati SpecificaLivelloServizio Erogazioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_201 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegatoSLivSer, key: allegatoSLivSer.allegato.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Create204SpecificaSicurezza
Scenario: Allegati SpecificaSicurezza Erogazioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call create_201 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegatoSSec, key: allegatoSSec.allegato.nome})
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
