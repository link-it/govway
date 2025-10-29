Miglioramenti alla funzionalità di Trasformazione
--------------------------------------------------

È stato aggiunto nella maschera di gestione di un header o di un parametro della url un campo 'Identificazione Fallita' che consente di definire il comportamento del Gateway quando non riesce a risolvere le parti dinamiche contenute nel valore indicato:

- Termina con errore: la transazione termina con un errore che riporta la fallita risoluzione della parte dinamica indicata per il valore;
- Continua senza header: la transazione continua senza che l'header o il parametro venga aggiunto o modificato.
