Feature: Creazione Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_esterno_modi_pdnd_codice_ente = read('classpath:bodies/soggetto-esterno-modi-pdnd-codice-ente.json')
* eval randomize(soggetto_esterno_modi_pdnd_codice_ente, ["nome", "credenziali.username"])

* def soggetto_interno_modi_pdnd_codice_ente = read('classpath:bodies/soggetto-interno-modi-pdnd-codice-ente.json')
* eval randomize(soggetto_interno_modi_pdnd_codice_ente, ["nome", "credenziali.username"])

@CreateSoggettEsternoModIPdndCodiceEnte
Scenario: Creazione Soggetto Esterno PDND con codice ente 201 OK

    * def query_param_profilo_modi = {'profilo': 'ModI'}
    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno_modi_pdnd_codice_ente, query_params: query_param_profilo_modi })
    * call get ({ resourcePath: 'soggetti', key: soggetto_esterno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
    * match response.modi == soggetto_esterno_modi_pdnd_codice_ente.modi
    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
   
@CreateSoggettInternoModIPdndCodiceEnte
Scenario: Creazione Soggetto Interno PDND con codice ente 201 OK

    * def query_param_profilo_modi = {'profilo': 'ModI'}
    * call create ({ resourcePath: 'soggetti', body: soggetto_interno_modi_pdnd_codice_ente, query_params: query_param_profilo_modi })
    * call get ({ resourcePath: 'soggetti', key: soggetto_interno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
    * match response.modi == soggetto_interno_modi_pdnd_codice_ente.modi
    * call delete ({ resourcePath: 'soggetti/' + soggetto_interno_modi_pdnd_codice_ente.nome , query_params: query_param_profilo_modi})
