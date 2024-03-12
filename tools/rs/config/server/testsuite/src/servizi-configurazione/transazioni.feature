Feature: Configurazione Registrazione Transazioni

Background:
    * def transazioniDefault = read('classpath:bodies/registrazione-transazioni-default.json')
    * def transazioni = read('classpath:bodies/registrazione-transazioni.json')

@Update204
Scenario: Update Registrazione Transazioni

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDefault

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioni
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioni

    # ripristino default

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDefault
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDefault




    # -------- Test su database -------

    * def transazioniDB = read('classpath:bodies/registrazione-transazioni.json')
    * remove transazioniDB.filetrace
    * remove transazioniDB.informazioni

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDB
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDB

    # cambio fasi

    * eval transazioniDB.database.fasi.richiesta_ingresso = 'non-bloccante'
    * eval transazioniDB.database.fasi.richiesta_uscita = 'disabilitato'
    * eval transazioniDB.database.fasi.risposta_uscita = 'non-bloccante'
    * eval transazioniDB.database.fasi.risposta_consegnata = 'disabilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDB
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDB

    # cambio fasi test 2

    * eval transazioniDB.database.fasi.richiesta_ingresso = 'disabilitato'
    * eval transazioniDB.database.fasi.richiesta_uscita = 'non-bloccante'
    * eval transazioniDB.database.fasi.risposta_uscita = 'disabilitato'
    * eval transazioniDB.database.fasi.risposta_consegnata = 'abilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDB
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDB

    # abilitato (senza filtri)

    * eval transazioniDB.database.stato = 'abilitato'
    * remove transazioniDB.database.fasi
    * eval transazioniDB.database.filtro_esiti = 'disabilitato'
    * remove transazioniDB.filtro

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDB
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDB

    # disabilitato

    * eval transazioniDB.database.stato = 'disabilitato'
    * remove transazioniDB.database.filtro_esiti

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDB
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDB

    # default

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDefault
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDefault




    # -------- Test su filetrace -------

    * def transazioniFileTrace = read('classpath:bodies/registrazione-transazioni.json')
    * remove transazioniFileTrace.database
    * remove transazioniFileTrace.informazioni

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # cambio fasi

    * eval transazioniFileTrace.filetrace.fasi.richiesta_ingresso = 'non-bloccante'
    * eval transazioniFileTrace.filetrace.fasi.richiesta_uscita = 'disabilitato'
    * eval transazioniFileTrace.filetrace.fasi.risposta_uscita = 'non-bloccante'
    * eval transazioniFileTrace.filetrace.fasi.risposta_consegnata = 'disabilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # cambio fasi test 2

    * eval transazioniFileTrace.filetrace.fasi.richiesta_ingresso = 'disabilitato'
    * eval transazioniFileTrace.filetrace.fasi.richiesta_uscita = 'non-bloccante'
    * eval transazioniFileTrace.filetrace.fasi.risposta_uscita = 'disabilitato'
    * eval transazioniFileTrace.filetrace.fasi.risposta_consegnata = 'abilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # abilitato (senza filtri)

    * eval transazioniFileTrace.filetrace.stato = 'abilitato'
    * remove transazioniFileTrace.filetrace.fasi
    * eval transazioniFileTrace.filetrace.filtro_esiti = 'disabilitato'
    * remove transazioniFileTrace.filtro

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # disabilitato

    * eval transazioniFileTrace.filetrace.stato = 'disabilitato'
    * remove transazioniFileTrace.filetrace.filtro_esiti
    * remove transazioniFileTrace.filetrace.config_path
    * remove transazioniFileTrace.filetrace.dump_client
    * remove transazioniFileTrace.filetrace.dump_server

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # configurazioneEsterna

    * eval transazioniFileTrace.filetrace.stato = 'configurazioneEsterna'
    * eval transazioniFileTrace.filetrace.filtro_esiti = 'disabilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTrace
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTrace

    # configurazioni custom

    * def transazioniFileTraceConfCustom = read('classpath:bodies/registrazione-transazioni.json')
    * remove transazioniFileTraceConfCustom.database
    * remove transazioniFileTraceConfCustom.informazioni
    * remove transazioniFileTraceConfCustom.filetrace.config_path
    * eval transazioniFileTraceConfCustom.filetrace.dump_client.headers = false
    * eval transazioniFileTraceConfCustom.filetrace.dump_client.payload = true
    * eval transazioniFileTraceConfCustom.filetrace.dump_server.headers = false
    * eval transazioniFileTraceConfCustom.filetrace.dump_server.payload = true

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTraceConfCustom
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTraceConfCustom

    # configurazioni custom (test 2)

    * eval transazioniFileTraceConfCustom.filetrace.dump_client.headers = true
    * eval transazioniFileTraceConfCustom.filetrace.dump_client.payload = false
    * eval transazioniFileTraceConfCustom.filetrace.dump_server.headers = true
    * eval transazioniFileTraceConfCustom.filetrace.dump_server.payload = false

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniFileTraceConfCustom
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniFileTraceConfCustom


    # esiti

    * def transazioniEsiti = read('classpath:bodies/registrazione-transazioni.json')
    * remove transazioniEsiti.informazioni
    * eval transazioniEsiti.filtro.esiti = [3,22]

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniEsiti
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response.filtro contains { esiti: [3, 22] }


    # informazioni transazioni

    * def transazioniInfo = read('classpath:bodies/registrazione-transazioni.json')
    * eval transazioniInfo.informazioni.token = 'disabilitato'
    * eval transazioniInfo.informazioni.tempi_elaborazione = 'abilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniInfo
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniInfo

    # informazioni transazioni (test2)

    * eval transazioniInfo.informazioni.token = 'abilitato'
    * eval transazioniInfo.informazioni.tempi_elaborazione = 'disabilitato'

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniInfo
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniInfo


    # informazioni transazioni (test3)

    * remove transazioniInfo.informazioni

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniInfo
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniInfo


    # default

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And request transazioniDefault
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path servizio_path, 'configurazioni', 'tracciamento', 'transazioni'
    And header Authorization = govwayConfAuth
    And params query_params
    When method get
    Then status 200
    And match response contains transazioniDefault

 
