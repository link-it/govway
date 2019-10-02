Feature: Lettura Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json') 
* eval randomize(api, ["nome"])

@FindAll200
Scenario: Api FindAll 200 OK
    
    * call findall_200 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@Get200
Scenario: Api Get 200 OK

    * call get_200 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@Get404
Scenario: Api Get 404

    * call get_404 ( { resourcePath: "api/" + api.nome + "/" + api.versione } )

@GetInterfaccia
Scenario: Api Get Interfaccia

    * call create ( { resourcePath: "api", body: api } )
    
    Given url configUrl
    And path 'api', api.nome, api.versione, 'interfaccia'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ( { resourcePath: "api/" + api.nome + "/" + api.versione })


@DownloadInterfaccia
Scenario: Api Download Interfaccia

    * call create ( { resourcePath: "api", body: api } )
    
    Given url configUrl
    And path 'api', api.nome, api.versione, 'interfaccia', 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ( { resourcePath: "api/" + api.nome + "/" + api.versione })



@GetReferente
Scenario: Api Get Api Referente

    * call create ( { resourcePath: "api", body: api } )
    
    Given url configUrl
    And path 'api', api.nome, api.versione, 'referente'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ( { resourcePath: "api/" + api.nome + "/" + api.versione })

@GetDescrizione
Scenario: Api Get Api Descrizione

    * call create ( { resourcePath: "api", body: api } )
    
    Given url configUrl
    And path 'api', api.nome, api.versione, 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ( { resourcePath: "api/" + api.nome + "/" + api.versione })
    
@GetTags
Scenario: Api Get Api Tags

* def api_key = api.nome + '/' + api.versione
* eval api.tags = ['TESTSUITE', 'TESTSUITE2']
		
* call create ( { resourcePath: 'api', body: api, key: api_key } )

* def api_tags_get_key = api_key + '/tags'
* def api_tags_response = call read('classpath:get_stub.feature') { resourcePath: 'api', key:  '#(api_tags_get_key)' }

* match api_tags_response.get_response_body.tags == '#notnull'
* match api_tags_response.get_response_body.tags == '#[2]'
* match api_tags_response.get_response_body.tags contains api.tags[0]
* match api_tags_response.get_response_body.tags contains api.tags[1]

* call delete ( { resourcePath: 'api/' + api_key })

#@GetAllegati
#Scenario: Get Api Allegati
#
#@GetAllegato
#Scenario: Get Api Allegato specifico
#
#@DownloadAllegato
#Scenario: Download Allegato
#
#@GetRisorsa
#Scenario: Get Api Risorsa
