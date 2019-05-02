Feature: Eliminazione Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

@Delete204
Scenario: Applicativi Delete 204 OK

    * call delete_204 { resourcePath: 'applicativi', body: '#(applicativo)', key:'#(applicativo.nome)'}

@Delete404
Scenario: Applicativi Delete 404

    * call delete_404 { resourcePath: 'applicativi', key:'#(applicativo.nome)'}
