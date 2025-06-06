.. _rateLimiting:

Rate Limiting
~~~~~~~~~~~~~

Questa sezione di configurazione consente di attivare policy di Rate
Limiting per una specifica erogazione o fruizione
(o specifico gruppo di configurazione nell'ambito di
un'erogazione/fruizione).

L'attivazione di policy di rate limiting rientra nell'ambito degli
strumenti per il controllo del traffico. La descrizione di dettaglio di
questi strumenti è presente nella sezione :ref:`traffico`, 
e nella sezione :ref:`trafficoPolicy` per quanto riguarda
l'attivazione di policy a valenza globale.

.. note::

    In presenza di una installazione con più nodi gateway attivi, GowWay per default effettua il conteggio delle metriche utilizzate dalle policy di rate limiting indipendentemente su ogni singolo nodo del cluster. Questa soluzione è certamente la più efficiente, ma presenta dei limiti evidenti se è necessaria una contabilità precisa del numero di accessi consentiti globalmente da parte dell'intero cluster. In tali situazioni è necessario modificare la configurazioni di default per attivare modalità di conteggio distribuite, come descritto nella sezione :ref:`headerGWRateLimitingCluster`.

Una policy di rate limiting si compone concettualmente dei seguenti elementi che verranno maggiormente dettagliati nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy`:

- *Criterio di Misurazione*: elemento che consente di calcolare un valore utile per la valutazione della policy. Il valore calcolato dipende dalla **metrica** scelta. La metrica viene scelta in fase di configurazione tra quelle disponibili, che sono:

    - *Numero Richieste*: consente di limitare il numero totale massimo di richieste consentite.
    - *Numero Richieste Simultanee*: limita il numero totale massimo di richieste simultanee consentite.
    - *Dimensione Massima Messaggi*: limita la dimensione massima accettata di una richiesta e di una risposta.
    - *Occupazione Banda*: limita il numero totale massimo di KB consentiti.
    - *Tempo Medio Risposta*: la policy blocca ogni successiva richiesta se viene rilevato un tempo medio di risposta elevato.
    - *Tempo Complessivo Risposta*: la policy limita il numero totale massimo di secondi consentiti.
    - *Numero Richieste Completate con Successo*: vengono conteggiate solamente il numero di richieste completate con successo; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Richieste Fallite*: vengono conteggiate il numero di richieste fallite; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Fault Applicativi*: vengono conteggiate il numero di richieste che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Richieste Completate con Successo o Fault Applicativi*: vengono conteggiate il numero di richieste completate con successo o che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Richieste Fallite o Fault Applicativi*: vengono conteggiate il numero di richieste fallite o che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata.

- *Dimensione della Finestra Temporale*: per ottenere un valore di confronto, alla metrica è necessario associare un intervallo di osservazione che consente di stabilire univocamente il conteggio risultante (fa eccezione la metrica 'Numero Richieste Simultanee'). L'intervallo di osservazione può essere espresso scegliendone uno tra i seguenti:

    - *Minuti*
    - *Orario*
    - *Giornaliero*

    .. note::
	L'intervallo indicato definisce una 'fixed window'. Ad esempio definendo 1 minuto, anche se la prima richiesta arriva alle 12:00:07 l'intervallo di osservazione sarà [12:00:00.000 - 12:00:59.999], il successivo [12:01:00.000 - 12:01:59.999] e così via...

- *Soglia di Confronto*: elemento della policy che fornisce il valore di soglia da confrontare con il valore ottenuto dalla metrica impostata.

- *Filtro di Applicabilità*: elemento della policy che stabilisce i criteri per i quali è applicabile la policy sui flussi in elaborazione sul Gateway (filtro su mittente, api, applicativo, ecc.).


*Criteri di valutazione delle policy*

Per ogni singola erogazione o fruizione di API è possibile definire più politiche di Rate Limiting, anche con analoga metrica. Per ogni richiesta viene applicato il seguente algoritmo di valutazione delle policy (una descrizione di maggior dettaglio viene fornita nella sezione :ref:`rateLimiting_criteriValutazione`):

- le policy vengono raggruppate «per metrica» e per ogni metrica vengono valutate nell’ordine di elenco.

- per ogni metrica vengono valutate le policy applicabili, cioè per le quali risultano soddisfatti il filtro e le condizioni di applicabilità.

- se la policy viola i livelli di soglia previsti, la transazione viene bloccata (o segnalata se configurata come «warning only») e la valutazione delle policy viene terminata.

- se la policy non viola invece i livelli di soglia previsti, si prosegue nella valutazione di ulteriori policy per quella metrica, solo se la policy è marcata come «prosegui».


*Header HTTP informativi restituiti ai client: quote e finestre temporali*

All'applicativo client vengono restituiti header http informativi che consentono di conoscere:

- il numero massimo di richieste effettuabili (quota);
- la finestra temporale in cui si applica la quota (informazione non attiva per default);
- il numero di secondi mancanti alla prossima finestra temporale dove il numero di richieste conteggiate verrà azzerato;
- il numero di richieste ancora effettuabili nella finestra temporale in corso;
- in caso di violazione della policy, il numero di secondi dopo i quali riprovare ad utilizzare il servizio.

Una descrizione di dettaglio degli header http viene fornita nella sezione :ref:`headerGWRateLimiting`.



.. toctree::
   :maxdepth: 2

   attivazioneNuovaPolicy
   criteriValutazione
   headerXRateLimit/index
   cluster/index
