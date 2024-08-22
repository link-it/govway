Feature: Delete Allegati

Background:

* call read('classpath:crud_commons.feature')
* def randomize = read('classpath:randomize.js')


  * def api = read('classpath:org/openspcoop2/core/config/rs/testsuite/api/api.json')
  * eval randomize(api, ["nome"])



* def api_path = api.nome + '/' + api.versione
* def allegato = read('allegato.json')

@Delete204
Scenario: Allegati Delete 204
    
    * call create ( { resourcePath: "api", body: api } )
    * call delete_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@Delete404
Scenario: Allegati Delete 404
    
    * call create ( { resourcePath: "api", body: api } )
    * call delete_404 ( { resourcePath: 'api/' + api_path + '/allegati' , key: allegato.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })
