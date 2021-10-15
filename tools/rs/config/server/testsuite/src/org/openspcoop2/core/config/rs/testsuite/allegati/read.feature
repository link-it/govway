Feature: Lettura Allegati

Background:
* call read('classpath:crud_commons.feature')

* def decodeBase64 = read('classpath:basic64-decode.js')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione
* def allegato = read('allegato.json')
* def allegato_specificasemiformale = read('allegato_specificasemiformale.json')

@FindAll200
Scenario: Allegati FindAll 200 OK

    * call create ({ resourcePath: "api", body: api })    
    * call findall_200 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome })
    * call delete ({ resourcePath: "api/" + api_path })

@Get200
Scenario: Allegati Get 200 OK

    * call create ({ resourcePath: "api", body: api })   

    Given url configUrl
    And path 'api/' + api_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path 'api/' + api_path + '/allegati'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.total == 1
    * match response.items[0].allegato.nome == allegato.allegato.nome
    * match response.items[0].allegato.ruolo == allegato.allegato.ruolo
    * match response.items[0].allegato.tipo_specifica == '#notpresent'
    * match response.items[0].allegato.documento == '#notpresent'

    # READ

    Given url configUrl
    And path 'api/' + api_path + '/allegati' , allegato.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato.allegato

    # DELETE

    Given url configUrl
    And path 'api/' + api_path + '/allegati' , allegato.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
 
    * call delete ({ resourcePath: "api/" + api_path })

@GetSpecificaSemiformale200
Scenario Outline: SpecificaSemiformale Get 200 OK

    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: "api", body: api })   

    Given url configUrl
    And path 'api/' + api_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato_specificasemiformale
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path 'api/' + api_path + '/allegati'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.total == 1
    * match response.items[0].allegato.nome == allegato_specificasemiformale.allegato.nome
    * match response.items[0].allegato.ruolo == allegato_specificasemiformale.allegato.ruolo
    * match response.items[0].allegato.tipo_specifica == allegato_specificasemiformale.allegato.tipo_specifica
    * match response.items[0].allegato.documento == '#notpresent'

    # READ

    Given url configUrl
    And path 'api/' + api_path + '/allegati' , allegato_specificasemiformale.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato_specificasemiformale.allegato

    # DELETE

    Given url configUrl
    And path 'api/' + api_path + '/allegati' , allegato_specificasemiformale.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204
 
    * call delete ({ resourcePath: "api/" + api_path })


Examples:
|tipo_specifica|nome|contenuto|
|UML|AllegatoJsonSpecSemiformale1.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|UML|Allegato con Nomi particolari 123-_.,.!"£$%&()=?^ e caratteri.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|UML|2020-02-03 file strano _test23.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|HTML|AllegatoJsonSpecSemiformale1.html|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|YAML|AllegatoJsonSpecSemiformale1.yaml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|Linguaggio Naturale|AllegatoJsonSpecSemiformale1.txt|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|
|XSD|AllegatoJsonSpecSemiformale1.xsd|PHhzZDpzY2hlbWEgdGFyZ2V0TmFtZXNwYWNlPSJodHRwOi8vd3MtaS5vcmcvcHJvZmlsZXMvYmFzaWMvMS4xL3hzZCIgDQogICAgICAgICAgICB4bWxuczp4c2Q9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hIj4gDQogIDx4c2Q6c2ltcGxlVHlwZSBuYW1lPSJzd2FSZWYiPiANCiAgICA8eHNkOnJlc3RyaWN0aW9uIGJhc2U9InhzZDphbnlVUkkiIC8+IA0KICA8L3hzZDpzaW1wbGVUeXBlPiANCjwveHNkOnNjaGVtYT4NCg==|
|XML|AllegatoJsonSpecSemiformale1.xml|PHRlc3Q+RXhhbXBsZTwvdGVzdD4K|
|JSON|AllegatoJsonSpecSemiformale1.json|eyJ0ZXN0IjoiZXhhbXBsZSJ9Cg==|


