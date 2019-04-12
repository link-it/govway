Feature: Eliminazione Soggetto

Background:

* call read('classpath:crud_commons.feature')

* def soggetto = read('soggetto2.json') 
* eval randomize(soggetto, ["nome", "credenziali.certificato.issuer", "credenziali.certificato.subject"])
* eval soggetto.ruoli = []


@Delete204
Scenario: Eliminazione Soggetto 204 OK

    * call delete_204 { resourcePath: 'soggetti', body: '#(soggetto)', key:'#(soggetto.nome)'}

@Delete404
Scenario: Eliminazione Soggetto 404

    * call delete_404 { resourcePath: 'soggetti', key:'#(soggetto.nome)'}
