.. _configGenerale_urlInvocazione:

URL di Invocazione API
~~~~~~~~~~~~~~~~~~~~~~~

Questa sezione visualizza:

-  *Base URL*: Indica il prefisso utilizzato per visualizzare le URL di Invocazione delle API.

-  *Base URL Fruizione*: permette di differenziare il prefisso utilizzato per visualizzare le URL di Invocazione delle fruizioni dalle erogazioni.

-  *Regole Proxy Pass*: tramite questa voce è possibile ridefinire le URL di Invocazioni, per specifiche fruizioni e/o erogazioni, allineandole a regole configurate su un revere proxy che media le comunicazioni http con GovWay.

**Regole Proxy Pass**

Questa sezione permette di ridefinire la modalità di visualizzazione delle Url di Invocazione delle API esposte da GovWay per assicurare che, in presenza di un reverse proxy che media le comunicazioni http con GovWay, sia possibile configurare opportunamente le url di invocazione delle API esposte da GovWay allineandole con le eventuali configurazioni specifiche realizzate sul reverse proxy.

   .. note::
      La funzionalità permette di configurare come vengono visualizzate le URL di Invocazione sulla govwayConsole, per allinearsi ad un eventuale reverse proxy che media le comunicazioni http con GovWay. Le API, su GovWay, rimangono raggiungibili solamente sulle url originali e dovrà essere il reverse proxy ad effettuare la conversione rispetto a quella esposta.

Le regole create sono visualizzate nella forma di elenco ordinato (:numref:`proxyPass_ListaRegole`). L'icona iniziale di ciascun elemento consente di modificarne la posizione. Per ogni regola viene visualizzato il suo stato, il nome e la descrizione.

   .. figure:: ../../_figure_console/ElencoRegoleProxyPass.png
    :scale: 100%
    :align: center
    :name: proxyPass_ListaRegole

    Lista Regole Proxy Pass

Per ogni regola (:numref:`proxyPass_addRegola`) deve essere obbligatoriamente definita una stringa libera o una espressione regolare utilizzata per individuare l'applicabilità della regola attraverso un confronto con il contesto dell'API. Il contesto è l'URL di Invocazione dell'API senza il prefisso Base URL.
Inoltre per ogni regola è possibile indicare altri criteri di applicabilità opzionali quali eventuali profilo di interoperabilità, un soggetto, una tipologia (fruizione/erogazione) o un tipo di api (soap/rest). 

   .. figure:: ../../_figure_console/AddRegolaProxyPass.png
    :scale: 100%
    :align: center
    :name: proxyPass_addRegola

    Creazione Regola Proxy Pass

Il dettaglio dei campi associati ad una regola sono raggruppati in tre sottosezioni:

Informazioni generiche:

- *Nome*: Identificativo della regola
- *Stato*: Indica se la regola è abilitata o meno.
- *Descrizione*: (Opzionale) Descrizione generica della regola

Le regole di applicabilità vengono definite dai seguenti campi:

- *Espressione Regolare*: Indica se la regola sottostante è una espressione regolare o una stringa libera.
- *Regola*: Stringa libera o espressione regolare.

   - L'espressione regolare viene utilizzata per verificarne il match sull contesto dell'API (url di invocazione senza la Base URL)
   - Nel caso di stringa libera si ha un'applicabilità se il contesto dell'API (url di invocazione senza la Base URL) inizia con la stringa fornita.
- *Profilo*: (Opzionale) Profilo di Interoperabilità per il quale si applica la regola
- *Soggetto*: (Opzionale) Soggetto interno per il quale si applica la regola
- *Ruolo*: (Opzionale) Tipologia di API (Erogazione/Fruizione) per il quale si applica la regola
- *Tipo API*: (Opzionale) Tipo di API (REST/SOAP) per il quale si applica la regola

La nuova url di invocazione viene definita attraverso i campi 'Base URL' e 'Contesto'. 

- *Base URL*: Permette di ridefinire la Base URL utilizzata rispetto a quanto definito nella configurazione generale
- *Contesto*: Indica il contesto da utilizzare dopo la Base URL

Nei campi 'Base URL' e 'Contesto' è possibile utilizzare le seguenti informazioni dinamiche:

- Se è stata fornita una espressione regolare, nei due campi possono essere utilizzati le keyword '${posizione}' per impostare un valore dinamico individuato tramite l'espressione regolare fornita. Il primo match, all'interno dell'espressione regolare, è rappresentata da '${0}' (Ad esempio: http://server:8080/${0}/altro/${1}/)

- Se è abilitata la gestione dei canali (vedi :ref:`console_canali`) è possibile utilizzare la keyword '${canale}' per impostare l'identificativo del canale associato all'API. Maggiori esempi vengono forniti nella sezione :ref:`console_canali_url`.

- È possibile utilizzare la keyword '${tag}' per impostare l'identificativo del tag associato all'API. Poichè ad un'API è possibile associare più tag verrà utilizzato quello alla prima posizione ma è possibile indicarne uno differente tramite l'espressione ${tag[posizione]}. Il primo tag, all'interno della lista, è rappresentata da '${tag[0]}', ad esempio: http://server:8080/${tag[0]}/


*Esempio 1*

Tutte le API REST erogate dal Soggetto 'ENTE' tramite il profilo 'ModI' possiedono nell'installazione di default la seguente URL di Invocazione:

- http://localhost:8080/rest/in/ENTE/NomeAPI/v1

Per modificare la url di invocazione in modo da spostare il nome del soggetto come hostname, e rimodulare il contesto in modo da visualizzare prima la versione, è possibile utilizzare la seguente configurazione di proxy pass:

Criteri di Applicabilità:

- Espressione Regolare: true
- Regola: .+/in/(.+)/(.+)/v(.+)
- Profilo: ModI
- Soggetto: ENTE
- Ruolo: Erogazione
- Tipo API: REST

Nuova URL di Invocazione

- Base URL: http://${0}/
- Contesto: v${2}/api/${1}

L'url di invocazione prodotta sarà:

- http://ENTE/v1/api/NomeAPI


*Esempio 2*

Supponiamo di voler modificare l'url di invocazione dell'API 'PetStore' versione 1 erogata dal soggetto 'ENTE' tramite il profilo di interoperabilità 'ModI'. Nell'installazione di default viene fornita la seguente URL di Invocazione:

- http://localhost:8080/rest/in/ENTE/PetStore/v1

Lo scopo è quello di eliminare il nome del soggetto e di togliere la 'v' dalla versione. Per farlo è possibile utilizzare la seguente configurazione di proxy pass:

Criteri di Applicabilità:

- Espressione Regolare: false
- Regola: /rest/in/ENTE/PetStore/v1
- Profilo: ModI
- Soggetto: Qualsiasi
- Ruolo: Qualsiasi
- Tipo API: Qualsiasi

Nuova URL di Invocazione

- Base URL: 
- Contesto: /rest/in/PetStore/1

L'url di invocazione prodotta sarà:

- http://localhost:8080/rest/in/PetStore/1



