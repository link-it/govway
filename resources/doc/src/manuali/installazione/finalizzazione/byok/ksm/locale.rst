.. _byokInstallKsmLocale:

KSM Locale
-------------------------------------------------------

In questa sezione viene descritta la sintassi da utilizzare per definire i KMS funzionali ad operazioni di cifratura (wrap) o di decifratura (unwrap) dove la master key è presente in un keystore locale (risiedente su filesystem) o all'interno di un HSM;

Di seguito un esempio di KSM basato sulla cifratura di un'informazione tramite una chiave pubblica

::

    # Esempio di KSM Wrap per una cifratura basata su chiave pubblica
    ksm.govway-async-keys-wrap.label=GovWay AsyncKeys Wrap Example
    ksm.govway-async-keys-wrap.type=async-keys-wrap
    ksm.govway-async-keys-wrap.mode=wrap
    ksm.govway-async-keys-wrap.encryptionMode=local
    ksm.govway-async-keys-wrap.local.impl=java
    ksm.govway-async-keys-wrap.local.keystore.type=public
    ksm.govway-async-keys-wrap.local.key.path=/etc/govway/keys/keyPair-test.rsa.publicKey.pem
    ksm.govway-async-keys-wrap.local.key.algorithm=RSA/ECB/OAEPWithSHA-256AndMGF1Padding
    ksm.govway-async-keys-wrap.local.key.wrap=true
    ksm.govway-async-keys-wrap.local.algorithm=AES/CBC/PKCS5Padding
    ksm.govway-async-keys-wrap.local.encoding=base64
    
Il corrispettivo esempio di KSM necessario a decifrare l'informazione tramite la chiave privata associata.

::
    
    # Esempio di KSM Unwrap per una cifratura basata su chiave asincrona
    ksm.govway-async-keys-unwrap.label=GovWay AsyncKeys Unwrap Example
    ksm.govway-async-keys-unwrap.type=async-keys-unwrap
    ksm.govway-async-keys-unwrap.mode=unwrap
    ksm.govway-async-keys-unwrap.encryptionMode=local
    ksm.govway-async-keys-unwrap.local.impl=java
    ksm.govway-async-keys-unwrap.local.keystore.type=keys
    ksm.govway-async-keys-unwrap.local.key.path=/etc/govway/keys/keyPair-test.rsa.pkcs8_encrypted.privateKey.pem
    ksm.govway-async-keys-unwrap.local.key.password=${envj:read(GOVWAY_PRIVATE_KEY_PASSWORD)}
    ksm.govway-async-keys-unwrap.local.publicKey.path=/etc/govway/keys/keyPair-test.rsa.publicKey.pem
    ksm.govway-async-keys-unwrap.local.key.algorithm=RSA/ECB/OAEPWithSHA-256AndMGF1Padding
    ksm.govway-async-keys-unwrap.local.key.wrap=true
    ksm.govway-async-keys-unwrap.local.algorithm=AES/CBC/PKCS5Padding
    ksm.govway-async-keys-unwrap.local.encoding=base64

L'operazione viene descritta da un insieme di direttive definite tramite la sintassi:

- '*ksm.<idKsm>.local.<direttiva>*'

Di seguito vengono fornite tutte le direttive supportate:

- *impl* [required]: indica il tipo di token cifrato prodotto o atteso per essere decifrato:

     - *jose*: un token JSON Web Encryption (JWE) conforme al RFC 7516;
     - *java*: viene utilizzata la classe Cipher fornita dal package javax.crypto per cifrare i dati che poi verranno serializzati su file di log a seconda della direttiva '*encoding*' fornita;
     - *openssl*: viene prodotto un cipher text, attraverso una chiave derivata da una password, che può essere decifrato utilizzando i comandi di encryption 'openssl'; richiede un keystore di tipo 'pass'.

- *encoding* [required; mode=java|openssl]: indica il tipo di codifica utilizzato per la rappresentazione dei dati cifrati:

     - *base64*: rappresentazione base64;
     - *hex*: rappresentazione esadecimale;

