Feature: Lettura Allegati Fruizioni
 
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

* def allegato = read('allegato.json')

@FindAll200
Scenario: Allegati Fruizioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call findall_200 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Allegati Fruizioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call get_200 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@DownloadAllegato
Scenario: Allegati Fruizioni Download

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato })

    Given url configUrl
    And path fruizione_petstore_path, "allegati", allegato.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato.allegato.nome })
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
    
@Get404
Scenario: Allegati Fruizioni Get 404

    * call get_404 ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato.allegato.nome })

