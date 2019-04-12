Feature: Lettura Allegati Erogazioni
 
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

@FindAll200
Scenario: Allegati Erogazioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call findall_200 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Allegati Erogazioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call get_200 ( { resourcePath: erogazione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome })
    
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@DownloadAllegato
Scenario: Allegati Erogazioni Download allegato

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    * call create ({ resourcePath: erogazione_petstore_path + '/allegati', body: allegato })

    Given url configUrl
    And path erogazione_petstore_path, "allegati", allegato.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: erogazione_petstore_path + '/allegati/' + allegato.allegato.nome })
    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
    
@Get404
Scenario: Allegati Erogazioni Get 404

    * call get_404 ({ resourcePath: erogazione_petstore_path + '/allegati/' + allegato.allegato.nome })

