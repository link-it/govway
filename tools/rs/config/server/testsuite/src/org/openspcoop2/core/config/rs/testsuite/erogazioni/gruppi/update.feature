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

* def descrizione_4000 = read('../../api/api_descrizione_4000.json')
* def descrizione_4001 = read('../../api/api_descrizione_4001.json')
* def descrizione_null = read('../../api/api_descrizione_null.json')

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

@UpdateDescrizione
Scenario: Gruppi Erogazioni Update Descrizione 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ({ resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_petstore })

    Given url configUrl
    And path erogazione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4000
    When method put
    Then status 204

    Given url configUrl
    And path erogazione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descrizione_4000.descrizione

    Given url configUrl
    And path erogazione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4001
    When method put
    Then status 400

    * match response.detail contains 'Max length is \'4000\', found \'4001\''

    Given url configUrl
    And path erogazione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_null
    When method put
    Then status 204

    Given url configUrl
    And path erogazione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'
    
    * call delete ({ resourcePath: erogazione_petstore_path + '/gruppi/' + gruppo_petstore.nome})
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })



