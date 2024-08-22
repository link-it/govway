Feature: Lettura Allegati Fruizioni
 
Background:
* call read('classpath:crud_commons.feature')

* def decodeBase64 = read('classpath:basic64-decode.js')

* def api_petstore = read('../api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def erogatore = read('../soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('../fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione
* def fruizione_petstore_path = 'fruizioni/' + fruizione_key

* def allegato = read('allegato.json')

* def allegato_specificasemiformale = read('allegato_specificasemiformale.json')

* def allegato_specificalivelloservizio = read('allegato_specificaLivelloServizio.json')

* def allegato_specificasicurezza = read('allegato_specificaSicurezza.json')


@FindAll200
Scenario: Allegati Fruizioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call findall_200 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Allegati Fruizioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
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
    And path fruizione_petstore_path + '/allegati' , allegato.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato.allegato

    # DELETE

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@GetSpecificaSemiformale200
Scenario Outline: SpecificaSemiformale Fruizioni Get 200 OK

    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato_specificasemiformale
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
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
    And path fruizione_petstore_path + '/allegati' , allegato_specificasemiformale.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato_specificasemiformale.allegato

    # DELETE

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato_specificasemiformale.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

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


@GetSpecificaLivelloServizio200
Scenario Outline: SpecificaLivelloServizio Fruizioni Get 200 OK

    * eval allegato_specificalivelloservizio.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificalivelloservizio.allegato.nome = '<nome>'
    * eval allegato_specificalivelloservizio.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato_specificalivelloservizio
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.total == 1
    * match response.items[0].allegato.nome == allegato_specificalivelloservizio.allegato.nome
    * match response.items[0].allegato.ruolo == allegato_specificalivelloservizio.allegato.ruolo
    * match response.items[0].allegato.tipo_specifica == allegato_specificalivelloservizio.allegato.tipo_specifica
    * match response.items[0].allegato.documento == '#notpresent'

    # READ

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato_specificalivelloservizio.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato_specificalivelloservizio.allegato

    # DELETE

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato_specificalivelloservizio.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|
|WS-Agreement|AllegatoJsonSpecLivelloServizioWS-Agreement.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4K|
|WSLA|AllegatoJsonSpecLivelloServizioWSLA.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4K|


@GetSpecificaSicurezza200
Scenario Outline: SpecificaSicurezza Fruizioni Get 200 OK

    * eval allegato_specificasicurezza.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasicurezza.allegato.nome = '<nome>'
    * eval allegato_specificasicurezza.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And  header Authorization = govwayConfAuth
    And request allegato_specificasicurezza
    And params query_params
    When method post
    Then assert responseStatus == 201

    # READ ALL

    Given url configUrl
    And path fruizione_petstore_path + '/allegati'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.total == 1
    * match response.items[0].allegato.nome == allegato_specificasicurezza.allegato.nome
    * match response.items[0].allegato.ruolo == allegato_specificasicurezza.allegato.ruolo
    * match response.items[0].allegato.tipo_specifica == allegato_specificasicurezza.allegato.tipo_specifica
    * match response.items[0].allegato.documento == '#notpresent'

    # READ

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato_specificasicurezza.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    * match response.allegato == allegato_specificasicurezza.allegato

    # DELETE

    Given url configUrl
    And path fruizione_petstore_path + '/allegati' , allegato_specificasicurezza.allegato.nome
    And header Authorization = govwayConfAuth
    And params query_params
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|
|WS-Policy|AllegatoJsonSpecLivelloServizioWS-Policy.xml|PHRlc3Q+RXhhbXBsZTwvdGVzdD4K|
|XACML-Policy|AllegatoJsonSpecLivelloServizioXACML-Policy.xml|PFBvbGljeSBQb2xpY3lJZD0iUG9saWN5IgoJUnVsZUNvbWJpbmluZ0FsZ0lkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOnJ1bGUtY29tYmluaW5nLWFsZ29yaXRobTpwZXJtaXQtb3ZlcnJpZGVzIgoJeG1sbnM9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoyLjA6cG9saWN5OnNjaGVtYTpvcyIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIKCXhzaTpzY2hlbWFMb2NhdGlvbj0idXJuOm9hc2lzOm5hbWVzOnRjOnhhY21sOjIuMDpwb2xpY3k6c2NoZW1hOm9zIGh0dHA6Ly9kb2NzLm9hc2lzLW9wZW4ub3JnL3hhY21sLzIuMC9hY2Nlc3NfY29udHJvbC14YWNtbC0yLjAtcG9saWN5LXNjaGVtYS1vcy54c2QiPgoJPFRhcmdldCAvPgoJPFJ1bGUgRWZmZWN0PSJQZXJtaXQiIFJ1bGVJZD0ib2siPgoJCTxDb25kaXRpb24+CgkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOm9yIj4KCgkJCQk8QXBwbHkKCQkJCQlGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IKCQkJCQkJQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIgoJCQkJCQlEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIiAvPgoJCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1iYWciPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+QW1taW5pc3RyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+T3BlcmF0b3JlPC9BdHRyaWJ1dGVWYWx1ZT4KCQkJCQk8L0FwcGx5PgoJCQkJPC9BcHBseT4KCgkJCQk8QXBwbHkKCQkJCQlGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IKCQkJCQkJQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIgoJCQkJCQlEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIiAvPgoJCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1iYWciPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+QW1taW5pc3RyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+T3BlcmF0b3JlPC9BdHRyaWJ1dGVWYWx1ZT4KCQkJCQk8L0FwcGx5PgoJCQkJPC9BcHBseT4KCgkJCTwvQXBwbHk+CgkJPC9Db25kaXRpb24+Cgk8L1J1bGU+Cgk8UnVsZSBFZmZlY3Q9IkRlbnkiIFJ1bGVJZD0ia28iIC8+CjwvUG9saWN5Pg==|
|Linguaggio Naturale|AllegatoJsonSpecSicurezza.txt|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|



@DownloadAllegato
Scenario: Allegati Fruizioni Download

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato })

    * def allegatoDecodificato = call decodeBase64 allegato.allegato.documento
    * def allegatoStringa = karate.toString(allegatoDecodificato)

    Given url configUrl
    And path fruizione_petstore_path, "allegati", allegato.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['text/plain'] }
    * match response == allegatoStringa

    * call delete ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato.allegato.nome })
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
 

