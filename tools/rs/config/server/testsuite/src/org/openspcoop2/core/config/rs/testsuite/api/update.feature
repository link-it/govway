Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def interfaccia = read('interfaccia_petstore.json')

* def info_generali = read('api_info_generali.json')
* eval randomize( info_generali, ["nome"])

* def descrizione = read('api_descrizione.json')

* def tags = read('api_tags.json')

@UpdateInterfaccia204
Scenario: Api Update Interfaccia 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: interfaccia, key: api.nome + '/' + api.versione + '/interfaccia', delete_key: api.nome + '/' + api.versione } )

@UpdateInfoGenerali204
Scenario: Api Update Info Generali 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: info_generali, key: api.nome + '/' + api.versione + '/informazioni', delete_key: info_generali.nome + '/' + info_generali.versione } )

@UpdateDescrizione204
Scenario: Api Update Descrizione 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: descrizione, key: api.nome + '/' + api.versione + '/descrizione', delete_key: api.nome + '/' + api.versione } )

@UpdateTags204
Scenario: Api Update Tags 204

* call update_204 ( { resourcePath: 'api', body: api, body_update: tags, key: api.nome + '/' + api.versione + '/tags', delete_key: api.nome + '/' + api.versione } )
    
@UpdateTagsListaVuota
Scenario: Api Update Tags eliminazione Tags associati

* eval api.tags = ['TESTSUITE']
* def api_key = api.nome + '/' + api.versione
		
* call create ( { resourcePath: 'api', body: api, key: api_key } )
* eval tags.tags = []
* call update ( { resourcePath: 'api', body_update: tags, key: api_key + '/tags'} )

* def api_tags_response = call read('classpath:get_stub.feature') { resourcePath: 'api', key:  '#(api_key)' }
* match api_tags_response.get_response_body.tags == '#notpresent'

* call delete ( { resourcePath: 'api/' + api_key })

@UpdateTagsListaNonVuota
Scenario: Api Update Tags associati

* eval api.tags = ['TESTSUITE']
* def api_key = api.nome + '/' + api.versione
		
* call create ( { resourcePath: 'api', body: api, key: api_key } )
* call update ( { resourcePath: 'api', body_update: tags, key: api_key + '/tags'} )

* def api_tags_response = call read('classpath:get_stub.feature') { resourcePath: 'api', key:  '#(api_key)' }
* match api_tags_response.get_response_body.tags == '#notnull'
* match api_tags_response.get_response_body.tags[0] == api.tags[0]

* call delete ( { resourcePath: 'api/' + api_key })
