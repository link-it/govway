@ignore
Feature: Preparazione Test

Scenario: Preparazione Test
    #* call read('classpath:crud_commons.feature')

    * def api_petstore = read('classpath:bodies/api-petstore.json')
    * eval randomize(api_petstore, ["nome"])
    * def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione
    * eval api_petstore.tags = ['TESTSUITE']

    * call create ({ resourcePath: 'api', body: api_petstore })

    * def erogazione_petstore = read('classpath:bodies/erogazione-petstore.json')
    * eval erogazione_petstore.api_nome = api_petstore.nome
    * eval erogazione_petstore.api_versione = api_petstore.versione
    * def erogazione_petstore_path = 'erogazioni/' + erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
    * call create ({ resourcePath: 'erogazioni', body: erogazione_petstore })

    * def erogatore = read('classpath:bodies/soggetto-erogatore.json')
    * eval randomize (erogatore, ["nome", "credenziali.username"])
    * def fruizione_petstore = read('classpath:bodies/fruizione-petstore.json')
    * set fruizione_petstore.api_nome = api_petstore.nome
    * set fruizione_petstore.api_versione = api_petstore.versione
    * set fruizione_petstore.erogatore = erogatore.nome
    * def fruizione_petstore_path = 'fruizioni/' +  fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore })

    
    * def gruppo = read('classpath:bodies/gruppo-put-http.json');
    * eval randomize(gruppo, ["nome"])
    * call create ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo })
    * call create ( { resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo })
    
    * def gruppo_authn_principal = read('classpath:bodies/gruppo-post-principal.json')
    * eval randomize(gruppo_authn_principal, ["nome"])
    * call create ( { resourcePath: erogazione_petstore_path + '/gruppi', body: gruppo_authn_principal })
    * call create ( { resourcePath: fruizione_petstore_path + '/gruppi', body: gruppo_authn_principal })

    # Configura la correlazione applicativa header based per il gruppo gruppo_authn_principal
    
    * def correlazione_richiesta = read('classpath:bodies/correlazione-applicativa-richiesta.json')
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta'
    And header Authorization = govwayConfAuth
    And request correlazione_richiesta
    And params ({ gruppo: gruppo_authn_principal.nome })
    When method post
    Then status 201

    Given url configUrl
    And path fruizione_petstore_path, 'configurazioni', 'tracciamento', 'correlazione-applicativa', 'richiesta'
    And header Authorization = govwayConfAuth
    And request correlazione_richiesta
    And params ({ gruppo: gruppo_authn_principal.nome })
    When method post
    Then status 201

    # Crea soggetti e applicativi da utilizzare poi nelle ricerche

    * def applicativo = read('classpath:bodies/applicativo-http.json')
    * eval randomize(applicativo, ["nome", "credenziali.username"])
    * call create { resourcePath: 'applicativi', body: '#(applicativo)' }

    * def applicativo_principal = read('classpath:bodies/applicativo-principal.json')
    * eval randomize(applicativo_principal, ["nome", "credenziali.userid"])
    * call create { resourcePath: 'applicativi', body: '#(applicativo_principal)' }

    * def applicativo_token = read('classpath:bodies/applicativo-token.json')
    * eval randomize(applicativo_token, ["nome", "credenziali.identificativo"])
    # lo uso solo come informazione statistica * call create { resourcePath: 'applicativi', body: '#(applicativo_token)' }

    * def soggetto_http = read('classpath:bodies/soggetto-esterno-http.json')
    * eval randomize(soggetto_http, ["nome", "credenziali.username"])
    * eval soggetto_http.ruoli = []
    * call create { resourcePath: 'soggetti', body: '#(soggetto_http)' }

    * def soggetto_certificato = read('classpath:bodies/soggetto-esterno-certificato.json')
    * eval soggetto_certificato.nome = "SoggettoTestMonitoraggioCertificato"

    Given url configUrl
    And path 'soggetti', soggetto_certificato.nome
    And header Authorization = govwayConfAuth
    When method delete
    Then assert responseStatus == 204 || responseStatus == 404
    * call create { resourcePath: 'soggetti', body: '#(soggetto_certificato)' }

    # Configuro il gruppo predefinito con un autenticazione https

    * def configurazione_https = read('classpath:bodies/controllo-accessi-autenticazione-https.json')
    Given url configUrl
    And path erogazione_petstore_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request configurazione_https
    And params query_params
    When method put
    Then status 204

    Given url configUrl
    And path fruizione_petstore_path, 'configurazioni', 'controllo-accessi', 'autenticazione'
    And header Authorization = govwayConfAuth
    And request configurazione_https
    And params query_params
    When method put
    Then status 204

    # ADESSO EFFETTUO LE RICHIESTE SUL PETSTORE

    # Ottengo la url invocazione
    * def dataInizio = getDate()
    * def dataInizioMinuteZero = getDateMinuteZero();

    Given url configUrl
    And path erogazione_petstore_path, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And headers {'X-Forwarded-For': '127.0.0.2' }
    When method get
    Then status 200
    * def url_invocazione = response.url_invocazione
    Given url configUrl
    And path fruizione_petstore_path, 'url-invocazione'
    And header Authorization = govwayConfAuth
    And headers {'X-Forwarded-For': '127.0.0.2' }
    When method get
    Then status 200
    * def fruizione_url_invocazione = response.url_invocazione
    
    * def applicativo_credentials = call basic ({ username: applicativo.credenziali.username, password: applicativo.credenziali.password})
    * def soggetto_credentials = call basic ({ username: soggetto_http.credenziali.username, password: soggetto_http.credenziali.password })
    * def pet_update = read('classpath:bodies/pet-update.json')
    * def pet_post = read('classpath:bodies/pet-post.json')

    # Richiesta con Applicativo Autenticato
    Given url url_invocazione
    And path 'pet'
    And header Authorization = applicativo_credentials
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And request pet_update
    When method put
    Then status 200
    Given url fruizione_url_invocazione
    And path 'pet'
    And header Authorization = applicativo_credentials
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And request pet_update
    When method put
    Then status 200

    # Richiesta con Soggetto Autenticato ( Solo Erogazioni )
    Given url url_invocazione
    And path 'pet'
    And header Authorization = soggetto_credentials
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And request pet_update
    When method put
    Then status 200

    # Richiesta con Autenticazione Principal Applicativo
    Given url url_invocazione
    And path 'pet'
    And header HeaderTestPrincipal = applicativo_principal.credenziali.userid
    And header HeaderTestCorrelazione = "prova"
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And request pet_post
    When method post
    Then status 200
    Given url fruizione_url_invocazione
    And path 'pet'
    And header HeaderTestPrincipal = applicativo_principal.credenziali.userid
    And header HeaderTestCorrelazione = "prova"
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And request pet_post
    When method post
    Then status 200

    # Questa richiesta fallirà perchè non è autenticata
    Given url url_invocazione
    And path 'pet'
    And request pet_update
    When method put
    Then status 401
    Given url fruizione_url_invocazione
    And path 'pet'
    And request pet_update
    When method put
    Then status 401

    # Prepara il database con le informazioni relative ad un token.
    * def claims = 
    """
    {
        username: 'username',
        issuer:   'issuer',
        client_id: 'client_id',
        subject:    'subject',
        email:      'email@test',
    }
    """
    * eval randomize(claims, ["username","issuer", "client_id", "subject", "email"]);
    * eval claims.email = claims.email + ".it"

    * def filtro_claims = 
    """
    ([
        { nome: "username", valore: claims.username },
        { nome: "issuer", valore: claims.issuer },
        { nome: "client_id", valore: claims.client_id },
        { nome: "subject", valore: claims.subject },
        { nome: "email", valore: claims.email },
    ])
    """

    * def id_credenziale =
    """
    {
        username: 0,
        issuer: 0,
        client_id_raw: 0,
	client_id_application: 0,
	client_id_only: 0,
        subject: 0,
        email: 0,
    }
    """
    
    * def DbUtils = Java.type('org.openspcoop2.core.monitor.rs.testsuite.DbUtils')
    * def db = new DbUtils(govwayDbConfig)

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_username', '"+claims.username+"')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_username' AND credenziale ='"+claims.username+"'");
    * eval id_credenziale.username = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_issuer', '"+claims.issuer+"')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_issuer' AND credenziale ='"+claims.issuer+"'");
    * eval id_credenziale.issuer = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_clientId', '"+claims.client_id+"')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_clientId' AND credenziale ='"+claims.client_id+"'");
    * eval id_credenziale.client_id_raw = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_clientId', '#C#"+claims.client_id+"#C# #A#gw/"+soggettoDefault+"/"+applicativo_token.nome+"#A#')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_clientId' AND credenziale ='#C#"+claims.client_id+"#C# #A#gw/"+soggettoDefault+"/"+applicativo_token.nome+"#A#'");
    * eval id_credenziale.client_id_application = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_clientId', '#C#"+claims.client_id+"#C#')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_clientId' AND credenziale ='#C#"+claims.client_id+"#C#'");
    * eval id_credenziale.client_id_only = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_subject', '"+claims.subject+"')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_subject' AND credenziale ='"+claims.subject+"'");
    * eval id_credenziale.subject = result[0].id;

    * eval db.update("INSERT INTO credenziale_mittente(tipo,credenziale) VALUES ('token_eMail', '"+claims.email+"')");
    * def result = db.readRows("SELECT * FROM credenziale_mittente WHERE tipo='token_eMail' AND credenziale ='"+claims.email+"'");
    * eval id_credenziale.email = result[0].id;

    # Esegue un'invocazione per recuperare l'id transazione dell'invocazione,
    # da associare poi alle credenziali mittente del soggetto creato in precedenza
    #
    # Richiesta https
    * configure ssl = { keyStore: 'classpath:client.p12', keyStorePassword:'openspcoop', keyStoreType: 'pkcs12'}
    
    * def fruizione_url_invocazione_https =  ( fruizione_url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url fruizione_url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_raw+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);
    * call pause(1000)

    * def fruizione_url_invocazione_https =  ( fruizione_url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url fruizione_url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_application+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);
    * call pause(1000)

    * def fruizione_url_invocazione_https =  ( fruizione_url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url fruizione_url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_only+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);
    * call pause(1000)


    * def url_invocazione_https = ( url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)  
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_raw+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);

    * def url_invocazione_https = ( url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)  
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_application+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);

    * def url_invocazione_https = ( url_invocazione.replace('http','https').replace('8080', '8444'))
    Given url url_invocazione_https
    And headers {'X-Forwarded-For': '127.0.0.2' }
    And path 'pet', pet_update.id
    When method get
    Then status 200
    * call pause(1000)  
    * def id_transazione = responseHeaders['GovWay-Transaction-ID'][0];
    * def dbquery = "UPDATE transazioni set token_username = '"+id_credenziale.username+"', token_issuer='"+id_credenziale.issuer+"', token_client_id='"+id_credenziale.client_id_only+"', token_subject='"+id_credenziale.subject+"', token_mail='"+id_credenziale.email+"' WHERE id='"+id_transazione+"'";
    * eval db.update(dbquery);


    * def dataFine = getDate()

    * def delete_lock =
    """
    function(db) {
        db.update("UPDATE op2_semaphore set node_id=NULL, creation_time=NULL, details=NULL, update_time=NULL WHERE node_id='TESTSUITE' AND applicative_id='GenerazioneStatisticheOrarie'");
    }
    """

     * def wait_for_lock =
    """
    function(db){ 
        do {
            karate.log("CHECKO LA LOCK FROM op2_semaphore")
            var result = db.readRows("SELECT * from op2_semaphore WHERE applicative_id='GenerazioneStatisticheOrarie'");
            karate.log(result);
            try {
                java.lang.Thread.sleep(100) 
            } catch (e) {

            }
        } while(result[0].node_id != null)
        db.update("UPDATE op2_semaphore set node_id='TESTSUITE', creation_time=CURRENT_TIMESTAMP, details='Lock testsuite monitoraggio' WHERE applicative_id='GenerazioneStatisticheOrarie'");
        
    }
    """
