Feature: Configurazione Erogazioni Tracciamento

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_spcoop = read('../api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def erogazione_petstore = read('../erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def erogazione_petstore_path = 'erogazioni/' + petstore_key
* def api_petstore_path = 'api/' + petstore_key

@Tracciamento
Scenario Outline: Configurazione Tracciamento

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def conf = call read('classpath:servizi-configurazione/tracciamento.feature') ({servizio_path: erogazione_petstore_path, richiesta: richiesta, richiesta_update: richiesta_update, risposta: risposta, risposta_update: risposta_update })

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|richiesta|richiesta_update|risposta|risposta_update|descrizione|
|correlazione-applicativa-richiesta.json|correlazione-applicativa-richiesta-update.json|correlazione-applicativa-risposta.json|correlazione-applicativa-risposta-update.json|richiesta è url-based, update richiesta è header-based, risposta è header-based, update riposta è disabilitato|
|correlazione-applicativa-richiesta-content-based.json|correlazione-applicativa-richiesta-input-based-update.json|correlazione-applicativa-template-risposta.json|correlazione-applicativa-risposta-content-based-update.json|si capisce dai nomi|
|correlazione-applicativa-richiesta-disabilitato.json|correlazione-applicativa-richiesta-freemarker-template-based-update.json|correlazione-applicativa-velocity-template-risposta.json|correlazione-applicativa-risposta-velocity-template-update.json|si capisce dai nomi|
