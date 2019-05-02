Feature: Configurazione Controllo Accessi Autenticazione

Background:
    * def autenticazione = read('classpath:bodies/controllo-accessi-autenticazione-token.json')
    * def authn_basic = read('classpath:bodies/controllo-accessi-autenticazione-http-no-token.json')
    * def authn_principal = read('classpath:bodies/controllo-accessi-autenticazione-principal-no-token.json')

@UpdateGestioneToken204
Scenario: Update Autenticazione con Gestione Token abilitata

   * def gestione_token_body = read('classpath:bodies/gestione-token-petstore.json')

   #Prima abilitiamo la gestione token
   Given url configUrl
   And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
   And header Authorization = govwayConfAuth
   And request gestione_token_body
   And params query_params
   When method put
   Then status 204

   Given url configUrl
   And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
   And header Authorization = govwayConfAuth
   And request autenticazione
   And params query_params
   When method put
   Then status 204

   Given url configUrl
   And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
   And header Authorization = govwayConfAuth
   And params query_params
   When method get
   Then status 200
   And match response.autenticazione.tipo == "http-basic"
   And match response.token contains autenticazione.token


@Get200
Scenario: Get Autenticazione

   Given url configUrl
   And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
   And header Authorization = govwayConfAuth
   And params query_params
   When method get
   Then status 200


@UpdateBasicForwardAuthorization204
Scenario: Imposta la autenticazione in modalità basic con ForwardAuthorization abilitato
    * eval authn_basic.autenticazione.configurazione = { forward: true }

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request authn_basic
    And params query_params
    When method put
    Then status 204

    #Recupero l'autenticazione e controllo che la forward authorization sia stata abilitata
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione contains { tipo: 'http-basic', configurazione: { forward: true } }

@UpdatePrincipalHeaderBased
Scenario: Imposta l'autenticazione in modalità principal\header-based
    * def configurazione = { tipo: 'header-based', nome: 'header1', forward: true }
    * eval authn_principal.autenticazione.configurazione = configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request authn_principal
    And params query_params
    When method put
    Then status 204

    #Recupero l'autenticazione e controllo che la forward authorization sia stata abilitata
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione.tipo == 'principal'
    And match response.autenticazione.configurazione contains configurazione


#container, header-based, form-based, url-based, ip-address ]
@UpdatePrincipalFormBased
Scenario: Imposta l'autenticazione in modalità principal\form-based
    * def configurazione = { tipo: 'form-based', nome: 'header1', forward: true }
    * eval authn_principal.autenticazione.configurazione = configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request authn_principal
    And params query_params
    When method put
    Then status 204

    #Recupero l'autenticazione e controllo che la forward authorization sia stata abilitata
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione.tipo == 'principal'
    And match response.autenticazione.configurazione contains configurazione


@UpdatePrincipalUrlBased
Scenario: Imposta l'autenticazione in modalità principal\url-based
    * def configurazione = { tipo: 'url-based', pattern: '*.*' }
    * eval authn_principal.autenticazione.configurazione = configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request authn_principal
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione.tipo == 'principal'
    And match response.autenticazione.configurazione contains configurazione

@UpdatePrincipalIpBased
Scenario: Imposta l'autenticazione in modalità principal\ip-based

    * def configurazione = { tipo: 'ip-address' }
    * eval authn_principal.autenticazione.configurazione = configurazione

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request authn_principal
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione.tipo == 'principal'
    And match response.autenticazione.configurazione contains configurazione


@UpdateAutenticazioneCustom
Scenario: Imposta l'autenticazione in modalità custom

    * def autenticazione_custom = read('classpath:bodies/controllo-accessi-autenticazione-custom.json')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request autenticazione_custom
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200    
    And match response.autenticazione.tipo == 'custom'
    And match response.autenticazione.configurazione.nome == autenticazione_custom.autenticazione.configurazione.nome
    And match response.autenticazione.opzionale == true