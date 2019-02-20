.. _trafficoPolicy:

Policy
^^^^^^

Le policy di rate limiting vengono definite ed inserite nel Registro
delle Policy secondo le modalità descritte nella sezione precedente. Le
policy presenti nel Registro delle Policy non sono attive fino a quando
non vengono istanziate.

Dalla pagina *Configurazione > Controllo del Traffico* selezionare il
collegamento *Policy*, nella sezione *Rate Limiting*, per accedere alle
policy istanziate (:numref:`policyRateLimitingList`).

   .. figure:: ../../_figure_console/ControlloTraffico-Policy-list.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingList

    Elenco delle policy di Rate Limiting attivate

La pagina indice delle Policy Istanziate mostra
l'elenco delle istanze già presenti e i pulsanti per le operazioni CRUD.

Tramite il pulsante *Aggiungi* è possibile aprire la pagina che contiene
il form di creazione di una nuova istanza di policy (:numref:`policyRateLimitingNew`).

   .. figure:: ../../_figure_console/ControlloTraffico-Policy-new.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingNew

    Creazione di una istanza relativa ad una policy di Rate Limiting

Dalla pagina di creazione dell'istanza di policy il primo passo è quello
di selezionare la policy di origine tra quelle disponibili nel Registro
delle Policy. Una volta selezionata la policy è visibile come il sistema
assegni automaticamente un identificativo univoco per l'istanza e mostri
quindi i rimanenti campi per completare la configurazione. Di seguito si
descrivono in dettaglio tali sezione di configurazione.

-  *Policy*: la policy da attivare. Si compone di:

   -  *Identificativo*: Identificativo univoco assegnato automaticamente
      all'istanza di policy.

   -  *Policy*: La policy su cui è basata l'istanza in fase di
      creazione.

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
      rispetto a quella prevista dalla policy d'origine.

-  *Filtro*: Abilitando questa sezione dell'istanza di policy è
   possibile indicare i criteri per stabilire quali richieste, in
   entrata sul gateway, sono soggette alla policy che si sta
   istanziando. In assenza di filtro, l'istanza della policy sarà
   valutata su tutte le richieste in ingresso.

   Per la creazione del filtro sono disponibili i seguenti campi (:numref:`policyRateLimitingFiltroFig`):

   -  *Stato*: Opzione per abilitare/disabilitare il filtro.

   -  *Ruolo Gateway*: Opzione per filtrare le richieste di servizio in
      base al ruolo ricoperto dal gateway nella specifica richiesta:
      Fruitore o Erogatore.

   -  *Modalità*: Opzione per filtrare le richieste di servizio in base
      alla modalità operativa. Nel caso si sia selezionata una singola
      modalità (o se il gateway supporta una sola modalità) viene
      visualizzato il valore attuale in modo non modificabile.

   -  *Ruolo Erogatore*: Opzione per filtrare le richieste di servizio
      in base al ruolo posseduto dal soggetto erogatore. Tramite la
      lista è possibile selezionare uno tra i ruoli censiti nel
      registro. La selezione di un ruolo esclude la possibilità di
      selezionare un soggetto erogatore.

   -  *Soggetto Erogatore*: Opzione per filtrare le richieste di
      servizio in base al soggetto erogatore. Tramite la lista è
      possibile selezionare uno tra i soggetti censiti nel registro. La
      selezione di un soggetto esclude la possibilità di selezionare un
      ruolo erogatore.

   -  *Servizio*: Opzione per filtrare le richieste di servizio in base
      al servizio invocato. Tramite la lista è possibile selezionare uno
      tra i servizi censiti nel registro. Se è stato selezionato un
      soggetto erogatore, saranno elencati solo i servizi da esso
      erogati. Analogamente, se è stata selezionata una modalità,
      saranno elencati solo i servizi appartenenti a quella modalità.

   -  *Azione*: Opzione per filtrare le richieste di servizio in base
      all'azione invocata. Tramite la lista è possibile selezionare una
      tra le azioni censite nel registro. Se è stato selezionato un
      servizio, saranno elencati solo le azioni ad esso appartenenti.

   -  *Applicativo Erogatore*: Opzione per filtrare le richieste di
      servizio in base all'applicativo erogatore (opzione non
      disponibile nel caso di una fruizione). Tramite la lista è
      possibile selezionare uno tra gli applicativi censiti nel
      registro. Se sono stati selezionati servizi e/o soggetti, la lista
      presentata sarà filtrata di conseguenza.

   -  *Ruolo Fruitore*: Opzione per filtrare le richieste di servizio in
      base al ruolo posseduto dal soggetto fruitore. Tramite la lista è
      possibile selezionare uno tra i ruoli censiti nel registro. La
      selezione di un ruolo esclude la possibilità di selezionare un
      soggetto fruitore.

   -  *Soggetto Fruitore*: Opzione per filtrare le richieste di servizio
      in base al soggetto fruitore. Tramite la lista è possibile
      selezionare uno tra i soggetti censiti nel registro. Se è stato
      selezionato un servizio, saranno elencati solo i soggetti fruitori
      del medesimo. La selezione di un soggetto esclude la possibilità
      di selezionare un ruolo fruitore.

   -  *Applicativo Fruitore*: Opzione per filtrare le richieste di
      servizio in base all'applicativo fruitore (opzione non disponibile
      nel caso di una erogazione). Tramite la lista è possibile
      selezionare uno tra i servizi applicativi censiti nel registro. Se
      sono stati selezionati servizi e/o soggetti, la lista presentata
      sarà filtrata di conseguenza.

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

.. note::
   È possibile specificare più di un criterio di filtro; la
   logica applicata sarà quella dell'operatore AND.

   .. figure:: ../../_figure_console/ControlloTraffico-Filtro.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingFiltroFig

    Definizione del filtro per l’istanza della policy di rate limiting


-  *Criterio di Collezionamento dei Dati*: In questa sezione è possibile
   attivare opzionalmente alcuni criteri per il raggruppamento dei dati
   utilizzati come indicatori di confronto per l'applicabilità della
   policy. Ad esempio se si è attivata una policy che limita a 20 il
   numero di richieste su una finestra di 5 minuti, significa che al
   raggiungimento della ventunesima richiesta, nella stessa finestra
   temporale, si otterrà una violazione della policy.

   Aggiungendo un criterio di collezionamento per Soggetto Erogatore,
   saranno conteggiate separatamente le richieste destinate ad ogni
   singolo soggetto erogatore. In questo caso la policy risulterà
   violata solo al raggiungimento della ventunesima richiesta, nella
   stessa finestra temporale, destinata al medesimo soggetto erogatore.

   È ammesso anche il raggruppamento su criteri multipli. La logica è
   del tutto analoga a quella dell'operatore GROUP BY del linguaggio
   SQL.

   I criteri di raggruppamento selezionabili sono (:numref:`policyRateLimitingCollezionamento`):

   -  *Ruolo (Fruitore/Erogatore)*

   -  *Soggetto Erogatore*

   -  *Servizio*

   -  *Azione*

   -  *Applicativo Erogatore*

   -  *Soggetto Fruitore*

   -  *Applicativo Fruitore*

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

   .. figure:: ../../_figure_console/ControlloTraffico-Collezionamento.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingCollezionamento

    Definizione criteri di raggruppamento per l’istanza della policy di rate limiting
