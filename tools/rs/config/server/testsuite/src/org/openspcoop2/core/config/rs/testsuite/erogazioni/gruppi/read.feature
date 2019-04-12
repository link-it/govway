Feature: Lettura Gruppi Erogazioni
 
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

@FindAll200
Scenario: Gruppi Erogazioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call findall_200 ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Gruppi Erogazioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call get_200 ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore, key: gruppo_petstore.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
    
@Get404
Scenario: Gruppi Erogazioni Get 404

    * call get_404 ({ resourcePath: erogazione_petstore_path + '/gruppi/' + gruppo_petstore.nome })

