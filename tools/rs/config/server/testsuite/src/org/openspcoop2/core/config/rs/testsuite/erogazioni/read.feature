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

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault
* eval api_spcoop.tags = (['testsuite-read-spcoop'])
* eval randomize(api_spcoop, ["tags.0"])

* def erogazione_spcoop = read('erogazione_spcoop.json')
* eval erogazione_spcoop.api_nome = api_spcoop.nome
* eval erogazione_spcoop.api_versione = api_spcoop.versione
* eval erogazione_spcoop.api_referente = api_spcoop.referente
* eval erogazione_spcoop.api_tags = ([api_spcoop.tags[0]])
* def spcoop_key = erogazione_spcoop.api_soap_servizio + '/' + erogazione_spcoop.api_versione
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione

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
Scenario Outline: Erogazioni FindAll di Api <profilo> con qualsiasi profilo e soggetto

* def api_test = read('<api>') 
* eval api_test.tags = (['TESTSUITE'])
* eval randomize(api_test, ["nome"])
* eval randomize(api_test, ["tags.0"])

* def erogazione_test = read('<erogazione>') 
* eval erogazione_test.api_tags = ([api_test.tags[0]])
* eval erogazione_test.api_nome = api_test.nome
* eval erogazione_test.erogazione_nome = api_test.nome
* eval erogazione_test.api_versione = api_test.versione

* def key = api_test.nome + '/' + api_test.versione
* def api_test_path = 'api/' + key
* def erogazione_test_path = 'erogazioni/' + key

* call create ({ resourcePath: 'api', body: api_test, query_params: { profilo: '<profilo>'}})
* call create ({ resourcePath: 'erogazioni', body: erogazione_test, query_params: { profilo: '<profilo>'}})

* def erogazioni_response = call read('classpath:findall_stub.feature') ({ resourcePath: 'erogazioni', query_params:  { profilo_qualsiasi: true, soggetto_qualsiasi: true, tag: api_test.tags[0] } })
* assert erogazioni_response.findall_response_body.items.length == 1
* assert erogazioni_response.findall_response_body.items[0].nome == erogazione_test.erogazione_nome
* assert erogazioni_response.findall_response_body.items[0].profilo == '<profilo>'

* call delete ({ resourcePath: erogazione_test_path, query_params: { profilo: '<profilo>'} })
* call delete ({ resourcePath: api_test_path, query_params: { profilo: '<profilo>'} })

Examples:
| api                | erogazione                | profilo    |
| api_petstore.json  | erogazione_petstore.json  | APIGateway |
| api_modi_soap.json | erogazione_modi_soap.json | ModI     |

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


@FindAllProfiloSPCoop
Scenario: Erogazioni FindAll di un'erogazione SPCoop restituisce profilo SPCoop e soggetto corretto (non quello di default)

    # Creo un Soggetto SPCoop interno randomizzato (diverso dal soggetto di default), da usare come referente API e soggetto erogatore
    * def soggetto_spcoop_erogatore = read('classpath:bodies/soggetto-interno.json')
    * eval randomize(soggetto_spcoop_erogatore, ["nome"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_spcoop_erogatore, query_params: { profilo: "SPCoop" } })

    * eval api_spcoop.referente = soggetto_spcoop_erogatore.nome
    * eval erogazione_spcoop.api_referente = soggetto_spcoop_erogatore.nome

    * def query_params = ( { profilo: "SPCoop", soggetto: soggetto_spcoop_erogatore.nome, tipo_servizio: "ldap" })
    * call create ({ resourcePath: 'api', body: api_spcoop })

    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato })

    * eval erogazione_spcoop.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval erogazione_spcoop.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create ({ resourcePath: 'erogazioni', body: erogazione_spcoop, key: spcoop_key })

    * def erogazioni_response = call read('classpath:findall_stub.feature') ({ resourcePath: 'erogazioni', query_params: { profilo_qualsiasi: true, soggetto_qualsiasi: true, tag: api_spcoop.tags[0] } })
    * assert erogazioni_response.findall_response_body.items.length == 1
    * assert erogazioni_response.findall_response_body.items[0].nome == erogazione_spcoop.api_soap_servizio
    * assert erogazioni_response.findall_response_body.items[0].profilo == 'SPCoop'
    * assert erogazioni_response.findall_response_body.items[0].soggetto == soggetto_spcoop_erogatore.nome

    * call delete ({ resourcePath: 'erogazioni/' + spcoop_key })
    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome })
    * call delete ({ resourcePath: api_spcoop_path })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_spcoop_erogatore.nome, query_params: { profilo: "SPCoop" } })
