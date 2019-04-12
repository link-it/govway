Feature: Lettura Servizi

Background:
* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval api.referente = soggettoDefault
* eval randomize(api, ["nome"])

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def api_path = api.nome + '/' + api.versione

@FindAll200
Scenario: Servizi FindAll 200 OK

    * def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
    * call create ({ resourcePath: "api", body: api })    
    * call findall_200 ({ resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome })
    * call delete ( { resourcePath: "api/" + api_path })

@Get200
Scenario: Servizi Get 200 OK

    * def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
    * call create ({ resourcePath: "api", body: api } )    
    * call get_200 ({ resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome })
    * call delete ({ resourcePath: "api/" + api_path })

@Get404
Scenario: Servizi Get 404

    * def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })
    * call create ({ resourcePath: "api", body: api } )    
    * call get_404 ({ resourcePath: 'api/' + api_path + '/servizi/' + servizio.nome })

