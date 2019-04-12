Feature: Lettura Fruizioni

Background:
* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione

* def api_info_generali = read('api_info_generali.json')

* def connettore = read('connettore_erogazione_petstore.json')
* def info_generali = read('informazioni_generali_petstore.json')
* def url_invocazione = read('erogazione_petstore_url_invocazione.json')
* def erogazione_versione = read('api_versione3.json')

@FindAll200
Scenario: Fruizioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })

    * call findall_200 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: fruizione_key })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@Get200
Scenario: Fruizioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    
    * call get_200 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: fruizione_key })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Get404
Scenario: Fruizioni Get 404

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_404 ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazione
Scenario: Fruizioni Get Url Invocazione PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path 'fruizioni', fruizione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@GetInterfacciaApi
Scenario: Fruizioni Get Interfaccia Api PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', fruizione_key, 'api'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@GetConnettore
Scenario: Fruizioni Get Connettore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', fruizione_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
