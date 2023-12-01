Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def interfaccia = read('interfaccia_petstore.json')

* def info_generali = read('api_info_generali.json')
* eval randomize( info_generali, ["nome"])

* def descrizione = read('api_descrizione.json')
* def descrizione_4000 = read('api_descrizione_4000.json')
* def descrizione_4001 = read('api_descrizione_4001.json')
* def descrizione_null = read('api_descrizione_null.json')

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

@UpdateDescrizione204_4000
Scenario: Api Update Descrizione 204 (4000 characters)

    * call update_204 ( { resourcePath: 'api', body: api, body_update: descrizione_4000, key: api.nome + '/' + api.versione + '/descrizione', delete_key: api.nome + '/' + api.versione } )

@UpdateDescrizione
Scenario: Api Update Descrizione dove si imposta 4000 e poi null

    * call create ({ resourcePath: 'api', body: api })

    Given url configUrl
    And path 'api', api.nome + '/' + api.versione , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_4000
    When method put
    Then status 204

    Given url configUrl
    And path 'api', api.nome + '/' + api.versione , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descrizione_4000.descrizione

    Given url configUrl
    And path 'api', api.nome + '/' + api.versione , 'descrizione'
    And header Authorization = govwayConfAuth
    And request descrizione_null
    When method put
    Then status 204

    Given url configUrl
    And path 'api', api.nome + '/' + api.versione , 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'

    * def api_path = 'api/' + api.nome + '/' + api.versione
    * call delete ({ resourcePath: api_path } )

@UpdateDescrizione400_4001
Scenario: Api Update Descrizione 400 (4001 characters)

    * call create ({ resourcePath: 'api', body: api })
    * call update_400 ( { resourcePath: 'api/' +api.nome + '/' + api.versione + '/descrizione', body: descrizione_4001 } )
    * match response.detail contains 'Max length is \'4000\', found \'4001\''
    * def api_path = 'api/' + api.nome + '/' + api.versione
    * call delete ({ resourcePath: api_path } )

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
