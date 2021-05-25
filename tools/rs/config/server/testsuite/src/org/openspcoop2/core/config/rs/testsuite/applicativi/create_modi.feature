Feature: CreazioneApplicativi

Background:

* call read('classpath:crud_commons.feature')

* def getExpectedEsterno =
"""
function(app) {

app.modi = app.modi != null ? app.modi: {}

var expected = app.modi;

expected.dominio = expected.dominio != null ? expected.dominio : 'esterno'

return expected;
} 
"""

* def getExpectedInterno =
"""
function(app) {

app.modi = app.modi != null ? app.modi: {}

var expected = app.modi;


expected.dominio = expected.dominio != null ? expected.dominio : 'interno'

return expected;
} 
"""

@Create204_modi_applicativi
Scenario Outline: Applicativi modi esterno Creazione 204 OK

		* def erogatore = read('soggetto_esterno.json')
		* eval randomize (erogatore, ["nome", "credenziali.certificato.subject"])
		* erogatore.credenziali.certificato.subject = "cn=" + erogatore.credenziali.certificato.subject 
		
		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : '#(erogatore.nome)'}
		
		* def applicativo_https_certificate = read('<nome>') 
		* eval randomize(applicativo_https_certificate, ["nome" ])

    * call create ({ resourcePath: 'soggetti', body: erogatore, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'applicativi', body: applicativo_https_certificate, key: applicativo_https_certificate.nome, query_params: query_param_applicativi })
    * call get ({ resourcePath: 'applicativi', key: applicativo_https_certificate.nome , query_params: query_param_applicativi})
    * match response.modi == getExpectedEsterno(applicativo_https_certificate)
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_https_certificate.nome , query_params: query_param_applicativi})
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome , query_params: query_param_applicativi})

Examples:
|nome|
|applicativo_esterno.json|
|applicativo_esterno_audience.json|

@Create204_modi_applicativi
Scenario Outline: Applicativi modi interno Creazione 204 OK

		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : 'ENTE'}
		
		* def applicativo_interno = read('<nome>') 
		* eval randomize(applicativo_interno, ["nome" ])

    * call create ({ resourcePath: 'applicativi', body: applicativo_interno, key: applicativo_interno.nome, query_params: query_param_applicativi })
    * call get ({ resourcePath: 'applicativi', key: applicativo_interno.nome , query_params: query_param_applicativi})
    * match response.modi == getExpectedInterno(applicativo_interno)
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_interno.nome , query_params: query_param_applicativi})

Examples:
|nome|
|applicativo_interno.json|
|applicativo_interno_keystore_archive.json|
|applicativo_interno_keystore_file.json|
|applicativo_interno_audience.json|
|applicativo_interno_url_x5u.json|

@Create400_applicativi_interni_<nome-test>
Scenario Outline: Applicativi interni Create 400 con <nome-test>
		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : 'ENTE'}
		
		* def applicativo_interno = read('<nome-test>') 
		* eval randomize(applicativo_interno, ["nome" ])

    * call create_400 ({ resourcePath: 'applicativi', body: applicativo_interno, key: applicativo_interno.nome, query_params: query_param_applicativi })
    * match response.detail == '<error>'

Examples:
| nome-test | error |
| applicativo_esterno_audience.json | Configurazione \'ModI\' non valida |


@Create400_applicativi_esterni_<nome-test>
Scenario Outline: Applicativi esterni Create 400 con <nome-test>

		* def erogatore = read('soggetto_esterno.json')
		* eval randomize (erogatore, ["nome", "credenziali.certificato.subject"])
		* erogatore.credenziali.certificato.subject = "cn=" + erogatore.credenziali.certificato.subject 
		
		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : '#(erogatore.nome)'}
		
		* def applicativo_https_certificate = read('<nome-test>') 
		* eval randomize(applicativo_https_certificate, ["nome" ])

    * call create ({ resourcePath: 'soggetti', body: erogatore, query_params: query_param_profilo_modi })
    * call create_400 ({ resourcePath: 'applicativi', body: applicativo_https_certificate, key: applicativo_https_certificate.nome, query_params: query_param_applicativi })
    * match response.detail == '<error>'
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome , query_params: query_param_applicativi})
    

Examples:
| nome-test | error |
| applicativo_interno_audience.json | Configurazione \'ModI\' non valida |
| applicativo_esterno_basic.json | Certificato, che identifica l’applicativo, non fornito |
