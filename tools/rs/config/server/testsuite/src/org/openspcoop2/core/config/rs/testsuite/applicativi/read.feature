Feature: Lettura Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

* def applicativo_proprieta = read('classpath:bodies/applicativo_proprieta.json') 
* eval randomize(applicativo_proprieta, ["nome", "credenziali.userid" ])

@FindAll200
Scenario: Applicativi FindAll 200 OK

    * call findall_200 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@FindAll200ProfiloSoggettoQualsiasi
Scenario: Applicativi FindAll ProfiloSoggettoQualsiasi 200 OK

    * call findall_200 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)', query_params:  { profilo_qualsiasi: true, soggetto_qualsiasi: true } }

@Get200
Scenario: Applicativi Get 200 OK

    * call get_200 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Get404
Scenario: Applicativi Get 404

    * call get_404 { resourcePath: '#("applicativi/" + applicativo.nome)' }

@Get200_proprieta
Scenario: Applicativi Get 200 OK (presenza di proprieta')

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo_proprieta
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ

    Given url configUrl
    And path 'applicativi' , applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.proprieta == applicativo_proprieta.proprieta

    # DELETE

    Given url configUrl
    And path 'applicativi' , applicativo_proprieta.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204


