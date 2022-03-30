.. _tokenValidazionePolicy:

Token Policy Validazione
------------------------

Per poter definire politiche di controllo degli accessi basate sui
Bearer Token è necessario creare delle Token Policy da riferire nelle
configurazioni degli specifici servizi. La gestione delle Token Policy
si effettua andando alla sezione *Configurazione > Token Policy* della
govwayConsole. Per creare una nuova policy si utilizza il pulsante
*Aggiungi*. Il form di creazione appare inizialmente come quello
illustrato in :numref:`tokenPolicyFig`.


   .. figure:: ../../_figure_console/TokenPolicy-generale.png
    :scale: 100%
    :align: center
    :name: tokenPolicyFig

    Informazioni generali di una Token Policy

Inizialmente si inseriscono i dati identificativi:

-  *Nome*: nome univoco da assegnare alla policy

-  *Tipo*: deve essere selezionato il tipo *Validazione*

-  *Descrizione*: testo di descrizione generale della policy

Al passo successivo si inseriscono le Informazioni Generali. Nella
sezione *Token* si specifica il tipo di token accettato e il metodo di
passaggio:

-  *Posizione*: indica la modalità di passaggio del token da parte
   dell'applicativo richiedente. I valori possibili sono:

   -  *RFC 6750 - Bearer Token Usage*: la modalità di passaggio del
      token è una qualsiasi delle tre previste dallo standard RFC 6750
      (le tre opzioni successive a questa).

   -  *RFC 6750 - Bearer Token Usage (Authorization Request Header
      Field)*: la modalità di passaggio del token è quella che prevede
      l'inserimento nell'header "Authorization" del messaggio di
      richiesta. Ad esempio:

      ::

          GET /resource HTTP/1.1
          Host: server.example.com
          Authorization: Bearer mF_9.B5f-4.1JqM

   -  *RFC 6750 - Bearer Token Usage (Form-Encoded Body Parameter)*: la
      modalità di passaggio del token è quella di inserirlo nel body
      della richiesta, eseguita con una POST, utilizzando il parametro
      *access\_token*, come ad esempio:

      ::

          POST /resource HTTP/1.1
          Host: server.example.com
          Content-Type: application/x-www-form-urlencoded

          access_token=mF_9.B5f-4.1JqM

   -  *RFC 6750 - Bearer Token Usage (URI Query Parameter)*: la modalità
      di passaggio del token è quella di utilizzare il parametro
      *access\_token* della Query String, come ad esempio:

      ::

          GET /resource?access_token=mF_9.B5f-4.1JqM HTTP/1.1
          Host: server.example.com

   -  *Header HTTP*: la modalità di passaggio del token è quella di
      inserirlo in un header http custom, il cui nome deve essere
      fornito nel campo *Nome Header Http*, che appare di seguito.

   -  *Parametro URL*: la modalità di passaggio del token è quella di
      inserirlo in un parametro custom della query string. Il nome del
      parametro deve essere fornito nel campo *Nome Parametro URL*, che
      appare di seguito.

-  *Tipo*: specifica il tipo di token che il gateway attende di
   ricevere. I valori possibili sono:

   -  *JWS*: un JSON Web Token di tipo "Signed".

   -  *JWE*: un JSON Web Token di tipo "Encrypt".

   -  *Opaco*: un generico token di tipo non specificato.

Nella sezione *Elaborazione Token* si specificano le azioni che si
possono compiere durante la fase di elaborazione del token ricevuto. Le
opzioni disponibili sono:

-  Validazione JWT

-  Token Introspection

-  OIDC - UserInfo

-  Token Forward

Le sezioni successive dettagliano questi elementi.

.. toctree::
        :maxdepth: 2

        validazioneJWT
	introspection
	userInfo
	tokenForward
        mappingToken