- *keystore.type* [required]: indica il tipo di keystore utilizzato dove è presente la chiave di cifratura da utilizzare:
  
     - *symm*: indica l'utilizzo di una chiave simmetrica fornita attraverso tramite la direttiva:

            - *key.inline*: chiave simmetrica (es. Chiave AES dovà essere di 16, 24 o 32 byte);
            - *key.path*: [ignorata se presente 'key.inline'] path su filesystem ad una chiave simmetrica (es. Chiave AES dovà essere di 16, 24 o 32 byte);
            - *key.encoding*: [optional; base64/hex] consente di indicare la codifica della chiave;

     - *pass*: indica la generazione di una chiave derivata da una password attraverso le seguenti direttive (non utilizzabile con la modalità 'jose'):

            - *password*: la password utilizzata per derivare la chiave;
            - *password.type*: [opzionale; default=openssl-pbkdf2-aes-256-cbc] consente di selezionare l’algoritmo di derivazione tra le seguenti opzioni disponibili:
            
                 - openssl-aes-256-cbc
                 - openssl-pbkdf2-aes-128-cbc
                 - openssl-pbkdf2-aes-192-cbc
                 - openssl-pbkdf2-aes-256-cbc
            - *password.iter*: [optional] consente di indicare il numero di iterazioni durante la derivazione della chiave con l’algoritmo pbkdf2.

     - *jceks*: indica l'utilizzo di una chiave simmetrica presente in un keystore java di tipo JCEKS indirizzato tramite le seguenti direttive:

            - *keystore.path*: path su filesystem del keystore;
            - *keystore.password*: password del keystore;
            - *key.alias*: alias che identifica la chiave simmetrica nel keystore;
            - *key.password*: password della chiave simmetrica;

     - *public*: indica l'utilizzo di una chiave pubblica asimmetrica fornita attraverso le seguenti direttive:

            - *key.inline*: chiave pubblica asimmetrica in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *key.path*: [ignorata se presente 'key.inline'] path su filesystem ad una chiave pubblica asimmetrica in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *key.encoding*: [optional; base64/hex] consente di indicare la codifica della chiave;
	    - *key.wrap* [optional; mode=java; boolean true/false]: indicazione se la chiave pubblica debba essere utilizzata per cifrare direttamente i dati (key.wrap=false) o per cifrare una chiave simmetrica AES generata dinamicamente (key.wrap=true);

               .. note::
                   La modalità 'key.wrap=false' è utilizzabile solamente con informazioni da cifrare "sufficientemente corte" rispetto alla capacità di cifratura della chiave RSA altrimenti si avrà un errore simile al seguente: "too much data for RSA block".
                   
     - *keys*: indica l'utilizzo di chiavi asincrone fornita attraverso le seguenti direttive:

            - *key.inline*: chiave privata in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *key.path*: [ignorata se presente 'key.inline'] path su filesystem ad una chiave privata in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *key.password*: password della chiave privata;
            - *key.encoding*: [optional; base64/hex] consente di indicare la codifica della chiave privata;
	    - *key.wrap* [optional; mode=java; boolean true/false]: indicazione se la chiave pubblica è stata utilizzata per cifrare direttamente i dati (key.wrap=false) o per cifrare una chiave simmetrica AES generata dinamicamente (key.wrap=true);
            - *publicKey.inline*: chiave pubblica in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *publicKey.path*: [ignorata se presente 'publicKey.inline'] path su filesystem ad una chiave pubblica in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
            - *publicKey.encoding*: [optional; base64/hex] consente di indicare la codifica della chiave pubblica;

     - *jwk*: indica l'utilizzo di keystore JWK che può contenere una chiave simmetrica o una chiave pubblica asimmetrica; le direttive supportate sono le seguenti:

            - *keystore.path*: path su filesystem del keystore;
            - *key.alias*: alias che identifica la chiave nel keystore;
            - *key.wrap* [optional; mode=java; boolean true/false]: indicazione se la chiave pubblica debba essere utilizzata per cifrare direttamente i dati (key.wrap=false) o per cifrare una chiave simmetrica AES generata dinamicamente (key.wrap=true);

     - *jks* o *pkcs12*: indica l'utilizzo di un certificato presente in un keystore java di tipo JKS o PKCS12 indirizzato tramite le seguenti direttive:

            - *keystore.path*: path su filesystem del keystore;
            - *keystore.password*: password del keystore;
            - *key.alias*: alias che identifica il certificato nel keystore;
            - *key.wrap* [optional; mode=java; boolean true/false]: indicazione se il certificato debba essere utilizzato per cifrare direttamente i dati (key.wrap=false) o per cifrare una chiave simmetrica AES generata dinamicamente (key.wrap=true);

     - *<tipoRegistratoPKCS11>*: indica l'utilizzo di uno dei tipi di keystore PKCS11 registrati (':ref:`pkcs11`') all'interno del quale è presente il certificato da utilizzare indicato tramite la direttiva:

            - *key.alias*: alias che identifica il certificato nel keystore;
            - *key.wrap* [optional; mode=java; boolean true/false]: indicazione se il certificato debba essere utilizzato per cifrare direttamente i dati (key.wrap=false) o per cifrare una chiave simmetrica AES generata dinamicamente (key.wrap=true);

