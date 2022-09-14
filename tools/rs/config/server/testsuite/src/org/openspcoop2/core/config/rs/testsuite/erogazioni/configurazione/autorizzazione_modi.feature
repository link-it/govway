Feature: Configurazione Autorizzazione Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def query_param_profilo_modi = {'profilo': 'ModI'}


@ModIAutorizzazioneApplicativiTokenInterniPuntuale
Scenario Outline: Controllo accessi autorizzazione applicativi token interni puntuale per le erogazioni (profilo ModI)
 
    * def api_modi = read('../<api>')
    * eval randomize(api_modi, ["nome"])
    * eval api_modi.referente = soggettoDefault

    * def erogazione_modi = read('../<erogazione>')
    * eval erogazione_modi.api_nome = api_modi.nome
    * eval erogazione_modi.api_versione = api_modi.versione
    * eval erogazione_modi.api_referente = api_modi.referente

    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
		
    * def petstore_key = erogazione_modi.erogazione_nome + '/' + erogazione_modi.api_versione
    * def api_petstore_path = 'api/' + api_modi.nome + '/' + api_modi.versione

    * call create ({ resourcePath: 'api', body: api_modi, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_modi,  key: petstore_key, query_params: query_param_profilo_modi } )

    * def petstore_key = erogazione_modi.api_nome + '/' + erogazione_modi.api_versione
    * def erogazione_petstore_path = 'erogazioni/' + petstore_key
    * def servizio_path = erogazione_petstore_path

    # Abilito/Disabilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/<token>')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_param_profilo_modi
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_param_profilo_modi
    When method put
    Then status 204
	
    * def query_param_applicativi = {'profilo': 'ModI'}

    * def applicativo = read('classpath:org/openspcoop2/core/config/rs/testsuite/applicativi/<nome>')
    * eval randomize(applicativo, ["nome", "<identificativo>"])

    * def applicativo_puntuale = ({ applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione (via trasporto)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall (non via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Recupero l'applicativo appena aggiunto per mezzo della findall (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Rimuovo l'applicativo dall'autorizzazione  (via trasporto)
    * def query_param_applicativi_remove_authz = {'profilo': 'ModI'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Aggiungo l'applicativo all'autorizzazione (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall (non via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Recupero l'applicativo appena aggiunto per mezzo della findall (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: '#string' } ])

    # Rimuovo l'applicativo dall'autorizzazione  (via token)
    * def query_param_applicativi_remove_authz = {'profilo': 'ModI'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: erogazione_petstore_path, query_params: query_param_profilo_modi })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi })

Examples:
|nome|identificativo|api|erogazione|token|
|applicativo_interno_token.json|modi.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|
|applicativo_interno_keystore_file.json|modi.sicurezza_messaggio.keystore.keystore_password|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|
|applicativo_interno_token_keystore_file.json|modi.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|
|applicativo_interno_token_keystore_file.json|modi.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|
|applicativo_interno_token_keystore_file.json|modi.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|



@ModIAutorizzazioneApplicativiTokenEsterniPuntuale
Scenario Outline: Controllo accessi autorizzazione applicativi token esterni puntuale per le erogazioni (profilo ModI)
 
    * def api_modi = read('../<api>')
    * eval randomize(api_modi, ["nome"])
    * eval api_modi.referente = soggettoDefault

    * def erogazione_modi = read('../<erogazione>')
    * eval erogazione_modi.api_nome = api_modi.nome
    * eval erogazione_modi.api_versione = api_modi.versione
    * eval erogazione_modi.api_referente = api_modi.referente

    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
		
    * def petstore_key = erogazione_modi.erogazione_nome + '/' + erogazione_modi.api_versione
    * def api_petstore_path = 'api/' + api_modi.nome + '/' + api_modi.versione

    * call create ({ resourcePath: 'api', body: api_modi, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_modi,  key: petstore_key, query_params: query_param_profilo_modi } )

    * def petstore_key = erogazione_modi.api_nome + '/' + erogazione_modi.api_versione
    * def erogazione_petstore_path = 'erogazioni/' + petstore_key
    * def servizio_path = erogazione_petstore_path

    # Abilito/Disabilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/<token>')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_param_profilo_modi
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_param_profilo_modi
    When method put
    Then status 204

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)', 'profilo': 'ModI'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno, query_params: query_param_profilo_modi })

    * def applicativo = read('classpath:bodies/<nome>')
    * eval randomize(applicativo, ["nome", "<identificativo>"])

    * def applicativo_puntuale = ({ soggetto: soggetto_esterno.nome , applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione (via trasporto)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall (non via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Recupero l'applicativo appena aggiunto per mezzo della findall (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Rimuovo l'applicativo dall'autorizzazione  (via trasporto)
    * def query_param_applicativi_remove_authz = {'soggetto_applicativo' : '#(soggetto_esterno.nome)', 'profilo': 'ModI'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Aggiungo l'applicativo all'autorizzazione (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 201

    # Recupero l'applicativo appena aggiunto per mezzo della findall (non via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Recupero l'applicativo appena aggiunto per mezzo della findall (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And params query_param_profilo_modi
    When method get
    Then status 200
    And match response.applicativi contains ([{ applicativo: applicativo_puntuale.applicativo, soggetto: soggetto_esterno.nome } ])

    # Rimuovo l'applicativo dall'autorizzazione  (via token)
    * def query_param_applicativi_remove_authz = {'soggetto_applicativo' : '#(soggetto_esterno.nome)', 'profilo': 'ModI'}
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi', applicativo_puntuale.applicativo
    And header Authorization = govwayConfAuth
    And params query_param_applicativi_remove_authz
    When method delete
    Then status 204

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome, query_params: query_param_profilo_modi})

    * call delete ({ resourcePath: erogazione_petstore_path, query_params: query_param_profilo_modi })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi })

Examples:
|nome|identificativo|api|erogazione|token|
|applicativo_token.json|credenziali.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|
|applicativo_https.json|credenziali.certificato.subject|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|
|applicativo_esterno_https_token.json|credenziali.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|
|applicativo_esterno_https_token.json|credenziali.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|
|applicativo_esterno_https_token.json|credenziali.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|




@ModIAutorizzazioneApplicativiTokenInterniPuntuale_400
Scenario Outline: Controllo accessi autorizzazione applicativi token interni puntuale per le erogazioni (profilo ModI) con configurazioni errate
 
    * def api_modi = read('../<api>')
    * eval randomize(api_modi, ["nome"])
    * eval api_modi.referente = soggettoDefault

    * def erogazione_modi = read('../<erogazione>')
    * eval erogazione_modi.api_nome = api_modi.nome
    * eval erogazione_modi.api_versione = api_modi.versione
    * eval erogazione_modi.api_referente = api_modi.referente

    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
		
    * def petstore_key = erogazione_modi.erogazione_nome + '/' + erogazione_modi.api_versione
    * def api_petstore_path = 'api/' + api_modi.nome + '/' + api_modi.versione

    * call create ({ resourcePath: 'api', body: api_modi, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_modi,  key: petstore_key, query_params: query_param_profilo_modi } )

    * def petstore_key = erogazione_modi.api_nome + '/' + erogazione_modi.api_versione
    * def erogazione_petstore_path = 'erogazioni/' + petstore_key
    * def servizio_path = erogazione_petstore_path

    # Abilito/Disabilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/<token>')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_param_profilo_modi
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_param_profilo_modi
    When method put
    Then status 204
		
    * def query_param_applicativi = {'profilo': 'ModI'}

    * def applicativo = read('classpath:org/openspcoop2/core/config/rs/testsuite/applicativi/<nome>')
    * eval randomize(applicativo, ["nome", "<identificativo>"])

    * def applicativo_puntuale = ({ applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione (via trasporto)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 400
    And match response.detail == '<error>'

    # Aggiungo l'applicativo all'autorizzazione (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 400
    And match response.detail == '<error>'

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: erogazione_petstore_path, query_params: query_param_profilo_modi })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi })

Examples:
|nome|identificativo|api|erogazione|token|error|motivo|
|applicativo_interno_token.json|modi.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|
|applicativo_interno_token.json|modi.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|L\'applicativo, di dominio interno, indicato non possiede una configurazione per la sicurezza messaggio richiesta per essere autorizzati ad invocare l\'erogazione con profilo ModI indicata|la sicurezza messaggio richiede che l'applicativo possieda un certificato|
|applicativo_interno_token.json|modi.token.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|L\'applicativo, di dominio interno, indicato non possiede una configurazione per la sicurezza messaggio richiesta per essere autorizzati ad invocare l\'erogazione con profilo ModI indicata|la sicurezza messaggio richiede che l'applicativo possieda un certificato; caso con anche gestione token disabilitata|
|applicativo_interno_keystore_file.json|modi.sicurezza_messaggio.keystore.keystore_password|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|L\'applicativo, di dominio interno, indicato non possiede una configurazione per la sicurezza token richiesta per essere autorizzati ad invocare l\'erogazione con profilo ModI indicata|se c'e' anche la gestione token abilitata, oltre alla sicurezzaMessaggio, l'applicativo deve possedere anche informazioni sul token|
|applicativo_interno_keystore_file.json|modi.sicurezza_messaggio.keystore.keystore_password|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|L\'applicativo, di dominio interno, indicato non possiede una configurazione per la sicurezza token richiesta per essere autorizzati ad invocare l\'erogazione con profilo ModI indicata|la gestione token richiede che l'applicativo definisca un token|
|applicativo_interno_keystore_file.json|modi.sicurezza_messaggio.keystore.keystore_password|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|
|applicativo_interno_token_keystore_file.json|modi.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|




@ModIAutorizzazioneApplicativiTokenEsterniPuntuale_400
Scenario Outline: Controllo accessi autorizzazione applicativi token esterni puntuale per le erogazioni (profilo ModI) con configurazioni errate
 
    * def api_modi = read('../<api>')
    * eval randomize(api_modi, ["nome"])
    * eval api_modi.referente = soggettoDefault

    * def erogazione_modi = read('../<erogazione>')
    * eval erogazione_modi.api_nome = api_modi.nome
    * eval erogazione_modi.api_versione = api_modi.versione
    * eval erogazione_modi.api_referente = api_modi.referente

    * def autorizzazione_disabilitata = ({ autorizzazione: { tipo: 'disabilitato' } })
    * def autorizzazione = read('classpath:bodies/controllo-accessi-autorizzazione-puntuale.json')
		
    * def petstore_key = erogazione_modi.erogazione_nome + '/' + erogazione_modi.api_versione
    * def api_petstore_path = 'api/' + api_modi.nome + '/' + api_modi.versione

    * call create ({ resourcePath: 'api', body: api_modi, query_params: query_param_profilo_modi })
    * call create ( { resourcePath: 'erogazioni', body: erogazione_modi,  key: petstore_key, query_params: query_param_profilo_modi } )

    * def petstore_key = erogazione_modi.api_nome + '/' + erogazione_modi.api_versione
    * def erogazione_petstore_path = 'erogazioni/' + petstore_key
    * def servizio_path = erogazione_petstore_path

    # Abilito/Disabilito la gestione dei tokens
    * def gestione_token_body = read('classpath:bodies/<token>')

    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'gestione-token'
    And header Authorization = govwayConfAuth
    And request gestione_token_body
    And params query_param_profilo_modi
    When method put
    Then status 204

    # Abilito l'autorizzazione per applicativi token
    * def options = { richiedente: false, ruoli: false, token_richiedente: true, scope: false, token: false }
    * eval autorizzazione.autorizzazione.richiedente = false
    * eval autorizzazione.autorizzazione.ruoli = false
    * eval autorizzazione.autorizzazione.token_richiedente = true
    * eval autorizzazione.autorizzazione.scope = false
    * eval autorizzazione.autorizzazione.token = false

    # Imposto l'autorizzazione in modo che supporti l'autorizzazione per applicativi token
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione'
    And header Authorization = govwayConfAuth
    And request autorizzazione
    And params query_param_profilo_modi
    When method put
    Then status 204

    * def soggetto_esterno = read('soggetto_esterno.json')
    * eval randomize (soggetto_esterno, ["nome", "credenziali.certificato.subject"])
    * soggetto_esterno.credenziali.certificato.subject = "cn=" + soggetto_esterno.credenziali.certificato.subject 
		
    * def query_param_applicativi = {'soggetto' : '#(soggetto_esterno.nome)', 'profilo': 'ModI'}

    * call create ({ resourcePath: 'soggetti', body: soggetto_esterno, query_params: query_param_profilo_modi })

    * def applicativo = read('classpath:bodies/<nome>')
    * eval randomize(applicativo, ["nome", "<identificativo>"])

    * def applicativo_puntuale = ({ soggetto: soggetto_esterno.nome , applicativo: applicativo.nome })

    # Creo l'applicativo nel registro
    Given url configUrl
    And path 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo
    And params query_param_applicativi
    When method post
    Then status 201

    # Aggiungo l'applicativo all'autorizzazione (via trasporto)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 400
    And match response.detail == '<error>'

    # Aggiungo l'applicativo all'autorizzazione (via token)
    Given url configUrl
    And path servizio_path, 'configurazioni', 'controllo-accessi', 'autorizzazione', 'token', 'applicativi'
    And header Authorization = govwayConfAuth
    And request applicativo_puntuale
    And params query_param_profilo_modi
    When method post
    Then status 400
    And match response.detail == '<error>'

    # Rimuovo l'applicativo dal registro
    Given url configUrl
    And path 'applicativi', applicativo.nome
    And header Authorization = govwayConfAuth
    And params query_param_applicativi
    When method delete
    Then status 204

    * call delete ({ resourcePath: 'soggetti/' + soggetto_esterno.nome, query_params: query_param_profilo_modi})

    * call delete ({ resourcePath: erogazione_petstore_path, query_params: query_param_profilo_modi })
    * call delete ({ resourcePath: api_petstore_path, query_params: query_param_profilo_modi })

Examples:
|nome|identificativo|api|erogazione|token|error|motivo|
|applicativo_token.json|credenziali.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|
|applicativo_token.json|credenziali.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|Il tipo di credenziali dell\'Applicativo non sono compatibili con l\'autenticazione impostata nell\'erogazione selezionata|la sicurezza messaggio richiede che l'applicativo possieda un certificato|
|applicativo_token.json|credenziali.identificativo|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore-disabilitato.json|Il tipo di credenziali dell\'Applicativo non sono compatibili con l\'autenticazione impostata nell\'erogazione selezionata|la sicurezza messaggio richiede che l'applicativo possieda un certificato; caso con anche gestione token disabilitata|
|applicativo_https.json|credenziali.certificato.subject|api_modi_rest.json|erogazione_modi_rest.json|gestione-token-petstore.json|Il tipo di credenziali dell\'Applicativo non sono compatibili con l\'autenticazione impostata nell\'erogazione selezionata|se c'e' anche la gestione token abilitata, oltre alla sicurezzaMessaggio, l'applicativo deve possedere anche informazioni sul token|
|applicativo_https.json|credenziali.certificato.subject|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore.json|Il tipo di credenziali dell\'Applicativo non sono compatibili con l\'autenticazione impostata nell\'erogazione selezionata|la gestione token richiede che l'applicativo definisca un token|
|applicativo_https.json|credenziali.certificato.subject|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|
|applicativo_esterno_https_token.json|credenziali.token.identificativo|api_modi_rest_no_sicurezza.json|erogazione_petstore.json|gestione-token-petstore-disabilitato.json|Non è possibile registrare alcun applicativo: non è stata riscontrato alcun criterio di sicurezza (messaggio o token)|nessun criterio di sicurezza impostato|