@DownloadAllegato
Scenario: Allegati Download
    
    * call create ({ resourcePath: "api", body: api })
    * call create ({ resourcePath: 'api/' + api_path + '/allegati', body: allegato })
    
    * def allegatoDecodificato = call decodeBase64 allegato.allegato.documento

    Given url configUrl
    And path "api", api_path, "allegati", allegato.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['text/plain'] }
    * match response == allegatoDecodificato
    
    * call delete ({ resourcePath: 'api/' + api_path + '/allegati/' + allegato.allegato.nome, body: allegato })
    * call delete ({ resourcePath: "api/" + api_path })

@DownloadSpecificaSemiformale
Scenario Outline: SpecificaSemiformale Download
    
    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: "api", body: api })
    * call create ({ resourcePath: 'api/' + api_path + '/allegati', body: allegato_specificasemiformale })
    
    * def allegatoDecodificato = call decodeBase64 allegato_specificasemiformale.allegato.documento

    Given url configUrl
    And path "api", api_path, "allegati", allegato_specificasemiformale.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['<content_type>'] }
    * match response == allegatoDecodificato
    
    * call delete ({ resourcePath: 'api/' + api_path + '/allegati/' + allegato_specificasemiformale.allegato.nome, body: allegato_specificasemiformale })
    * call delete ({ resourcePath: "api/" + api_path })

Examples:
|tipo_specifica|nome|contenuto|content_type|
|UML|AllegatoJsonSpecSemiformale1.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|application/x-download|
|UML|Allegato con Nomi particolari 123-_.,.!"£$%&()=?^ e caratteri.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|application/x-download|
|UML|2020-02-03 file strano _test23.uml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|application/x-download|
|HTML|AllegatoJsonSpecSemiformale1.html|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|text/html|
|YAML|AllegatoJsonSpecSemiformale1.yaml|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|application/x-download|
|Linguaggio Naturale|AllegatoJsonSpecSemiformale1.txt|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|text/plain|
|XSD|AllegatoJsonSpecSemiformale1.xsd|PHhzZDpzY2hlbWEgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgdGFyZ2V0TmFtZXNwYWNlPSJodHRwOi8vd3MtaS5vcmcvcHJvZmlsZXMvYmFzaWMvMS4xL3hzZCI+IAogIDx4c2Q6c2ltcGxlVHlwZSBuYW1lPSJzd2FSZWYiPiAKICAgIDx4c2Q6cmVzdHJpY3Rpb24gYmFzZT0ieHNkOmFueVVSSSIvPiAKICA8L3hzZDpzaW1wbGVUeXBlPiAKPC94c2Q6c2NoZW1hPg==|text/xml|
|XML|AllegatoJsonSpecSemiformale1.xml|PHRlc3Q+RXhhbXBsZTwvdGVzdD4=|text/xml|

@DownloadSpecificaSemiformaleJson
Scenario Outline: SpecificaSemiformale Download Json
    
    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: "api", body: api })
    * call create ({ resourcePath: 'api/' + api_path + '/allegati', body: allegato_specificasemiformale })
    
    * def allegatoDecodificato = call decodeBase64 allegato_specificasemiformale.allegato.documento

    Given url configUrl
    And path "api", api_path, "allegati", allegato_specificasemiformale.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['<content_type>'] }
    * match response.test == 'example'
    
    * call delete ({ resourcePath: 'api/' + api_path + '/allegati/' + allegato_specificasemiformale.allegato.nome, body: allegato_specificasemiformale })
    * call delete ({ resourcePath: "api/" + api_path })

Examples:
|tipo_specifica|nome|contenuto|content_type|
|JSON|AllegatoJsonSpecSemiformale1.json|eyJ0ZXN0IjoiZXhhbXBsZSJ9Cg==|application/json|

@Get404
Scenario: Allegati Get 404

    * call get_404 ({ resourcePath: "api/" + api_path + '/allegati/' + allegato.allegato.nome })

