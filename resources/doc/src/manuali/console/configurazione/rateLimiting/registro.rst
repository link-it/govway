.. _registroPolicy:

Registro Policy
^^^^^^^^^^^^^^^

Il Registro delle Policy è il repository dove si possono creare le
policy di rate limiting che potranno essere successivamente istanziate.
L'accesso alla sezione è possibile grazie all'omonimo collegamento
presente nella sezione *Rate Limiting* della pagina principale del
controllo del traffico.

La pagina indice del Registro delle Policy mostra l'elenco delle policy
già presenti (:numref:`policyList`).

   .. figure:: ../../_figure_console/ControlloTraffico-RegistroPolicy-list.png
    :scale: 100%
    :align: center
    :name: policyList

    Elenco delle Policy di Rate Limiting presenti nel registro

Tramite il pulsante “Aggiungi” è possibile aprire la
pagina di creazione di una policy di Rate Limiting (:numref:`policyNew`).

   .. figure:: ../../_figure_console/ControlloTraffico-RegistroPolicy-new.png
    :scale: 100%
    :align: center
    :name: policyNew

    Maschera per la creazione di una policy di Rate Limiting

Descriviamo nel
seguito i dati che è necessario inserire per la creazione di una policy.
Si tenga presente che il sistema propone valori di default per alcuni
campi; tali valori cambiano in base alle scelte operate sugli altri
campi e possono essere considerati come “consigliati” in base alla
combinazione di scelte attuate.

-  *Policy*: In questa sezione sono presente i dati che identificano la
   policy.

   -  *Nome*: Nome assegnato alla policy. Finché il campo non viene
      modificato dall'utente, viene proposto automaticamente un nome
      espressivo sulla base delle scelte operate sui rimanenti elementi
      del form.

   -  *Descrizione*: Un testo di descrizione riferito alla policy.
      Finché il campo non viene modificato dall'utente, viene proposto
      un testo automatico di descrizione sulla base delle scelte operate
      sui rimanenti elementi del form.

   -  *Metrica*: Si seleziona la metrica che la policy deve monitorare
      al fine di attuare le eventuali restrizioni. Sono disponibili le
      seguenti risorse:

      -  *NumeroRichieste*: La policy effettua il controllo sul numero
         di richieste gestite. Selezionando questa risorsa si
         attiveranno i seguenti elementi per la configurazione dei
         valori di soglia:

         -  *Modalità di Controllo*

         -  *Numero Massimo di Richieste*

         -  *Frequenza Intervallo Osservazione*

         -  *Intervallo Osservazione*

         -  *Finestra Osservazione*

      -  *NumeroRichiesteSimultanee*: La policy effettua il controllo sul numero
         di richieste simultanee gestite. Selezionando questa metrica si
         attiveranno i seguenti elementi per la configurazione dei
         valori di soglia:

         -  *Numero Massimo di Richieste*

      -  *Dimensione Massima Messaggi*: La policy limita la dimensione massima, in KB, consentita per una richiesta e/o per una risposta.
	 Selezionando questa metrica siattiveranno i seguenti elementi per la configurazione dei
         valori di soglia:

         -  *Dimensione Richiesta*
	
	 -  *Dimensione Risposta*

      -  *OccupazioneBanda*: La policy effettua il controllo sulla banda
         occupata da e verso le comunicazioni con il gateway.
         Selezionando questa risorsa si attiveranno i seguenti elementi
         per la configurazione dei valori di soglia:

         -  *Modalità di Controllo*

         -  *Tipo Banda*

         -  *Occupazione Massima di Banda (kb)*

         -  *Frequenza Intervallo Osservazione*

         -  *Intervallo Osservazione*

         -  *Finestra Osservazione*

      -  *TempoComplessivioRisposta*: La policy controlla la quantità di
         tempo complessivamente impiegata dal gateway per la ricezione
         delle risposte dai servizi invocati. Selezionando questa
         metrica si attiveranno i seguenti elementi per la
         configurazione dei valori di soglia:

         -  *Modalità di Controllo su Realtime (non modificabile)*

         -  *Tipo Latenza*

         -  *Occupazione Massima di Tempo (secondi)*

         -  *Frequenza Intervallo Osservazione*

         -  *Intervallo Osservazione*

         -  *Finestra Osservazione*

      -  *TempoMedioRisposta*: La policy controlla il tempo medio
         impiegato dal gateway per la ricezione delle risposte dai
         servizi invocati. Selezionando questa metrica si attiveranno i
         seguenti elementi per la configurazione dei valori di soglia:

         -  *Modalità di Controllo*

         -  *Tipo Latenza*

         -  *Tempo Medio Risposta (ms)*

         -  *Frequenza Intervallo Osservazione*

         -  *Intervallo Osservazione*

         -  *Finestra Osservazione*

      -  *NumeroRichiesteCompletateConSuccesso*

         *NumeroRichiesteFallite*

         *NumeroFaultApplicativi*

         La policy effettua il controllo sul numero di richieste gestite
         dal gateway e terminate con un esito che rientra nella
         casistica associata alla risorsa selezionata (completate con
         successo, fallite o fault applicativi). Selezionando questa
         metrica si attiveranno i seguenti elementi per la
         configurazione dei valori di soglia:

         -  *Modalità di Controllo*

         -  *Numero Massimo di Richieste*

         -  *Frequenza Intervallo Osservazione*

         -  *Intervallo Osservazione*

         -  *Finestra Osservazione*

