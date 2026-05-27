@parallel=false
Feature: Toggle stato di una erogazione o fruizione (Operativita' API)

Background:

    * call read('classpath:crud_commons.feature')
    * def setup = callonce read('classpath:prepare_tests.feature')
    * configure afterFeature = function(){ karate.call('classpath:cleanup_tests.feature'); }

    * def statoApiUrl = monitorUrl + '/reportistica/configurazione-api/stato'

    # URL al backend rs-api-monitor diretto. Usata SOLO dagli scenari "simple search" (PUT no-body):
    # la spec OpenAPI del PUT non dichiara requestBody, ma Karate 0.9.6 obbliga 'request' per il PUT
    # e auto-imposta Content-Type 'text/plain' non rimovibile (verificato: header X = null / '',
    # request '' / null tutti falliscono o vengono ignorati). Il proxy GovWay (monitorUrl) rifiuta
    # giustamente come 400 InvalidRequestContent. Invocando il backend in diretto (no proxy
    # validator) la richiesta passa: il JAX-RS @PUT non ha @Consumes e ignora il body.
    * def statoApiSimpleSearchUrl = monitorDirectUrl + '/reportistica/configurazione-api/stato'

    # URL configurazione (rs-api-config) per verifica stato per gruppo:
    # GET {configUrl}/erogazioni|fruizioni/.../configurazioni/stato?soggetto=...&gruppo=...
    # ritorna ApiImplStato { abilitato: boolean }.
    * def confStatoErogazioneUrl = configUrl + '/erogazioni/' + setup.api_petstore.nome + '/' + setup.api_petstore.versione + '/configurazioni/stato'
    * def confStatoFruizioneUrl = configUrl + '/fruizioni/' + setup.fruizione_petstore.erogatore + '/' + setup.fruizione_petstore.api_nome + '/' + setup.fruizione_petstore.api_versione + '/configurazioni/stato'

    * def verifyStato = read('classpath:verify_stato_stub.feature')

    # L'ACL auth.aclReportisticaStatoApi su /reportistica/configurazione-api/stato* richiede
    # il ruolo 'operativitaApi'. L'utente 'operatoreO' (seedato da prepare_tests.feature)
    # ha solo quel permesso, sufficiente per le operazioni di toggle stato API.
    * def operatoreCred = call basic ({ username: 'operatoreO', password: '123456' })

    * configure headers = ({ "Authorization": operatoreCred })


@ToggleStatoApi
Scenario: Disabilita e riabilita una erogazione via full search (tutti i gruppi)

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione }
    })
    """

    # disabilita
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # verifica: tutti i mapping (predefinito + 2 nominati) disabilitati
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })

    # riabilita
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # verifica: tutti i mapping riabilitati
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })


@ToggleStatoApi
Scenario: Toggle stato di una fruizione via full search (tutti i gruppi)

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        }
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })


@ToggleStatoApi
Scenario: Toggle stato by simple search (erogazione, tutti i gruppi)

    Given url statoApiSimpleSearchUrl
    And param tipo = 'erogazione'
    And param abilitato = false
    And param nome_servizio = setup.api_petstore.nome
    And param versione_servizio = setup.api_petstore.versione
    And param soggetto = soggettoDefault
    And request ''
    When method PUT
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })

    # riabilita
    Given url statoApiSimpleSearchUrl
    And param tipo = 'erogazione'
    And param abilitato = true
    And param nome_servizio = setup.api_petstore.nome
    And param versione_servizio = setup.api_petstore.versione
    And param soggetto = soggettoDefault
    And request ''
    When method PUT
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })


@ToggleStatoApi
Scenario: Toggle stato by simple search (fruizione)

    Given url statoApiSimpleSearchUrl
    And param tipo = 'fruizione'
    And param abilitato = false
    And param nome_servizio = setup.fruizione_petstore.api_nome
    And param versione_servizio = setup.fruizione_petstore.api_versione
    And param soggetto_remoto = setup.fruizione_petstore.erogatore
    And param soggetto = soggettoDefault
    And request ''
    When method PUT
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })

    # riabilita per cleanup
    Given url statoApiSimpleSearchUrl
    And param tipo = 'fruizione'
    And param abilitato = true
    And param nome_servizio = setup.fruizione_petstore.api_nome
    And param versione_servizio = setup.fruizione_petstore.api_versione
    And param soggetto_remoto = setup.fruizione_petstore.erogatore
    And param soggetto = soggettoDefault
    And request ''
    When method PUT
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })


@ToggleStatoApi
Scenario: API inesistente => 404

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: true,
        api: { nome: 'api-non-esistente-' + random(), versione: 1 }
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 404


@ToggleStatoApi
Scenario: Body invalido (manca tipo) => 400

    * def body = ({ abilitato: true, api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione } })

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 400


@ToggleStatoApi
Scenario: Idempotenza - disabilita due volte di seguito

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione }
    })
    """

    # prima disabilita
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: false })

    # seconda disabilita: deve restare 204 e lo stato non deve cambiare
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: false })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: true })


