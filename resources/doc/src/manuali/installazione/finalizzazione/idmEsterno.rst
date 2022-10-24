.. _idmEsterno:

Integrazione delle Console con IdM esterno
------------------------------------------

Dopo aver completato l'installazione e il dispiegamento del software, come mostrato nella sezione :ref:`inst_verifica`, le console di gestione e monitoraggio sono accessibili utilizzando le credenziali fornite durante l'esecuzione dell'installer. 

La gestione delle utenze applicative, descritta nella sezione :ref:`utenti` della Guida alla Console di Gestione, può essere effettuata tramite l'utenza 'amministratore' associata alla 'govwayConsole', così come definita durante l'esecuzione dell'installer. Tale utenza permette di creare nuove utenze e gestire i permessi e le password associate alle utenze esistenti.

Per default, l'autenticazione da parte delle console di gestione e monitoraggio avviene localmente utilizzando le password assegnate in tal modo alle utenze tramite la 'govwayConsole'.

In alternativa, è possibile delegare l'autenticazione ad un IdM esterno, intervenendo sulle configurazioni delle Console come documentato di seguito.

.. note::

  Abilitando l'autenticazione tramite IdM esterno, la gestione delle utenze applicative avviene sempore tramite la 'govwayConsole', associando ad ogni utenza i 'principal' ottenuti dall'IdM. Non saranno invece più gestite le password associate alle utenze.

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

