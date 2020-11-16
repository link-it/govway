.. _tokenPolicy_introspection:

Token Introspection
~~~~~~~~~~~~~~~~~~~

Questa sezione consente di attivare la validazione del token ricevuto
attraverso un servizio di Token Introspection i cui dati di accesso
devono essere forniti in questo contesto (:numref:`tokenIntrospection`).

   .. figure:: ../../_figure_console/TokenIntrospection.png
    :scale: 100%
    :align: center
    :name: tokenIntrospection

    Dati di puntamento al servizio di Token Instrospection

Per il corretto puntamento
al servizio di Token Introspection devono essere forniti in prima
istanza i parametri generali legati all'endpoint riferito:

-  *Connection Timeout*: Tempo massimo in millisecondi di attesa per
   stabilire una connessione con il server di validazione token.

-  *Read Timeout*: Tempo massimo in millisecondi di attesa per la
   ricezione di una risposta dal server di validazione token.

-  *Https*: Parametri di configurazione nel caso in cui il server di
   validazione token richieda un accesso Https.

-  *Proxy*: Parametri di configurazione nel caso in cui il server di
   validazione token richieda l'uso di un proxy per l'accesso.

Successivamente devono essere forniti i dati di configurazione specifici
del servizio di Token Introspection:

-  *Tipo*: tipologia del servizio. A scelta tra i seguenti valori:

   -  *RFC 7662 - Introspection*: Servizio di introspection conforme
      allo standard RFC 7662. Richiede che vengano forniti i seguenti
      dati:

      -  *URL*: endpoint del servizio di introspection.

      -  *Autenticazione Http Basic*: flag da attivare nel caso in cui
         il servizio di introspection richieda autenticazione di tipo
         HTTP-BASIC. In questo caso dovranno essere forniti Username e
         Password nei campi successivi.

      -  *Autenticazione Bearer*: flag da attivare nel caso in cui il
         servizio di introspection richieda autenticazione tramite un
         token. Quest'ultimo dovrà essere indicato nel campo seguente.

      -  *Autenticazione Https*: flag da attivare nel caso in cui il
         servizio di introspection richieda autenticazione di tipo
         Https. In questo caso dovranno essere forniti tutti i dati di
         configurazione nei campi seguenti.

   -  *Google - TokenInfo*: Riferimento al servizio di token
      introspection di Google. L'unico campo da fornire in questo caso è
      la URL del servizio. Il sistema precompila questo campo con il
      valore di default
      *https://www.googleapis.com/oauth2/v3/tokeninfo*.

   -  *Personalizzato*: Questa opzione consente di configurare un
      servizio di Token Introspection personalizzato (:numref:`tokenIntrospectionCustom`).

   .. figure:: ../../_figure_console/TokenIntrospectionCustom.png
    :scale: 100%
    :align: center
    :name: tokenIntrospectionCustom

    Configurazione personalizzata del servizio di Token Instrospection

I dati da fornire sono:

-  *URL*: la URL del servizio di introspection.

-  *Http Method*: Il metodo HTTP che deve essere utilizzato per la
   chiamata al servizio di introspection.

-  *Posizione Token*: Il metodo di passaggio del token al servizio
   di introspection. Sono supportati i classici metodi: HTTP
   Authorization Bearer, Header HTTP, Parametro URL e Parametro
   Form-Encoded Body. Negli ultimi tre casi sarà necessario
   fornire il nome dell'header o del parametro.

-  *Claims Parser*: Il metodo di parsing dei claims che vengono
   restituiti dal servizio di introspection. I valori possibili
   sono: RFC 7662 - Introspection, Google - TokenInfo e
   Personalizzato. In quest'ultimo caso si dovrà fornire il
   ClassName della classe contenente la logica di parsing.

-  *Autenticazione*: Analogamente a quanto visto in precedenza è
   necessario indicare con il flag opportuno il tipo di
   autenticazione richiesta dal servizio di introspection
   personalizzato.
