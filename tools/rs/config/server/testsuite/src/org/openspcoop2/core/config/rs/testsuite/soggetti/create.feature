Feature: Creazione Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_http = read('classpath:bodies/soggetto-esterno-http.json')
* def soggetto_principal = read('classpath:bodies/soggetto-esterno-principal.json')
* def ruolo = read('classpath:bodies/ruolo.json')

* eval randomize(soggetto_http, ["nome", "credenziali.username"])
* eval randomize(soggetto_principal, ["nome", "credenziali.userid"])
* eval randomize(ruolo, ["nome"])
* eval soggetto_http.ruoli = [ ruolo.nome ]
* eval soggetto_principal.ruoli = [ ruolo.nome ]

@CreateCredHttp
Scenario: Creazione Soggetti 204 OK

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_http)', key: '#(soggetto_http.nome)' }
    * call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@CreateCredPrincipal
Scenario: Creazione Soggetti 204 OK

    * call create { resourcePath: 'ruoli', body: '#(ruolo)' }
    * call create_201 { resourcePath: 'soggetti', body: '#(soggetto_principal)', key: '#(soggetto_principal.nome)' }
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
