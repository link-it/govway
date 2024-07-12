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

    * def credenziali_token = read('classpath:bodies/credenziali_token.json') 

    * def applicativo_proprieta = read('classpath:bodies/applicativo_proprieta.json') 
    * eval randomize(applicativo_proprieta, ["nome", "credenziali.userid" ])

    * def applicativo_esterno = read('classpath:bodies/applicativo_esterno.json') 
    * eval randomize(applicativo_esterno, ["nome", "credenziali.userid" ])

    * def applicativo_esterno_update = read('applicativo_esterno_update.json') 
    * eval applicativo_esterno_update.nome = applicativo_esterno.nome

    * def applicativo_esterno_descrizione4000 = read('classpath:bodies/applicativo_esterno_descrizione4000.json') 
    * eval randomize(applicativo_esterno_descrizione4000, ["nome", "credenziali.userid" ])


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

@UpdateCredenzialiToken
Scenario: Applicativi Aggiornamento Credenziali Token

    * def options = { modalita_accesso: 'token', identificativo: '#(credenziali_token.credenziali.identificativo)', token_policy: '#(credenziali_token.credenziali.token_policy)' }

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_token
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

@UpdateCredenzialiToken400
Scenario: Applicativi Aggiornamento Token Policy Inesistente 400

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    * eval credenziali_token.credenziali.token_policy = 'TokenPolicyInesistente_' + random()

    Given url configUrl
    And path 'applicativi/' + applicativo.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_token
    When method put
    Then status 400

    * call delete ( { resourcePath: 'applicativi/' + applicativo.nome } )

@UpdateProprieta
Scenario: Applicativi Aggiornamento Proprieta

    * call create { resourcePath: 'applicativi', body: '#(applicativo_proprieta)' }

    # UPDATE 1

    * eval applicativo_proprieta.proprieta[0].nome='pModificata'
    * eval applicativo_proprieta.proprieta[1].valore='vModificato'

    Given url configUrl
    And path 'applicativi/' + applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And request applicativo_proprieta
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    # READ 1

    Given url configUrl
    And path 'applicativi' , applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And assert response.proprieta.length == 2
    And match response.proprieta[*] contains { 'nome': 'pModificata', 'valore': 'ValoreProprieta1', 'encrypted': false }
    And match response.proprieta[*] contains { 'nome': 'NomeProprieta2', 'valore': 'vModificato', 'encrypted': false }

    # UPDATE 2

    * remove applicativo_proprieta.proprieta

    Given url configUrl
    And path 'applicativi/' + applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And request applicativo_proprieta
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    # READ 2

    Given url configUrl
    And path 'applicativi' , applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains { 'proprieta': '#notpresent' }

    * call delete ( { resourcePath: 'applicativi/' + applicativo_proprieta.nome } )


@UpdateEsterno204
Scenario: Applicativi Esterno Aggiornamento 204 OK

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * call update_204 { resourcePath: 'applicativi',  body: '#(applicativo_esterno)',  body_update: '#(applicativo_esterno_update)',  key: '#(applicativo_esterno.nome)',  delete_key: '#(applicativo_esterno_update.nome)', query_params: '#(query_param_applicativi)' }

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})

@UpdateCredenzialiApplicativoEsterno
Scenario: Applicativi Aggiornamento Credenziali per un applicativo esterno

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * def options = { modalita_accesso: 'http-basic', username: '#(credenziali_httpBasic.credenziali.username)' }

    * call create { resourcePath: 'applicativi', body: '#(applicativo_esterno)', query_params: '#(query_param_applicativi)' }

    Given url configUrl
    And path 'applicativi/' + applicativo_esterno.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    And request credenziali_httpBasic
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'applicativi' , applicativo_esterno.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method get
    Then status 200
    And match response.credenziali contains options
    And match response.credenziali.password == '#notpresent'

    * call delete ( { resourcePath: 'applicativi/' + applicativo_esterno.nome, query_params: query_param_applicativi } )

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})


@UpdateDescrizione4000
Scenario: Aggiornamento Applicativo descrizione 4000 204 OK

    * call create { resourcePath: 'applicativi', body: '#(applicativo_esterno_descrizione4000)' }


    Given url configUrl
    And path 'applicativi', applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == applicativo_esterno_descrizione4000.descrizione

    # UPDATE 1

    * eval descr4000=applicativo_esterno_descrizione4000.descrizione
    * eval applicativo_esterno_descrizione4000.descrizione='descrModificata'

    Given url configUrl
    And path 'applicativi/' + applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request applicativo_esterno_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'applicativi', applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == 'descrModificata'

    # UPDATE 2

    * remove applicativo_esterno_descrizione4000.descrizione

    Given url configUrl
    And path 'applicativi/' + applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request applicativo_esterno_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'applicativi', applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'

    # UPDATE 3

    * eval applicativo_esterno_descrizione4000.descrizione=descr4000

    Given url configUrl
    And path 'applicativi/' + applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request applicativo_esterno_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'applicativi', applicativo_esterno_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descr4000

    * call delete ( { resourcePath: 'applicativi' + '/' + applicativo_esterno_descrizione4000.nome } )
