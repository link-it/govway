.. _modipa_idar01_erogazione:

Erogazione ID_AUTH_REST_01 / ID_AUTH_SOAP_01 (X509)
---------------------------------------------------

In un'erogazione di una API le richieste provengono da amministrazioni esterne al dominio e sono dirette ad applicativi interni. Prima di procedere con l'inoltro della richiesta verso il backend interno, GovWay valida il token di sicurezza ricevuto rispetto al pattern associato all'operazione invocata: verifica firma, validazione temporale, filtro duplicati, verifica integrità del messaggio, verifica del token di audit etc.

Nella figura ':numref:`ErogazioneModIPA2`' viene raffigurato lo scenario di erogazione in cui il trust avviene tra fruitore ed erogatore tramite certificati x509.

.. figure:: ../../../../_figure_console/ErogazioneModIPA_TokenLocale.jpg
 :scale: 70%
 :align: center
 :name: ErogazioneModIPA2

 Erogazione con Profilo di Interoperabilità 'ModI', pattern 'ID_AUTH_REST_01': trust tra fruitore ed erogatore tramite certificati x509


**API**

La registrazione della API deve essere effettuata seguendo le indicazioni descritte nella sezione :ref:`modipa_idar01`


**Erogazione**

L'interfaccia per la creazione dell'erogazione, basata su una API con pattern "ID_AUTH_REST_01" (o "ID_AUTH_SOAP_01"), presenta le sezioni "ModI - Richiesta" e "ModI - Risposta":

- ModI - Richiesta (:numref:`erogazione_richiesta_fig`): la maschera relativa alla richiesta prevede la configurazione del meccanismo di validazione del token ricevuto sul messaggio di richiesta:

    + Riferimento X.509 (presente solo per API REST): il metodo per la localizzazione del certificato dell'applicativo mittente nel messaggio di richiesta. Il valore fornito deve corrispondere alla scelta operata dai mittenti.  I valori possibili sono quelli previsti nella specifica AGID.
    + TrustStore Certificati: Riferimento al truststore che contiene le CA, i certificati, CRL e policy OCSP da utilizzare per poter verificare i token di sicurezza ricevuti nelle richieste. È possibile mantenere l'impostazione di default che è stata fornita al momento dell'installazione del prodotto, oppure definire un diverso riferimento (opzione "Ridefinito") fornendo Path, Tipo, Password del TrustStore e criteri di verifica tramite CRL o OCSP.
    + Time to Live (secondi): consente di modificare l'intervallo temporale di default (300 secondi) utilizzato per rifiutare i token creati precedentemente all'intervallo indicato.
    + Audience: valore del campo Audience atteso nel token di sicurezza della richiesta.

  .. figure:: ../../../../_figure_console/modipa_erogazione_richiesta.png
   :scale: 70%
   :name: erogazione_richiesta_fig

   Dati per la configurazione della sicurezza messaggio sulla richiesta di una erogazione


- ModI - Risposta (:numref:`erogazione_risposta_fig`): la maschera prevede la configurazione del meccanismo di firma digitale del messaggio di risposta, e la produzione del relativo token di sicurezza, da inviare all'applicativo mittente:

    + Algoritmo: l'algoritmo che si vuole utilizzare per la firma digitale del messaggio di risposta
    + Riferimento X.509: il metodo da utilizzare per l'inserimento del certificato nel messaggio di risposta. Nelle API di tipo REST si può mantenere la medesima impostazione prevista per il messaggio di richiesta o ridefinirla.
    + Certificate Chain: se è stata selezionata la modalità 'x5c', è possibile indicare se nel token di sicurezza verrà incluso solo il certificato utilizzato per la firma o l'intera catena.
    + Keystore: il keystore da utilizzare per la firma del messaggio di risposta. È possibile mantenere il riferimento al keystore di default, fornito in fase di installazione del prodotto, oppure indicare un diverso riferimento (opzione "Ridefinito") fornendo il path sul filesystem, o in alternativa direttamente l'archivio, unitamente a Tipo, Password, Alias Chiave Privata e Password Chiave Privata.
    + Time to Live (secondi): validità temporale del token prodotto.

  .. figure:: ../../../../_figure_console/modipa_erogazione_risposta.png
   :scale: 70%
   :name: erogazione_risposta_fig

   Dati per la configurazione della sicurezza messaggio sulla risposta di una erogazione

