Bug Fix
-------

Sono stati risolti i seguenti bug:

- Le richieste contenenti credenziali 'http-basic', veicolate all'interno dell'header 'Authorization' in un formato non corretto, non venivano registrate nello storico delle transazioni. Il client riceveva un codice http di risposta 404 insieme ad una pagina html contenente il codice di errore 'GovWay-OP20000-0001'.

- Risolto bug che non consentiva di processare SOAP Envelope 1.2 con WSSecurity quando la configurazione prevedeva l'utilizzo di un 'role' o l'abilitazione dell'attributo 'mustUnderstand'.

- Aggiornate librerie bouncy castle alla versione 1.69 per risolvere la problematica che avveniva casualmente dopo aver aggiornato OpenJDK ad una versione superiore alla 11.0.8; la seguente eccezione occorreva di rado su invocazioni in https: "arraycopy: last source index 32 out of bounds for byte[31] at java.base/sun.security.ssl.Alert.createSSLException"

- È stato risolto un problema presente nella libreria xPath disponibile tra le funzioni built-in nelle trasformazioni. Quando si estraevano frammenti di elementi xml, eventuali entity reference presenti nei valori degli elementi estratti venivano erroneamente risolti. La risoluzione poteva comportare una generazione di un frammento xml sintatticamente non valido. Ad esempio se nella trasformazione si utilizzava il frammento xml '<descrizione>Esempio con &lt;30</descrizione>' per comporre un nuovo xml, si otteneva un errore di parsing poichè l'entity reference presente nella descrizione '&lt;30' veniva risolta con il carattere '<' comportando quindi la generazione di un elemento xml malformato: '<descrizione>Esempio con <30</descrizione>'.

- La risoluzione dinamica dell'endpoint dei connettori http/https e dei path sul connettore 'file' non consentiva di utilizzare i valori di header HTTP e/o parametri della url ricevuti, se questi non venivano serializzati verso il backend. È stata inoltre aggiunta la possibilità di utilizzare una espressione regolare applicata all'url di invocazione, nella definizione dell'endpoint di un connettore.

- Le richieste ricevute prima del completamento dello startup di GovWay, provocano una inizializzazione non corretta degli handler. Il problema è stato risolto facendo in modo che le richieste ricevute prima dello startup rimangano in attesa che il gateway sia completamente inizializzato prima di essere processate.

- La registrazione della transazione falliva, con l'errore riportato di seguito, se l'erogazione era configurata con diversi connettori multipli in cui la somma dei nomi superava i 2000 caratteri: org.openspcoop2.generic_project.exception.ServiceException: ERRORE: il valore è troppo lungo per il tipo character varying(2000)

- Migliorati log emessi in casi di errore avvenuti durante l'aggiornamento dell'esito della consegna di un connettore multiplo o durante l'aggiornamento della transazione che raggruppa le varie consegne multiple.

- Su API REST, se in un handler si aggiungeva alle proprietà del trasporto (direttamente e non tramite i metodi 'forceHeader') un header http che non risiedeva in black list si otteneva un errore simile al seguente: "java.util.ConcurrentModificationException: null at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1042)..."

- In una configurazione senza connettori multipli, dove era stato abilitato il salvataggio dei messaggi su MessageBox, in occasione dell'eliminazione di un messaggio via Integration Manager o per scadenza naturale, si otteneva un errore simile al seguente: ERROR <23-09-2021 16:03:00> [id:4eccbf4e-181a-11ec-b5a0-00505686878f][sa:gw_SOGGETTO/gw_SERVIZIO/v1][null] 'aggiornaInformazioneConsegnaTerminata' non riuscta. Tutti gli intervalli di update non hanno comportato un aggiornamento della transazione.

- Le operazioni 'getAllMessageIds', esposte dal servizio MessageBox via IntegrationManager, che non prevedono tra i parametri un limite massimo di id ritornati utilizzano adesso il limite di default di sistema (1000) per evitare OutOfMemory in presenza di una mole di messaggi considerevole.


Per quanto concerne il Profilo 'Fatturazione Elettronica' sono stati risolti i seguenti bug:

