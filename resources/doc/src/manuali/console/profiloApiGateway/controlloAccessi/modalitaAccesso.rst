.. _modalitaAccesso:

Modalità di Accesso
^^^^^^^^^^^^^^^^^^^

Quando il gateway richiede l'autenticazione per l'accesso è possibile selezionare la relativa modalità e quindi il tipo di credenziali che dovranno essere fornite.

Le modalità per l'accesso autenticato supportate sono le seguenti:

-   **http-basic**
    Se il tipo selezionato è *http-basic* sarà necessario fornire l'identificativo utente e la relativa password (:numref:`authBasicFig`).

   .. figure:: ../../_figure_console/AuthBasic.png
    :scale: 100%
    :align: center
    :name: authBasicFig

    Credenziali di tipo HTTP-Basic


-   **https**
    Se il tipo selezionato è *https* si procede con la configurazione del certificato che sarà fornito durante l'autenticazione (:numref:`authSSLFig`). Per la configurazione si procede selezionando dall'elemento *Modalità* una tra le seguenti opzioni:

    - **Upload Archivio**: con questa modalità di configurazione si procede con il caricamento del certificato che sarà utilizzato per l'autenticazione. È necessario indicare il formato del certificato fornito specificando tra le seguenti opzioni supportate:

        - *CER*: il certificato da caricare è in formato *DER* o *PEM*.

        - *JKS*: il certificato da caricare è contenuto in un keystore JKS.

        - *PKCS12*: il certificato da caricare è contenuto in un keystore PKCS12.

    - **Password**: campo visibile nel caso in cui il certificato da caricare è contenuto in un keystore. Rappresenta la password per l'accesso al keystore.

    - **Certificato**: selezionare dal proprio filesystem il file che contiene il certificato.

    - **Alias**: nel caso in cui il keystore contenga più di un certificato, questa lista consente di selezionare l'alias che riferisce l'elemento corretto. Dopo aver selezionato un alias verranno mostrati a video i dettagli del certficato selezionato, al fine di poterli verificare prima di confermare l'inserimento.

    - **Verifica tutti i campi**: questa opzione, se attivata, comporta il confronto di tutti i campi del certificato fornito per l'autenticazione con quelli presenti nel certiicato fornito come campione in configurazione. Il fallimento di tale verifica (ad esempio anche il caso di superamento della data di scadenza) causeranno il fallimento dell'autenticazione.

    - **Configurazione Manuale**: con questa modalità di configurazione si procede con l'inserimento dei seguenti dati:

        - *Self Signed*: opzione per indicare se il cerfificato è self-signed oppure rilasciato da una CA.

        - *Subject*: il subject del certificato.

        - *Issuer*: l'issuer del certificato, nel caso in cui non sia self-signed.

   .. figure:: ../../_figure_console/AuthSSL.png
    :scale: 100%
    :align: center
    :name: authSSLFig

    Credenziali di tipo HTTPS

-   **principal**
    Se il tipo selezionato è *principal* sarà necessario fornire lo UserId (:numref:`authPrincipalFig`).

   .. figure:: ../../_figure_console/AuthPrincipal.png
    :scale: 100%
    :align: center
    :name: authPrincipalFig

    Credenziali di tipo Principal
