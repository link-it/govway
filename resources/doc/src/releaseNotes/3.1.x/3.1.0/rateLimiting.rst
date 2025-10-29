Miglioramenti alla funzionalità di RateLimiting
-------------------------------------------------

La gestione delle politiche di 'Rate Limiting' è stata semplificata, introducendo la distinzione tra due diverse modalità di registrazione:

- *Basata su Criteri*: permette di indicare direttamente i
  criteri che la politica deve garantire; tra i criteri utilizzabili:
  la metrica (numero richieste, occupazione banda, tempi medi, ....),
  l'intervallo temporale (minuto, ora, giorno) e le condizioni di
  applicabilità (congestione, degrado prestazionale).

- *Basata su Policy Utente*: permette di utilizzare una politica arbitraria, precedentemente definita dall'utente.
  
È stato rivisto l'algoritmo di valutazione delle politiche di rate
limiting, come segue:

- le policy vengono raggruppate "per metrica" e per ogni metrica
  vengono valutate nell'ordine di inserimento, per cui è ora possibile
  modificare la posizione della policy;
  
- per ogni metrica vengono valutate le policy applicabili, cioè
  per le quali risultano soddisfatti il filtro e le condizioni di
  applicabilità;

- se la policy viola i livelli di soglia previsti, la transazione
  viene bloccata (o segnalata se configurata come "warning only") e la
  valutazione delle policy viene terminata;
  
- se la policy non viola invece i livelli di soglia previsti, si
  prosegue nella valutazione di ulteriori policy per quella metrica,
  solo se la policy è marcata come "prosegui".

Sono state inoltre realizzate le seguenti modifiche:

- *Livelli di Soglia*: riviste le maschere per la gestione dei valori di soglia (con o senza criteri di raggruppamento).

- *Raggruppamento per Token*: aggiunti criteri di raggruppamento dei dati per token, dove è possibile selezionare i claim da utilizzare (subject, clientId ...).

- *Filtro*: riviste le maschere per la gestione dei criteri di
  applicabilità. Nelle politiche relative alle API è adesso possibile
  definire all'interno del filtro più risorse e il ruolo del
  richiedente.

