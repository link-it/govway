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

#TODO: CREAZIONE API SDI

@Create204
Scenario: Api Create 204
    * call create_204 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@Create409
Scenario: Api Create 409
    * call create_409 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@CreateSPCoop204
Scenario: Api Create SPCoop 204
    
    * def query_params = ( { profilo: "SPCoop", soggetto: soggetto.nome })

    * call create ( { resourcePath: 'soggetti', body: soggetto, key: soggetto.nome })
    * call create_204 ( { resourcePath: 'api', body: api_spcoop, key: api_spcoop.nome + '/' + api_spcoop.versione } )
    * call delete ( { resourcePath: 'soggetti/' + soggetto.nome })

@CreateSPCoopNoSoggetto400
Scenario:Api  Create SPCoop Referente Not Found
    
    * def query_params = ( { profilo: "SPCoop", soggetto: soggetto.nome })

    Given url configUrl
    And path 'api'
    And  header Authorization = govwayConfAuth
    And request api_spcoop
    And params query_params
    When method post
    Then assert responseStatus == 400

    
