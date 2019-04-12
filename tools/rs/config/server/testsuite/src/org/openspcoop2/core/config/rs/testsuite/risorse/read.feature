Feature: Lettura Risorse

Background:
* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])
* eval api.referente = soggettoDefault

* def risorsa = read('risorsa.json')
* eval randomize(risorsa, ["nome"])

* def api_path = 'api/' + api.nome + '/' + api.versione
* def risorse_path = api_path + '/risorse'

@FindAll200
Scenario: Risorse FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api })
    * call findall_200 ({ resourcePath: risorse_path, body: risorsa,  key: risorsa.nome })
    * call delete ({ resourcePath: api_path })

@Get200
Scenario: Risorse Get 200 OK

    * call create ({ resourcePath: 'api', body: api })
    * call get_200 ({ resourcePath: risorse_path, body: risorsa,  key: risorsa.nome })
    * call delete ({ resourcePath: api_path })

@Get404
Scenario: Risorse Get 404

    * call create ({ resourcePath: 'api', body: api })
    * call get_404 ({ resourcePath: risorse_path + '/' + risorsa.nome })
    * call delete ({ resourcePath: api_path })
