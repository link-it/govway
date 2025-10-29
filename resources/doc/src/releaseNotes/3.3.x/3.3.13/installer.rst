Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- aggiunti script di svecchiamento delle tracce per tipo di database postgresql e oracle;

- l'esecuzione in modalità testuale ('./install.sh text') rimaneva bloccata in caso di tipologia d'installazione 'Aggiornamento' durante la selezione della 'Versione Precedente'.

Inoltre la modalità "gestione dei nodi dinamica", indicata per le installazioni in cloud e selezionabile con una installazione in modalità avanzata, è stata modificata per rendere utilizzabile la soluzione anche in architetture cloud dove i nodi runtime (pod) non risultano invocabili tra di loro. 