-  *Valori di Soglia*: In questa sezione si specificano i valori di
   soglia (già anticipati al punto precedente), superati i quali, la
   policy risulta violata. Alcuni campi presenti in questa sezione
   cambiano in base alla risorsa monitorata.

   -  *Simultanee*: Questa opzione è presente solo per la risorsa
      “NumeroRichieste”. Attivandola si specifica che il criterio
      restrittivo entra in funzione al superamento di una soglia sul
      numero di richieste simultaneamente in gestione.

   -  *Modalità di Controllo*: Rappresenta la modalità di raccolta dei
      dati di traffico che saranno usati per la valutazione della
      policy. Si può scegliere tra le seguenti opzioni:

      -  *Realtime*: L'indicatore utilizzato per valutare la policy
         viene calcolato sulla base di dati raccolti in tempo reale
         durante l'elaborazione. Questa modalità assicura la massima
         accuratezza ma occorre tenere presenti le seguenti restrizioni
         nell'uso:

         1. I dati “realtime” vengono raccolti in maniera separata sui
            singoli nodi del cluster. Quindi il controllo effettuato
            dalla policy riguarderà il traffico sul singolo nodo.

         2. Si possono impostare criteri di controllo su grana temporale
            piccola: secondi, minuti, orario, giornaliero.

      -  *Statistica*: L'indicatore utilizzato per valutare la policy
         viene calcolato sulla base delle informazioni statistiche
         presenti nel database di monitoraggio. L'accuratezza dei dati
         utilizzati per la valutazione è subordinata alla frequenza di
         aggiornamento dei dati statistici sul database. Inoltre tale
         modalità richiede il tracciamento delle transazioni sulle quali
         viene poi calcolata la statisticha (vedi sezione :ref:`tracciamento`). In questa
         modalità:

         1. L'indicatore utilizzato per il confronto con la soglia della
            policy è sempre complessivo rispetto a tutti i nodi del
            cluster.

         2. Si possono impostare criteri di controllo con grana
            temporale ampia: orario, giornaliero, settimanale, mensile.

         3. Si può utilizzare la tipologia “finestra scorrevole” come
            valore per la “Finestra Osservazione”, che descriveremo poco
            più avanti.

   -  *Numero Massimo di Richieste*: Campo visibile solo per la metrica “NumeroRichieste”. Consente di specificare la soglia
      per la policy. Quando il numero delle richieste, conteggiate
      secondo la logica specificata nella policy, supera questo valore,
      la policy risulta violata.

   -  *Tipo Banda*: Campo visibile solo per la metrica
      “OccupazioneBanda”. Consente di specificare la modalità di calcolo
      della banda occupata per il confronto con la soglia impostata
      nella policy. Sono disponibili le seguenti opzioni:

      -  *Banda Interna*: Ai fini del conteggio dell'occupazione di
         banda (in KB) verrà considerato il solo traffico relativo alle
         comunicazioni con gli applicativi interni al dominio.

      -  *Banda Esterna*: Ai fini del conteggio dell'occupazione di
         banda (in KB) verrà considerato il solo traffico relativo alle
         comunicazioni con i servizi esterni al dominio.

      -  *Banda Complessiva*: Ai fini del conteggio dell'occupazione di
         banda (in KB) verrà considerato tutto il traffico in entrata ed
         uscita sul gateway.

   -  *Occupazione Massima di Banda (kb)*: Campo visibile solo per la
      metrica “OccupazioneBanda”. Consente di specificare la
      soglia per la policy. Quando la banda, calcolata secondo la logica
      specificata nella policy, supera questo valore, la policy risulta
      violata.

   -  *Tipo Latenza*: Campo visibile solo per le metriche
      “TempoComplessivoRisposta” e “TempoMedioRisposta”. Consente di
      specificare la logica di calcolo del tempo di risposta sulla base
      delle due seguenti opzioni:

      -  *Latenza Servizio*: Per il calcolo del tempo di risposta si
         considera unicamente il tempo di attesa del gateway dall'invio
         della richiesta alla ricezione della risposta.

      -  *Latenza Totale*: Per il calcolo del tempo di risposta si
         considera, oltre alla latenza del servizio, anche il tempo di
         elaborazione del gateway dal momento dell'ingresso della
         richiesta fino all'uscita della risposta.

   -  *Occupazione Massima di Tempo (secondi)*: Campo visibile solo per
      la metrica “TempoComplessivoRisposta”. Consente di
      specificare la soglia per la policy. Quando la latenza
      complessiva, calcolata secondo la logica specificata nella policy,
      supera questo valore, la policy risulta violata.

   -  *Tempo Medio Risposta (ms)*: Campo visibile solo per la metrica
      “TempoMedioRisposta”. Consente di specificare la soglia
      per la policy. Quando la latenza media, calcolata secondo la
      logica specificata nella policy, supera questo valore, la policy
      risulta violata.

   -  *Frequenza Intervallo Osservazione*

      *Intervallo Osservazione*

      *Finestra Osservazione*

      La composizione di questi 3 campi specifica in quale intervallo
      temporale devono essere selezionati i dati da utilizzare per
      calcolare l'indicatore che deve essere confrontato con la soglia
      della policy.

      I valori di “Frequenza Intervallo Osservazione” e “Intervallo
      Osservazione” specificano la frequenza di campionamento dei dati
      utilizzati per la valutazione delle soglie. In particolare il
      valore da specificare come Intervallo Osservazione è sempre un
      numero intero (ad esempio inserendo 8 si campioneranno i dati su
      finestre di 8 secondi, 8 minuti, ecc, in base all'unità di misura
      indicata per la frequenza). Il valore selezionato come “Finestra"
      individua l'esatto intervallo utilizzato nella
      catena temporale ogni volta che si valuta la policy per una
      specifica richiesta di servizio.

      Per comprendere la logica con cui viene calcolata la finestra di
      osservazione è necessario introdurre il concetto di Data
      Attivazione Policy. Si tratta della data in cui la policy è stata
      applicata ad una richiesta in transito sul gateway. A partire da
      questa data vengono calcolate le finestre di osservazione in base
      alla frequenza di campionamento selezionata.

      In :numref:`finestreCampionamento` è mostrato un confronto tra le diverse finestre di
      osservazione su un campionamento di 2 ore. La determinazione della
      finestra può essere analogamente trasposta su altre frequenze di
      campionamento.

      Riepilogando:

      -  *Corrente*: Indica che per il calcolo dell'indicatore saranno
         utilizzati i dati che rientrano nella finestra temporale in cui
         ricade la richiesta in esame.

      -  *Precedente*: Indica che per il calcolo dell'indicatore saranno
         utilizzati i dati che rientrano nella finestra temporale
         precedente a quella in cui ricade la richiesta in esame.

      -  *Scorrevole (disponibile solo nella Modalità Controllo
         “Statistica”)*: Indica che per il calcolo dell'indicatore
         saranno utilizzati i dati che rientrano in una finestra
         dinamica che ha come estremo superiore l'ora piena subito
         precedente all'istante della richiesta in fase di valutazione.

   .. figure:: ../../_figure_console/ControlloTraffico-Finestre.png
    :scale: 100%
    :align: center
    :name: finestreCampionamento

    Finestre di osservazione su un campionamento di 2 ore

-  *Applicabilità*: Questa sezione della policy consente di restringere
   l'applicabilità della policy sulla base di alcuni criteri (:numref:`opzioniRateLimitingFig`). Sono presenti i seguenti campi:

   -  *Condizionale*: Se questa opzione non è attiva, la policy si
      applica in maniera incondizionata. Attivando l'opzione, la policy
      risulterà applicabile sulla base dei criteri specificati nei campi
      successivi.

   -  *In presenza di Congestione del Traffico*: Attivando questa
      opzione la policy risulta applicabile solo quando sussiste lo
      stato di congestionamento. Affinché questo evento venga rilevato è
      necessario che sia abilitato il “Controllo della Congestione”,
      descritto in precedenza, e che risulti superata la soglia
      impostata sul numero di richieste simultanee.

   -  *In presenza di Degrado Prestazionale*: Attivando questa opzione,
      la policy risulta applicabile solo in caso si rilevi un degrado
      prestazionale sullo specifico servizio corrispondente alla
      richiesta in gestione sul gateway. Per la rilevazione del degrado
      prestazionale si utilizzano le soglie “Tempo Medio di Risposta”
      impostate sia per le fruizioni che per le erogazioni. Come
      descritto in precedenza, tali soglie vengono definite per default
      nella sezione “Configurazione > Controllo del Traffico”, ma
      possono essere ridefinite al livello del singolo connettore. Per
      il calcolo del tempo medio di risposta del servizio, da
      confrontare con la soglia impostata, si utilizza il criterio
      definito con i campi seguenti:

      -  *Modalità di Controllo*

      -  *Tempo Medio Risposta*

      -  *Frequenza Intervallo Osservazione*

      -  *Intervallo Osservazione*

      -  *Finestra Osservazione*

      Per tutti questi campi valgono le medesime descrizioni già
      riportate nella sezione precedente “Valori di Soglia”.

   .. figure:: ../../_figure_console/ControlloTraffico-Applicabilita.png
    :scale: 100%
    :align: center
    :name: opzioniRateLimitingFig

    Opzioni per l’applicabilità di una policy di rate limiting

.. note::
   Se si selezionano più opzioni di applicabilità queste si
   considerano connesse secondo l'operatore logico AND.
