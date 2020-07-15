.. _modalitaAccessoHttps:

Credenziali 'https'
^^^^^^^^^^^^^^^^^^^

Agli applicativi ed ai soggetti registrati nel gateway, identificabili con credenziali 'https', deve essere associato un certificato client X509. 

Per la configurazione si procede selezionando dall'elemento *Modalità* una tra le seguenti opzioni:

- **Upload Archivio** (:numref:`authSSLFig`):  con questa modalità di configurazione si procede con il caricamento del certificato che sarà utilizzato per l'autenticazione. È necessario indicare:

   - *Formato*: il formato del certificato fornito specificando tra le seguenti opzioni supportate:

      - *CER*: il certificato da caricare è in formato *DER* o *PEM*.

      - *JKS*: il certificato da caricare è contenuto in un keystore JKS.

      - *PKCS12*: il certificato da caricare è contenuto in un keystore PKCS12.

   - *Password*: campo visibile nel caso in cui il certificato da caricare è contenuto in un keystore JKS o PCKS12. Rappresenta la password per l'accesso al keystore.

   - *Archivio*: selezionare dal proprio filesystem il file che contiene il certificato.

   - *Alias*: nel caso in cui il keystore contenga più di un certificato (frequente in formati JKS), questa lista consente di selezionare l'alias che riferisce l'elemento corretto. 

   .. figure:: ../../../_figure_console/AuthSSL.png
    :scale: 80%
    :align: center
    :name: authSSLFig

    Credenziali di tipo HTTPS (upload archivio 1/2)

   Una volta caricato l'archivio verranno mostrati a video i dettagli del certficato selezionato (:numref:`authSSLFig3`), al fine di poterli verificare prima di confermare l'inserimento. Verrà richiesto anche di indicare se abilitare o meno la *Verifica di tutti i campi* che comporta il confronto di tutti i campi del certificato fornito durante l'autenticazione con quelli presenti nel certiicato fornito come campione in configurazione. Il fallimento di tale verifica (ad esempio anche il caso di superamento della data di scadenza) causeranno il fallimento dell'autenticazione.

   .. figure:: ../../../_figure_console/AuthSSL3.png
    :scale: 80%
    :align: center
    :name: authSSLFig3

    Credenziali di tipo HTTPS (upload archivio 2/2)

- **Configurazione Manuale** (:numref:`authSSLFig2`): con questa modalità di configurazione si procede con l'inserimento dei seguenti dati:

   - *Self Signed*: opzione per indicare se il cerfificato è self-signed oppure rilasciato da una CA.

   - *Subject*: il subject del certificato.

   - *Issuer*: l'issuer del certificato, nel caso in cui non sia self-signed.

   .. figure:: ../../../_figure_console/AuthSSL2.png
    :scale: 80%
    :align: center
    :name: authSSLFig2

    Credenziali di tipo HTTPS (configurazione manuale)
