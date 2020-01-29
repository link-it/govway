Feature: Configurazione Autorizzazione Erogazioni

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

@Autorizzazione
Scenario: Configurazione Autorizzazione Erogazioni

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def conf = call read('classpath:servizi-configurazione/autorizzazione.feature') ({ servizio_path: erogazione_petstore_path })

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@AutorizzazioneSoggettiPuntuale
Scenario: Configurazione Autorizzazione Erogazioni add Soggetto in Autorizzazione Puntuale

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')

    # Imposto l'autorizzazione in modo che supporti soggetti puntuali
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def autenticazione = read('classpath:bodies/controllo-accessi-autenticazione-http-no-token.json')
    
    # Abilito l'autenticazione http
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request autenticazione
    And params query_params
    When method put
    Then status 204

    * def soggetto_puntuale = read('classpath:bodies/controllo-accessi-autenticazione-soggetto.json')
    * def soggetto = read('classpath:bodies/soggetto-esterno-http.json')
    * eval randomize(soggetto, ["nome", "credenziali.username"])
    * eval soggetto_puntuale.soggetto = soggetto.nome

    # Creo il soggetto nel registro
    Given url configUrl
    And path 'soggetti'
    And header Authorization = govwayConfAuth
    And request soggetto
    And params query_params
    When method post
    Then status 201

    # Aggiungo il soggetto
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'soggetti'
    And header Authorization = govwayConfAuth
    And request soggetto_puntuale
    And params query_params
    When method post
    Then status 201

    # Recupero il soggetto appena aggiunto per mezzo della findall
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'soggetti'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    # Rimuovo Il soggetto dall'autorizzazione
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'soggetti', soggetto_puntuale.soggetto
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    # Rimuovo il soggetto dal registro
    Given url configUrl
    And path 'soggetti', soggetto.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

@AutorizzazioneApplicativiPuntuale
Scenario: Controllo accessi autorizzazione applicativi puntuale per le erogazioni
 
    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
    * def autorizzazione_xacml = read('classpath:bodies/controllo-accessi-autorizzazione-xacml.json')
    * def autorizzazione_custom = read('classpath:bodies/controllo-accessi-autorizzazione-custom.json')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    # Abilito l'autenticazione http
    * def autenticazione = read('classpath:bodies/controllo-accessi-autenticazione-http-no-token.json')
    * def servizio_path = erogazione_petstore_path
    
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request autenticazione
    And params query_params
    When method put
    Then status 204

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione puntuale
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def applicativo = read('classpath:bodies/applicativo_http.json')
    * eval randomize(applicativo, ["nome", "credenziali.username"])

    * def applicativo_puntuale = ({ applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_params
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_params
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Rimuovo l'applicativo dall'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })
