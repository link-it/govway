Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Migliorata la gestione della registrazione di repository multipli delle chiavi PDND e/o di utilizzo di client interop differenti in ambiente Multi-Tenant:

- i repository multipli vengono ora rilevati automaticamente nella console di gestione e nei timer dedicati alla gestione delle interazioni con la PDND, eliminando la necessità di attivazione manuale nelle configurazioni dei vari tool;
- la proprietà 'remoteStore.pdnd.baseUrl' può ora essere definita utilizzando esclusivamente la base URL, senza il suffisso '/keys';
- aggiunti ulteriori criteri di personalizzazione per le configurazioni Multi-Tenant.


È stata inoltre risolta un'anomalia nella funzionalità ModI relativa all'imbustamento delle fruizioni per applicativi identificati tramite autenticazione interna di tipo 'token'. Il keystore definito nell'applicativo non veniva correttamente utilizzato, generando le seguenti segnalazioni di errore:

- in caso di token generati senza PDND:

  "Il profilo di sicurezza richiesto 'idam01' richiede l'identificazione di un applicativo".

- in caso di negoziazione del token tramite PDND:

  "Il tipo di keystore indicato nella token policy 'PDND' richiede l'autenticazione e l'identificazione di un applicativo fruitore: Servizio applicativo anonimo".


