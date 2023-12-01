Feature: Lettura Erogazioni

Background:
* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_soap_piu_azioni = read('api_soap_piu_azioni.json')
* eval randomize(api_soap_piu_azioni, ["nome"])
* eval api_soap_piu_azioni.referente = soggettoDefault

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione
* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key

* def erogazione_soap_piu_azioni = read('erogazione_soap_piu_azioni.json')
* eval erogazione_soap_piu_azioni.api_nome = api_soap_piu_azioni.nome
* eval erogazione_soap_piu_azioni.api_versione = api_soap_piu_azioni.versione
* eval erogazione_soap_piu_azioni.erogazione_nome = api_soap_piu_azioni.nome
* def soap_piu_azioni_key = erogazione_soap_piu_azioni.api_nome + '/' + erogazione_soap_piu_azioni.api_versione
* def api_soap_piu_azioni_path = 'api/' + soap_piu_azioni_key
* def erogazione_soap_piu_azioni_path = 'erogazioni/' + soap_piu_azioni_key

* def erogazione_petstore_descrizione4000 = read('erogazione_petstore_descrizione4000.json')
* eval erogazione_petstore_descrizione4000.api_nome = api_petstore.nome
* eval erogazione_petstore_descrizione4000.api_versione = api_petstore.versione
* def petstore_key_descrizione4000 = erogazione_petstore_descrizione4000.api_nome + '/' + erogazione_petstore_descrizione4000.api_versione

@FindAll200
Scenario: Erogazioni FindAll 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call findall_200 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@Get200
Scenario: Erogazioni Get 200 OK

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_200 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@Get404
Scenario: Erogazioni Get 404

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call get_404 ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazioneRest
Scenario: Erogazioni Get Url Invocazione PetStore di una API REST appena creata

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == 'interface-based'
    And match response.force_interface == false
    And assert response.nome == null
    And assert response.pattern == null

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetUrlInvocazioneSoap
Scenario: Erogazioni Get Url Invocazione PetStore di una API SOAP appena creata

    * call create ({ resourcePath: 'api', body: api_soap_piu_azioni })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_soap_piu_azioni })

    Given url configUrl
    And path 'erogazioni', soap_piu_azioni_key, 'url-invocazione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200
    And assert response.modalita == 'url-based'
    And match response.force_interface == true
    And assert response.nome == null
    And assert response.pattern == '/(?:gw_)?'+soggettoDefault+'/(?:gw_)?'+erogazione_soap_piu_azioni.erogazione_nome+'/v1/([^/?]*).*'

    * call delete ({ resourcePath: 'erogazioni/' + soap_piu_azioni_key })
    * call delete ({ resourcePath: api_soap_piu_azioni_path })


@GetInterfacciaApi
Scenario: Erogazioni Get Interfaccia Api PetStore

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'api'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@GetConnettore
Scenario: Erogazioni Get Connettore Erogazione

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    Given url configUrl
    And path 'erogazioni', petstore_key, 'connettore'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * call delete ({ resourcePath: 'erogazioni/' + petstore_key })
    * call delete ({ resourcePath: api_petstore_path })


@FindAllTags
Scenario: Erogazioni FindAll di Api con Tags definiti 

* eval api_petstore.tags = ['TESTSUITE']

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

* def erogazioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'erogazioni', query_params:  { tag: '#(api_petstore.tags[0])' } }
* match each erogazioni_response.findall_response_body.items[*].api_tags == '#notnull'
* match each erogazioni_response.findall_response_body.items[*].api_tags[*] contains api_petstore.tags[0]

* call delete ({ resourcePath: 'erogazioni/' + petstore_key })
* call delete ({ resourcePath: api_petstore_path })


@FindAllProfiloSoggettoQualsiasi
Scenario: Erogazioni FindAll di Api con qualsiasi profilo e soggetto

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

* def erogazioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'erogazioni', query_params:  { profilo_qualsiasi: true, soggetto_qualsiasi: true } }
* assert erogazioni_response.findall_response_body.items.length > 0

* call delete ({ resourcePath: 'erogazioni/' + petstore_key })
* call delete ({ resourcePath: api_petstore_path })


@FindAllUriApiImplementata
Scenario: Erogazioni FindAll di Api con indicazione della uri implementata

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

* def erogazioni_response = call read('classpath:findall_stub.feature') { resourcePath: 'erogazioni', query_params:  { uri_api_implementata: 'api-config:1' } }
* assert erogazioni_response.findall_response_body.items.length > 0
* match each erogazioni_response.findall_response_body.items[*].api_nome == 'api-config'
* match each erogazioni_response.findall_response_body.items[*].api_versione == 1

* call delete ({ resourcePath: 'erogazioni/' + petstore_key })
* call delete ({ resourcePath: api_petstore_path })


@Descrizione
Scenario: Erogazioni creazione e lettura descrizione

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'erogazioni', body: erogazione_petstore_descrizione4000 })

    Given url configUrl
    And path 'erogazioni', petstore_key_descrizione4000, 'descrizione'
    And header Authorization = govwayConfAuth
    When method get
    Then status 200

    * match response.descrizione == erogazione_petstore_descrizione4000.descrizione

* call delete ({ resourcePath: 'erogazioni/' + petstore_key_descrizione4000 })
* call delete ({ resourcePath: api_petstore_path })
