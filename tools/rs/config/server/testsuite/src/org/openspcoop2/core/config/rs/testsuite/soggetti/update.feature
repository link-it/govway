Feature: Aggiornamento Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_https_manuale = read('classpath:bodies/soggetto-esterno-https.json') 
* def soggetto_https_cert = read('classpath:bodies/soggetto-esterno-certificato.json')
* def ruolo = read('classpath:bodies/ruolo.json')

* eval randomize(soggetto_https_manuale, ["nome", "credenziali.certificato.issuer", "credenziali.certificato.subject"])
* eval randomize(ruolo, ["nome"])
* eval soggetto_https_manuale.ruoli = [ ruolo.nome ]
* eval soggetto_https_cert.ruoli = [ ruolo.nome ]

* def credenziali_httpBasic = read('classpath:bodies/credenziali_httpBasic.json')
* eval randomize(credenziali_httpBasic, ["credenziali.username"])

* def credenziali_httpBasic_noPassword = read('classpath:bodies/credenziali_httpBasic_noPassword.json')
* eval randomize(credenziali_httpBasic_noPassword, ["credenziali.username"])

* def credenziali_principal = read('classpath:bodies/credenziali_principal.json')
* eval randomize(credenziali_principal, ["credenziali.userid"])

* def credenziali_apikey = read('classpath:bodies/credenziali_apikey.json')

* def credenziali_multipleApikey = read('classpath:bodies/credenziali_multipleApikey.json')

* def soggetto_https_multipleCertificate = read('classpath:bodies/soggetto-esterno-https_multipleCertificate.json')
* eval randomize(soggetto_https_multipleCertificate, ["nome"])
* eval soggetto_https_multipleCertificate.ruoli = [ ruolo.nome ]

* def credenziali_https_multipleCertificate = read('classpath:bodies/credenziali_https_multipleCertificate.json')

* def soggetto_proprieta = read('classpath:bodies/soggetto-esterno-proprieta.json') 
* eval randomize(soggetto_proprieta, ["nome", "credenziali.userid" ])
* eval soggetto_proprieta.ruoli = []

* def soggetto_descrizione4000 = read('classpath:bodies/soggetto-esterno-descrizione4000.json')
* eval randomize(soggetto_descrizione4000, ["nome", "credenziali.username"])


@Update204
Scenario: Aggiornamento Soggetto 204

* call create { resourcePath: 'ruoli', body: '#(ruolo)' }
* call update_204 ( { resourcePath: 'soggetti', body: soggetto_https_manuale,  body_update: soggetto_https_cert, key: soggetto_https_manuale.nome, delete_key: soggetto_https_cert.nome } )
* call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@Update404
Scenario: Aggiornamento Soggetto 404

* call update_404 { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)', key: '#(soggetto_https_manuale.nome)' }

@Update204_httpsMultipleCertificato
Scenario: Aggiornamento Soggetto 204

* call create { resourcePath: 'ruoli', body: '#(ruolo)' }
* call update_204 ( { resourcePath: 'soggetti', body: soggetto_https_multipleCertificate,  body_update: soggetto_https_multipleCertificate, key: soggetto_https_multipleCertificate.nome, delete_key: soggetto_https_multipleCertificate.nome } )
* call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateCredenzialiHttpBasic
Scenario: Soggetti Aggiornamento Credenziali HttpBasic

    * def options = { modalita_accesso: 'http-basic', username: '#(credenziali_httpBasic.credenziali.username)' }

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_manuale.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_httpBasic
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'soggetti' , soggetto_https_manuale.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options
    And match response.credenziali.password == '#notpresent'

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_manuale.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateCredenzialiHttpBasicNoPassword
Scenario: Soggetti Aggiornamento Credenziali HttpBasic No Password

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_manuale.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_httpBasic_noPassword
    When method put
    Then status 400

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_manuale.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateCredenzialiPrincipal
Scenario: Soggetti Aggiornamento Credenziali Principal

    * def options = { modalita_accesso: 'principal', userid: '#(credenziali_principal.credenziali.userid)' }

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_manuale.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_principal
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'soggetti' , soggetto_https_manuale.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_manuale.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateCredenzialiApiKey
Scenario: Soggetti Aggiornamento Credenziali ApiKey

    * def options = { modalita_accesso: 'api-key', app_id: false }

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_manuale.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_apikey
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#present' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    Given url configUrl
    And path 'soggetti' , soggetto_https_manuale.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_manuale.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateCredenzialiMultipleApiKey
