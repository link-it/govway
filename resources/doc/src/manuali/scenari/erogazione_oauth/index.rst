.. _scenari_erogazione_oauth:

Erogazione OAuth
================

Obiettivo
---------
Esporre un servizio accessibile tramite protocollo OAuth2 (Authorization Code).

Sintesi
-------
Assumendo che sia stata effettuata la configurazione di un'erogazione ad accesso pubblico (vedi scenario :ref:`scenari_erogazione_pubblica`), verifichiamo in questo scenario come impostare il sistema di controllo degli accessi affinché il servizio richieda un token di sicurezza, come previsto dal protocollo OAuth2. In particolare la limitazione dell'accesso sarà configurata solo per le operazioni di scrittura, lasciando libero accesso per le letture.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/ErogazioneOAuth.png
    :scale: 80%
    :align: center
    :name: erogazione_oauth_fig

    Erogazione OAuth

I passi previsti sono i seguenti:

1. Il client entra in possesso del token, previa autenticazione e consenso dell'utente richiedente.
2. Il client utilizza il token per l'invio della richiesta.
3. Govway valida il token ricevuto e verifica i criteri di controllo degli accessi.
4. Se la validazione è superata, Govway inoltra la richiesta al servizio erogatore.

Esecuzione
----------

Facendo riferimento al progetto Postman è possibile verificare direttamente l'esecuzione dei passi di questo scenario.
Passi da eseguire:

1. All'inizio possiamo verificare come il client non riesca ad accedere al servizio senza l'utilizzo del token. La request "2a. Erogazione Token (postPet) Error" effettua una chiamata alla risorsa "POST /pet" in assenza del token richiesto. Govway respinge la richiesta con la restituzione dell'errore mostrato in :numref:`postman_erogazione_errore_fig`.

   .. figure:: ../_figure_scenari/Postman_erogazione_errore.png
    :scale: 80%
    :align: center
    :name: postman_erogazione_errore_fig

    Invocazione della POST /pet senza token

2. Successivamente si passa alla chiamata della "POST /pet" seguendo il flusso OAuth2 richiesto per l'approvvigionamento del token di autorizzazione. Posizionarsi sulla request "2b. Erogazione Token (postPet) OK":

  - Nella sezione "Authorization" selezionare il Type "OAuth 2.0" e premere il pulsante "Get New Access Token"
  - La maschera fornita (:numref:`postman_newAccessToken_fig`) deve essere compilata con i parametri necessari ad richiedere un token all'authorization server. Utilizzare i seguenti parametri che permettono di richiedere un token all'authorization server preconfigurato per lo scenario:

      ::

          Callback URL: {{keycloak-callback-url}}
	  Auth URL: {{keycloak-url-auth}}
	  Access Token URL: {{keycloak-url-token}}
	  Client ID: {{keycloak-client-id}}
	  Client Secret: {{keycloak-client-secret}}

   .. figure:: ../_figure_scenari/postman_newAccessToken.png
    :scale: 80%
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

   .. figure:: ../_figure_scenari/Postman_erogazione_OK.png
    :scale: 80%
    :align: center
    :name: postman_erogazione_OK_fig

    Invocazione della POST /pet con token

3. Possiamo verificare che le limitazioni sul'accesso non sono efficaci nel caso di invocazione di operazioni di lettura. Il passo "3. Erogazione Pubblica (getPet)" esegue una GET. Si noti come la sezione Authorization abbia l'impostazione del Type su "No Auth". Questa request legge il dato creato con la POST precedente e, come è possibile riscontrare al termine dell'esecuzione, viene correttamente eseguita in assenza di credenziali.


Configurazione
--------------

Per effettuare le configurazioni necessarie al funzionamento dello scenario partiamo dall'erogazione già configurata con accesso pubblico. Si procede quindi con i passi di configurazione finalizzati a limitare l'accesso alle sole operazioni di scrittura. Per fare questo si eseguono i seguenti passi sulla govwayConsole:

1. Dal dettaglio dell'erogazione, si procede con la creazione di una nuova configurazione, cui diamo il nome *"Scritture"* (:numref:`erogazione_config_scritture_fig`).

    - Selezionare dall'elenco delle risorse quelle che riguardano operazioni di scrittura (POST, PUT, DELETE)
    - Indicare per la *Modalità* il valore *"Nuova"* e quindi selezionare *"autenticato"* nel campo *Accesso API*

   .. figure:: ../_figure_scenari/Erogazione_config_scritture.png
    :scale: 80%
    :align: center
    :name: erogazione_config_scritture_fig

    Creazione di una configurazione specifica per le operazioni di scrittura

2. Nella nuova configurazione "Scritture" si va ad aggiornare la sezione *"Controllo Accessi"* effettuando le seguenti azioni (:numref:`erogazione_controlloaccessi_token_fig`):

    - Abilitare l'autenticazione token selezionando la policy *"KeyCloak"* (configurazione preesistente per l'integrazione all'authorization server), lasciando invariate le altre opzioni del medesimo riquadro.
    - Disabilitare le altre funzionalità di controllo degli accessi: Autenticazione Trasporto, Autorizzazione e Autorizzazione Contenuti.

   .. figure:: ../_figure_scenari/Erogazione_controlloaccessi_token.png
    :scale: 80%
    :align: center
    :name: erogazione_controlloaccessi_token_fig

    Impostazione dell'autenticazione token nel controllo degli accessi

3. Dopo aver salvato la nuova configurazione, verificare il riepilogo delle informazioni, che devono corrispondere a quanto riportato in :numref:`erogazione_token_riepilogo_fig`.

   .. figure:: ../_figure_scenari/Erogazione_token_riepilogo.png
    :scale: 80%
    :align: center
    :name: erogazione_token_riepilogo_fig

    Riepilogo della configurazione effettuata