**Identificazione ed Autorizzazione dei fruitori**

È possibile registrare gli applicativi dei domini esterni al fine di:

1. identificare puntualmente le componenti esterne coinvolte nella comunicazione abilitando le funzionalità di tracciamento e statistica per tali elementi.
2. abilitare le funzionalità di autorizzazione sugli applicativi identificando puntualmente chi autorizzare dopo il superamento del processo di autenticazione/autorizzazione canale e validazione del token di sicurezza.


Per abilitare quanto al punto 1 è sufficiente la sola registrazione degli applicativi esterni coinvolti (:numref:`applicativo_esterno_fig`).

.. figure:: ../../../../_figure_console/modipa_applicativo_esterno.png
 :scale: 70%
 :name: applicativo_esterno_fig

 Registrazione di un applicativo esterno

Dopo aver indicato il dominio "Esterno" per l'applicativo, sarà necessario selezionare il soggetto che identifica il dominio esterno di riferimento.

La registrazione dell'applicativo esterno comprende anche la sezione con i dati relativi alla sicurezza messaggio (:numref:`applicativo_esterno_upload_fig`).

.. figure:: ../../../../_figure_console/modipa_applicativo_esterno_upload.png
 :scale: 70%
 :name: applicativo_esterno_upload_fig

 Dati ModI relativi ad un applicativo esterno con upload del certificato


I dati da fornire sono:

- *Modalità*: si seleziona tra il caricamento del certificato e la configurazione manuale
- Caso *Upload Archivio*:

    + *Formato*: formato dell'archivio fornito (CER, JKS; PKCS12)
    + *Certificato*: elemento per l'upload dell'archivio che contiene il certificato
    + *Reply Audience/WSA-To*: identificativo dell'applicativo da confrontare con il valore "Audience" eventualmente presente nelle richieste.

- Caso *Configurazione Manuale* (:numref:`applicativo_esterno_manuale_fig`):

    + *Self Signed*: opzione per indicare se il cerfificato è self-signed oppure rilasciato da una CA
    + *Subject*: il subject del certificato
    + *Issuer*: l’issuer del certificato, nel caso in cui non sia self-signed
    + *Reply Audience/WSA-To*: identificativo dell'applicativo da confrontare con il valore "Audience" eventualmente presente nelle richieste.

.. figure:: ../../../../_figure_console/modipa_applicativo_esterno_manuale.png
 :scale: 50%
 :name: applicativo_esterno_manuale_fig

 Dati ModI relativi ad un applicativo esterno con configurazione manuale dei dati di sicurezza


Per abilitare le funzionalità di autorizzazione dei singoli applicativi (punto 2 del precedente elenco) si deve procedere alla configurazione della sezione "Controllo Accessi" relativa all'erogazione. Quando attiva la sicurezza messaggio, questa sezione conterrà il form "Autorizzazione Messaggio" (:numref:`erogazione_secMessaggio_fig`). Qui è possibile specificare un elenco puntuale di applicativi (esterni) autorizzati, ad accedere all'erogazione, tra quelli identificati nella fase di verifica del relativo certificato. Gli applicativi esterni saranno selezionabili tra quelli censiti nella sezione "Applicativi" (:numref:`erogazione_secMessaggio_fig`). In alternativa è possibile definire i ruoli che gli applicativi devono possedere.

.. figure:: ../../../../_figure_console/modipa_erogazione_secMessaggio.png
 :scale: 60%
 :name: erogazione_secMessaggio_fig

 Autorizzazione di singoli applicativi per l'accesso all'erogazione

.. note::
    L'autorizzazione basata sugli identificativi degli applicativi mittenti del dominio fruitore esterno, è possibile soltanto se è stata effettuata la registrazione degli applicativi interessati, in associazione al soggetto esterno di riferimento.
