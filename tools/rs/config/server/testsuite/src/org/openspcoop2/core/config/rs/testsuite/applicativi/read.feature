Feature: Lettura Applicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

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