- *key.algorithm* [required]: specifica l'algoritmo utilizzato per generare o gestire le chiavi crittografiche utilizzate durante il processo di cifratura; 

- *algorithm* [required]: specifica l'algoritmo utilizzato per cifrare effettivamente i dati;

- *include.key.id* [optional; mode=jose; boolean true/false]: indicazione se inserire nell'header del token JWE  (claim 'kid') l'alias della chiave utilizzata per la cifratura;

- *key.id* [optional; mode=jose]: indica il nome della chiave che verrà inserito nel claim 'kid' presente nell'header del token JWE;

- *include.cert* [optional; mode=jose; boolean true/false]: indicazione se inserire nell'header del token JWE  (claim 'x5c') il certificato utilizzato per la cifratura;

- *include.cert.sha1* [optional; mode=jose; boolean true/false]: indicazione se inserire nell'header del token JWE  (claim 'x5t') il digest SHA-1 del certificato utilizzato per la cifratura;

- *include.cert.sha256* [optional; mode=jose; boolean true/false]: indicazione se inserire nell'header del token JWE  (claim 'x5t#256') il digest SHA-256 del certificato utilizzato per la cifratura;

- *include.public.key* [optional; mode=jose; boolean true/false]: indicazione se inserire nell'header del token JWE  (claim 'jwk') la chiave pubblica utilizzata per la cifratura.


**Rappresentazione dei dati cifrati con mode=java**

Come descritto in precedenza indicando la modalità 'java' nella direttiva 'mode' viene utilizzata la classe Cipher fornita dal package javax.crypto per cifrare i dati che poi verranno serializzati su file di log a seconda della direttiva '*encoding*' fornita: base64 o hex.

In funzione del tipo di chiave (simmetrica o asimmetrica) e della direttiva key.wrap la rappresentazione dei dati cifrati conterrà più parti che devono essere considerate per poter effettuare l'operazione inversa di decifratura:

- *chiave simmetrica*:  il dato cifrato è formato da due parti, separate tramite un punto, entrambe codificate in base64 o hex a seconda dell'encoding selezionato; la prima parte rappresenta il Vettore di Inizializzazione (IV) mentre la seconda sono i dati cifrati:

   - <IV>.<DatiCifrati>

- *chiave pubblica asimmetrica con direttiva key.wrap=true*: il dato cifrato è formato da tre parti, separate tramite un punto, entrambe codificate in base64 o hex a seconda dell'encoding selezionato; la prima parte rappresenta la chiave AES generata dinamicamente e cifrata con la chiave pubblica (wrap), la seconda parte il Vettore di Inizializzazione (IV) della cifratura simmetrica e la terza parte sono i dati cifrati con la chiave simmetrica:

    - <WRAP_KEY>.<IV>.<DatiCifrati>

- *chiave pubblica asimmetrica con direttiva key.wrap=false*: è presente solo una parte contenente i dati cifrati con la chiave pubblica asimmetrica:

    - <DatiCifrati>

