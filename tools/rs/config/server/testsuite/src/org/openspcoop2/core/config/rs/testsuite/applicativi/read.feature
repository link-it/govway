Feature: Lettura Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('applicativo.json') 
* eval applicativo.nome = applicativo.nome + random()

@FindAll200
Scenario: FindAll 200 OK

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete { resourcePath: '#("applicativi/" + applicativo.nome)'}

@Get200
Scenario: Get 200 OK

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete { resourcePath: '#("applicativi/" + applicativo.nome)'}

@Get404
Scenario: Get 404

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    When method get
    Then status 404


# Scenario: Findall 404 TODO: Questo dipende da property