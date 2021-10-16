Feature: Create Allegati

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione

* def allegato = read('allegato.json')
* def allegato_specificasemiformale = read('allegato_specificasemiformale.json')

@Create204
Scenario: Allegati Create 204

    * call create ( { resourcePath: "api", body: api } )
    * call create_201 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })

@Create409
Scenario: Allegati Create 409

    * call create ( { resourcePath: "api", body: api } )
    * call create_409 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })

@CreateSpecSemiformale204
Scenario Outline: Allegati Create Specifica Semiformale 204

    * call create ( { resourcePath: "api", body: api } )
    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'
    * call create_201 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato_specificasemiformale, key: allegato_specificasemiformale.allegato.nome})
    * call delete ( { resourcePath: "api/" + api_path })

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

