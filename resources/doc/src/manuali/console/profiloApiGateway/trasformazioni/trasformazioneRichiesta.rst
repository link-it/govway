.. _trasformazioniRichiesta:

Regole di Trasformazione della Richiesta
****************************************

Selezionando il collegamento "Richiesta", nel riquadro delle Regole di Trasformazione, si procede con la definizione formale della trasformazione attuata sulle richieste in ingresso sulle quali è applicabile la trasformazione corrente.
Le trasformazioni possono essere applicate sia a livello del trasporto che del contenuto, come mostrano le sezioni visualizzate in :numref:`trasf_Richiesta`.

   .. figure:: ../../_figure_console/TrasformazioniRichiesta.png
    :scale: 100%
    :align: center
    :name: trasf_Richiesta

    Regola di trasformazione della richiesta

A livello del trasporto è possibile applicare trasformazioni sugli "HTTP Headers", selezionando l'omonimo collegamento e quindi aggiungendo le operazioni da effettuare (:numref:`trasf_Headers`).

   .. figure:: ../../_figure_console/TrasformazioniHTTPHeaders.png
    :scale: 100%
    :align: center
    :name: trasf_Headers

    Operazioni sugli Header HTTP

Ciascuna operazione può essere selezionata tra le seguenti:

- add: per aggiungere un nuovo header specificando successivamente nome e valore
- delete: per eliminare un header indicandone successivamente il nome
- update: per modificare un header indicandone successivamente il nome ed il nuovo valore
- updateOrAdd: per modificare un header indicandone successivamente il nome ed il nuovo valore. Nel caso l'header non si presente, verrà aggiunto.

.. note::
    i valori specificati per gli header http possono contenere le proprietà dinamiche descritte nella sezione :ref:`valoriDinamici`.
    Il campo 'Identificazione Fallita' permette di definire il comportamento del Gateway quando non riesce a risolvere parti dinamiche contenute nel valore indicato. Le configurazioni utilizzabili sono:

    - Termina con errore: la transazione termina con un errore che riporta la fallita risoluzione della parte dinamica indicata per il valore;
    - Continua senza header: la transazione continua senza completare la gestione dell'header.

Sempre a livello del trasporto è possibile applicare trasformazioni anche sui parametri presenti nella Query String, selezionando il collegamento "URL Parameters". La modalità di configurazione è del tuto analoga a quanto appena descritto per gli Header HTTP.

Abilitando l'opzione sul Contenuto è possibile procedere con la definizione di operazioni sul contenuto della richiesta (:numref:`trasf_Contenuto`).

   .. figure:: ../../_figure_console/TrasformazioniContenuto.png
    :scale: 100%
    :align: center
    :name: trasf_Contenuto

    Modifica del Contenuto della Richiesta

Per la modifica del contenuto della richiesta devono essere forniti i seguenti dati:

- Tipo Conversione: indica il tipo di trasformazione da applicare al contenuto. Si può scegliere una tra le seguenti opzioni:

    - HTTP Payload Vuoto: opzione presente nel caso REST. Il contenuto della richiesta diventa un payload http vuoto.
    - SOAP Body Vuoto: opzione presente nel caso SOAP. Il contenuto della richiesta diventa un messaggio SOAP con SoapBody vuoto.
    - Template: il contenuto della richiesta viene assegnato utilizzando il template fornito in configurazione, che può contenere parti dinamiche definite tramite una sintassi proprietaria di GovWay.
    - Freemarker Template: il contenuto della richiesta viene assegnato utilizzando il template "Freemarker" (https://freemarker.apache.org/) fornito in configurazione.
    - Freemarker Template (Archivio Zip): il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Freemarker'. Viene richiesta la presenza, all'interno dell'archivio zip, di un file indice che possieda il nome 'index.ftl'.
    - Velocity Template: il contenuto della richiesta viene assegnato utilizzando il template "Velocity" (http://velocity.apache.org/) fornito in configurazione.
    - Velocity Template (Archivio Zip): il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Velocity'. Viene richiesta la presenza, all'interno dell'archivio zip, di un file indice che possieda il nome 'index.vm'.
    - XSLT: il contenuto della richiesta viene modificato applicando la trasformazione XSLT fornita in configurazione. Questo metodo è applicbile nel caso di messaggi XML o SOAP.
    - ZIP Compressor: il contenuto della richiesta verrà trasformato in un archizio zip il cui contenuto viene definito dal file fornito che deve contenere proprietà indicate come nome=valore in ogni linea. Il nome della proprietà corrisponde all'entry name all'interno dell'archivio (es. dir/subDir/entryName1). Il valore della proprietà corrisponde al contenuto dell'entry. È possibile selezionare parti del messaggio, per associarle come contenuto dell'entry, utilizzando le espressioni dinamiche risolte a runtime dal Gateway (sezione :ref:`valoriDinamici`).
    - TGZ Compressor: il contenuto della richiesta verrà trasformato in un archizio tgz il cui contenuto è definito tramite il file fornito che deve possedere la medesima struttura descritta per il tipo 'ZIP'.
    - TAR Compressor: il contenuto della richiesta verrà trasformato in un archizio tar il cui contenuto è definito tramite il file fornito che deve possedere la medesima struttura descritta per il tipo 'ZIP'.

- Template: nei casi che lo prevedono, con questo elemento si fornisce il template da utilizzare per ottenere il nuovo contenuto della richiesta.
- Content-Type: opzionalmente, tramite questo elemento, è possibile assegnare un content-type alla richiesta modificata.

.. note::
    i template possono contenere le proprietà dinamiche descritte nella sezione :ref:`valoriDinamici`. La sintassi adottata dipende dal template. Una finestra di help contestuale, presente nell'interfaccia, guiderà l'utente nell'applicazione della sintassi corretta.

.. toctree::
   :maxdepth: 2

   Conversione-REST-SOAP
   Conversione-SOAP-REST
