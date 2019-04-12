Feature: Configurazione Controllo Accessi Autorizzazione

Background:

    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
    * def autorizzazione_xacml = read('classpath:bodies/controllo-accessi-autorizzazione-xacml.json')
    * def autorizzazione_custom = read('classpath:bodies/controllo-accessi-autorizzazione-custom.json')


@UpdateAutorizzazione
Scenario: Update Autorizzazione

    * eval autorizzazione.autorizzazione.configurazione = ({ puntuale: true, ruoli: true, scope: false })

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

@UpdateAutorizzazioneXacml
Scenario: Update Autorizzazione Xacml

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione_xacml
    And params query_params
    When method put
    Then status 204

@UpdateAutorizzazioneCustom
Scenario: Update Autorizzazione Custom

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione_custom
    And params query_params
    When method put
    Then status 204

@GetAutorizzazione
Scenario: Get Autorizzazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200


@DownloadXacmlPolicy200
Scenario: DOWNLOAD XACML POLICY

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione_xacml
    And params query_params
    When method put
    Then status 204


    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'download-xacml-policy'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200


@AutorizzazioneApplicativiPuntuale
Scenario: Controllo accessi autorizzazione applicativi puntuale
 
    # Abilito l'autenticazione http
    * def autenticazione = read('classpath:bodies/controllo-accessi-autenticazione-http-no-token.json')
    
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
    Then status 204

    # Aggiungo l'applicativo all'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_params
    When method post
    Then status 204

    # Recupero l'applicativo appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    # TODO: matchare la risposta, che deve contenere l'applicativo appena aggiunto.

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


@AutorizzazioneRuoli
Scenario: Controllo accessi autorizzazione ruoli

    * eval autorizzazione.autorizzazione.configurazione = ({ puntuale: false, ruoli: true, scope: false })
    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per ruoli
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def ruolo_registro = read('classpath:bodies/ruolo.json')
    * eval randomize(ruolo_registro, ["nome"] )
    * def ruolo = ({ ruolo: ruolo_registro.nome })
    
    # Creo il ruolo nel registro
    Given url configUrl
    And path 'ruoli'
    And header Authorization = govwayConfAuth
    And request ruolo_registro
    And params query_params
    When method post
    Then status 204

    # Aggiungo il ruolo all'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'ruoli'
    And header Authorization = govwayConfAuth
    And request ruolo
    And params query_params
    When method post
    Then status 204

    # Recupero il ruolo appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'ruoli'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    # TODO: matchare la risposta, che deve contenere il ruolo appena aggiunto.

    # Rimuovo il ruolo
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'ruoli', ruolo.ruolo
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

@AutorizzazioneScope
Scenario: Controllo Accessi Autorizzazione Scope

    # Abilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_params
    When method put
    Then status 204

    # Abilito l'autorizzazione per scope
    * eval autorizzazione.autorizzazione.configurazione = ({ puntuale: false, ruoli: false, scope: true })

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_params
    When method put
    Then status 204

    * def scope_registro = read('classpath:bodies/scope.json')
    * eval randomize(scope_registro, ["nome"])
    * def scope = ({ scope: scope_registro.nome })

    # Creo lo scope nel registro
    * call create ({ resourcePath: 'scope', body: scope_registro })

    # Aggiungo lo scope all'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'scope'
    And header Authorization = govwayConfAuth
    And request scope
    And params query_params
    When method post
    Then status 204

    # Recupero lo scope appena aggiunto per mezzo della findall
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'scope'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200

    # TODO: matchare la risposta, che deve contenere l'applicativo appena aggiunto.

    # Rimuovo lo scope dall'autorizzazione
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'scope', scope.scope
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    # Rimuovo lo scope dal registro
    * call delete ({ resourcePath: 'scope/' +  scope_registro.nome })
