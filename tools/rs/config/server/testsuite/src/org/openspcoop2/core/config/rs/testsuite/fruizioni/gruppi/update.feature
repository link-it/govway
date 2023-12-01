Feature: Update Gruppi Fruizioni

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

* def gruppo_petstore = read('gruppo_petstore.json')
* def gruppo_update = read('gruppo_nome.json')

* def descrizione_4000 = read('../../api/api_descrizione_4000.json')
* def descrizione_4001 = read('../../api/api_descrizione_4001.json')
* def descrizione_null = read('../../api/api_descrizione_null.json')

@Update204
Scenario: Update Gruppi Fruizioni 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    * call update_204 ({ resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_petstore, body_update: gruppo_update, key: gruppo_petstore.nome, delete_key: gruppo_update.nome })
    
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Update404
Scenario: Update Gruppi Fruizioni 404

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    * call update_404 ({ resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_update, key: gruppo_petstore.nome })
    
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@UpdateDescrizione
Scenario: Gruppi Fruizioni Update Descrizione 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_petstore })

    Given url configUrl
    And path fruizione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4000
    When method put
    Then status 204

    Given url configUrl
    And path fruizione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descrizione_4000.descrizione

    Given url configUrl
    And path fruizione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4001
    When method put
    Then status 400

    * match response.detail contains 'Max length is \'4000\', found \'4001\''

    Given url configUrl
    And path fruizione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_null
    When method put
    Then status 204

    Given url configUrl
    And path fruizione_petstore_path, 'gruppi', gruppo_petstore.nome , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'
    
    * call delete ({ resourcePath: fruizione_petstore_path + '/gruppi/' + gruppo_petstore.nome})
    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
