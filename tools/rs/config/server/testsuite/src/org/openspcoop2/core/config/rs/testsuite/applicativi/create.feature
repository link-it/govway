Feature: CrudApplicativi

Background:

* call read('classpath:crud_commons.feature')

* def applicativo = read('applicativo.json') 
* eval applicativo.nome = applicativo.nome + random()

* def applicativo_https = read('applicativo_https.json') 
* eval applicativo_https.nome = applicativo_https.nome + random()

* def applicativo_https_certificate = read('applicativo_https_certificate.json') 
* eval applicativo_https_certificate.nome = applicativo_https_certificate.nome + random()

# Note, Potremmo:
#   Partire da uno scheletro di un body e poi randomizzare tutti i campi.
#   In questo modo evitiamo di creare conflitti nei casi in cui un test falliscano
#       e dover pulire a mano il db. Altrimenti bisogna trovare un modo per pulire il 
#       db quando un test fallisce.

# 
@Create204
Scenario: Creazione 204 OK

    * def step1 = call create { resourcePath: 'applicativi', body: '#(applicativo)' }
    * def step2 = call delete { resourcePath: '#("applicativi/" + applicativo.nome)'}

@Create204_httpsConfManuale
Scenario: Creazione 204 OK (credenziali https, configurazione manuale)

    * def step1 = call create { resourcePath: 'applicativi', body: '#(applicativo_https)' }
    * def step2 = call delete { resourcePath: '#("applicativi/" + applicativo_https.nome)'}
    
@Create204_httpsCertificato
Scenario: Creazione 204 OK (credenziali https, upload certificato)

    * def step1 = call create { resourcePath: 'applicativi', body: '#(applicativo_https_certificate)' }
    * def step2 = call delete { resourcePath: '#("applicativi/" + applicativo_https_certificate.nome)'}

@Create409
Scenario: Creazione 409 Conflitto

    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo
    When method post
    Then status 409

    * call delete { resourcePath: '#("applicativi/" + applicativo.nome)'}

@Create40O
Scenario: Creazione con gruppo inesistente
 
    * eval applicativo.ruoli = ['RuoloInesistente' + random() ]

    Given url configUrl
    And path 'applicativi'
    And  header Authorization = govwayConfAuth
    And request applicativo
    When method post
    Then status 400
