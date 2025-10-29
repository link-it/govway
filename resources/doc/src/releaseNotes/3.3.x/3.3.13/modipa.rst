Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Il profilo di interoperabilità 'ModI' è stato adeguato agli `aggiornamenti AGID sulle Linee Guida di Interoperabilità <https://www.agid.gov.it/it/agenzia/stampa-e-comunicazione/notizie/2023/05/23/aggiornati-i-pattern-sicurezza-linee-guida-sullinteroperabilita-tecnica-pa>`_ indicati nella `determina n.128 del 23 maggio 2023 <https://www.agid.gov.it/sites/default/files/repository_files/128_dt_dg_n_128_23_mag_2023_aggiornamento_lg_interoperabilit_tecnica.pdf>`_.

Sono ora supportati anche i seguenti nuovi pattern di sicurezza:

- 'INTEGRITY_REST_02'
- 'AUDIT_REST_01'
- 'AUDIT_REST_02'

L'insieme dei claim da includere nel JWT di Audit 'Agid-JWT-TrackingEvidence' è configurabile consentendo di definire insiemi differenti da associare alle API.

La validazione dei token di tipo AUDIT o INTEGRITY ricevuti può essere effettuata anche utilizzando una validazione 'PDND' configurata per prelevare la chiave pubblica del mittente tramite le API di interoperabilità esposte dalla PDND. La chiave viene poi memorizzata in una cache locale e mantenuta aggiornata tramite gli eventi, emessi dalla PDND, relativi alle chiavi prelevate.

Sono inoltre stati apportati i seguenti miglioramenti:

- è stata rivista la definizione del pattern di sicurezza nella API al fine di indicare chi genera il token ID_AUTH tra il mittente e la PDND;

- aggiunta possibilità di arricchire la traccia di informazioni relative al client-id presente nel token 'ID_AUTH' attraverso informazioni prelevate utilizzando le API della PDND; 

- nella personalizzazione dei keystore è adesso possibile utilizzare una chiave privata, protetta da password o meno, nei formati pkcs1 o pkcs8 in codifica PEM o DER; 

- aggiunta possibilità di utilizzare un keystore JWK sia come keystore che come truststore;

- migliorata diagnostica emessa in presenza di una richiesta con pattern 'INTEGRITY_REST' che presenta l'header digest e un payload http vuoto. L'errore che veniva segnalato nel diagnostico era fuorviante poichè indicava: "Header HTTP 'digest', dichiarato tra gli header firmati, non trovato". Adesso invece l'errore riportato è il seguente: "Header HTTP 'Digest' presente in una risposta con http payload vuoto".

- risolto bug di verifica audience; l'anomalia avveniva in presenza di erogazioni configurate con un valore di audience di default e operazioni suddivise in gruppi di configurazione differente. Per le operazioni associate a gruppi di configurazione diverse da quello predefinito la verifica dell'audience falliva erroneamente.
