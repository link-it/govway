Feature: Create Allegati Fruizioni

Background:

* call read('classpath:crud_commons.feature')

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
* def allegatoSSem = read('allegato_specificasemiformale.json')
* def allegatoSLivSer = read('allegato_specificaLivelloServizio.json')
* def allegatoSSec = read('allegato_specificaSicurezza.json')

@Create204
Scenario: Create Allegati Fruizioni 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call create_201 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Create409
Scenario: Create Allegati Fruizioni 409

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * call create_409 ( { resourcePath: fruizione_petstore_path + '/allegati', body: allegato, key: allegato.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Create204SpecificaSemiformale
Scenario Outline: Allegati SpecificaSemiformale Fruizioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * eval allegatoSSem.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegatoSSem.allegato.nome = '<nome>'
    * eval allegatoSSem.allegato.documento = '<contenuto>'

    * call create_201 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegatoSSem, key: allegatoSSem.allegato.nome})

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

@Create204SpecificaLivelloServizio
Scenario Outline: Allegati SpecificaLivelloServizio Fruizioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    * eval allegatoSLivSer.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegatoSLivSer.allegato.nome = '<nome>'
    * eval allegatoSLivSer.allegato.documento = '<contenuto>'

    * call create_201 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegatoSLivSer, key: allegatoSLivSer.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|
|WS-Agreement|AllegatoJsonSpecLivelloServizioWS-Agreement.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4K|
|WSLA|AllegatoJsonSpecLivelloServizioWSLA.xml|PFdTTEE+RXNlbXBpbzwvV1NMQT4K|

@Create204SpecificaSicurezza
Scenario Outline: Allegati SpecificaSicurezza Fruizioni Create 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
      
    * eval allegatoSSec.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegatoSSec.allegato.nome = '<nome>'
    * eval allegatoSSec.allegato.documento = '<contenuto>'
   
    * call create_201 ({ resourcePath: fruizione_petstore_path + '/allegati', body: allegatoSSec, key: allegatoSSec.allegato.nome})

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|tipo_specifica|nome|contenuto|
|WS-Policy|AllegatoJsonSpecLivelloServizioWS-Policy.xml|PHRlc3Q+RXhhbXBsZTwvdGVzdD4K|
|XACML-Policy|AllegatoJsonSpecLivelloServizioXACML-Policy.xml|PFBvbGljeSBQb2xpY3lJZD0iUG9saWN5IgoJUnVsZUNvbWJpbmluZ0FsZ0lkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOnJ1bGUtY29tYmluaW5nLWFsZ29yaXRobTpwZXJtaXQtb3ZlcnJpZGVzIgoJeG1sbnM9InVybjpvYXNpczpuYW1lczp0Yzp4YWNtbDoyLjA6cG9saWN5OnNjaGVtYTpvcyIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIKCXhzaTpzY2hlbWFMb2NhdGlvbj0idXJuOm9hc2lzOm5hbWVzOnRjOnhhY21sOjIuMDpwb2xpY3k6c2NoZW1hOm9zIGh0dHA6Ly9kb2NzLm9hc2lzLW9wZW4ub3JnL3hhY21sLzIuMC9hY2Nlc3NfY29udHJvbC14YWNtbC0yLjAtcG9saWN5LXNjaGVtYS1vcy54c2QiPgoJPFRhcmdldCAvPgoJPFJ1bGUgRWZmZWN0PSJQZXJtaXQiIFJ1bGVJZD0ib2siPgoJCTxDb25kaXRpb24+CgkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOm9yIj4KCgkJCQk8QXBwbHkKCQkJCQlGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IKCQkJCQkJQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIgoJCQkJCQlEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIiAvPgoJCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1iYWciPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+QW1taW5pc3RyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+T3BlcmF0b3JlPC9BdHRyaWJ1dGVWYWx1ZT4KCQkJCQk8L0FwcGx5PgoJCQkJPC9BcHBseT4KCgkJCQk8QXBwbHkKCQkJCQlGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1hdC1sZWFzdC1vbmUtbWVtYmVyLW9mIj4KCQkJCQk8U3ViamVjdEF0dHJpYnV0ZURlc2lnbmF0b3IKCQkJCQkJQXR0cmlidXRlSWQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphc3NlcnRpb246QXR0cmlidXRlU3RhdGVtZW50OkF0dHJpYnV0ZTpOYW1lOlJ1b2xvIgoJCQkJCQlEYXRhVHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIiAvPgoJCQkJCTxBcHBseSBGdW5jdGlvbklkPSJ1cm46b2FzaXM6bmFtZXM6dGM6eGFjbWw6MS4wOmZ1bmN0aW9uOnN0cmluZy1iYWciPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+QW1taW5pc3RyYXRvcmU8L0F0dHJpYnV0ZVZhbHVlPgoJCQkJCQk8QXR0cmlidXRlVmFsdWUgRGF0YVR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+T3BlcmF0b3JlPC9BdHRyaWJ1dGVWYWx1ZT4KCQkJCQk8L0FwcGx5PgoJCQkJPC9BcHBseT4KCgkJCTwvQXBwbHk+CgkJPC9Db25kaXRpb24+Cgk8L1J1bGU+Cgk8UnVsZSBFZmZlY3Q9IkRlbnkiIFJ1bGVJZD0ia28iIC8+CjwvUG9saWN5Pg==|
|Linguaggio Naturale|AllegatoJsonSpecSicurezza.txt|Q29udGVudXRvIGRlbGwnIGFsbGVnYXRvLmpzb24K|

