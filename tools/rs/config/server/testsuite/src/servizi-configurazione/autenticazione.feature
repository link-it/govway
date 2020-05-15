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
    * eval authn_basic.autenticazione.forward = true

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
    And match response.autenticazione contains { tipo: 'http-basic', forward: true }

@UpdatePrincipalHeaderBased
Scenario: Imposta l'autenticazione in modalità principal\header-based
    * def options = { tipo_principal: 'header-based', nome: 'header1', forward: true }
    * eval authn_principal.autenticazione.tipo_principal = 'header-based'
    * eval authn_principal.autenticazione.nome = 'header1'
    * eval authn_principal.autenticazione.forward = true

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
    And match response.autenticazione contains options


#container, header-based, form-based, url-based, ip-address ]
@UpdatePrincipalFormBased
Scenario: Imposta l'autenticazione in modalità principal\form-based
    * def options = { tipo_principal: 'form-based', nome: 'header1', forward: true }
    * eval authn_principal.autenticazione.tipo_principal = 'form-based'
    * eval authn_principal.autenticazione.nome = 'header1'
    * eval authn_principal.autenticazione.forward = true

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
    And match response.autenticazione contains options


@UpdatePrincipalUrlBased
Scenario: Imposta l'autenticazione in modalità principal\url-based
    * def options = { tipo_principal: 'url-based', pattern: '*.*' }
    * eval authn_principal.autenticazione.tipo_principal = 'url-based'
    * eval authn_principal.autenticazione.pattern = '*.*'

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
    And match response.autenticazione contains options

@UpdatePrincipalIpBased
Scenario: Imposta l'autenticazione in modalità principal\ip-based

    * def options = { tipo_principal: 'ip-address' }
    * eval authn_principal.autenticazione.tipo_principal = 'ip-address'

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
    And match response.autenticazione contains options

@UpdateTokenSubject
Scenario: Imposta l'autenticazione in modalità principal\token\subject
    * def options = { tipo_principal: 'token', token: 'subject' }
    * eval authn_principal.autenticazione.tipo_principal = 'token'
    * eval authn_principal.autenticazione.token = 'subject'

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
    And match response.autenticazione contains options

@UpdateTokenClientId
Scenario: Imposta l'autenticazione in modalità principal\token\clientId
    * def options = { tipo_principal: 'token', token: 'clientId' }
    * eval authn_principal.autenticazione.tipo_principal = 'token'
    * eval authn_principal.autenticazione.token = 'clientId'

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
    And match response.autenticazione contains options

@UpdateTokenUsername
Scenario: Imposta l'autenticazione in modalità principal\token\username
    * def options = { tipo_principal: 'token', token: 'username' }
    * eval authn_principal.autenticazione.tipo_principal = 'token'
    * eval authn_principal.autenticazione.token = 'username'

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
    And match response.autenticazione contains options

@UpdateTokenEMail
Scenario: Imposta l'autenticazione in modalità principal\token\eMail
    * def options = { tipo_principal: 'token', token: 'eMail' }
    * eval authn_principal.autenticazione.tipo_principal = 'token'
    * eval authn_principal.autenticazione.token = 'eMail'

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
    And match response.autenticazione contains options

@UpdateTokenCustom
Scenario: Imposta l'autenticazione in modalità principal\token\custom
    * def options = { tipo_principal: 'token', token: 'custom' , nome: 'aud' }
    * eval authn_principal.autenticazione.tipo_principal = 'token'
    * eval authn_principal.autenticazione.token = 'custom'
    * eval authn_principal.autenticazione.nome = 'aud'

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
    And match response.autenticazione contains options


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
    And match response.autenticazione.nome == autenticazione_custom.autenticazione.nome
    And match response.autenticazione.opzionale == true
