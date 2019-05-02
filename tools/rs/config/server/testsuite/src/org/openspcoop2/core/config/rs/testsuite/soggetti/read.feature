Feature: Lettura Soggetti

Background:

* call read('classpath:crud_commons.feature')

* def soggetto = read('classpath:bodies/soggetto-esterno-https.json') 
* eval randomize(soggetto, ["nome", "credenziali.certificato.issuer", "credenziali.certificato.subject"])
* eval soggetto.ruoli = []


@FindAll200
Scenario: Soggetti FindAll 200 OK
    
    * call findall_200 { resourcePath: 'soggetti', body: '#(soggetto)', key: '#(soggetto.nome)' }

@Get200
Scenario: Soggetti Get 200 OK

    * call get_200 { resourcePath: 'soggetti', body: '#(soggetto)', key: '#(soggetto.nome)' }

@Get404
Scenario: Soggetti Get 404

    * call get_404 ( { resourcePath: "soggetti/" + soggetto.nome } )
