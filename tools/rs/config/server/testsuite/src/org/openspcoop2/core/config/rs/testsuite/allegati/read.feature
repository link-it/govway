Feature: Lettura Allegati

Background:
* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione
* def allegato = read('allegato.json')

@FindAll200
Scenario: Allegati FindAll 200 OK

    * call create ({ resourcePath: "api", body: api })    
    * call findall_200 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome })
    * call delete ({ resourcePath: "api/" + api_path })

@Get200
Scenario: Allegati Get 200 OK

    * call create ({ resourcePath: "api", body: api })    
    * call get_200 ({ resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome })
    * call delete ({ resourcePath: "api/" + api_path })

@DownloadAllegato
Scenario: Allegati Download
    
    * call create ({ resourcePath: "api", body: api })
    * call create ({ resourcePath: 'api/' + api_path + '/allegati', body: allegato })
    
    Given url configUrl
    And path "api", api_path, "allegati", allegato.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    
    * call delete ({ resourcePath: 'api/' + api_path + '/allegati/' + allegato.allegato.nome, body: allegato })
    * call delete ({ resourcePath: "api/" + api_path })

@Get404
Scenario: Allegati Get 404

    * call get_404 ({ resourcePath: "api/" + api_path + '/allegati/' + allegato.allegato.nome })

