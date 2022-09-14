Feature: Configurazione Autorizzazione Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_spcoop = read('../api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def query_param_profilo_spcoop = {'profilo': 'SPCoop'}

* def erogazione_spcoop = read('../erogazione_spcoop_noauthn_noauthz.json')
* eval erogazione_spcoop.erogazione_nome = api_spcoop.nome
* eval erogazione_spcoop.api_nome = api_spcoop.nome
* eval erogazione_spcoop.api_versione = api_spcoop.versione
* eval erogazione_spcoop.api_referente = api_spcoop.referente

* def petstore_key = erogazione_spcoop.api_nome + '/' + erogazione_spcoop.api_versione
* def erogazione_petstore_path = 'erogazioni/' + petstore_key
* def api_petstore_path = 'api/' + petstore_key

@AutorizzazioneSoggettiPuntuale
Scenario: Configurazione Autorizzazione Erogazioni add Soggetto in Autorizzazione Puntuale

    * call create ({ resourcePath: 'api', body: api_spcoop, query_params: query_param_profilo_spcoop })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_spcoop, query_params: query_param_profilo_spcoop })

    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')

    # Imposto l'autorizzazione in modo che supporti soggetti puntuali
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_param_profilo_spcoop
    When method put
    Then status 204

    * def soggetto_puntuale = read('classpath:bodies/controllo-accessi-autenticazione-soggetto.json')
    * def soggetto = read('classpath:bodies/soggetto-esterno-nocredentials.json')
    * eval randomize(soggetto, ["nome"])
    * eval soggetto_puntuale.soggetto = soggetto.nome

    # Creo il soggetto nel registro
    Given url configUrl
    And path 'soggetti'
    And header Authorization = govwayConfAuth
    And request soggetto
    And params query_param_profilo_spcoop
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
    And params query_param_profilo_spcoop
    When method get
    Then status 200

    # Rimuovo Il soggetto dall'autorizzazione
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'soggetti', soggetto_puntuale.soggetto
    And header Authorization = govwayConfAuth
    And params query_param_profilo_spcoop
    When method delete
    Then status 204

    # Rimuovo il soggetto dal registro
    Given url configUrl
    And path 'soggetti', soggetto.nome
    And header Authorization = govwayConfAuth
    And params query_param_profilo_spcoop
    When method delete
    Then status 204

    * call delete ({ resourcePath: erogazione_petstore_path, query_params: query_param_profilo_spcoop })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_spcoop })

