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

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.tags = (['testsuite-read-spcoop'])
* eval randomize(api_spcoop, ["tags.0"])
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione

* def fruizione_spcoop = read('fruizione_spcoop.json')
* eval fruizione_spcoop.api_nome = api_spcoop.nome
* eval fruizione_spcoop.api_versione = api_spcoop.versione
* eval fruizione_spcoop.api_tags = ([api_spcoop.tags[0]])

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
Scenario Outline: Fruizioni FindAll di Api con qualsiasi profilo e soggetto


* def api_test = read('<api>') 
* eval api_test.tags = (['TESTSUITE'])
* eval randomize(api_test, ["nome"])
* eval randomize(api_test, ["tags.0"])

* def fruizione_test = read('<fruizione>') 
* eval fruizione_test.api_tags = ([api_test.tags[0]])
* eval fruizione_test.api_nome = api_test.nome
* eval fruizione_test.fruizione_nome = api_test.nome
* eval fruizione_test.erogatore = erogatore.nome
* eval fruizione_test.api_versione = api_test.versione

* def key = api_test.nome + '/' + api_test.versione
* def api_test_path = 'api/' + key
* def fruizione_test_path = 'fruizioni/' + erogatore.nome + '/' + key


* call create ({ resourcePath: 'api', body: api_test, query_params: { profilo: '<profilo>' } })
* call create ({ resourcePath: 'soggetti', body: erogatore, query_params: { profilo: '<profilo>' }  })
* call create ({ resourcePath: 'fruizioni', body: fruizione_test,  query_params: { profilo: '<profilo>' }  })

* def fruizioni_response = call read('classpath:findall_stub.feature') ({ resourcePath: 'fruizioni', query_params:  { profilo_qualsiasi: true, soggetto_qualsiasi: true, tag: api_test.tags[0] } })
* assert fruizioni_response.findall_response_body.items.length == 1
* assert fruizioni_response.findall_response_body.items[0].nome == fruizione_test.fruizione_nome
* assert fruizioni_response.findall_response_body.items[0].profilo == '<profilo>'

* call delete ({ resourcePath:  fruizione_test_path,  query_params: { profilo: '<profilo>' }  })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome,  query_params: { profilo: '<profilo>' }  })
* call delete ({ resourcePath: api_test_path, query_params: { profilo: '<profilo>' }  })

Examples:
| api                | fruizione                | profilo    |
| api_petstore.json  | fruizione_petstore.json  | APIGateway |
| api_modi_soap.json | fruizione_modi_soap.json | ModI       |

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


@FindAllProfiloSPCoop
Scenario: Fruizioni FindAll di una fruizione SPCoop restituisce profilo SPCoop e soggetto corretto (non quello di default)

    # Creo un soggetto SPCoop interno randomizzato come fruitore (diverso dal soggetto di default)
    * def soggetto_fruitore_spcoop = read('classpath:bodies/soggetto-interno.json')
    * eval randomize(soggetto_fruitore_spcoop, ["nome"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_fruitore_spcoop, query_params: { profilo: "SPCoop" } })

    # Creo un soggetto SPCoop esterno randomizzato come erogatore (sarà anche referente dell'API SPCoop, di dominio esterno)
    * def soggetto_erogatore_spcoop = read('classpath:bodies/soggetto-esterno.json')
    * eval randomize(soggetto_erogatore_spcoop, ["nome", "credenziali.username"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_erogatore_spcoop, query_params: { profilo: "SPCoop" } })

    * eval api_spcoop.referente = soggetto_erogatore_spcoop.nome
    * eval fruizione_spcoop.api_referente = soggetto_erogatore_spcoop.nome
    * eval fruizione_spcoop.erogatore = soggetto_erogatore_spcoop.nome

    * def spcoop_key = soggetto_erogatore_spcoop.nome + '/' + fruizione_spcoop.api_soap_servizio + '/' + fruizione_spcoop.api_versione

    * def query_params_erogatore = ( { profilo: "SPCoop", soggetto: soggetto_erogatore_spcoop.nome })
    * def query_params_fruitore = ( { profilo: "SPCoop", soggetto: soggetto_fruitore_spcoop.nome, tipo_servizio: "ldap" })

    * call create ({ resourcePath: 'api', body: api_spcoop, query_params: query_params_erogatore })

    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato, query_params: { profilo: "SPCoop" } })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato, query_params: { profilo: "SPCoop" } })

    * eval fruizione_spcoop.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval fruizione_spcoop.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create ({ resourcePath: 'fruizioni', body: fruizione_spcoop, query_params: query_params_fruitore })

    * def fruizioni_response = call read('classpath:findall_stub.feature') ({ resourcePath: 'fruizioni', query_params: { profilo_qualsiasi: true, soggetto_qualsiasi: true, tag: api_spcoop.tags[0] } })
    * assert fruizioni_response.findall_response_body.items.length == 1
    * assert fruizioni_response.findall_response_body.items[0].nome == fruizione_spcoop.api_soap_servizio
    * assert fruizioni_response.findall_response_body.items[0].profilo == 'SPCoop'
    * assert fruizioni_response.findall_response_body.items[0].soggetto == soggetto_fruitore_spcoop.nome

    * call delete ( { resourcePath: 'fruizioni/' + spcoop_key, query_params: query_params_fruitore })
    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome, query_params: { profilo: "SPCoop" } })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome, query_params: { profilo: "SPCoop" } })
    * call delete ( { resourcePath: api_spcoop_path, query_params: query_params_erogatore })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_erogatore_spcoop.nome, query_params: { profilo: "SPCoop" } })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_fruitore_spcoop.nome, query_params: { profilo: "SPCoop" } })
