Feature: Configurazione Autorizzazione Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_spcoop = read('../api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def query_param_profilo_modi = {'profilo': 'ModI'}

* def erogazione_petstore = read('../erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def erogazione_spcoop = read('../erogazione_spcoop.json')
* eval erogazione_spcoop.api_nome = api_spcoop.nome
* eval erogazione_spcoop.api_versione = api_spcoop.versione
* eval erogazione_spcoop.api_referente = api_spcoop.referente

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

@AutorizzazioneApplicativiEsterniPuntuale
Scenario: Controllo accessi autorizzazione applicativi esterni puntuale per le erogazioni
 
    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')

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

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * def applicativo = read('classpath:bodies/applicativo_http.json')
    * eval randomize(applicativo, ["nome", "credenziali.username"])

    * def applicativo_puntuale = ({ soggetto: soggetto_esterno.nome , applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
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
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Rimuovo l'applicativo dall'autorizzazione
    * def query_param_applicativi_remove_authz = {'soggetto_applicativo' : '#(soggetto_esterno.nome)'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })



@AutorizzazioneApplicativiTokenPuntuale
Scenario: Controllo accessi autorizzazione applicativi token puntuale per le erogazioni
 
    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def servizio_path = erogazione_petstore_path

    # Abilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_params
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def applicativo = read('classpath:bodies/applicativo_token.json')
    * eval randomize(applicativo, ["nome", "credenziali.identificativo"])

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
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_params
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Rimuovo l'applicativo dall'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi', applicativo_puntuale.applicativo
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



@AutorizzazioneApplicativiTokenEsterniPuntuale
Scenario: Controllo accessi autorizzazione applicativi token esterni puntuale per le erogazioni
 
    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def servizio_path = erogazione_petstore_path

    # Abilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_params
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * def applicativo = read('classpath:bodies/applicativo_token.json')
    * eval randomize(applicativo, ["nome", "credenziali.identificativo"])

    * def applicativo_puntuale = ({ soggetto: soggetto_esterno.nome , applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_params
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Rimuovo l'applicativo dall'autorizzazione
    * def query_param_applicativi_remove_authz = {'soggetto_applicativo' : '#(soggetto_esterno.nome)'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})

    * call delete ({ resourcePath: erogazione_petstore_path })
    * call delete ({ resourcePath: api_petstore_path })

