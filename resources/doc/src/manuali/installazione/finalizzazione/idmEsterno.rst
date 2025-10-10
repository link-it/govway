.. _idmEsterno:

Integrazione delle Console con IdM esterno
------------------------------------------

Dopo aver completato l'installazione e il dispiegamento del software, come mostrato nella sezione :ref:`inst_verifica`, le console di gestione e monitoraggio sono accessibili utilizzando le credenziali fornite durante l'esecuzione dell'installer.

La gestione delle utenze applicative, descritta nella sezione :ref:`utenti` della Guida alla Console di Gestione, può essere effettuata tramite l'utenza 'amministratore' associata alla 'govwayConsole', così come definita durante l'esecuzione dell'installer. Tale utenza permette di creare nuove utenze e gestire i permessi e le password associate alle utenze esistenti.

Per default, l'autenticazione da parte delle console di gestione e monitoraggio avviene localmente utilizzando le password assegnate in tal modo alle utenze tramite la 'govwayConsole'.

In alternativa, è possibile delegare l'autenticazione ad un IdM esterno, intervenendo sulle configurazioni delle Console come documentato di seguito.

.. note::

  Abilitando l'autenticazione tramite IdM esterno, la gestione delle utenze applicative avviene sempre tramite la 'govwayConsole', associando ad ogni utenza i 'principal' ottenuti dall'IdM. Non saranno invece più gestite le password associate alle utenze.

Per attivare l'autenticazione delle utenze tramite un IdM esterno è necessario intervenire sui seguenti file di configurazione:

- per la 'govwayConsole': *<directory-lavoro>/console_local.properties*;
- per la 'govwayMonitor': *<directory-lavoro>/monitor_local.properties*

attuando le seguenti modifiche:

1. Disabilitare l'autenticazione locale delle utenze:

   ::

      login.application=false
      logout.mostraButton.enabled=false

2. Definire la modalità di accesso all'identità dell'utenza autenticata tramite l'IdM esterno, utilizzando una delle seguenti modalità supportate:

   - Utilizzando il tipo 'header' l'identità viene acquisita tramite un header http il cui nome è definito nella proprietà 'login.props.header', come indicato di seguito:

     ::

        login.tipo=header
        login.props.header=<HTTP-HEADER-NAME>

   - Utilizzando il tipo 'principal' l'identità viene acquisita tramite la api 'javax.servlet.http.HttpServletRequest.getUserPrincipal()'.

     ::

        login.tipo=principal

     La modalità 'principal' richiede che l'autenticazione degli utenti sia stata preliminarmente configurata a livello del container dell'application server che ospita le console.

   - Utilizzando il tipo 'oauth2' l'identità viene acquisita tramite tramite il token di accesso (access token) rilasciato dal provider OAuth2, che contiene o riferisce le informazioni sull'utente.

     .. note::
        L'autenticazione con modalità OAuth2 può essere utilizzata contemporaneamente alla modalità di autenticazione interna. In questo caso, gli utenti possono scegliere di autenticarsi tramite OAuth2 o utilizzando le credenziali locali gestite dalla console.

     ::

        login.tipo=oauth2
        # Url dell'authorization server
        login.props.oauth2.authorization.endpoint=
        # Url dove richiedere il token
        login.props.oauth2.token.endpoint=
        # Url dove scaricare le indormazioni utente
        login.props.oauth2.userInfo.endpoint=
        # URL di validazione dei certificati JWT
        login.props.oauth2.jwks.endpoint=
        # URL del servizio logout federato
        login.props.oauth2.logout.endpoint=
        # Client ID rilasciato dal server OAuth2
        login.props.oauth2.clientId=
        # URL dove viene rediretto l'utente dopo l'autenticazione
        login.props.oauth2.redirectUri=
        # Scope dell'autenticazione
        login.props.oauth2.scope=
        # Nome del claim da dove leggere il principal
        login.props.oauth2.principalClaim=

        # Parametri timeout connessione verso il server OAuth2
        #login.props.oauth2.readTimeout=15000
        #login.props.oauth2.connectTimeout=10000

        # Validazione dei claim del token
        # Inserire una riga per ogni claim da validare nella forma: login.props.oauth2.claims.validation.claimName=claimValues (lista di valori separati da virgola)
        #login.props.oauth2.claims.validation.claimName=claimValue1,claimValue2,...

        # Truststore https
        #login.props.oauth2.https.hostnameVerifier=true
        #login.props.oauth2.https.trustAllCerts=false
        #login.props.oauth2.https.trustStore=PATH
        #login.props.oauth2.https.trustStore.password=changeme
        #login.props.oauth2.https.trustStore.type=jks
        #login.props.oauth2.https.trustStore.crl=PATH

        # Keystore https
        #login.props.oauth2.https.keyStore=PATH
        #login.props.oauth2.https.keyStore.password=changeme
        #login.props.oauth2.https.keyStore.type=jks
        #login.props.oauth2.https.key.alias=mykey
        #login.props.oauth2.https.key.password=changeme

   - È infine possibile configurare una modalità custom indicando una classe che implementi l'interfaccia 'org.openspcoop2.utils.credential.IPrincipalReader'.
     Eventuali proprietà di configurazione da fornire alla classe possono essere indicate nella forma 'login.props.<NOME_PROP>=<VALORE_PROP>'.

     ::

        login.tipo=org.example.packageCustom.CustomPrincipalReader
        login.props.nomeProp1=val1
        ...
        login.props.nomePropN=valN

3. Nel caso le console siano integrate all'interno di altre applicazioni o portali, è possibile ridefinire le url alle quali vada rediretto l'utente nei casi di autorizzazione di accesso negata.
   Lasciando le proprietà non valorizzate verranno utilizzati le pagine di default previste dall'applicazione.

   ::

      # Errore interno durante il login
      login.erroreInterno.redirectUrl=
      # Autorizzazione negata
      login.utenteNonAutorizzato.redirectUrl=
      # Utenza non valida
      login.utenteNonValido.redirectUrl=
      # Sessione scaduta
      login.sessioneScaduta.redirectUrl=
      # Pagina successiva all'operazione di logout
      logout.urlDestinazione=
