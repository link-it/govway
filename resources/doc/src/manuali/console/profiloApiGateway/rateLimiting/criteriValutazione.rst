.. _rateLimiting_criteriValutazione:

Criteri di valutazione delle policy
+++++++++++++++++++++++++++++++++++

Le policy di rate limiting create, per la data erogazione/fruizione, sono visualizzate in un elenco che filtra automaticamente su una singola metrica (ad esempio "Numero Richieste" o "Occupazione Banda"). L'elenco delle policy visualizzato è analogo a quello riportato in :numref:`elencoPolicyRateLimiting`.

   .. figure:: ../../_figure_console/ElencoPolicyRateLimiting.png
    :scale: 50%
    :align: center
    :name: elencoPolicyRateLimiting

    Elenco delle policy di Rate Limiting

Ciascun elemento in elenco riporta le seguenti informazioni:

- *Ordine*: pulsanti per variare la posizione della policy nell'elenco per la data metrica.

- *Stato*: lo stato di abilitazione della policy, sulla base di quanto descritto in precedenza (:ref:`rateLimiting_attivazioneNuovaPolicy`).

- *Nome*: il nome della policy.

- *Soglia*: il valore di soglia impostato per la policy.

- *Runtime*: permette di effettuare una verifica in tempo reale della metrica interrogando il runtime del gateway. Maggiori dettagli sono presenti nella sezione :ref:`configurazioneRateLimiting_statistiche`.

- *Elaborazione*: flusso di elaborazione (prosegui, interrompi) nel caso di superamento del controllo relativo alla policy.

L'elenco delle policy può essere aggiornato utilizzando il meccanismo di filtro presente nell'intestazione della tabella. Sono disponibili le seguenti opzioni:

- *Metrica*: permette di stabilire le policy da visualizzare in base alla rispettiva metrica.

- *Ricerca*: permette di visualizzare le policy in base alla presenza di un pattern nel nome.

Per ogni richiesta relativa alla specifica erogazione/fruizione viene applicato l'algoritmo di valutazione delle policy che è il seguente:

- le policy vengono raggruppate «per metrica» e per ogni metrica vengono valutate nell’ordine di elenco prima utilizzando le politiche di Rate Limiting definite  sull'API e poi, se esistenti, le politiche a valenza globale (:ref:`trafficoPolicy`).

- per ogni metrica vengono valutate le policy applicabili, cioè per le quali risultano soddisfatti il filtro e le condizioni di applicabilità.

- se la policy viola i livelli di soglia previsti, la transazione viene bloccata (o segnalata se configurata come «warning only») e la valutazione delle policy viene terminata.

- se la policy non viola invece i livelli di soglia previsti, si prosegue nella valutazione di ulteriori policy per quella metrica, solo se la policy è marcata come «prosegui».


