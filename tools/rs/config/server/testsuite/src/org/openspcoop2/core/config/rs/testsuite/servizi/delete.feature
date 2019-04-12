Feature: Delete Servizi

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def query_params = { profilo: "SPCoop" }
* def api_path = api.nome + '/' + api.versione

@Delete204
Scenario: Delete Servizi 204
    
    * call create ( { resourcePath: "api", body: api } )
    * call delete_204 ( { resourcePath: 'api/' + api_path + '/servizi', body: servizio, key: servizio.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@Delete404
Scenario: Delete Servizi 404
    
    * call create ( { resourcePath: "api", body: api } )
    * call delete_404 ( { resourcePath: 'api/' + api_path + '/servizi' , key: servizio.nome } )
    * call delete ( { resourcePath: "api/" + api_path })
