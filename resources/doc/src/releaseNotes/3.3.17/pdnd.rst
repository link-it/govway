Miglioramenti all'integrazione con la PDND
------------------------------------------------------

Per allinearsi alle modifiche introdotte dalla `nuova versione delle Linee Guida AgID <https://www.interop.pagopa.it/news/nuove-llgg-agid-pubblicate>`__, la piattaforma è stata estesa con il supporto alle seguenti funzionalità.

**Signal Hub**

GovWay supporta l’integrazione dei soggetti erogatori di e-service, permettendo la configurazione diretta della funzionalità `Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ all’interno della piattaforma. In questo modo, tutte le funzionalità relative alla pseudoanonimizzazione dei dati e alla comunicazione con la PDND sono gestite automaticamente da GovWay, senza richiedere interventi sul backend dell’e-service.

L’integrazione trasparente è articolata in due componenti principali:

- Esposizione di un’operazione dedicata per il recupero delle informazioni crittografiche. GovWay espone automaticamente un’operazione REST/SOAP, conforme alle specifiche PDND, che consente ai fruitori di ottenere le informazioni crittografiche necessarie alla pseudoanonimizzazione degli identificativi relativi ai dati oggetto di variazione. L’implementazione è completamente gestita dalla piattaforma: l’e-service non deve sviluppare alcuna logica specifica, ma solo adeguare l’interfaccia pubblicata sulla PDND secondo le specifiche fornite da GovWay.
- Esposizione di un’interfaccia semplificata per l’invio dei segnali. GovWay fornisce un endpoint dedicato che l’e-service può invocare per pubblicare le variazioni di dato (segnali) senza doversi occupare della generazione dell’identificativo pseudoanonimizzato o della gestione delle complessità del protocollo Signal Hub (es. progressivi per ogni segnale). La piattaforma si occupa internamente di garantire la conformità alle specifiche PDND.

**Tracing**

In conformità con le nuove disposizioni, gli e-service devono `trasmettere regolarmente alla PDND <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-tracing>`__ un report giornaliero in formato CSV contenente i log delle chiamate ricevute tramite l’interoperabilità.

GovWay gestisce in modo completamente trasparente questa funzionalità, occupandosi della raccolta, aggregazione e invio automatico dei tracciamenti attraverso le API messe a disposizione dalla PDND.
In questo modo, il soggetto erogatore non deve implementare alcuna logica aggiuntiva né preoccuparsi degli aspetti tecnici legati al conferimento dei log.

**Identificativi presenti nel Voucher PDND**

Tra gli header di integrazione inoltrati al backend sono adesso presenti anche le informazioni relative al consumerId, producerId, eserviceId e descriptorId presenti nel voucher PDND.

**API Interoperabilità**

Le informazioni recuperate tramite le API di interoperabilità relative all'identificativo esterno (externalId) e all'identificativo di registro della PDND (consumerId) di una organizzazione sono adesso utilizzabili per:

- filtrare le transazioni nelle ricerche dello storico e nella generazione di report statistici;
- generare report '3D' che utilizzano gli identificativi come informazione da visualizzare;
- raggruppare le richieste nel criterio di conteggio di una policy di Rate Limiting.

Inoltre il consumerId viene adesso visualizzato tra le informazioni di dettaglio di una transazione. 

Infine è stata migliorata la gestione delle informazioni parziali o non disponibili ottenute dalle API Interop in relazione al clientId. In tali casi, le informazioni vengono comunque memorizzate temporaneamente nella cache locale, al fine di evitare chiamate ripetute e non necessarie verso la PDND.  Tuttavia, rispetto alle informazioni acquisite correttamente, la loro permanenza in cache è ora ridotta: il valore predefinito è fissato a 5 minuti, contro i 30 giorni previsti per i dati completi

**Finalità**

È adesso possibile effettuare ricerche nello storico delle transazioni tramite l'identificativo della finalità PDND (purposeId).
Inoltre nel dettaglio di una transazione, nella sezione 'Informazioni Mittente', viene adesso visualizzato anche l'identificativo della finalità.

**Token Policy di negoziazione**

È stato introdotto un valore di default per il campo 'purposeId', che consente di definire e gestire purposeId differenti in funzione degli applicativi associati alla fruizione.
Inoltre, il box informativo relativo al campo purposeId è stato arricchito con una descrizione dettagliata dei valori ammessi, fornendo indicazioni utili per l’implementazione di diversi scenari configurativi.