- Nel caso di fatture P7M, nel formato PEM, contenenti i prefissi -----BEGIN e -----END il software non era in grado di parsare la fattura se si abilitavano i controlli che lo richiedevano (ad esempio abilitando la proprietà 'org.openspcoop2.protocol.sdi.accesso.campiFattura.enable' nel file esterno 'sdi_local.properties'.


Per quanto concerne il Profilo 'SPCoop' sono stati risolti i seguenti bug:

- Durante il restyling grafico delle liste accessibili dal menù principale, relativamente agli Applicativi e realizzato con il rilascio della versione 3.3.3, è venuta meno la possibilità di configurare i connettori relativi alla 'Risposta Asincrona' richiesti dai profili asincroni. La problematica è stata risolta.


Per la console di gestione sono stati risolti i seguenti bug:

- Caricando un'interfaccia OpenAPI 3 in cui gli elementi principali (openapi, info, servers, paths ...) non erano posizionati sulla prima colonna, si otteneva il seguente errore (https://github.com/link-it/govway/issues/83):
  [Interfaccia OpenAPI 3] Documento non valido: org.openspcoop2.utils.rest.ProcessingException: Parse failed: mapping values are not allowed here in 'reader', line 2, column 9: info: ^ at [Source: (StringReader); line: 2, column: 9]

- Utilizzando il browser IE11 quando si selezionava un filtro di ricerca, al momento del reload della pagina, l'applicazione si bloccava. La console del browser indicava il seguente errore: "Errore: L'oggetto non supporta la proprietà o il metodo 'endsWith'".

- Risolto bug che non consentiva l'associazione di ruoli ad un applicativo server in cui era stata abilitata l'opzione 'Utilizzabile come Client'.

- La console terminava con un errore non atteso in caso di caricamento di certificati, sugli applicativi o sui soggetti, che non possedevano il campo 'CN' nel subject o nell'issuer.

- La console non consentiva di aggiungere credenziali (es. un certificato x.509) ad un soggetto creato precedentemente senza.

- Il dialog informativo, che riporta le credenziali http-basic o api-key, non visualizzava correttamente credenziali che possedevano caratteri particolari come i doppi apici.

- La creazione di un nuovo gruppo, in un'erogazione, non andava a buon fine se durante la creazione veniva scelto di ereditare le configurazioni di un gruppo non predefinito, configurato per utilizzare un connettore ridefinito con consegna multipla.

- La funzionalità 'Importa' non gestiva correttamente l'importazione di fruizioni in cui era stato ridefinito il connettore, per specifiche azioni/risorse, attraverso l'utilizzo della tipologia 'https'.

- L'eliminazione di un utente (o la modifica del permesso 'S') comportava una riassegnazione degli oggetti appartenenti all'utente nella quale venivano erroneamente eliminate eventuali proprietà o credenziali presenti nei soggetti aggiornati. La problematica è stata risolta, e l'attività di riassegnamento è stata eliminata se la visibilità degli oggetti risulta globale per tutte le utenze di gestione (comportamento di default).


Per la console di monitoraggio sono stati risolti i seguenti bug:

- gli applicativi server, utilizzabili anche come client, non erano selezionabili tra la lista degli applicativi fornita dalla tipologia di ricerca per applicativo;

- sono stati risolti i seguenti problemi relativamente alla funzionalità di export CSV:

	- nella colonna delle azioni/risorse non venivano riportate le risorse per le API di tipo REST;

	- risolto problema di conflitto di nome tra il nome dell'API e il nome della fruizione/erogazione dove per entrambi la colonna si chiamava con l'header 'API';

	- risolto problema di ordine differente tra header e valori nelle fruizioni per quanto concerne i parametri dell'autenticazione dove venivano fornite informazioni errate;

	- la funzionalità di esportazione tramite la voce 'seleziona tutti' non considerava gli eventuali filtri impostati su API e Tag;

	- non venivano riportati i dati del connettore se questo veniva ridefinito in un gruppo di una fruizione.

Per le API di gestione sono stati risolti i seguenti bug:

- il download di specifiche semiformali non funzionava e veniva generato un errore "404 Not Found" anche per specifiche esistenti sull'API;

- se tramite console veniva registrata una specifica semiformale in Linguaggio Naturale, la raccolta degli allegati via api terminava con errore;

- non era consentito allegare documenti i cui nomi contenevano spazi o iniziavano con un numero.


