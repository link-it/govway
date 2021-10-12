Feature: Eliminazione Applicativi Server

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('applicativo.json') 
* eval randomize(applicativo, ["nome"])

@Delete204
Scenario: Applicativi Server Delete 204 OK

    * call delete_204 { resourcePath: 'applicativi-server', body: '#(applicativo)', key:'#(applicativo.nome)'}

@Delete404
Scenario: Applicativi Delete 404

    * call delete_404 { resourcePath: 'applicativi-server', key:'#(applicativo.nome)'}
