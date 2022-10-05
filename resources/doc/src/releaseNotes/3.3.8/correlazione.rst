Miglioramenti alla funzionalità di Correlazione Applicativa
------------------------------------------------------------

E' stata aggiunta la possibilità di modificare l'attuale comportamento
di govway, attivando il troncamento dell'identificativo estratto alla
massima lunghezza consentita di 255 caratteri disponibile del testo
identificativo da estrarre dai messaggi.

Il comportamento di default resta lo stesso della precedente versione:
nel caso l'identificativo estratto superi la massima lunghezza
consentita di 255 caratteri, la transazione termina con errore o senza
estrarre l'identificativo a seconda della modalità di gestione della
regola di estrazione configurata (blocca/accetta).


