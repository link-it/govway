Feature: Eliminazione Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('applicativo.json') 
* eval applicativo.nome = applicativo.nome + random()

@Delete204
Scenario: Delete 204 OK

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }
    * call delete { resourcePath: '#("applicativi/" + applicativo.nome)'}

@Delete404
Scenario: Delete 404

    Given url configUrl
    And path 'applicativi' , applicativo.nome
    And header Authorization = govwayConfAuth
    When method delete
    Then status 404
