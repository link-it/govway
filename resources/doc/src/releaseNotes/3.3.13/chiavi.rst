Miglioramenti alla gestione degli archivi delle chiavi
-------------------------------------------------------

Nelle Token Policy sia di validazione che di negoziazione e negli Attribute Authority è stato aggiunto il supporto per i seguenti archivi:

- 'Key Pair': chiave pubblica e privata, protetta da password o meno, nei formati pkcs1 o pkcs8 in codifica PEM o DER; 

- 'Public Key': chiave pubblica in codifica PEM o DER; 

- 'JWK Set': l'archivio è adesso utilizzabile in tutti i contesti in cui è definibile un keystore o un truststore.

La funzionalità 'Verifica Certificati' è stata migliorata al fine di:

- supportare i nuovi tipi di archivio;

- aggiungere la verifica di accesso alla chiave privata tramite la password fornita per i tipi di archivio già esistenti (JKS, PKCS12, ...).


