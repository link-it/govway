Feature: Update Api

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def interfaccia = read('interfaccia_petstore.json')

* def info_generali = read('api_info_generali.json')
* eval randomize( info_generali, ["nome"])

* def descrizione = read('api_descrizione.json')

@UpdateInterfaccia204
Scenario: Api Update Interfaccia 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: interfaccia, key: api.nome + '/' + api.versione + '/interfaccia', delete_key: api.nome + '/' + api.versione } )

@UpdateInfoGenerali204
Scenario: Api Update Info Generali 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: info_generali, key: api.nome + '/' + api.versione + '/informazioni', delete_key: info_generali.nome + '/' + info_generali.versione } )

@UpdateDescrizione204
Scenario: Api Update Descrizione 204

    * call update_204 ( { resourcePath: 'api', body: api, body_update: descrizione, key: api.nome + '/' + api.versione + '/descrizione', delete_key: api.nome + '/' + api.versione } )
