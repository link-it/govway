Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- viene adesso supportato un nuovo scenario ModI di fruizione in cui il keystore utilizzato per la firma viene associato direttamente sulla fruizione ed è configurabile in alternativa alla modalità già esistente in cui il keystore viene associato all'applicativo mittente;

- aggiunta la possibilità di definire una token policy di negoziazione in cui i dati relativi al keystore, il KID e il clientId sono prelevabili dalla fruizione la quale consente adesso di configurare tali parametri se al connettore viene associata una token policy con tale caratteristica;

- rivista la label 'Contemporaneità Token Authorization e Agid-JWT-Signature' in 'Coesistenza Token Authorization e Agid-JWT-Signature';

- aggiunta la possibilità di individuare una proprietà registrata in una fruizione in relazione all'applicativo chiamante. La funzionalità è utilizzabile per configurare in una fruizione purposeId differenti rispetto all'applicativo chiamante e in una token policy di negoziazione verso la PDND configurare un purposeId che utilizzi la nuova funzionalità. Analogamente è possibile individuare una proprietà sull'applicativo rispetto all'api invocata.
