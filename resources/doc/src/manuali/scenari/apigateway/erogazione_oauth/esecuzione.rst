.. _scenari_erogazione_oauth_esecuzione:

Esecuzione
----------

Facendo riferimento al progetto Postman è possibile verificare direttamente l'esecuzione dei passi di questo scenario.
Passi da eseguire:

1. All'inizio possiamo verificare come il client non riesca ad accedere al servizio senza l'utilizzo del token. La request "IN token-auth (postPet) Error" effettua una chiamata alla risorsa "POST /pet" in assenza del token richiesto. Govway respinge la richiesta con la restituzione dell'errore mostrato in :numref:`postman_erogazione_errore_fig`.

   .. figure:: ../../_figure_scenari/Postman_erogazione_errore.png
    :scale: 70%
    :align: center
    :name: postman_erogazione_errore_fig

    Invocazione della POST /pet senza token

2. Successivamente si passa alla chiamata della "POST /pet" seguendo il flusso OAuth2 richiesto per l'approvvigionamento del token di autorizzazione. Posizionarsi sulla request "IN token-auth (postPet) OK":

  - Nella sezione "Authorization" selezionare il Type "OAuth 2.0" e premere il pulsante "Get New Access Token"
  - La maschera fornita (:numref:`postman_newAccessToken_fig`) deve essere compilata con i parametri necessari ad richiedere un token all'authorization server. Utilizzare i seguenti parametri che permettono di richiedere un token all'authorization server preconfigurato per lo scenario:

      ::

          Callback URL: {{keycloak-callback-url}}
	  Auth URL: {{keycloak-url-auth}}
	  Access Token URL: {{keycloak-url-token}}
	  Client ID: {{keycloak-client-id}}
	  Client Secret: {{keycloak-client-secret}}

   .. figure:: ../../_figure_scenari/postman_newAccessToken.png
    :scale: 70%
    :align: center
    :name: postman_newAccessToken_fig

    Ottenimento nuovo token

  - Compilati correttamente i campi per ottenere un token cliccare sul pulsante "Request Token"
  - Completare il processo di autenticazione dell'utente seguendo il flusso proposto ed utilizzando le credenziali dell'utente preconfigurato sull'authorization server per lo scenario di test:

      ::

          username: paolorossi
	  password: 123456

  - Superata l'autenticazione, viene restituito l'access token (mostrato a video sulla finestra popup).
  - Inserire il token nella richiesta premendo il pulsante "Use Token".
  - Eseguire la richiesta tramite il pulsante "Send".
  - L'operazione viene eseguita con successo e restituito l'esito (:numref:`postman_erogazione_OK_fig`).

   .. figure:: ../../_figure_scenari/Postman_erogazione_OK.png
    :scale: 70%
    :align: center
    :name: postman_erogazione_OK_fig

    Invocazione della risorsa 'POST /pet' con token

3. Possiamo verificare che le limitazioni sul'accesso non sono efficaci nel caso di invocazione di operazioni di lettura. Il passo "IN public (getPet)" esegue una GET. Si noti come la sezione Authorization abbia l'impostazione del Type su "No Auth". Questa request legge il dato creato con la POST precedente e, come è possibile riscontrare al termine dell'esecuzione, viene correttamente eseguita in assenza di credenziali  (:numref:`postman_erogazione_OKget_fig`).

   .. figure:: ../../_figure_scenari/Postman_erogazione_OKget.png
    :scale: 70%
    :align: center
    :name: postman_erogazione_OKget_fig

    Invocazione della risorsa 'GET /pet/id' con token