@DownloadSpecificaSemiformale
Scenario Outline: Fruizioni Download SpecificaSemiformale

    # * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    # * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    # * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'

    * eval karate.set('allegato_specificasemiformale.allegato.tipo_specifica', '<tipo_specifica>')
    * eval karate.set('allegato_specificasemiformale.allegato.nome', '<nome>')
    * eval karate.set('allegato_specificasemiformale.allegato.documento', '<contenuto>')
 
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato_specificasemiformale })

    * def allegatoDecodificato = call decodeBase64 allegato_specificasemiformale.allegato.documento
    * def allegatoStringa = karate.toString(allegatoDecodificato)
    
    Given url configUrl
    And path fruizione_petstore_path, "allegati", allegato_specificasemiformale.allegato.nome, 'download'
    
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['<content_type>'] }
    * match response == allegatoDecodificato

    * call delete ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato_specificasemiformale.allegato.nome })
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
   
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
 

@DownloadSpecificaLivelloServizio
Scenario Outline: Fruizioni Download SpecificaLivelloServizio

    * eval allegato_specificalivelloservizio.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificalivelloservizio.allegato.nome = '<nome>'
    * eval allegato_specificalivelloservizio.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato_specificalivelloservizio })

    * def allegatoDecodificato = call decodeBase64 allegato_specificalivelloservizio.allegato.documento
    * def allegatoStringa = karate.toString(allegatoDecodificato)
    
    Given url configUrl
    And path fruizione_petstore_path, "allegati", allegato_specificalivelloservizio.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['<content_type>'] }
    * match response == allegatoStringa

    * call delete ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato_specificalivelloservizio.allegato.nome })
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|content_type|
|WS-Agreement|AllegatoJsonSpecLivelloServizioWS-Agreement.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4=|text/xml|
|WSLA|AllegatoJsonSpecLivelloServizioWSLA.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4=|text/xml|




