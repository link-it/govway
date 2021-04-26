Feature: Lettura Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto = read('classpath:bodies/soggetto-esterno-https.json') 
* eval randomize(soggetto, ["nome", "credenziali.certificato.issuer", "credenziali.certificato.subject"])
* eval soggetto.ruoli = []

* def soggetto_proprieta = read('classpath:bodies/soggetto-esterno-proprieta.json') 
* eval randomize(soggetto_proprieta, ["nome", "credenziali.userid" ])
* eval soggetto_proprieta.ruoli = []

@FindAll200
Scenario: Soggetti FindAll 200 OK
    
    * call findall_200 { resourcePath: 'soggetti', body: '#(soggetto)', key: '#(soggetto.nome)' }

@FindAll200ProfiloQualsiasi
Scenario: Soggetti FindAll ProfiloQualsiasi 200 OK
    
    * call findall_200 { resourcePath: 'soggetti', body: '#(soggetto)', key: '#(soggetto.nome)', query_params:  { profilo_qualsiasi: true } }

@Get200
Scenario: Soggetti Get 200 OK

    * call get_200 { resourcePath: 'soggetti', body: '#(soggetto)', key: '#(soggetto.nome)' }

@Get404
Scenario: Soggetti Get 404

    * call get_404 ( { resourcePath: "soggetti/" + soggetto.nome } )

@Get200_proprieta
Scenario: Soggetti Get 200 OK (presenza di proprieta')

    Given url configUrl
    And path 'soggetti'
    And  header Authorization = govwayConfAuth
    And request soggetto_proprieta
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ

    Given url configUrl
    And path 'soggetti' , soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And assert response.proprieta.length == 2
    And match response.proprieta[*] contains { 'nome': 'NomeProprieta1', 'valore': 'ValoreProprieta1' }
    And match response.proprieta[*] contains { 'nome': 'NomeProprieta2', 'valore': 'ValoreProprieta2' }

    # DELETE

    Given url configUrl
    And path 'soggetti' , soggetto_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

