.. _tracciamento:

Tracciamento
------------

Accedendo la sezione *Configurazione > Tracciamento* si possono
configurare i dettagli per la registrazione delle informazioni inerenti
gli scambi sui servizi gestiti dal gateway. In particolare il gateway è
in grado di memorizzare le seguenti tipologie di informazioni:

-  *Transazioni*: tutte le proprietà inerenti il contesto di invocazione
   dei servizi (dati di indirizzamento, esito, tempi di
   elaborazione,...)

-  *Messaggi Diagnostici*: tutte le informazioni necessarie per
   comprendere la fase di elaborazione delle richieste e indagare sulle
   anomalie occorse

-  *Messaggi Applicativi*: salvataggio dei messaggi in transito sulle
   singole comunicazioni

In :numref:`tracciamentoFig` è mostrata la pagina di configurazione del servizio di
tracciamento.

   .. figure:: ../_figure_console/Tracciamento.png
    :scale: 50%
    :align: center
    :name: tracciamentoFig

    Configurazione del servizio di tracciamento

Vediamo il significato delle sezioni di questa pagina:

-  *Transazioni Registrate*: questa sezione consente di specificare
   quali transazioni memorizzare nell'archivio di monitoraggio in base
   all'esito rilevato in fase di elaborazione. Gli esiti sono suddivisi
   nei seguenti gruppi: Completate con successo, Fault applicativo,
   Fallite, Scartate, Violazione Policy Rate Limiting e Superamento Limite Richieste Complessive. Per ciascun esito è possibile
   abilitare o disabilitare la registrazione. È possibile inoltre,
   scegliendo l'opzione *Personalizzato* specificare puntualmente quali
   esiti di dettaglio includere.

-  *Messaggi Diagnostici*: questa sezione consente di specificare il
   livello di verbosità dei messaggi diagnostici da generare. Si può
   distinguere il livello di verbosità per il salvataggio su *Database*
   e su *File*.

-  *Registrazione Messaggi*: questa sezione consente di abilitare e
   configurare la registrazione dei messaggi in transito sul gateway
   durante l'elaborazione delle richieste e relative risposte. Una volta
   abilitata l'opzione si possono configurare i dettagli della
   funzionalità tramite il link *Configurazione*.

   Dalla sottosezione di configurazione si può distinguere il criterio
   di registrazione dei messaggi tra la Richiesta e la Risposta,
   abilitando/disabilitando solo la comunicazione desiderata. Sia per la
   Richiesta che per la Risposta, dopo aver optato per l'abilitazione
   della registrazione, si distingue tra:

   -  *Ingresso*: il messaggio di richiesta o risposta nel momento in
      cui giunge sul gateway e quindi prima che venga sottoposto al
      processo di elaborazione previsto.

   -  *Uscita*: il messaggio di richiesta o risposta nel momento in cui
      esce dal gateway, per raggiungere il nodo successivo del flusso, e
      quindi dopo che è stato sottoposto al processo di elaborazione
      previsto.

   Per ciascuno dei messaggi, su cui è stata abilitata la registrazione,
   è possibile scegliere quale elemento viene registrato:

   -  *Headers*: vengono salvati gli header di trasporto (HTTP HEADERS)
      associati al messaggio.

   -  *Body*: viene salvato il corpo del messaggio.

   -  *Attachments*: vengono salvati gli eventuali attachments presenti
      nel messaggio.

.. note::
    Le configurazioni effettuate in questa sezione della console hanno
    valenza globale e quindi rappresentano il comportamento di default
    adottato dal gateway nella gestione dei diversi flussi di
    comunicazione. Tale comportamento può essere ridefinito puntualmente
    su ogni singola erogazione/fruizione agendo sulla voce di
    configurazione *Tracciamento* in quel contesto.
