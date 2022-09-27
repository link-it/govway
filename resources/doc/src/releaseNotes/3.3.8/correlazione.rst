Miglioramenti alla funzionalità di Correlazione Applicativa
------------------------------------------------------------

Durante la gestione della correlazione applicativa, nel caso l'identificativo estratto superi la massima lunghezza consentita di 255 caratteri, per default GovWay termina la transazione con errore o non estrae alcun identificativo a seconda della modalità di gestione della regola di estrazione configurata (blocca/accetta).

È adesso possibile modificare il comportamento di default abilitando su entrambe le modalità di gestione un troncamento dell'identificativo alla massima lunghezza consentita. Il troncamento è abilitabile sia a livello di proprietà della singola erogazione o fruizione che a livello globale.

