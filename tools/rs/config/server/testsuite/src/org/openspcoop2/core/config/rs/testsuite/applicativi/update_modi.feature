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

* def notNullify =
"""
function(app) {

if(app == null) return {};

return app;
}
"""

* def getExpectedInterno =
"""
function(app) {

if(app.modi == null) return null;

var expected = app.modi;


expected.dominio = expected.dominio != null ? expected.dominio : 'interno'

return expected;
} 
"""

@Update204_modi_applicativi_esterni
Scenario Outline: Applicativi modi esterno Creazione 204 OK <nome>

		* def erogatore = read('soggetto_esterno.json')
		* eval randomize (erogatore, ["nome", "credenziali.certificato.subject"])
		* erogatore.credenziali.certificato.subject = "cn=" + erogatore.credenziali.certificato.subject 
		
		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : '#(erogatore.nome)'}
		
		* def applicativo_https_certificate = read('applicativo_esterno.json') 
		* eval randomize(applicativo_https_certificate, ["nome" ])

		* def applicativo_update = read('<nome>') 
    * eval applicativo_update.nome = applicativo_https_certificate.nome
		
    * call create ({ resourcePath: 'soggetti', body: erogatore, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'applicativi', body: applicativo_https_certificate, key: applicativo_https_certificate.nome, query_params: query_param_applicativi })
    * call put ({ resourcePath: 'applicativi/'+applicativo_https_certificate.nome, body: applicativo_update, query_params: query_param_applicativi})
    * call get ({ resourcePath: 'applicativi', key: applicativo_https_certificate.nome , query_params: query_param_applicativi})
    * match response.modi == getExpectedEsterno(applicativo_update)
    * match response.credenziali == applicativo_update.credenziali
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_https_certificate.nome , query_params: query_param_applicativi})

Examples:
|nome|
|applicativo_esterno_audience.json|
|applicativo_esterno_https.json|
|applicativo_esterno_https_token.json|
|applicativo_esterno_token.json|

@Update400_modi_applicativi_esterni
Scenario Outline: Applicativi modi esterno Creazione 400 <nome>

		* def erogatore = read('soggetto_esterno.json')
		* eval randomize (erogatore, ["nome", "credenziali.certificato.subject"])
		* erogatore.credenziali.certificato.subject = "cn=" + erogatore.credenziali.certificato.subject 
		
		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = {'profilo': 'ModI', 'soggetto' : '#(erogatore.nome)'}
		
		* def applicativo_https_certificate = read('applicativo_esterno.json') 
		* eval randomize(applicativo_https_certificate, ["nome" ])

		* def applicativo_update = read('<nome>') 
    * eval applicativo_update.nome = applicativo_https_certificate.nome
		
    * call create ({ resourcePath: 'soggetti', body: erogatore, query_params: query_param_profilo_modi })
    * call create ({ resourcePath: 'applicativi', body: applicativo_https_certificate, key: applicativo_https_certificate.nome, query_params: query_param_applicativi })
    * call update_400 ({ resourcePath: 'applicativi/'+applicativo_https_certificate.nome, body: applicativo_update, query_params: query_param_applicativi})
    * match response.detail == '<error>'
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_https_certificate.nome , query_params: query_param_applicativi})

Examples:
|nome| error|
| applicativo_interno_audience.json | Configurazione \'ModI\' non valida |
| applicativo_esterno_basic.json | Non sono state fornite credenziali (certificato o tokenClientId) che identificano l’applicativo |
| applicativo_esterno_https_token_policy_inesistente.json | TokenPolicy indicata \'GoogleNonEsistente\' non esiste |
| applicativo_esterno_token_policy_inesistente.json | TokenPolicy indicata \'GoogleNonEsistente\' non esiste |

@Update204_modi_applicativi_interni
Scenario Outline: Applicativi modi interno Creazione 204 OK <nome>

		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = ({'profilo': 'ModI', 'soggetto' : soggettoDefault})
		
		* def applicativo_interno = read('applicativo_interno.json') 
		* eval randomize(applicativo_interno, ["nome" ])

		* def applicativo_update = read('<nome>') 
    * eval applicativo_update.nome = applicativo_interno.nome

    * call create ({ resourcePath: 'applicativi', body: applicativo_interno, key: applicativo_interno.nome, query_params: query_param_applicativi })
    * call put ({ resourcePath: 'applicativi/'+applicativo_interno.nome, body: applicativo_update, query_params: query_param_applicativi})
    * call get ({ resourcePath: 'applicativi', key: applicativo_interno.nome , query_params: query_param_applicativi})
    * match notNullify(response.modi) == notNullify(getExpectedInterno(applicativo_update))
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_interno.nome , query_params: query_param_applicativi})

Examples:
|nome|
|applicativo_interno.json|
|applicativo_interno_keystore_archive.json|
|applicativo_interno_keystore_file.json|
|applicativo_interno_keystore_hsm.json|
|applicativo_interno_keystore_file_certificato.json|
|applicativo_interno_keystore_hsm_certificato.json|
|applicativo_interno_audience.json|
|applicativo_interno_url_x5u.json|
|applicativo_interno_token_keystore_archive.json|
|applicativo_interno_token_keystore_file.json|
|applicativo_interno_token_keystore_hsm.json|
|applicativo_interno_token.json|


@Update400_modi_applicativi_interni
Scenario Outline: Applicativi modi interno Creazione 400 <nome>

		* def query_param_profilo_modi = {'profilo': 'ModI'}
		* def query_param_applicativi = ({'profilo': 'ModI', 'soggetto' : soggettoDefault})
		
		* def applicativo_interno = read('applicativo_interno.json') 
		* eval randomize(applicativo_interno, ["nome" ])

		* def applicativo_update = read('<nome>') 
    * eval applicativo_update.nome = applicativo_interno.nome

    * call create ({ resourcePath: 'applicativi', body: applicativo_interno, key: applicativo_interno.nome, query_params: query_param_applicativi })
    * call update_400 ({ resourcePath: 'applicativi/'+applicativo_interno.nome, body: applicativo_update, query_params: query_param_applicativi})
    * match response.detail == '<error>'
    
    * call delete ({ resourcePath: 'applicativi/' + applicativo_interno.nome , query_params: query_param_applicativi})

Examples:
|nome| error |
| applicativo_esterno_audience.json | Configurazione \'ModI\' non valida |
| applicativo_interno_token_policy_inesistente.json | Token Policy indicata \'GoogleNonEsistente\' non esiste |
| applicativo_interno_undefined_security.json | Configurazione \'ModI\' non valida: definire almeno una configurazione tra sicurezza messaggio e token |

