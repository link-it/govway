Feature: Aggiornamento Applicativi

Background:

    * call read('classpath:crud_commons.feature')

    * def applicativo = read('classpath:bodies/applicativo_http.json') 
    * eval randomize(applicativo, ["nome", "credenziali.username"]);

    * def applicativo_update = read('applicativo_update.json')
    * eval applicativo_update.nome = applicativo.nome
    
    * def applicativo_https_multipleCertificate = read('classpath:bodies/applicativo_https_multipleCertificate.json') 
		* eval randomize(applicativo_https_multipleCertificate, ["nome" ])
		
		* def applicativo_https_multipleCertificate_update = read('applicativo_update.json')
    * eval applicativo_https_multipleCertificate_update.nome = applicativo_https_multipleCertificate.nome
    * eval applicativo_https_multipleCertificate_update.credenziali = applicativo_https_multipleCertificate.credenziali

    * def credenziali_httpBasic = read('classpath:bodies/credenziali_httpBasic.json')
    * eval randomize(credenziali_httpBasic, ["credenziali.username"])

    * def credenziali_httpBasic_noPassword = read('classpath:bodies/credenziali_httpBasic_noPassword.json')
    * eval randomize(credenziali_httpBasic_noPassword, ["credenziali.username"])

    * def credenziali_principal = read('classpath:bodies/credenziali_principal.json')
    * eval randomize(credenziali_principal, ["credenziali.userid"])

    * def credenziali_apikey = read('classpath:bodies/credenziali_apikey.json')

    * def credenziali_multipleApikey = read('classpath:bodies/credenziali_multipleApikey.json')
    
    * def credenziali_https_multipleCertificate = read('classpath:bodies/credenziali_https_multipleCertificate.json')

@Update204
Scenario: Applicativi Aggiornamento 204 OK

    * call update_204 { resourcePath: 'applicativi',  body: '#(applicativo)',  body_update: '#(applicativo_update)',  key: '#(applicativo.nome)',  delete_key: '#(applicativo_update.nome)' }

@Update404
Scenario: Applicativi Aggiornamento 404 

    * call update_404 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Update400
Scenario: Applicativi Aggiornamento Ruolo Inesistente 400

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    * eval applicativo_update.ruoli = [ 'RuoloInesistente_' + random() ]

    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And request applicativo_update
    When method put
    Then status 400

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )
    
@Update204_httpsMultipleCertificato
Scenario: Applicativi Aggiornamento 204 OK (credenziali https, lista certificati)

    * call update_204 { resourcePath: 'applicativi',  body: '#(applicativo_https_multipleCertificate)',  body_update: '#(applicativo_https_multipleCertificate_update)',  key: '#(applicativo_https_multipleCertificate.nome)',  delete_key: '#(applicativo_https_multipleCertificate_update.nome)' }

@UpdateCredenzialiHttpBasic
Scenario: Applicativi Aggiornamento Credenziali HttpBasic

    * def options = { modalita_accesso: 'http-basic', username: '#(credenziali_httpBasic.credenziali.username)' }

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_httpBasic
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options
    And match response.credenziali.password == '#notpresent'

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@UpdateCredenzialiHttpBasicNoPassword
Scenario: Applicativi Aggiornamento Credenziali HttpBasic No Password

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_httpBasic_noPassword
    When method put
    Then status 400

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@UpdateCredenzialiPrincipal
Scenario: Applicativi Aggiornamento Credenziali Principal

    * def options = { modalita_accesso: 'principal', userid: '#(credenziali_principal.credenziali.userid)' }

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_principal
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@UpdateCredenzialiApiKey
Scenario: Applicativi Aggiornamento Credenziali ApiKey

    * def options = { modalita_accesso: 'api-key', app_id: false }

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_apikey
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#present' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@UpdateCredenzialiMultipleApiKey
Scenario: Applicativi Aggiornamento Credenziali MultipleApiKey

    * def options = { modalita_accesso: 'api-key', app_id: true }

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_multipleApikey
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#present' }
    And match responseHeaders contains { 'X-App-Id': '#present' }

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )
    
@UpdateCredenzialiHttsCertificatiMultipli
Scenario: Applicativi Aggiornamento Credenziali Https Certificati Multipli

    * def options = { modalita_accesso: 'https', certificato: { tipo_certificato: "CER", strict_verification : false, tipo: "certificato", archivio: "#notnull" }, certificati: [ { tipo_certificato: "CER", strict_verification : false, archivio: "#notnull" }] }

    * call create { resourcePath: 'applicativi', body: '#(applicativo_https_multipleCertificate)' }

    Given url configUrl
    And path 'applicativi/' + applicativo_https_multipleCertificate.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_https_multipleCertificate
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'applicativi' , applicativo_https_multipleCertificate.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'applicativi/' + applicativo_https_multipleCertificate.nome } )
