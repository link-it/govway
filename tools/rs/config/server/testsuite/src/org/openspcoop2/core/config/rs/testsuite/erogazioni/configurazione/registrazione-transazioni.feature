Feature: Configurazione Registrazione Transazioni Erogazioni

Background:

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


@GestioneTransazioni
Scenario: Configurazione Registrazione Transazioni Erogazioni

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def conf = call read('classpath:servizi-configurazione/transazioni.feature') ({servizio_path: erogazione_petstore_path })

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

