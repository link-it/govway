Feature: Delete Allegati Erogazioni

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

@Delete204
Scenario: Allegati Erogazioni Delete 204
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call delete_204 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome } )
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Delete404
Scenario: Allegati Erogazioni Delete 404
    
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call delete_404 ( { resourcePath: erogazione_petstore_path + '/allegati' , key: allegato.allegato.nome } )
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