# -------------------------------------------------------------------
# Toggle puntuale per gruppi (campo body.gruppi, solo via POST)
# -------------------------------------------------------------------
# In prepare_tests.feature ogni erogazione/fruizione petstore ha:
#   - setup.gruppo.nome                 (gruppo http-basic)
#   - setup.gruppo_authn_principal.nome (gruppo principal header-based)
# Comportamento atteso (ToggleStatoApiHelper:154-189):
#   - 'gruppi' contiene almeno un nome che matcha   -> 204 sui matchati
#   - 'gruppi' contiene SOLO nomi che non matchano  -> 404
#   - 'gruppi' assente o vuoto                      -> applica a tutti


@ToggleStatoApiGruppi
Scenario: Disabilita solo un gruppo di una erogazione: l'altro gruppo deve restare abilitato

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ setup.gruppo.nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # il gruppo target e' disabilitato, predefinito e g_authn restano invariati (abilitati)
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true  })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # tutti e tre abilitati (gruppo target ri-abilitato, gli altri sempre rimasti tali)
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Disabilita un sotto-insieme (entrambi i gruppi nominati) su erogazione

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ setup.gruppo.nome, setup.gruppo_authn_principal.nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # i 2 nominati disabilitati, il PREDEFINITO resta abilitato (non era nell'array)
    # -- questo e' cio' che distingue 'sotto-insieme' da 'tutti i gruppi'
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Gruppo inesistente unico => 404 e nessuno stato modificato

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ 'gruppo-non-esistente-' + random() ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 404

    # tutti i mapping reali devono essere rimasti abilitati (404 = no-op)
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Mix esistente + inesistente => 204 applicato al solo esistente

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ setup.gruppo.nome, 'gruppo-non-esistente-' + random() ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # il gruppo esistente e' stato disabilitato, l'altro reale e il predefinito NO
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true  })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true  })

    # riabilita per cleanup solo il gruppo effettivamente toccato
    * def cleanupBody =
    """
    ({
        tipo: 'erogazione',
        abilitato: true,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ setup.gruppo.nome ]
    })
    """
    Given url statoApiUrl
    And request cleanupBody
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: gruppi=[] equivale a 'tutti i gruppi'

    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: []
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # gruppi=[] -> tutti i mapping (incluso il predefinito) disabilitati
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: false })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Disabilita solo un gruppo di una fruizione: l'altro gruppo deve restare abilitato

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ setup.gruppo_authn_principal.nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # gruppo target disabilitato; l'altro nominato e il predefinito invariati
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true  })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # tutti e tre abilitati (target ri-abilitato, gli altri sempre rimasti tali)
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Gruppo inesistente su fruizione => 404 e nessuno stato modificato

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ 'gruppo-non-esistente-' + random() ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 404

    # tutti i mapping reali devono essere rimasti abilitati (404 = no-op)
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })


