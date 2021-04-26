Feature: Creazione Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_http = read('classpath:bodies/soggetto-esterno-http.json')
* def soggetto_principal = read('classpath:bodies/soggetto-esterno-principal.json')
* def soggetto_apikey = read('classpath:bodies/soggetto-esterno-apikey.json')
* def soggetto_multipleapikey = read('classpath:bodies/soggetto-esterno-multipleapikey.json')
* def soggetto_https_multipleCertificate = read('classpath:bodies/soggetto-esterno-https_multipleCertificate.json')
* def soggetto_proprieta = read('classpath:bodies/soggetto-esterno-proprieta.json') 
* def ruolo = read('classpath:bodies/ruolo.json')

* eval randomize(soggetto_http, ["nome", "credenziali.username"])
* eval randomize(soggetto_principal, ["nome", "credenziali.userid"])
* eval randomize(soggetto_https_multipleCertificate, ["nome"])
* eval randomize(soggetto_proprieta, ["nome", "credenziali.userid" ])

* eval randomize(ruolo, ["nome"])

* eval soggetto_http.ruoli = [ ruolo.nome ]
* eval soggetto_principal.ruoli = [ ruolo.nome ]
* eval soggetto_apikey.ruoli = [ ruolo.nome ]
* eval soggetto_multipleapikey.ruoli = [ ruolo.nome ]
* eval soggetto_https_multipleCertificate.ruoli = [ ruolo.nome ]
* eval soggetto_proprieta.ruoli = [ ruolo.nome ]

@CreateCredHttp
Scenario: Creazione Soggetti 204 OK

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_http)', key: '#(soggetto_http.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateCredPrincipal
Scenario: Creazione Soggetti 204 OK (principal)

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_principal)', key: '#(soggetto_principal.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateCredApiKey
Scenario: Creazione Soggetti 204 OK (apikey)

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201_apikey { resourcePath: 'soggetti', body: '#(soggetto_apikey)', key: '#(soggetto_apikey.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateCredMultipleApiKey
Scenario: Creazione Soggetti 204 OK (multipleapikey)

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201_multipleapikey { resourcePath: 'soggetti', body: '#(soggetto_multipleapikey)', key: '#(soggetto_multipleapikey.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )
    
@CreateCredHttpsMultipleCertificate
Scenario: Creazione Soggetti 204 OK (credenziali https, lista certificati)

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_https_multipleCertificate)', key: '#(soggetto_https_multipleCertificate.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateSPCoop204
Scenario: Creazione Soggetto SPCoop

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * def query_params = { profilo: "SPCoop" }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_http)', key: '#(soggetto_http.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@Create409
Scenario: Creazione Soggetti 409 Conflitto

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_409 { resourcePath: 'soggetti', body: '#(soggetto_http)', key: '#(soggetto_http.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateRuoloInesistente
Scenario: Creazione Soggetti 400 Ruolo Inesistente
    
    * eval soggetto_http.ruoli = ['RuoloInesistente' + random() ]

    Given url configUrl
    And path 'soggetti'
    And  header Authorization = govwayConfAuth
    And request soggetto_http
    When method post
    Then status 400

@Create204_proprieta
Scenario: Soggetti Creazione 204 OK (presenza di proprieta')
    
    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_proprieta)', key: '#(soggetto_proprieta.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

