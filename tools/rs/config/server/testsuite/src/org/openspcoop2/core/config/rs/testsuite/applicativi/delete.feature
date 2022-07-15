Feature: Eliminazione Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

* def applicativo_esterno = read('classpath:bodies/applicativo_esterno.json') 
* eval randomize(applicativo_esterno, ["nome", "credenziali.userid" ])

@Delete204
Scenario: Applicativi Delete 204 OK

    * call delete_204 { resourcePath: 'applicativi', body: '#(applicativo)', key:'#(applicativo.nome)'}

@Delete404
Scenario: Applicativi Delete 404

    * call delete_404 { resourcePath: 'applicativi', key:'#(applicativo.nome)'}

@DeleteEsterno204
Scenario: Applicativi Esterni Delete 204 OK

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * call delete_204 { resourcePath: 'applicativi', body: '#(applicativo)', key:'#(applicativo.nome)', query_params: '#(query_param_applicativi)' }

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})
