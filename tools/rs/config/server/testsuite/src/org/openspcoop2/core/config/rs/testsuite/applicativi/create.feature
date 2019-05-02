Feature: CreazioneApplicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('classpath:bodies/applicativo_http.json') 
* eval randomize(applicativo, ["nome", "credenziali.username"])

* def applicativo_https = read('classpath:bodies/applicativo_https.json') 

* def applicativo_https_certificate = read('classpath:bodies/applicativo_https_certificate.json') 
* eval randomize(applicativo_https_certificate, ["nome" ])

@Create204
Scenario: Applicativi Creazione 204 OK

    * call create_204 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Create204_httpsConfManuale
Scenario: Applicativi Creazione 204 OK (credenziali https, configurazione manuale)
    
    * call create_204 { resourcePath: 'applicativi', body: '#(applicativo_https)', key: '#(applicativo_https.nome)' }
    
@Create204_httpsCertificato
Scenario: Applicativi Creazione 204 OK (credenziali https, upload certificato)

    * call create_204 { resourcePath: 'applicativi', body: '#(applicativo_https_certificate)', key: '#(applicativo_https_certificate.nome)' }

@Create409
Scenario: Applicativi Creazione 409 Conflitto

    * call create_409 { resourcePath: 'applicativi', body: '#(applicativo)', key: '#(applicativo.nome)' }

@Create40O
Scenario: Applicativi Creazione con gruppo inesistente
 
    * eval applicativo.ruoli = ['RuoloInesistente' + random() ]

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo
    When method post
    Then status 400
