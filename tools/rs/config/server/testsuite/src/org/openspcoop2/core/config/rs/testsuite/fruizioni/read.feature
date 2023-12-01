Feature: Lettura Fruizioni

Background:
* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault
* def api_soap_piu_azioni_path = 'api/' + api_soap_piu_azioni.nome + '/' + api_soap_piu_azioni.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def fruizione_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione

* def fruizione_soap_piu_azioni = read('fruizione_soap_piu_azioni.json')
* eval fruizione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval fruizione_soap_piu_azioni.fruizione_nome = api_soap_piu_azioni.nome
* eval fruizione_soap_piu_azioni.erogatore = erogatore.nome
* def soap_piu_azioni_key = fruizione_soap_piu_azioni.erogatore + '/' + fruizione_soap_piu_azioni.api_nome + '/' + fruizione_soap_piu_azioni.api_versione
* def fruizione_soap_piu_azioni_path = 'erogazioni/' + soap_piu_azioni_key

* def api_info_generali = read('api_info_generali.json')

* def connettore = read('connettore_fruizione_http.json')
* def info_generali = read('informazioni_generali_petstore.json')
* def erogazione_versione = read('api_versione3.json')

* def fruizione_petstore_descrizione4000 = read('fruizione_petstore_descrizione4000.json')
* eval fruizione_petstore_descrizione4000.api_nome = api_petstore.nome
* eval fruizione_petstore_descrizione4000.api_versione = api_petstore.versione
* eval fruizione_petstore_descrizione4000.erogatore = erogatore.nome
* eval fruizione_petstore_descrizione4000.api_referente = api_petstore.referente

* def fruizione_key_descrizione4000 = fruizione_petstore_descrizione4000.erogatore + '/' + fruizione_petstore_descrizione4000.api_nome + '/' + fruizione_petstore_descrizione4000.api_versione

@FindAll200
Scenario: Fruizioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })

    * call findall_200 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: fruizione_key })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@Get200
Scenario: Fruizioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    
    * call get_200 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: fruizione_key })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@Get404
Scenario: Fruizioni Get 404

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_404 ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazioneRest
Scenario: Fruizioni Get Url Invocazione PetStore di una API REST appena creata

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })
    
    Given url configUrl
    And path 'fruizioni', fruizione_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == 'interface-based'
    And match response.force_interface == false
    And assert response.nome == null
    And assert response.pattern == null

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazioneSoap
Scenario: Fruizioni Get Url Invocazione PetStore di una API SOAP appena creata

    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_soap_piu_azioni })

    Given url configUrl
    And path 'fruizioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == 'url-based'
    And match response.force_interface == true
    And assert response.nome == null
    And assert response.pattern == '/(?:gw_)?'+soggettoDefault+'/(?:gw_)?'+fruizione_soap_piu_azioni.erogatore+'/(?:gw_)?'+fruizione_soap_piu_azioni.fruizione_nome+'/v1/([^/?]*).*'

    * call delete ({ resourcePath: 'fruizioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })

@GetInterfacciaApi
Scenario: Fruizioni Get Interfaccia Api PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', fruizione_key, 'api'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@GetConnettore
Scenario: Fruizioni Get Connettore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    Given url configUrl
    And path 'fruizioni', fruizione_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })
    
    
@FindAllTags
Scenario: Fruizioni FindAll di Api con Tags definiti 

* eval api_petstore.tags = ['TESTSUITE']

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'soggetti', body: erogatore })
* call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

* def fruizioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'fruizioni', query_params:  { tag: '#(api_petstore.tags[0])' } }
* match each fruizioni_response.findall_response_body.items[*].api_tags == '#notnull'
* match each fruizioni_response.findall_response_body.items[*].api_tags[*] contains api_petstore.tags[0]

* call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
* call delete ({ resourcePath: api_petstore_path })


@FindAllProfiloSoggettoQualsiasi
Scenario: Fruizioni FindAll di Api con qualsiasi profilo e soggetto

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'soggetti', body: erogatore })
* call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

* def fruizioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'fruizioni', query_params:  { profilo_qualsiasi: true, soggetto_qualsiasi: true } }
* assert fruizioni_response.findall_response_body.items.length > 0

* call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
* call delete ({ resourcePath: api_petstore_path })


@FindAllUriApiImplementata
Scenario: Fruizioni FindAll di Api con indicazione della uri implementata

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'soggetti', body: erogatore })
* call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

* def fruizioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'fruizioni', query_params:  { uri_api_implementata: 'api-config:1' } }
* assert fruizioni_response.findall_response_body.items.length == 0

* call delete ({ resourcePath: 'fruizioni/' + fruizione_key })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
* call delete ({ resourcePath: api_petstore_path })


@Descrizione
Scenario: Fruizione creazione e lettura descrizione

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'soggetti', body: erogatore })
* call create ({ resourcePath: 'fruizioni', body: fruizione_petstore_descrizione4000 })

    Given url configUrl
    And path 'fruizioni', fruizione_key_descrizione4000, 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == fruizione_petstore_descrizione4000.descrizione

* call delete ({ resourcePath: 'fruizioni/' + fruizione_key_descrizione4000 })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
* call delete ({ resourcePath: api_petstore_path })
