.. _erogazione:

Registrazione dell'erogazione
-----------------------------

Una volta disponibile la definizione delle API, si passa alla
registrazione dell'erogazione fornendo i dati di base per l'esposizione
del servizio erogato tramite GovWay. In :numref:`scenarioErogazione` è illustrato graficamente il
caso dell'erogazione.

   .. figure:: ../_figure_console/ErogazioneScenario.png
    :scale: 100%
    :align: center
    :name: scenarioErogazione

    Scenario di riferimento per l’erogazione


Per registrare l'erogazione del servizio ci si posiziona nella sezione
*Registro > Erogazioni* e si preme il pulsante *Aggiungi*.

   .. figure:: ../_figure_console/Erogazione-new.png
    :scale: 100%
    :align: center
    :name: erogazioneNewFig

    Registrazione di una Erogazione

Compilare il form (:numref:`erogazioneNewFig`) inserendo i seguenti dati:

-  *API - Nome:* Selezionare dall'elenco il nome e la versione relativa
   alla API cui l'erogazione fa riferimento. Se la API selezionata è di
   tipo Soap, sarà necessario selezionare anche il Servizio che si vuole
   erogare.

-  *Autenticazione:* In questa sezione è possibile configurare il
   meccanismo di autenticazione richiesto per l'accesso al servizio da
   parte dei fruitori. Il valore di default proposto prevede
   l'autenticazione di tipo *https*.

   Come mostrato in :numref:`erogazioneNewFig`, è possibile selezionare il tipo di autenticazione
   a livello del trasporto, selezionando uno tra i valori disponibili:

   -  *disabilitato*: nessuna autenticazione

   -  *ssl*: autenticazione ssl

   -  *basic*: autenticazione http-basic

   -  *principal*: autenticazione sull'application server con
      identificazione tramite principal

   -  *custom*: metodo di autenticazione fornito tramite estensione di
      GovWay

   Il flag *Opzionale* consente di non rendere bloccante il superamento
   dell'autenticazione per l'accesso al servizio.

-  *Connettore:* In questa sezione devono essere specificati i
   riferimenti al servizio, al fine di rendere possibile il corretto
   instradamento delle richieste inviate dai soggetti fruitori. Questo
   connettore riferisce il servizio del dominio interno che si sta
   erogando.

   Le informazioni da fornire sono:

   -  *Endpoint:* la url per la consegna delle richieste al servizio.

   -  *Autenticazione Http:* credenziali da fornire nel caso in cui il
      servizio richieda autenticazione di tipo HTTP-BASIC.

   -  *Autenticazione Https:* credenziali da fornire nel caso in cui il
      servizio richieda autenticazione di tipo HTTPS.

   -  *Proxy:* nel caso in cui l'endpoint del servizio sia raggiungibile
      solo attraverso un proxy, possono essere indicati qui i relativi
      riferimenti.

   -  *Ridefinisci Tempi Risposta:* permette di ridefinire i tempi di
      risposta che sono stati configurati a livello generale,
      nell'ambito del controllo del traffico (vedi sezione :ref:`console_tempiRisposta`)

Completamento configurazione e indirizzamento del servizio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dopo aver definito le API e registrato la relativa erogazione, come
descritto nelle sezioni precedenti, si dispone della configurazione di
un servizio erogato i cui riferimenti possono essere comunicati ai
fruitori.

Per aggiungere ulteriori dettagli di configurazione, o semplicemente per
conoscere il giusto endpoint cui il fruitore deve indirizzare le
richieste, si procede dalla pagina di dettaglio dell'erogazione già
creata. Il dettaglio dell'erogazione si raggiunge andando alla sezione
del menu *Registro > Erogazioni*, cliccando sull'elemento visualizzato
nell'elenco delle erogazioni presenti nel registro (:numref:`erogazioneList`).

   .. figure:: ../_figure_console/ListaErogazioni.png
    :scale: 100%
    :align: center
    :name: erogazioneList

    Elenco Erogazioni presenti nel registro

.. note::
    Le erogazioni in elenco sono visualizzate con un'icona colorata
    affiancata al nome. L'icona di colore rosso indica che l'erogazione
    è disabilitata. L'icona di colore giallo indica che solo alcuni
    gruppi di risorse/azioni sono abilitati all'uso. L'icona verde
    indica lo stato abilitato.

Per la ricerca dell'elemento nell'elenco delle erogazioni è possibile
filtrare i dati visualizzati tramite la maschera di filtro che compare
cliccando sulla voce *Erogazioni* nell'intestazione dell'elenco (:numref:`filtroErogazioni`).

   .. figure:: ../_figure_console/FiltroErogazioni.png
    :scale: 100%
    :align: center
    :name: filtroErogazioni

    Filtro delle Erogazioni presenti nel registro


