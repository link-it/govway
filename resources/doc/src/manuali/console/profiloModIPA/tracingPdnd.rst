.. _modipa_tracingPdnd:

Tracing PDND
----------------------

La PDND mette a disposizione delle API che permettono agli e-service di depositare informazioni relative all’esito delle transazioni effettuate, con riferimento a specifiche date, finalità e identificativo del token negoziato.

Un e-service iscritto alla PDND deve quindi, con cadenza giornaliera, depositare un file CSV contenente le informazioni relative a tutte le transazioni effettuate in un determinato giorno. Questo CSV deve includere i seguenti campi:

.. list-table:: Campi richiesti dal tracciamento PDND
   :widths: 10 50
   :header-rows: 1

   * - Campo
     - Descrizione
   * - date
     - La data in cui sono state eseguite le operazioni, nel formato YYYY-MM-DD
   * - purpose_id
     - L’ID della finalità, come presente nella richiesta del fruitore
   * - status
     - Il codice di stato HTTP con cui il servizio ha risposto al chiamante
   * - token_id
     - L’ID del token utilizzato per effettuare la richiesta HTTP verso il servizio
   * - requests_count
     - Il numero di richieste che hanno generato il medesimo codice di stato HTTP

Dopo ogni invio, la PDND provvede a elaborare il file CSV in modalità asincrona. L’erogatore può controllare lo stato di ogni caricamento e, nel caso in cui esso non venga accettato, potrà correggere e ritrasmettere il file.

Poiché il caricamento giornaliero è obbligatorio, la PDND segnala i giorni in cui il file non è stato ricevuto, richiedendo all’erogatore l’invio dei CSV mancanti per quei giorni.

Nel caso in cui si voglia sostituire un tracciato già caricato con successo, è comunque possibile effettuare un’operazione di sostituzione, anche se il tracciato risulta già presente.


.. toctree::
   :maxdepth: 2

   tracingPdnd/introduzioneGovway
   tracingPdnd/configurazioneProperties