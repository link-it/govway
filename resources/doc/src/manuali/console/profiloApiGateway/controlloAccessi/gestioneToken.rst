.. _apiGwGestioneToken:

Autenticazione Token
^^^^^^^^^^^^^^^^^^^^

Questa sezione consente di configurare il controllo degli accessi basato
su Bearer Token OAuth2. Facendo transitare lo stato su "abilitato"
compare l'elemento *Policy* (obbligatorio) per la selezione della policy
di autenticazione token che si vuole applicare. In questa lista a discesa
saranno visualizzate tutte le *Token Policy* di tipo *Validazione* che sono state registrate
in precedenza. Per le istruzioni sulla registrazione delle Token Policy
si faccia riferimento alla sezione :ref:`tokenPolicy`.

Una volta selezionata la policy compariranno sotto gli elementi per
stabilire le specifiche azioni da abilitare rispetto al totale di quelle
previste nella policy stessa (:numref:`gestioneTokenFig`).

    .. _gestioneTokenFig:

   .. figure:: ../../_figure_console/GestioneToken.png
    :scale: 100%
    :align: center

    Configurazione della gestione token

Supponendo che la policy copra tutti
gli aspetti disponibili, le opzioni configurabili sono le seguenti:

-  *Token Opzionale*: consente di non forzare i richiedenti al passaggio
   del token, che rimane quindi un'operazione opzionale.

-  *Introspection*: consente di abilitare/disabilitare l'operazione di
   Token Introspection, al fine di validare il token ricevuto ed
   ottenere le metainformazioni associate (ad esempio scope e
   riferimento al possessore del token). Selezionando l'opzione
   *WarningOnly* è possibile non rendere bloccante l'evento di
   fallimento della validazione, ottenendo come unico effetto
   l'emissione di un messaggio diagnostico di segnalazione.

-  *User Info*: consente di abilitare/disabilitare l'operazione UserInfo
   al fine di ottenere le informazioni di dettaglio dell'utente
   possessore del token. Selezionando l'opzione *WarningOnly* è
   possibile non rendere bloccante l'evento di fallimento della
   validazione, ottenendo come unico effetto l'emissione di un messaggio
   diagnostico di segnalazione.

-  *Token Forward*: consente di abilitare/disabilitare l'operazione di
   inoltro, al servizio destinatario, del token ricevuto dal mittente.

Le azioni che sono state abilitate saranno effettuate in accordo a
quanto configurato nella relativa Token Policy selezionata.

.. note::
    È disponibile la Token Policy *Google* preconfigurata in modo da
    utilizzare i servizi di elaborazione token esposti pubblicamente da
    Google e quindi:

    -  La Validazione JWT basata su *Google - ID Token* (https://www.googleapis.com/oauth2/v3/certs)
    -  Il servizio di token introspection basato su *Google - TokenInfo* (https://www.googleapis.com/oauth2/v3/tokeninfo)
    -  Il servizio di User Info basato su *Google - UserInfo* (https://www.googleapis.com/oauth2/v3/userinfo)

È possibile inoltre far verificare la presenza obbligatoria delle seguenti metainformazioni all'interno del token: 

- Issuer
- ClientId
- Subject
- Username
- Email
