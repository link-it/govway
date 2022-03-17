.. _3.3.6.1_bug:

Bug Fix 3.3.6.p1
----------------

Sono stati effettuati i seguenti interventi migliorativi degli aspetti prestazionali:

- introdotta ottimizzazione nella gestione degli header SOAP: durante la gestione della richiesta o della risposta, se le funzionalità attivate richiedono solamente la gestione del SOAPHeader, viene costruito in memoria la sola rappresentazione DOM dell'header e non dell'intero messaggio mentre il contenuto presente nel body viene trattato in modalità streaming.

Sono stati risolti i seguenti bug:

- la serializzazione di un token malformato causava la mancata scrittura della transazione su database;

- i medesimi controlli di consistenza del formato di un content-type, già attivi nelle richieste ricevute, sono stati introdotti anche per le risposte in modo da avere una diagnostica conforme in entrambi i casi.


Per la console di gestione sono stati risolti i seguenti bug:

- l'aggiornamento della configurazione ModI di una erogazione o fruizione andava in errore su InternetExplorer 11, per via della presenza di elementi contenenti caratteri che mandavano in confusione il visualizzatore di IE;

- il reset puntuale di una API non eliminava dalla cache la definizione dell'interfaccia OpenAPI o Wsdl;

- la funzionalità di download dei certificati server di una Token Policy di Negoziazione non funzionava;

- sono stati risolte le seguenti anomalie presenti nella funzionalità di consegna condizionale:

	- eliminata voce 'SOAPAction' nell'impostazione del filtro di una consegna condizionale per API di tipo REST;
	- aggiunta la possibilità di individuare una risorsa REST, nelle regole specifiche di una consegna condizionale, anche per metodo e path oltre che per identificativo;
	- migliorata la pagina di configurazione per la gestione delle notifiche;
	- differenziato diagnostico emesso in caso di consegna condizionale non applicabile e impostazione configurata per non inviare la notifica a nessun connettore registrato;
	- se configurate regole di condizionalità specifiche per determinate azioni, una qualsiasi modifica della configurazione generale del connettore multiplo provocava la perdita delle regole precedentemente configurate.

- sono stati risolte le seguenti anomalie presenti nella funzionalità di consegna con notifiche:

	- il connettore indicato come 'Connettore Implementa API' presentava accanto al nome l'indicazione di uno stato (abilitato/disabilitato) che non aveva senso poichè non è possibile disabilitarlo;
	- nella lista degli esiti con cui si configura l'invio delle notifiche, è stato escluso il gruppo 'Richiesta Scartate' essendo richieste non ancora accettate in ingresso;
	- nel connettore associato all'implementazione dell'API non è adesso più possibile definire i criteri di consegna asincrona;
	- tra i connettori selezionabili, in caso di identificazione della condizione fallita o di individuazione del connettore non riuscita, non viene adesso più presentato il connettore che implementa l'API;
	- selezionando un 'Connettore che Implementa API' differente da quello iniziale, la maschera dei connettori non riportava correttamente il connettore scelto alla prima posizione dell'elenco.

Sull'installer è stato corretto il seguente bug:

- utilizzando l'installer in modalità avanzata, l'opzione che consente di abilitare le funzionalità di consegna delle notifiche (in fase di sviluppo) non generava degli artefatti corretti se l'application server selezionato era Tomcat.


