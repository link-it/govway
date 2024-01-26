Miglioramenti alla funzionalità di Correlazione Applicativa
-------------------------------------------------------------

Sono state modificate le seguenti logiche di gestione.

- Una richiesta non applicabile a nessuna regola di correlazione applicativa, fino alla versione 3.3.13.p1, terminava con l’errore: 

  "Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body". 

  È stato modificato il default in modo da accettare la richiesta. Il precedente comportamento è ripristinabile agendo sulle proprietà della singola fruizione o erogazione di API.

- Una correlazione applicativa configurata con una modalità d'identificazione basata su header HTTP e un comportamento di identificazione fallita uguale al valore 'accetta', provocava la terminazione con errore della transazione se la richiesta non presentava l'header HTTP configurato nonostante fosse stato indicato di proseguire con successo in caso di identificazione fallita. L'errore riportato era il seguente: 

  "Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body". 

  L'anomalia si presentava anche su altre modalità di identificazione nel caso in cui l'identificativo estratto risultasse null o una stringa vuota. È stato modificato il comportamento di default del gateway in modo da considerare entrambi i casi come una estrazione di correlazione applicativa fallita per la specifica modalità, per la quale si può poi decidere se proseguire comunque o terminare con errore. Il precedente comportamento di accettare identificativi null o stringhe vuote è ripristinabile agendo sulle proprietà della singola fruizione o erogazione di API.
