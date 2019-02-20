
.. _mon_export:

Export Transazioni
~~~~~~~~~~~~~~~~~~~~~

Dopo aver effettuato una ricerca vi è la possibilità di selezionare le
transazioni desiderate ed effettuare un export su file. Dopo aver
selezionato le transazioni tramite il checkbox posto a inizio riga
nell'elenco, si sceglie quali dati includere nell'export tra Tracce,
Diagnostici e Contenuti.

.. figure:: ../_figure_monitoraggio/ElencoTransazioni.png
    :scale: 100%
    :align: center
    :name: mon_elencoTransazioni_fig

    Lista delle transazioni risultanti da una ricerca

Il risultato dell'esportazione è un archivio zip contenente una
directory per ciascuna transazione esportata. A loro volta le directory
possono contenere una combinazione dei seguenti files, in base al tipo
dei dati esportati:

-  *manifest.xml*

   Sempre presente, contiene i dati descrittivi della transazione
   (mittente, destinatario, ora di registrazione, id-messaggio, ecc.);

-  *tracce.xml*

   In caso di esportazione delle tracce questo file contiene le tracce
   della richiesta e della risposta nel formato previsto dalla specifica
   del protocollo (es. SPCoop);

-  *diagnostici.xml*

   In caso di esportazione dei diagnostici questo file contiene i
   messaggi diagnostici associati alla transazione nel formato previsto
   dalla specifica del protocollo (es. SPCoop);

-  *fault.xml*

   In caso di esportazione dei contenuti questo file contiene il
   messaggio di soapFault restituito nei casi di errore.

-  *contenuti*

   -  *richiesta*

      -  *header.xml*: header HTTP riguardante la gestione del messaggio
         di richiesta

      -  *envelope.xml*: SOAPEnvelope del messaggio di richiesta

      -  *allegati*: eventuali allegati presenti nel messaggio di
         richiesta

   -  *risposta*

      -  *header.xml*: header HTTP riguardante la gestione del messaggio
         di risposta

      -  *envelope.xml*: SOAPEnvelope del messaggio di risposta

      -  *allegati*: eventuali allegati presenti nel messaggio di
         risposta

.. figure:: ../_figure_monitoraggio/EsportaTransazioni.png
    :scale: 100%
    :align: center
    :name: mon_esportaTransazioni_fig

    Struttura dell'archivio di esportazione delle transazioni

Oltre all'export appena descritto è possibile effettuare anche
un'esportazione dei dati delle transazione in un formato CSV. Per
ottenere tale tipo di esportazione si deve selezionare le transazioni
interessate tramite il checkbox posto a inizio riga nell'elenco,
scegliere se includere nell'export anche le Tracce e Diagnostici (i
Contenuti non sono esportabili nel formato CSV) e cliccare sul pulsante
'Export CSV'. Prima di procedere con l'esportazione effettiva vengono
richiesti all'utente:

-  Tipologia di Documento: è possibile indicare un file CSV semplice o
   un XLS.

-  Colonne Esportate: tale opzione permette di decidere quali colonne
   saranno presenti nel file esportato:

   -  Solamente le colonne visualizzate nello storico delle transazioni
      (opzione: 'Visualizzate nello Storico')

   -  Tutte le informazioni disponibili per ogni transazione (opzione:
      Tutte)

   -  Solamente le informazioni che interessano all'utente, modificando
      anche l'ordine di apparizione di tali informazioni nelle colonne
      del file CSV (opzione: Personalizza)

.. figure:: ../_figure_monitoraggio/EsportaCSV.png
    :scale: 100%
    :align: center
    :name: mon_EsportaCSV_fig

    Export CSV personalizzato
