.. _rateLimiting:

Rate Limiting
~~~~~~~~~~~~~

Questa sezione di configurazione, specifica per erogazioni e fruizioni
(o specifico gruppo di configurazione nell'ambito di
un'erogazione/fruizione), consente di attivare delle policy di Rate
Limiting specifiche per l'istanza configurata.

L'attivazione di policy di rate limiting rientra nell'ambito degli
strumenti per il controllo del traffico. La descrizione di dettaglio di
questi strumenti è presente nella sezione :ref:`traffico`, dove viene illustrato il meccanismo
per configurare le policy e più in dettaglio nella sezione :ref:`trafficoPolicy` riguardo
l'attivazione di policy a valenza globale.

Una policy di rate limiting si compone concettualmente dei seguenti elementi:

- *Criterio di Misurazione*: elemento che consente di calcolare un valore utile per la valutazione della policy. Il valore calcolato dipende dalla **metrica** scelta. La metrica viene scelta in fase di configurazione tra quelle disponibili, che sono:

    - *Numero Richieste*: consente di limitare il numero totale massimo di richieste consentite.
    - *Numero Richieste Simultanee*: limita il numero totale massimo di richieste simultanee consentite.
    - *Occupazione Banda*: limita il numero totale massimo di KB consentiti.
    - *Tempo Medio Risposta*: la policy blocca ogni successiva richiesta se viene rilevato un tempo medio di risposta elevato.
    - *Tempo Complessivo Risposta*: la policy limita il numero totale massimo di secondi consentiti.
    - *Numero Richieste Completate con Successo*: vengono conteggiate solamente il numero di richieste completate con successo; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Richieste Fallite*: vengono conteggiate il numero di richieste fallite; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Fault Applicativi*: vengono conteggiate il numero di richieste che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata.
    - *Numero Richieste Fallite o Fault Applicativi*: vengono conteggiate il numero di richieste fallite o che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata.

  Per ottenere un valore di confronto, alla metrica è necessario associare un intervallo di osservazione che consente di stabilire univocamente il conteggio risultante (fa eccezione 'Numero Richieste Simultanee'). L'intervallo di osservazione può essere espresso scegliendone uno tra i seguenti:

    - *Minuti*
    - *Orario*
    - *Giornaliero*

- *Soglia di Confronto*: elemento della policy che fornisce il valore di soglia da confrontare con il valore ottenuto dalla metrica impostata.

- *Filtro di Applicabilità*: elemento della policy che stabilisce i criteri per i quali è applicabile la policy sui flussi in elaborazione sul Gateway (filtro su mittente, api, applicativo, ecc.).


Per ogni singola erogazione o fruizione di API è possibile definire più politiche di Rate Limiting, anche con medesima metrica. Per ogni richiesta viene applicato un algoritmo di valutazione delle policy che è il seguente (una descrizione di dettaglio viene fornita nella sezioni successive):

- le policy vengono raggruppate «per metrica» e per ogni metrica vengono valutate nell’ordine di elenco.

- per ogni metrica vengono valutate le policy applicabili, cioè per le quali risultano soddisfatti il filtro e le condizioni di applicabilità.

- se la policy viola i livelli di soglia previsti, la transazione viene bloccata (o segnalata se configurata come «warning only») e la valutazione delle policy viene terminata.

- se la policy non viola invece i livelli di soglia previsti, si prosegue nella valutazione di ulteriori policy per quella metrica, solo se la policy è marcata come «prosegui».


.. toctree::
   :maxdepth: 2

   attivazioneNuovaPolicy
   criteriValutazione
