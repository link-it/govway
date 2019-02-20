.. _definizioneAPI:

Definizione delle API
---------------------

Indipendentemente che si voglia erogare o fruire un servizio, è
necessario iniziare il processo di configurazione con il censimento
delle relative API. Questa operazione si effettua sulla govwayConsole
posizionandosi nella sezione *Registro > API*.

La pagina di ingresso mostra l'elenco delle API eventualmente già
presenti in configurazione. Ciascun elemento dell'elenco riporta
l'identificativo, il tipo SOAP o REST e il formato del descrittore
fornito in configurazione (:numref:`apiList`).

   .. figure:: ../_figure_console/API-list.png
    :scale: 100%
    :align: center
    :name: apiList

    Elenco delle API


Gli elementi dell'elenco possono essere selezionati per l'eliminazione,
con il pulsante Elimina, e per l'esportazione, con il pulsante Esporta.
La funzione di esportazione è descritta nella sezione :ref:`esporta`.

.. note::
    Le API in elenco sono visualizzate con un'icona colorata affiancata
    al nome. L'icona di colore rosso indica un problema nella
    configurazione e quindi l'inutilizzabilità della API. L'icona gialla
    indica il parziale utilizzo limitato agli elementi configurati
    correttamente. L'icona verde indica la piena funzionalità.

Si crea una nuova API premendo il pulsante *Aggiungi*.

   .. figure:: ../_figure_console/API-new.png
    :scale: 100%
    :align: center
    :name: apiNew

    Definizione di una API

Compilare il form (:numref:`apiNew`) inserendo i seguenti dati:

-  *Tipo*: Selezionare il tipo delle API a scelta tra "Soap" e "Rest".

-  *Nome*: Assegnare un nome che identifichi le API.

-  *Descrizione*: un testo opzionale di descrizione.

-  *Versione*: progressivo numerico che identifica l'indice di
   revisione.

-  *Specifica delle Interfacce*: In questa sezione è possibile caricare
   il descrittore formale dell'interfaccia, analizzando il quale, il
   gateway produce la corrispondente configurazione. Nel caso di
   interfacce Soap si potrà caricare il relativo WSDL. Nel caso di
   interfacce Rest si potrà scegliere tra i formati: WADL, Swagger 2.0 e
   OpenAPI 3.0.

   Nel caso non si disponga del descrittore dell'interfaccia è sempre
   possibile inserire manualmente la relativa configurazione seguendo le
   modalità descritte alla sezione :ref:`confManuale`.

