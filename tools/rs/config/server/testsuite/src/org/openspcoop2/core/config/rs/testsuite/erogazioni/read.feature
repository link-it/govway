Feature: Lettura Erogazioni

Background:
* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione
* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key

@FindAll200
Scenario: Erogazioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call findall_200 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Erogazioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_200 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@Get404
Scenario: Erogazioni Get 404

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_404 ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazione
Scenario: Erogazioni Get Url Invocazione PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })


@GetInterfacciaApi
Scenario: Erogazioni Get Interfaccia Api PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetConnettore
Scenario: Erogazioni Get Connettore Erogazione

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })


@FindAllTags
Scenario: Erogazioni FindAll di Api con Tags definiti 

* eval api_petstore.tags = ['TESTSUITE']

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

* def erogazioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'erogazioni', query_params:  { tag: '#(api_petstore.tags[0])' } }
* match each erogazioni_response.findall_response_body.items[*].api_tags == '#notnull'
* match each erogazioni_response.findall_response_body.items[*].api_tags[*] contains api_petstore.tags[0]

* call delete ({ resourcePath: 'erogazioni/' + petstore_key })
* call delete ({ resourcePath: api_petstore_path })