Scenario: Soggetti Aggiornamento Credenziali MultipleApiKey

    * def options = { modalita_accesso: 'api-key', app_id: true }

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_manuale.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_multipleApikey
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#present' }
    And match responseHeaders contains { 'X-App-Id': '#present' }

    Given url configUrl
    And path 'soggetti' , soggetto_https_manuale.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_manuale.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )
    
@UpdateCredenzialiHttsCertificatiMultipli
Scenario: Soggetti Aggiornamento Credenziali Https Certificati Multipli

    * def options = { modalita_accesso: 'https', certificato: { tipo_certificato: "CER", strict_verification : false, tipo: "certificato", archivio: "#notnull" }, certificati: [ { tipo_certificato: "CER", strict_verification : false, archivio: "#notnull" }] }

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create { resourcePath: 'soggetti', body: '#(soggetto_https_multipleCertificate)' }

    Given url configUrl
    And path 'soggetti/' + soggetto_https_multipleCertificate.nome + '/credenziali'
    And header Authorization = govwayConfAuth
    And request credenziali_https_multipleCertificate
    When method put
    Then status 204

    Given url configUrl
    And path 'soggetti' , soggetto_https_multipleCertificate.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.credenziali contains options

    * call delete ( { resourcePath: 'soggetti/' + soggetto_https_multipleCertificate.nome } )
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@UpdateProprieta
Scenario: Soggetti Aggiornamento Proprieta

    * call create { resourcePath: 'soggetti', body: '#(soggetto_proprieta)' }

    # UPDATE 1

    * eval soggetto_proprieta.proprieta[0].nome='pModificata'
    * eval soggetto_proprieta.proprieta[1].valore='vModificato'

    Given url configUrl
    And path 'soggetti/' + soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And request soggetto_proprieta
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    # READ 1

    Given url configUrl
    And path 'soggetti' , soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And assert response.proprieta.length == 2
    And match response.proprieta[*] contains { 'nome': 'pModificata', 'valore': 'ValoreProprieta1', 'encrypted': false }
    And match response.proprieta[*] contains { 'nome': 'NomeProprieta2', 'valore': 'vModificato', 'encrypted': false }

    # UPDATE 2

    * remove soggetto_proprieta.proprieta

    Given url configUrl
    And path 'soggetti/' + soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And request soggetto_proprieta
    When method put
    Then status 204
    And match responseHeaders contains { 'X-Api-Key': '#notpresent' }
    And match responseHeaders contains { 'X-App-Id': '#notpresent' }

    # READ 2

    Given url configUrl
    And path 'soggetti' , soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains { 'proprieta': '#notpresent' }

    * call delete ( { resourcePath: 'soggetti/' + soggetto_proprieta.nome } )

@UpdateDescrizione4000
Scenario: Aggiornamento Soggetti descrizione 4000 204 OK

    * call create { resourcePath: 'soggetti', body: '#(soggetto_descrizione4000)' }

    Given url configUrl
    And path 'soggetti', soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == soggetto_descrizione4000.descrizione

    # UPDATE 1

    * eval descr4000=soggetto_descrizione4000.descrizione
    * eval soggetto_descrizione4000.descrizione='descrModificata'

    Given url configUrl
    And path 'soggetti/' + soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request soggetto_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'soggetti', soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == 'descrModificata'

    # UPDATE 2

    * remove soggetto_descrizione4000.descrizione

    Given url configUrl
    And path 'soggetti/' + soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request soggetto_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'soggetti', soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == '#notpresent'

    # UPDATE 3

    * eval soggetto_descrizione4000.descrizione=descr4000

    Given url configUrl
    And path 'soggetti/' + soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    And request soggetto_descrizione4000
    When method put
    Then status 204

    Given url configUrl
    And path 'soggetti', soggetto_descrizione4000.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == descr4000

    * call delete ( { resourcePath: 'soggetti' + '/' + soggetto_descrizione4000.nome } )
