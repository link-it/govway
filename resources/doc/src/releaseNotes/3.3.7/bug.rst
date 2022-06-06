Bug Fix
-------

Sono stati risolti i seguenti bug:

- aggiunto il timezone nella serializzazione delle date negli header HTTP inoltrati ai backend;

- migliorata diagnostica emessa in presenza di una richiesta SOAP vuota (senza http payload), rispetto al precedente errore che riportava un null pointer:  "Riscontrato errore ... errore durante il controllo del namespace del soap envelope: null". La nuova diagnostica riporta invece la causa dell'errore: "Riscontrato errore ... errore durante il controllo del namespace del soap envelope: Invalid empty message"

- in presenza di una API SOAP con registrazione messaggi abilitata, se la risposta pervenuta conteneva un Content-Type non compatibile con quello della richiesta, GovWay segnalava l'anomalia correttamente nei diagnostici ma non registrava il contenuto della risposta e degli header HTTP;

- la funzionalità di lettura delle richieste SOAP in streaming veniva erroneamente attivata nel servizio di imbustamento Xml2Soap e causava, nel profilo di interoperabilità 'Fatturazione Elettronica', una spedizione di fatture binarie (P7M/ZIP) corrotte;

- modificato nome della proprietà 'topic.*.requestSended' in 'topic.*.requestSent' nella tracciatura su file;

- la configurazione di default della funzionalità 'GovWayProxy' per Token Policy e A.A. descritta nel file govway.properties veniva ignorata;

- sono stati risolti i seguenti problemi che avvenivano disabilitando il tracciamento delle transazioni:

	- la funzionalità di tracciatura su file non registrava le informazioni nei topic configurati;

	- la funzionaltà di consegna asincrona produceva un errore durante il tentativo di registrazione delle tracce e dei diagnostici.

Sono stati risolti i seguenti bug relativi al profilo di interoperabilità 'ModI':

- nel pattern 'Integrity_REST_01' l'header Digest veniva erroneamente sia generato che atteso con una codifica 'hex'. La codifica è stata rivista per utilizzare 'base64' in modo da essere conformi al RFC 5843 (che estende il RFC 3230 indicato nelle Linee Guida). Per garantire la retrocompatibilità è possibile configurare la singola erogazione/fruizione per produrre un header Digest codificato in esadecimale, mentre in fase di validazione è possibile accettare entrambe le codifiche. Per default sia la codifica attesa che quella generata è sempre base64.

- risolta mancata rilevazione di identificativo duplicato quando sia il dominio fruitore che quello erogatore venivano gestiti sulla stessa instanza di GovWay. La mancata rilevazione avveniva solamente se le richieste duplicate venivano ricevute simultaneamente rispetto all'originale.

Per la console di gestione sono stati risolti i seguenti bug:

- risolti i seguenti problemi relativi alla validazione di un'interfaccia OpenAPI 3.0:

	- la validazione risultava esssere troppo stringente per quanto concerne i valori di default associati ai tipi primitivi (integer, number o boolean) definiti con gli apici (es. '65' invece di 65). Il validatore segnalava un errore simile al seguente: "Validation error(s) :	paths./pets1.patch.parameters.schema.default: Value '65' is incompatible with schema type 'integer' (code: 138)"

	- OpenAPI in formato YAML che possedevano anchor "merge key" (es. <<: \*) non superavano la validazione dell'interfaccia;

	- il caricamento di un'interfaccia contenente una descrizione superiore a 255 caratteri, causava un errore inatteso in alcuni casi su database Oracle dove si otteneva l'errore: "Caused by: java.sql.SQLException: ORA-12899: value too large for column "GOVWAY334TESTBYSETUP"."ACCORDI"."DESCRIZIONE" (actual: 257, maximum: 255)".


Per la console di monitoraggio sono stati risolti i seguenti bug:

- i grafici (PieChart e BarChart) della distribuzione per errore non venivano visualizzati dalla console nel caso in cui tra le tipologie di errore individuate fosse presente un errore la cui descrizione presentava l'apice singolo.
