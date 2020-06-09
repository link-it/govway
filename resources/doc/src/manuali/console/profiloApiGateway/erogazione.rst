.. _erogazione:

Registrazione dell'erogazione
-----------------------------

Una volta disponibile la definizione delle API, si passa alla
registrazione dell'erogazione fornendo i dati di base per l'esposizione
del servizio erogato tramite GovWay. In :numref:`scenarioErogazione` è illustrato graficamente il
caso dell'erogazione.

   .. figure:: ../_figure_console/ErogazioneScenario.png
    :scale: 80%
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

-  *Controllo degli Accessi:* In questa sezione è possibile stabilire l'eventuale controllo degli accessi all'erogazione:
    - *Pubblico*: non sono richieste credenziali per l'accesso.
    - *Autenticato*:  l'accesso è ammesso solo previa verifica dei criteri di autenticazione e autorizzazione previsti in configurazione.

    Selezionando l'opzione *Autenticato*, dopo la creazione dell'erogazione, sarà necessario completare la configurazione del controllo degli accessi come descritto nella sezione :ref:`apiGwAutenticazione`.

-  *Connettore:* In questa sezione devono essere specificati i
   riferimenti al servizio, al fine di rendere possibile il corretto
   instradamento delle richieste inviate dai soggetti fruitori. Questo
   connettore riferisce il servizio del dominio interno che si sta
   erogando.

   Le informazioni da fornire sono:

   -  *Utilizza Applicativo Server:* flag che consente di selezionare un applicativo di tipo "Server" invece di fornire tutte le informazioni relative al connettore. Per i dettagli consultare la sezione :ref:`configSpecificaConnettore`.

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

.. note::
    Se l'API riferita dall'erogazione possiede un descrittore (WSDL, OpenAPI, ecc.) l'interfaccia propone come valore di default per il connettore l'endpoint del servizio.

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

.. note::**Icona di Stato**

    Le erogazioni in elenco sono visualizzate con un'icona colorata
    affiancata al nome. L'icona di colore rosso indica che l'erogazione
    è disabilitata. L'icona di colore giallo indica che solo alcuni
    gruppi di risorse/azioni sono abilitati all'uso. L'icona verde
    indica lo stato abilitato.

.. note::**Tags**

    A fianco del nome può accadere che venga visualizzato l'elenco dei tags che sono assegnati all'API cui l'erogazione fa riferimento.

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
delle modifiche.
In corrispondenza del connettore è disponibile anche un pulsante che consente di verificare la raggiungibilità dell'indirizzo impostato.
In corrispondenza della API riferita, è possibile
accedere al relativo dettaglio aprendo un nuovo tab del browser (:numref:`dettaglioErogazione`).


   .. figure:: ../_figure_console/DettaglioErogazione.png
    :scale: 100%
    :align: center
    :name: dettaglioErogazione

    Dettaglio dell’erogazione


La pagina di dettaglio dell'erogazione visualizza i principali elementi di configurazione, che sono:

    - **Nome**: nome dell'erogazione. Accanto al valore è presente l'icona a matita che consente di modificare tale valore. In assenza di configurazioni specifiche per risorsa/azione (sezione :ref:`configSpecificaRisorsa`) è presente anche un'icona che permette di disattivare/riattivare l'erogazione. Lo stato di attivazione dell'erogazione è segnalato tramite l'icona colorata presente accanto al nome.
    - **API**: API cui fa riferimento l'erogazione con evidenza degli eventuali tags. È presente un'icona che apre in una nuova finestra l'interfaccia per la gestione della configurazione della specifica API.
    - **URL Invocazione**: URL che deve utilizzare il mittente per accedere al servizio erogato tramite il gateway. Questo dato rappresenta la *URL* del servizio nel caso Soap o la *Base URL* nel caso Rest. Per la selezione dell'operazione da invocare si distinguono i seguenti casi:
        -  *REST*: Indipendentemente che l'API sia stata configurata fornendo il relativo descrittore, WADL o OpenAPI, l'identificazione dell'operation sarà sempre effettuata in automatico dal contesto di invocazione. Non è quindi necessario fornire ulteriori indicazioni.
        -  *SOAP*
           -  *API con WSDL*: l'operation viene automaticamente identificata dal contesto di invocazione grazie alle informazioni presenti nel descrittore.
           -  *API senza WSDL*: l'operation viene identificata inserendo il relativo identificativo nella URL di invocazione, <URL\_Invocazione>/<Azione>

          Sono disponibili ulteriori metodi per l'identificazione dell'operation nel caso SOAP, per i cui dettagli si rimanda alla sezione :ref:`identificazioneAzione`.
    - **Connettore**: Endpoint del servizio acceduto dal gateway, cui verranno consegnate le richieste pervenute. È presente l'icona a matita per aggiornare il valore del connettore. È inoltre presente un'icona che consente di testare la raggiungibilità del servizio tramite il connettore fornito. Maggiori dettagli vengono forniti nella sezione :ref:`configSpecificaConnettore`.
    - **Gestione CORS**: stato abilitazione della funzione CORS. L'icona a matita consente di modificare l'impostazione corrente come descritto nella sezione :ref:`configSpecificaCORS`.

Ulteriori elementi possono essere indicati per specificare il funzionamento dell'erogazione. Si tratta degli elementi di configurazione specifica, per i cui dettagli si rimanda alla sezione :ref:`configSpecifica`.

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

La gestione dei casi di errore, nelle comunicazioni mediate da un Gateway, deve tener conto di ulteriori casi di errore che possono presentarsi rispetto al dialogo diretto tra gli applicativi.
Oltre agli errori già previsti nei descrittori del servizio, gli applicativi client possono pertanto ricevere due tipi di errori generati direttamente da GovWay:

- *Errori Client*: sono identificabili da un codice http 4xx su API REST o da un fault code 'Client' su API SOAP. Indicano che GovWay ha rilevato problemi nella richiesta effettuata dal client (es. errore autenticazione, autorizzazione, validazione contenuti...).

- *Errori Server*: sono identificabili dai codici http 502, 503 e 504 per le API REST o da un fault code 'Server' generato dal Gateway e restituito con codice http 500 per le API SOAP.

Per ciascun errore GovWay riporta le seguenti informazioni:

- Un codice http su API REST o un fault code su API SOAP come descritto in precedenza.
- Un codice di errore, indicato nell'header http 'GovWay-Transaction-ErrorType', che riporta l'errore rilevato dal gateway (es. AuthenticationRequired, TokenExpired, InvalidRequestContent ...). 
- Un identificativo di transazione, indicato nell'header http 'GovWay-Transaction-ID', che identifica la transazione in errore, utile principalmente per indagini diagnostiche.
- Un payload http, contenente maggiori dettagli sull'errore, opportunamente codificato per API REST (:ref:`rfc7807`) o SOAP (:ref:`soapFault`).

Maggiori dettagli, sulla gestione degli errori, sono disponibili nella sezione :ref:`erroriGovWay`.

