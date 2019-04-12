Feature: Eliminazione Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json') 
* eval randomize(api, ["nome"])

@Delete204
Scenario: Api Delete 204
    * call delete_204 ( { resourcePath: 'api', body: api, key: api.nome + '/' + api.versione } )

@Delete404
Scenario: Api Delete 404
    * call delete_404 ( { resourcePath: 'api', key: api.nome + '/' + api.versione } )
