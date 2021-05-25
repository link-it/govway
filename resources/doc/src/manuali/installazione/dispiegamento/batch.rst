.. _deploy_batch:

Batch Generazione Statistiche
-----------------------------

Tra le opzioni dell'installazione avanzata vi è la possibilità di mantenere il controllo diretto sull'attività di generazione delle statistiche tramite una procedura batch.
Se si è optato per la *Generazione tramite Applicazione Batch" relativamente alla voce *Generazione delle Statistiche*, il processo di installazione produrrà la directory *dist/batch*.
La procedura inclusa in tale directory può essere integrata al sistema in accordo alle politiche più idonee per l'ambiente di installazione di Govway.

Dentro la directory *dist/batch* troviamo le seguenti subdirectory:

	- *generatoreStatistiche*, che comprende il batch

	- *crond*, che comprende un esempio di utilizzo del batch agganciato a un cron

La directory *dist/batch/generatoreStatistiche* è strutturata nel modo seguente:

	- directory *lib*, che comprende le librerie per l'esecuzione del batch

	- directory *jdbc*, che deve essere popolata con il driver JDBC adeguato alla piattaforma database adottata

	- directory *properties*, che comprende i file di configurazione. Tra questi troviamo il file *daoFactory.properties* dove sono presenti i dati per la connessione al database

	- script shell per l'esecuzione del batch nei diversi periodi: orario, giornaliero, settimanale e mensile.