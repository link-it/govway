Feature: Lettura Azioni

Background:
* call read('classpath:crud_commons.feature')

* def api = read('api_spcoop.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def servizio = read('servizio.json')
* eval randomize(servizio, ["nome"])

* def azione = read('azione.json')
* eval randomize(azione, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def servizi_path = api_path + '/servizi'
* def servizio_path = servizi_path + '/' + servizio.nome
* def azioni_path = servizio_path + '/azioni'
* def query_params = ({ profilo: "SPCoop", soggetto: soggettoDefault })

@FindAll200
Scenario: Azioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call findall_200 ({ resourcePath: azioni_path, body: azione,  key: azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })

@Get200
Scenario: Azioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call get_200 ({ resourcePath: azioni_path, body: azione,  key: azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })

@Get404
Scenario: Azioni Get 404

    * call create ({ resourcePath: 'api', body: api })
    * call create ({ resourcePath: servizi_path, body: servizio, key: servizio.nome })
    * call get_404 ({ resourcePath: azioni_path + '/' + azione.nome })
    * call delete ({ resourcePath: servizio_path })
    * call delete ({ resourcePath: api_path })