Il dettaglio dell'erogazione mostra i dati principali e con le icone
"matita" è possibile entrare sulle maschere di editing per effettuare
delle modifiche. In corrispondenza della API riferita, è possibile
accedere al relativo dettaglio aprendo un nuovo tab del browser (:numref:`dettaglioErogazione`).


   .. figure:: ../_figure_console/DettaglioErogazione.png
    :scale: 100%
    :align: center
    :name: dettaglioErogazione

    Dettaglio dell’erogazione


La pagina di dettaglio dell'erogazione comprende inoltre i seguenti
elementi:

-  *Gestione Configurazione*: link per accedere alla configurazione
   specifica per l'aggiunta di ulteriori funzionalità all'erogazione
   (vedi sezione :ref:`configSpecifica`).

-  *Gestione Gruppi Risorse/Azioni*: link per differenziare la
   configurazione specifica sulla base di diversi gruppi di
   azioni/risorse, come meglio spiegato alla sezione :ref:`configSpecificaRisorsa`.

Dal dettaglio dell'erogazione si ricava il valore *URL Invocazione* che
rappresenta l'endpoint da comunicare ai fruitori per contattare il
servizio. Questo dato rappresenta la *URL* del servizio nel caso Soap o
la *Base URL* nel caso Rest.

Per la selezione dell'operazione da invocare si distinguono i seguenti
casi:

-  *REST*: Indipendentemente che l'API sia stata configurata fornendo il
   relativo descrittore, WADL o OpenAPI, l'identificazione
   dell'operation sarà sempre effettuata in automatico dal contesto di
   invocazione. Non è quindi necessario fornire ulteriori indicazioni.

-  *SOAP*

   -  *API con WSDL*: l'operation viene automaticamente identificata dal
      contesto di invocazione grazie alle informazioni presenti nel
      descrittore.

   -  *API senza WSDL*: l'operation viene identificata inserendo il
      relativo identificativo nella URL di invocazione:

      -  <URL\_Invocazione>/<Azione>

      Sono disponibili ulteriori metodi per l'identificazione
      dell'operation nel caso SOAP, per i cui dettagli si rimanda alla
      sezione :ref:`identificazioneAzione`.

Condivisione dei dati di integrazione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le richieste di erogazione, pervenute a GovWay, vengono elaborate e, nel
corso dell'operazione, vengono creati i riferimenti alle entità di
configurazione presenti nel registro.

GovWay comunica i dati di contesto ricavati, ai sistemi interlocutori,
ed in particolare:

-  Tutti i dati dell'header di integrazione, relativi al messaggio di
   richiesta, vengono inviati all'applicativo destinatario (erogatore).
   I dati che compongono l'header di integrazione sono quelli descritti
   nelle tabelle presenti alla sezione :ref:`headerIntegrazione`.

-  Un sottoinsieme dell'header di integrazione, relativo al messaggio di
   risposta, viene inviato al soggetto mittente (fruitore). I dati
   inviati (sempre in riferimento alle tabelle della :ref:`headerIntegrazione`) sono:

   -  *GovWay-Message-ID*

   -  *GovWay-Relates-To*

   -  *GovWay-Conversation-ID*

   -  *GovWay-Transaction-ID*

Errori Generati dal Gateway
~~~~~~~~~~~~~~~~~~~~~~~~~~~

La gestione dei casi di errore nelle comunicazioni mediate da un Gateway
devono tenere conto di ulteriori situazioni che possono presentarsi
rispetto alla situazione di dialogo diretto tra gli applicativi. Oltre
agli errori conosciuti dagli applicativi, e quindi previsti nei
descrittori del servizio, gli applicativi client possono ricevere
ulteriori errori generati dal gateway.

Govway genera differenti errori a seconda se l'erogazione o la fruizione
riguarda una API di tipologia SOAP o REST.

-  *REST*: viene generato un oggetto *Problem Details* come definito
   nella specifica *RFC 7807* (https://tools.ietf.org/html/rfc7807).
   Ulteriori dettagli vengono descritti nella sezione :ref:`rfc7807`.

-  *SOAP*: viene generato un SOAPFault contenente un actor (o role in
   SOAP 1.2) valorizzato con *http://govway.org/integration*.
   Nell'elemento *fault string* è presente il dettaglio dell'errore
   mentre nell'elemento *fault code* una codifica di tale errore.
   Ulteriori dettagli vengono descritti nella sezione :ref:`soapFault`.
