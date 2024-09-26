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
    :scale: 80%
    :align: center

    Configurazione della gestione token

Supponendo che la policy copra tutti gli aspetti disponibili, le opzioni configurabili sono tutte quelle descritte di seguito. Le azioni che sono state abilitate saranno effettuate in accordo a
quanto configurato nella relativa Token Policy selezionata.

**- Token Opzionale** 

Consente di non forzare i richiedenti al passaggio del token, che rimane quindi un'operazione opzionale.

**- Validazione JWT** 

Nel caso in cui il token sia di tipo JWT (quindi JWE o JWS) la funzionalità consente di validare il token ricevuto rispetto ad un truststore di certificati.   

Per maggiori dettagli sul tipo di validazione si rimanda alla sezione :ref:`tokenPolicy_validazioneJWT`.

Selezionando l'opzione *WarningOnly* è possibile non rendere bloccante l'evento di fallimento della validazione, ottenendo come unico effetto l'emissione di un messaggio diagnostico di segnalazione. 

Durante il processo di validazione, se il token viene firmato tramite un certificato x509, viene effettuato per default il controllo della validità (scadenza) del certificato. È possibile modificare tale controllo registrando la :ref:`configProprieta` '*tokenValidation.validityCheck*' sull'erogazione o sulla fruizione con uno dei seguenti valori:

- true: (default) il controllo di validità viene effettuato;
- false: il controllo viene disabilitato; questo consente di accettare token firmati con certificati scaduti;
- ifNotInTruststore: permette di eseguire la verifica della validità del certificato di firma solo se il certificato non è presente nel truststore utilizzato per la validazione (ad esempio, quando nel truststore è presente solo la CA). Con questa impostazione, un certificato scaduto verrà accettato se è presente nel truststore; in caso contrario, la transazione verrà rifiutata.

**- Introspection** 

Consente di abilitare/disabilitare l'operazione di Token Introspection, al fine di validare il token ricevuto ed ottenere le metainformazioni associate (ad esempio scope e riferimento al possessore del token). 

Selezionando l'opzione *WarningOnly* è possibile non rendere bloccante l'evento di fallimento della validazione, ottenendo come unico effetto l'emissione di un messaggio diagnostico di segnalazione.

Per maggiori dettagli sull'integrazione con il servizio di introspection si rimanda alla sezione :ref:`tokenPolicy_introspection`.

**- User Info** 

Consente di abilitare/disabilitare l'operazione UserInfo al fine di ottenere le informazioni di dettaglio dell'utente possessore del token. 

Selezionando l'opzione *WarningOnly* è possibile non rendere bloccante l'evento di fallimento della validazione, ottenendo come unico effetto l'emissione di un messaggio diagnostico di segnalazione. 

Per maggiori dettagli sull'integrazione con il servizio user info si rimanda alla sezione :ref:`tokenPolicy_userInfo`.

**- Token Forward** 

Consente di abilitare/disabilitare l'operazione di inoltro, al servizio destinatario, del token ricevuto dal mittente. 

Per maggiori dettagli sulle modalità di inoltro si rimanda alla sezione :ref:`tokenPolicy_tokenForward`.

**- Required Claims** 

È possibile inoltre far verificare la presenza obbligatoria delle seguenti metainformazioni all'interno del token: 

- Issuer
- ClientId
- Subject
- Username
- Email



.. note::
    È disponibile la Token Policy *Google* preconfigurata in modo da
    utilizzare i servizi di elaborazione token esposti pubblicamente da
    Google e quindi:

    -  La Validazione JWT basata su *Google - ID Token* (https://www.googleapis.com/oauth2/v3/certs)
    -  Il servizio di token introspection basato su *Google - TokenInfo* (https://www.googleapis.com/oauth2/v3/tokeninfo)
    -  Il servizio di User Info basato su *Google - UserInfo* (https://www.googleapis.com/oauth2/v3/userinfo)
