Feature: Creazione Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])

* def soggetto = read('soggetto.json')
* eval randomize(soggetto, ["nome", "credenziali.username"])
* eval api_spcoop.referente = soggetto.nome

@Create204
Scenario: Api Create 204
    * call create_201 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@Create409
Scenario: Api Create 409
    * call create_409 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@CreateSPCoop204
Scenario: Api Create SPCoop 204
    
    * def query_params = ( { profilo: "SPCoop", soggetto: soggetto.nome })

    * call create ( { resourcePath: 'soggetti', body: soggetto, key: soggetto.nome })
    * call create_201 ( { resourcePath: 'api', body: api_spcoop, key: api_spcoop.nome + '/' + api_spcoop.versione } )
    * call delete ( { resourcePath: 'soggetti/' + soggetto.nome })

@CreateSPCoopNoSoggetto400
Scenario: Api Create SPCoop Referente Not Found
    
    * def query_params = ( { profilo: "SPCoop", soggetto: soggetto.nome })

    Given url configUrl
    And path 'api'
    And  header Authorization = govwayConfAuth
    And request api_spcoop
    And params query_params
    When method post
    Then assert responseStatus == 400

@CreateApiTags
Scenario: Api Create Tags definiti check get
	
* eval api.tags = ['TESTSUITE']
* def api_key = api.nome + '/' + api.versione
		
* call create ( { resourcePath: 'api', body: api, key: api_key } )

* def api_response = call read('classpath:get_stub.feature') { resourcePath: 'api', key:  '#(api_key)' }
* match api_response.get_response_body.tags == '#notnull'
* match api_response.get_response_body.tags[0] == api.tags[0]

* call delete ( { resourcePath: 'api/' + api_key })

@CreateApiTagsFindAll
Scenario: Api Create Tags definiti check findall

* eval api.tags = ['TESTSUITE']
* def api_key = api.nome + '/' + api.versione
		
* call create ( { resourcePath: 'api', body: api, key: api_key } )

* def api_response = call read('classpath:findall_stub.feature') { resourcePath: 'api', query_params:  { tag: '#(api.tags[0])' } }
* match each api_response.findall_response_body.items[*].tags == '#notnull'
* match each api_response.findall_response_body.items[*].tags[*] contains api.tags[0]

* call delete ( { resourcePath: 'api/' + api_key })
    
    
    
