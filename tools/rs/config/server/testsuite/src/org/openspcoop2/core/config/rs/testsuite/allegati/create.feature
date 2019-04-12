Feature: Create Allegati

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione

* def allegato = read('allegato.json')
* def allegato2 = read('allegato_specificasemiformale.json')

@Create204
Scenario: Allegati Create 204

    * call create ( { resourcePath: "api", body: api } )
    * call create_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })

@Create409
Scenario: Allegati Create 409

    * call create ( { resourcePath: "api", body: api } )
    * call create_409 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })

@CreateSpecSemiformale204
Scenario: Allegati Create Specifica Semiformale 204

    * call create ( { resourcePath: "api", body: api } )
    * call create_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato2, key: allegato2.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })
