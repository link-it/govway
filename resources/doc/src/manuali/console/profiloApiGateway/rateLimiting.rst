.. _rateLimiting:

Rate Limiting
~~~~~~~~~~~~~

Questa sezione di configurazione, specifica per erogazioni e fruizioni
(o specifico gruppo di configurazione nell'ambito di
un'erogazione/fruizione), consente di attivare delle policy di rate
limiting specifiche per l'istanza configurata.

L'attivazione di policy di rate limiting rientra nell'ambito degli
strumenti per il controllo del traffico. La descrizione di dettaglio di
questi strumenti è presente nella sezione :ref:`traffico`, dove viene illustrato il meccanismo
per configurare le policy e più in dettaglio nella sezione :ref:`trafficoPolicy` riguardo
l'attivazione di policy a valenza globale.

Sono presenti alcune policy di rate limiting preconfigurate e pronte
all'uso. L'utente può crearne di nuove seguendo le indicazioni presenti
nella sezione :ref:`traffico`. Le policy preconfigurate sono le seguenti:

-  *NumeroRichieste-ControlloRealtimeGiornaliero*: La policy confronta
   il numero di richieste, sulla finestra giornaliera corrente, della
   specifica istanza di servizio, con la soglia stabilita.

-  *NumeroRichieste-ControlloRealtimeMinuti*: La policy confronta il
   numero di richieste, sulla finestra del minuto corrente, della
   specifica istanza di servizio, con la soglia stabilita.

-  *NumeroRichieste-ControlloRealtimeOrario*: La policy confronta il
   numero di richieste, sulla finestra oraria corrente, della specifica
   istanza di servizio, con la soglia stabilita.

-  *NumeroRichieste-RichiesteSimultanee*: La policy confronta il numero
   di richieste simultaneamente attive della specifica istanza di
   servizio, con la soglia stabilita.

-  *OccupazioneBanda-ControlloRealtimeOrario*: La policy confronta
   l'occupazione di banda della specifica istanza di servizio, sulla
   finestra oraria corrente, con la soglia stabilita.

-  *TempoMedioRisposta-ControlloRealtimeOrario*: La policy confronta il
   tempo medio di risposta della specifica istanza di servizio, sulla
   finestra oraria corrente, con la soglia stabilita.

.. note::
    L'attivazione di policy di Rate Limiting fa si che il Gateway generi
    gli header http descritti nella sezione :ref:`headerRisposta`.

Per attivare una nuova policy dalla sezione di rate limiting si procede
utilizzando il pulsante *Aggiungi* che apre il form di :numref:`erogazioneRateLimiting`.

   .. figure:: ../_figure_console/ErogazioneRateLimiting_Aggiungi.png
    :scale: 50%
    :align: center
    :name: erogazioneRateLimiting

    Attivazione di una policy di Rate Limiting

Si compilano i campi seguenti:

-  *Policy*: la policy da attivare. Si compone di:

   -  *Identificativo*: Identificativo univoco assegnato automaticamente
      all'istanza di policy.

   -  *Policy*: L'elemento del registro delle policy che si vuole
      attivare. Inizialmente saranno presenti solo le policy
      preconfigurate. Eventualmente saranno presenti in seguito le
      policy definite dall'utente

   -  *Nome*: Opzionale. Permette di identificare l’istanza della policy
      tramite un nome alternativo all’identificativo assegnato
      automaticamente dal sistema.

   -  *Descrizione*: Il testo di descrizione della policy.

   -  *Stato*: Lo stato dell'istanza di policy una volta creata. Sono
      disponibili le seguenti opzioni:

      -  *Abilitato*: L'istanza di policy è abilitata. Questo significa
         che le violazioni rilevate saranno gestite in maniera
         restrittiva (negazione del servizio).

      -  *WarningOnly*: L'istanza di policy è abilitata in modalità
         WarningOnly. Questo significa che le violazioni rilevate
         saranno solo segnalate tramite messaggi diagnostici ma non ci
         saranno ripercussioni sull'elaborazione della richiesta.

      -  *Disabilitato*: L'istanza di policy è disabilitata.

   -  *Ridefinisci Valori di Soglia*: Attivando questa opzione sarà
      possibile utilizzare una soglia per l'istanza di policy differente
      rispetto al valore di default previsto dalla policy d'origine.

-  *Filtro*: Abilitando questa sezione dell'istanza di policy è
   possibile indicare i criteri per stabilire quali richieste,
   nell'ambito della istanza in fase di configurazione, sono soggette
   alla policy che si sta istanziando. In assenza di filtro, l'istanza
   della policy sarà valutata su tutte le richieste in ingresso per la
   specifica istanza di servizio che si sta configurando.

   Per la creazione del filtro sono disponibili i seguenti campi:

   -  *Azione*: Opzione per filtrare le richieste in base all'azione
      invocata.

   -  *Soggetto Fruitore*: Opzione disponibile per le erogazioni al fine
      di filtrare le richieste di servizio in base al soggetto fruitore.

   -  *Applicativo Fruitore*: Opzione disponibile per le fruizioni al
      fine di filtrare le richieste di servizio in base all'applicativo
      fruitore

   -  *Filtro per Chiave*: Si tratta di un'opzione avanzata che consente
      di filtrare le richieste in ingresso sul gateway base ad una
      chiave che può essere specificata in maniera personalizzata
      effettuando una delle seguenti scelte per il campo *Tipologia*:

      -  *HeaderBased*: Occorre fornire i dati “Nome” e “Valore”. La
         policy si applicherà soltanto alle richieste che hanno,
         nell'header di trasporto, una proprietà che corrisponde.

      -  *URLBased*: Occorre fornire i dati “Espressione Regolare” e
         “Valore”. La policy si applicherà soltanto alle richieste ove,
         applicando l'espressione regolare alla URL di invocazione, si
         ottiene un valore identico a quello fornito.

      -  *FormBased*: Occorre fornire i dati “Nome” e “Valore”. La
         policy si applicherà soltanto alle richieste che contengono
         nella query string un parametro corrispondente ai dati forniti.

      -  *SOAPActionBased*: Occorre fornire il dato “Valore”. + La
         policy si applicherà soltanto alle richieste che si presentano
         con una SOAPAction avente il valore fornito.

      -  *ContentBased*: Occorre fornire i dati “Espressione XPath” e
         “Valore”. La policy si applicherà soltanto alle richieste dove,
         applicando l'espressione XPath al messaggio di richiesta, si
         ottiene un valore identico a quello fornito.

      -  *PluginBased*: Occorre fonire i dati “Tipo Personalizzato” e
         “Valore”. Il parametro “Tipo Personalizzato” è una chiave,
         registrata nella configurazione, cui corrisponde una classe
         java che restituisce un valore da confrontare con quello
         fornito. Per realizzare un plugin con una logica di filtro
         personalizzata è necessario fornire un'implementazione della
         seguente interfaccia:

         ::

             package org.openspcoop2.pdd.core.controllo_traffico.plugins; 
             public interface IRateLimiting {
                 public String estraiValoreFiltro(Logger log,Dati datiRichiesta) throws PluginsException;
                 public String estraiValoreCollezionamentoDati(Logger log,Dati datiRichiesta) throws PluginsException;
             }

         La classe realizzata viene successivamente registrata tramite
         una entry nel file *className.properties* di GovWay:

         ::

             org.openspcoop2.pdd.controlloTraffico.rateLimiting.test=<fully qualified class name>

         La stringa <nome>, fornita in configurazione, diventa
         utilizzabile come “Tipo Personalizzato”.

-  *Criterio di Collezionamento dei Dati*: In questa sezione è possibile
   attivare opzionalmente alcuni criteri per il raggruppamento dei dati
   utilizzati come indicatori di confronto per l'applicabilità della
   policy. Ad esempio se si è attivata una policy che limita a 20 il
   numero di richieste su una finestra di 5 minuti, significa che al
   raggiungimento della ventunesima richiesta, nella stessa finestra
   temporale, si otterrà una violazione della policy.

   Aggiungendo un criterio di collezionamento per Azione, saranno
   conteggiate separatamente le richieste in base alla specifica azione
   invocata. In questo caso la policy risulterà violata solo al
   raggiungimento della ventunesima richiesta, nella stessa finestra
   temporale, relativa alla medesima azione.

   È ammesso anche il raggruppamento su criteri multipli. La logica è
   del tutto analoga a quella dell'operatore GROUP BY del linguaggio
   SQL.

   I criteri di raggruppamento selezionabili sono:

   -  *Azione*

   -  *Soggetto Fruitore* (caso erogazioni)

   -  *Applicativo Fruitore* (caso fruizioni)

   -  *Raggruppamento per Chiave*: le richieste saranno raggruppate in
      base al valore di una chiave personalizzata il cui valore viene
      fornito secondo uno dei metodi selezionati tra i seguenti:

      -  *HeaderBased*: La chiave è presente nell'header di trasporto
         indicato nella proprietà "Nome".

      -  *URLBased*: La chiave è presente nella URL ricavandola tramite
         l'espressione regolare fornita nell'elemento seguente.

      -  *FormBased*: La chiave viene fornita in modalità Form Encoded
         con il parametro indicato nell'elemento "Nome".

      -  *SOAPActionBased*: La chiave corrisponde al valore della
         SoapAction.

      -  *ContentBased*: La chiave è presente nel body del messaggio e
         viene ricavata tramite il valore Xpath fornito nell'elemento
         seguente.

      -  *PluginBased*: La chiave viene restituita tramite l'esecuzione
         di una classe il cui nome viene fornito con il campo "Tipo
         Personalizzato"
