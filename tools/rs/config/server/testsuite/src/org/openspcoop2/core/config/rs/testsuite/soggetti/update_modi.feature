Feature: Aggiornamento Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_esterno = read('classpath:bodies/soggetto-esterno.json')
* eval randomize(soggetto_esterno, ["nome", "credenziali.username"])

* def soggetto_interno = read('classpath:bodies/soggetto-interno.json')
* eval randomize(soggetto_interno, ["nome", "credenziali.username"])

* def soggetto_esterno_modi_pdnd_codice_ente = read('classpath:bodies/soggetto-esterno-modi-pdnd-codice-ente.json')
* eval randomize(soggetto_esterno_modi_pdnd_codice_ente, ["nome", "credenziali.username"])

* def soggetto_interno_modi_pdnd_codice_ente = read('classpath:bodies/soggetto-interno-modi-pdnd-codice-ente.json')
* eval randomize(soggetto_interno_modi_pdnd_codice_ente, ["nome", "credenziali.username"])

@UpdateSoggettEsternoModIPdndCodiceEnte
Scenario: Aggiornamento Soggetto Esterno PDND con codice ente 204 OK

    * def query_param_profilo_modi = {'profilo': 'ModI'}
    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno, query_params: query_param_profilo_modi })
    * call put ({ resourcePath: 'soggetti/'+soggetto_esterno.nome, body: soggetto_esterno_modi_pdnd_codice_ente, query_params: query_param_profilo_modi})    
    * call get ({ resourcePath: 'soggetti', key: soggetto_esterno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
    * match response.modi == soggetto_esterno_modi_pdnd_codice_ente.modi
    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})

@UpdateSoggettInternoModIPdndCodiceEnte
Scenario: Aggiornamento Soggetto Interno PDND con codice ente 204 OK

    * def query_param_profilo_modi = {'profilo': 'ModI'}
    * call create ({ resourcePath: 'soggetti', body: soggetto_interno, query_params: query_param_profilo_modi })
    * call put ({ resourcePath: 'soggetti/'+soggetto_interno.nome, body: soggetto_interno_modi_pdnd_codice_ente, query_params: query_param_profilo_modi})    
    * call get ({ resourcePath: 'soggetti', key: soggetto_interno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
    * match response.modi == soggetto_interno_modi_pdnd_codice_ente.modi
    * call delete ({ resourcePath: 'soggetti/' + soggetto_interno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
