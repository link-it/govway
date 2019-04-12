Feature: Lettura Ruoli

Background:

* call read('classpath:crud_commons.feature')

* def ruolo = read('ruolo.json') 
* eval randomize( ruolo, ["nome"] )

@FindAll200
Scenario: Ruoli FindAll 200 OK
    
    * call findall_200 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }

@Get200
Scenario: Ruoli Get 200 OK

    * call get_200 { resourcePath: 'ruoli', body: '#(ruolo)', key: '#(ruolo.nome)' }

@Get404
Scenario: Ruoli Get 404

    * call get_404 { resourcePath: '#("ruoli/" + ruolo.nome)' }
