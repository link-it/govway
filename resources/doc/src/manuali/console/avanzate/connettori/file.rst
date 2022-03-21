.. _avanzate_connettori_file:

Connettore File
~~~~~~~~~~~~~~~

Il connettore permette di serializzare la richiesta su FileSystem ed
opzionalmente di generare una risposta.

Il connettore File supporta:

-  **Richiesta**, è possibile serializzare il messaggio di richiesta su
   file-system fornendo un path che può contenere anche parti dinamiche
   risolte a runtime da GovWay. È permesso anche abilitare l'eventuale
   sovrascrittura del file, se risulta già esistente, e la creazione
   automatica delle directory padre, se non esistono.

-  **Risposta**, è opzionale; se abilitata permette di generare una
   risposta costruita utilizzando il contenuto di un file indirizzabile
   a sua volta tramite gli stessi meccanismi dinamici della richiesta.
   Il file contenente la risposta può essere eliminato una volta
   consumato (opzione configurabile). L'utente può inoltre indicare un
   tempo di attesa (ms) qualora il file non sia immediatamente
   disponibile.

   .. figure:: ../../_figure_console/ConnettoreFILE.jpg
    :scale: 100%
    :align: center
    :name: ConnettoreFILEFig

    Dati di configurazione di un connettore File

Facendo riferimento alla maschera raffigurata in :numref:`ConnettoreFILEFig` andiamo a descrivere
il significato dei parametri:

-  *Richiesta*

   -  **File**: indirizzo su file-system (path) dove verrà serializzato
      il messaggio di richiesta. È possibile fornire delle macro per
      creare dei path dinamici (per ulteriori dettagli vedi sezione
      'Informazioni Dinamiche').

   -  **File (Permessi)**: consente di impostare i permessi del file, indicato nel campo precedente, tramite il formato '[o/a]+/-rwx'.

   -  **File Headers** (opzionale): indirizzo su file-system (path) dove
      verranno serializzati gli header di trasporto associati alla
      richiesta. È possibile fornire delle macro per creare dei path
      dinamici (per ulteriori dettagli vedi sezione 'Informazioni
      Dinamiche').

   -  **File Headers (Permessi)**: consente di impostare i permessi del file, indicato nel campo precedente, tramite il formato '[o/a]+/-rwx'.

   -  **Overwrite If Exists** (true/false): abilita l'eventuale
      sovrascrittura del file, se risulta già esistere.

   -  **AutoCreate Parent Directory** (true/false): abilita la creazione
      automatica delle directory padre, se non esistono.

-  *Risposta (Opzionale)*

   -  **Generazione** (true/false): abilita la generazione di una
      risposta. Tutte le successive opzioni della sezione 'Risposta'
      sono configurabili solamente se la generazione è abilitata.

   -  **File**: indirizzo su file-system (path) dove verrà letto il
      messaggio di risposta. È possibile fornire delle macro per creare
      dei path dinamici, come descritto più avanti al punto
      "Informazioni Dinamiche".

   -  **File Headers** (opzionale): indirizzo su file system (path) dove
      verranno letti gli header di trasporto da associare alla risposta.
      E' possibile fornire delle macro per creare dei path dinamici,
      come descritto più avanti al punto "Informazioni Dinamiche".

   -  **Delete After Read** (true/false): abilita l'eventuale
      eliminazione del file una volta utilizzato per la generazione
      della risposta.

   -  **Wait Time If Not Exists (ms)** (opzionale): indica un tempo di
      attesa (ms) qualora il file per la generazione della risposta non
      sia immediatamente disponibile.

-  *Informazioni Dinamiche*. Per creare dei path dinamici rispetto alla
   transazione in corso di elaborazione, possono essere utilizzate le
   seguenti macro:

   -  **{date:FORMAT}** indica la data di elaborazione del messaggio. Il
      formato fornito deve essere conforme a quanto richiesto dalla
      classe java 'java.text.SimpleDateFormat'. Ad esempio:
      {date:yyyyMMdd\_HHmmssSSS}.

   -  **{transaction:id}** indica l'identificativo della transazione
      (UUID).

   -  **{busta:FIELD}** permette di utilizzare informazioni di
      protocollo riguardanti la transazione in corso. Il valore 'FIELD'
      fornito deve rappresentare un field valido all'interno della
      classe di openspcoop 'org.openspcoop2.protocol.sdk.Busta'. Ad
      esempio per ottenere il mittente della busta usare
      {busta:mittente}.

   -  **{header:NAME}** permette di utilizzare informazioni, relative
      alla transazione in corso, inserite negli header http generati da
      GovWay (maggiori dettagli in sezione :ref:`headerIntegrazione`). Il valore 'NAME' indica il nome
      dell'header da utilizzare. Ad esempio per utilizzare il nome del
      mittente è possibile usare {header:GovWay-Sender}. Un altro
      esempio valido nello scenario della fatturazione elettronica (sezione :ref:`profiloFatturaPA`)
      potrebbe essere quello di utilizzare il nome originale del file
      fattura utilizzando la sintassi {header:GovWay-SDI-NomeFile}

   -  **{query:NAME}** permette di utilizzare informazioni, relative
      alla transazione in corso, inserite nei query parameter aggiunti
      all'endpoint da GovWay (maggiori dettagli in sezione :ref:`headerIntegrazione`). Il valore 'NAME'
      indica il nome della proprietà da utilizzare. Ad esempio per
      utilizzare il nome del mittente è possibile usare
      {query:govway\_sender}.

   -  **{property:NAME}** permette di utilizzare informazioni, relative
      alla transazione in corso, specifiche della sezione relativa al
      profilo utilizzato all'interno della traccia (es. sezione
      'Informazioni Fatturazione Elettronica'). Il valore 'NAME' indica
      il nome della proprietà da utilizzare. Un esempio valido nello
      scenario della fatturazione elettronica (sezione :ref:`profiloFatturaPA`) potrebbe essere quello
      di utilizzare l'identificativo sdi utilizzando la sintassi
      {property:IdentificativoSdI}
