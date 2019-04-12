Feature: Update Allegati

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione
* def allegato = read('allegato.json')
* def allegato2 = read('allegato2.json')


@Update204
Scenario: Allegati Update 204

    * call create ( { resourcePath: "api", body: api } )
    * call update_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, body_update: allegato2, key: allegato.allegato.nome, delete_key: allegato2.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@Update404
Scenario: Allegati Update 404

    * call create ( { resourcePath: "api", body: api } )
    * call update_404 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