@DownloadSpecificaSicurezza
Scenario Outline: Fruizioni Download SpecificaSicurezza

    * eval allegato_specificasicurezza.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasicurezza.allegato.nome = '<nome>'
    * eval allegato_specificasicurezza.allegato.documento = '<contenuto>'

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    * call create ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato_specificasicurezza })

    * def allegatoDecodificato = call decodeBase64 allegato_specificasicurezza.allegato.documento
    * def allegatoStringa = karate.toString(allegatoDecodificato)
    
    Given url configUrl
    And path fruizione_petstore_path, "allegati", allegato_specificasicurezza.allegato.nome, 'download'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And match responseHeaders contains { 'Content-Type': ['<content_type>'] }
    * match response == allegatoStringa

    * call delete ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato_specificasicurezza.allegato.nome })
    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|content_type|
|WS-Policy|AllegatoJsonSpecLivelloServizioWS-Policy.xml|PHRlc3Q+RXhhbXBsZTwvdGVzdD4=|text/xml|
|XACML-Policy|AllegatoJsonSpecLivelloServizioXACML-Policy.xml|PFBvbGljeSB4bWxucz0idXJuOm9hc2lzOm5hbWVzOnRjOnhhY21sOjIuMDpwb2xpY3k6c2NoZW1hOm9zIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiBQb2xpY3lJZD0iUG9saWN5IiBSdWxlQ29tYmluaW5nQWxnSWQ9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoxLjA6cnVsZS1jb21iaW5pbmctYWxnb3JpdGhtOnBlcm1pdC1vdmVycmlkZXMiIHhzaTpzY2hlbWFMb2NhdGlvbj0idXJuOm9hc2lzOm5hbWVzOnRjOnhhY21sOjIuMDpwb2xpY3k6c2NoZW1hOm9zIGh0dHA6Ly9kb2NzLm9hc2lzLW9wZW4ub3JnL3hhY21sLzIuMC9hY2Nlc3NfY29udHJvbC14YWNtbC0yLjAtcG9saWN5LXNjaGVtYS1vcy54c2QiPgoJPFRhcmdldC8+Cgk8UnVsZSBFZmZlY3Q9IlBlcm1pdCIgUnVsZUlkPSJvayI+CgkJPENvbmRpdGlvbj4KCQkJPEFwcGx5IEZ1bmN0aW9uSWQ9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoxLjA6ZnVuY3Rpb246b3IiPgoKCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IgQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIiBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CgkJCQkJPEFwcGx5IEZ1bmN0aW9uSWQ9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoxLjA6ZnVuY3Rpb246c3RyaW5nLWJhZyI+CgkJCQkJCTxBdHRyaWJ1dGVWYWx1ZSBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIj5BbW1pbmlzdHJhdG9yZTwvQXR0cmlidXRlVmFsdWU+CgkJCQkJCTxBdHRyaWJ1dGVWYWx1ZSBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIj5PcGVyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCTwvQXBwbHk+CgkJCQk8L0FwcGx5PgoKCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IgQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIiBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CgkJCQkJPEFwcGx5IEZ1bmN0aW9uSWQ9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoxLjA6ZnVuY3Rpb246c3RyaW5nLWJhZyI+CgkJCQkJCTxBdHRyaWJ1dGVWYWx1ZSBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIj5BbW1pbmlzdHJhdG9yZTwvQXR0cmlidXRlVmFsdWU+CgkJCQkJCTxBdHRyaWJ1dGVWYWx1ZSBEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIj5PcGVyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCTwvQXBwbHk+CgkJCQk8L0FwcGx5PgoKCQkJPC9BcHBseT4KCQk8L0NvbmRpdGlvbj4KCTwvUnVsZT4KCTxSdWxlIEVmZmVjdD0iRGVueSIgUnVsZUlkPSJrbyIvPgo8L1BvbGljeT4=|text/xml|
|Linguaggio Naturale|AllegatoJsonSpecSicurezza.txt|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|text/plain|

   
@Get404
Scenario: Allegati Fruizioni Get 404

    * call get_404 ({ resourcePath: fruizione_petstore_path + '/allegati/' + allegato.allegato.nome })

