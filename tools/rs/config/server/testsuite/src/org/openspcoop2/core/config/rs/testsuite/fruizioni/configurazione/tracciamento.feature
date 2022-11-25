Feature: Configurazione Tracciamento Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def erogatore = read('../soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def api_spcoop = read('../api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def fruizione_petstore = read('../fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione
* def fruizione_petstore_path = 'fruizioni/' + fruizione_key
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

@Validazione
Scenario Outline: Configurazione Correlazione applicativa

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    * def conf = call read('classpath:servizi-configurazione/tracciamento.feature') ({servizio_path: fruizione_petstore_path, richiesta: richiesta, richiesta_update: richiesta_update, risposta: risposta, risposta_update: risposta_update })

    * call delete ({ resourcePath: fruizione_petstore_path })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|richiesta|richiesta_update|risposta|risposta_update|descrizione|
|correlazione-applicativa-richiesta.json|correlazione-applicativa-richiesta-update.json|correlazione-applicativa-risposta.json|correlazione-applicativa-risposta-update.json|richiesta è url-based, update richiesta è header-based, risposta è header-based, update riposta è disabilitato|
|correlazione-applicativa-richiesta-content-based.json|correlazione-applicativa-richiesta-input-based-update.json|correlazione-applicativa-template-risposta.json|correlazione-applicativa-risposta-content-based-update.json|si capisce dai nomi|
|correlazione-applicativa-richiesta-disabilitato.json|correlazione-applicativa-richiesta-freemarker-template-based-update.json|correlazione-applicativa-velocity-template-risposta.json|correlazione-applicativa-risposta-velocity-template-update.json|si capisce dai nomi|
