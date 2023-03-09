Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- viene adesso supportato una nuova modalità di fruizione ModI in cui il keystore utilizzato per la firma viene associato direttamente alla fruizione, in alternativa alla modalità già esistente in cui il keystore viene associato all'applicativo mittente;

- aggiunta la possibilità di definire una token policy di negoziazione in cui i dati relativi al keystore, al KID e al clientId possono essere configurati nelle fruizioni con connettore che utilizza token policy con tali caratteristiche;

- rivista la label 'Contemporaneità Token Authorization e Agid-JWT-Signature' in 'Coesistenza Token Authorization e Agid-JWT-Signature';

- aggiunta la possibilità di registrare nelle fruizioni proprietà relative ad uno specifico applicativo mittente. La funzionalità è utilizzabile per configurare in una fruizione purposeId differenti per ogni applicativo mittente. Analogamente è ora possibile registrare proprietà diverse per ogni applicativo rispetto all'api invocata.
