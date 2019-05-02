Feature: Aggiornamento Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto_https_manuale = read('classpath:bodies/soggetto-esterno-https.json') 
* def soggetto_https_cert = read('classpath:bodies/soggetto-esterno-certificato.json')
* def ruolo = read('classpath:bodies/ruolo.json')

* eval randomize(soggetto_https_manuale, ["nome", "credenziali.certificato.issuer", "credenziali.certificato.subject"])
* eval randomize(soggetto_https_cert, ["nome", "credenziali.userid"])
* eval randomize(ruolo, ["nome"])
* eval soggetto_https_manuale.ruoli = [ ruolo.nome ]
* eval soggetto_https_cert.ruoli = [ ruolo.nome ]

@Update204
Scenario: Aggiornamento Soggetto 204

* call create { resourcePath: 'ruoli', body: '#(ruolo)' }
* call update_204 ( { resourcePath: 'soggetti', body: soggetto_https_manuale,  body_update: soggetto_https_cert, key: soggetto_https_manuale.nome, delete_key: soggetto_https_cert.nome } )
* call delete ( { resourcePath: 'ruoli' + '/' + ruolo.nome } )

@Update404
Scenario: Aggiornamento Soggetto 404

* call update_404 { resourcePath: 'soggetti', body: '#(soggetto_https_manuale)', key: '#(soggetto_https_manuale.nome)' }

