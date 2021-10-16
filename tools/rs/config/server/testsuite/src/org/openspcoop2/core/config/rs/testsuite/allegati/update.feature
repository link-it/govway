Feature: Update Allegati

Background:

* call read('classpath:crud_commons.feature')

* def api = read('api.json')
* eval randomize(api, ["nome"])

* def api_path = api.nome + '/' + api.versione
* def allegato = read('allegato.json')
* def allegato2 = read('allegato2.json')
* def allegato_specificasemiformale_src = read('allegato_specificasemiformale.json')
* def allegato_specificasemiformale = read('allegato_specificasemiformale.json')


@Update204
Scenario: Allegati Update 204

    * call create ( { resourcePath: "api", body: api } )
    * call update_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, body_update: allegato2, key: allegato.allegato.nome, delete_key: allegato2.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@Update404
Scenario: Allegati Update 404

    * call create ( { resourcePath: "api", body: api } )
    * call update_404 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato, key: allegato.allegato.nome } )
    * call delete ( { resourcePath: "api/" + api_path })

@UpdateSpecificaSemiformale204
Scenario Outline: SpecificaSemiformale Update 204

    * call create ( { resourcePath: "api", body: api } )
    * eval allegato_specificasemiformale.allegato.tipo_specifica = '<tipo_specifica>'
    * eval allegato_specificasemiformale.allegato.nome = '<nome>'
    * eval allegato_specificasemiformale.allegato.documento = '<contenuto>'
    * call update_204 ( { resourcePath: 'api/' + api_path + '/allegati', body: allegato_specificasemiformale_src, body_update: allegato_specificasemiformale, key: allegato_specificasemiformale_src.allegato.nome, delete_key: allegato_specificasemiformale.allegato.nome } )
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