# -------------------------------------------------------------------
# Scenari fruizione gemelli degli erogazione gia' testati sopra
# -------------------------------------------------------------------


@ToggleStatoApiGruppi
Scenario: Disabilita un sotto-insieme (entrambi i gruppi nominati) su fruizione

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ setup.gruppo.nome, setup.gruppo_authn_principal.nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # i 2 nominati disabilitati, il PREDEFINITO resta abilitato (non era nell'array)
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Mix esistente + inesistente su fruizione => 204 applicato al solo esistente

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ setup.gruppo_authn_principal.nome, 'gruppo-non-esistente-' + random() ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # il gruppo esistente disabilitato, l'altro reale e il predefinito NO
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true  })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true  })

    # riabilita per cleanup solo il gruppo effettivamente toccato
    * def cleanupBody =
    """
    ({
        tipo: 'fruizione',
        abilitato: true,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ setup.gruppo_authn_principal.nome ]
    })
    """
    Given url statoApiUrl
    And request cleanupBody
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: gruppi=[] equivale a 'tutti i gruppi' su fruizione

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: []
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # gruppi=[] -> tutti i mapping (incluso il predefinito) disabilitati
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: false })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })


@ToggleStatoApi
Scenario: Fruizione inesistente => 404

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: true,
        api: {
            nome: 'api-non-esistente-' + random(),
            versione: 1,
            erogatore: setup.fruizione_petstore.erogatore
        }
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 404


@ToggleStatoApi
Scenario: Body invalido fruizione (manca erogatore) => 400

    # Specifico fruizione: il backend richiede esplicitamente il campo api.erogatore
    # (vedi ToggleStatoApiHelper#executeByFullSearch). Manca -> RICHIESTA_NON_VALIDA -> 400.
    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: true,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione
        }
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 400


@ToggleStatoApi
Scenario: Idempotenza su fruizione - disabilita due volte di seguito

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        }
    })
    """

    # prima disabilita
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: false })

    # seconda disabilita: deve restare 204 e lo stato non deve cambiare
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: false })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome, abilitato: true })


@ToggleStatoApiGruppi
Scenario: Disabilita solo il gruppo predefinito di una erogazione: gli altri devono restare abilitati

    # Verifica che il filtro 'gruppi' funzioni anche puntando al mapping con isDefault=true
    # (nome user-visible: Costanti.MAPPING_DESCRIZIONE_DEFAULT = 'Predefinito').
    * def body =
    """
    ({
        tipo: 'erogazione',
        abilitato: false,
        api: { nome: setup.api_petstore.nome, versione: setup.api_petstore.versione },
        gruppi: [ setup.gruppo_predefinito_erogazione_nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # solo il predefinito disabilitato, i 2 nominati restano abilitati
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true  })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # predefinito riabilitato, gli altri sempre rimasti tali
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_erogazione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoErogazioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })


@ToggleStatoApiGruppi
Scenario: Disabilita solo il gruppo predefinito di una fruizione: gli altri devono restare abilitati

    * def body =
    """
    ({
        tipo: 'fruizione',
        abilitato: false,
        api: {
            nome: setup.fruizione_petstore.api_nome,
            versione: setup.fruizione_petstore.api_versione,
            erogatore: setup.fruizione_petstore.erogatore
        },
        gruppi: [ setup.gruppo_predefinito_fruizione_nome ]
    })
    """

    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # solo il predefinito disabilitato, i 2 nominati restano abilitati
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: false })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true  })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true  })

    # riabilita per cleanup
    * set body.abilitato = true
    Given url statoApiUrl
    And request body
    And param soggetto = soggettoDefault
    When method POST
    Then status 204

    # predefinito riabilitato, gli altri sempre rimasti tali
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_predefinito_fruizione_nome, abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo.nome,                       abilitato: true })
    * call verifyStato ({ url: confStatoFruizioneUrl, soggetto: soggettoDefault, gruppo: setup.gruppo_authn_principal.nome,       abilitato: true })
