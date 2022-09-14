Feature: CreazioneApplicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

* def applicativo_https = read('classpath:bodies/applicativo_https.json') 

* def applicativo_https_certificate = read('classpath:bodies/applicativo_https_certificate.json') 
* eval randomize(applicativo_https_certificate, ["nome" ])

* def applicativo_principal = read('classpath:bodies/applicativo_principal.json') 
* eval randomize(applicativo_principal, ["nome" ])

* def applicativo_apikey = read('classpath:bodies/applicativo_apikey.json') 
* eval randomize(applicativo_apikey, ["nome" ])

* def applicativo_multipleApikey = read('classpath:bodies/applicativo_multipleApikey.json') 
* eval randomize(applicativo_multipleApikey, ["nome" ])

* def applicativo_https_multipleCertificate = read('classpath:bodies/applicativo_https_multipleCertificate.json') 
* eval randomize(applicativo_https_multipleCertificate, ["nome" ])

* def applicativo_token = read('classpath:bodies/applicativo_token.json') 
* eval randomize(applicativo_token, ["nome", "credenziali.identificativo"])

* def applicativo_proprieta = read('classpath:bodies/applicativo_proprieta.json') 
* eval randomize(applicativo_proprieta, ["nome", "credenziali.userid" ])

* def applicativo_esterno = read('classpath:bodies/applicativo_esterno.json') 
* eval randomize(applicativo_esterno, ["nome", "credenziali.userid" ])

@Create204
Scenario: Applicativi Creazione 204 OK

    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Create204_httpsConfManuale
Scenario: Applicativi Creazione 204 OK (credenziali https, configurazione manuale)
    
    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_https)', key: '#(applicativo_https.nome)' }
    
@Create204_httpsCertificato
Scenario: Applicativi Creazione 204 OK (credenziali https, upload certificato)

    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_https_certificate)', key: '#(applicativo_https_certificate.nome)' }
    
@Create204_httpsMultipleCertificato
Scenario: Applicativi Creazione 204 OK (credenziali https, lista certificati)

    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_https_multipleCertificate)', key: '#(applicativo_https_multipleCertificate.nome)' }

@Create409
Scenario: Applicativi Creazione 409 Conflitto

    * call create_409 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Create400
Scenario: Applicativi Creazione con ruolo inesistente
 
    * eval applicativo.ruoli = ['RuoloInesistente' + random() ]

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo
    When method post
    Then status 400

@Create204_principal
Scenario: Applicativi Creazione 204 OK (credenziali principal)
    
    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_principal)', key: '#(applicativo_principal.nome)' }

@Create204_apikey
Scenario: Applicativi Creazione 204 OK (credenziali apikey)
    
    * call create_201_apikey { resourcePath: 'applicativi', body: '#(applicativo_apikey)', key: '#(applicativo_apikey.nome)' }

@Create204_multipleApikey
Scenario: Applicativi Creazione 204 OK (credenziali multipleApikey)
    
    * call create_201_multipleapikey { resourcePath: 'applicativi', body: '#(applicativo_multipleApikey)', key: '#(applicativo_multipleApikey.nome)' }

@Create204_token
Scenario: Applicativi Creazione 204 OK (credenziali token)
    
    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_token)', key: '#(applicativo_token.nome)' }

@Create400_token
Scenario: Applicativi Creazione con token policy inesistente
 
    * eval applicativo_token.credenziali.token_policy = 'TokenPolicyNonEsistente' + random()

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo_token
    When method post
    Then status 400

@Create204_proprieta
Scenario: Applicativi Creazione 204 OK (presenza di proprieta')
    
    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_proprieta)', key: '#(applicativo_proprieta.nome)' }

@Create204_esterno
Scenario: Applicativi Creazione 204 OK (dominio esterno)
    
    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno })

    * call create_201 { resourcePath: 'applicativi', body: '#(applicativo_esterno)', key: '#(applicativo_esterno.nome)', query_params: '#(query_param_applicativi)' }

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome})



