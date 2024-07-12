.. _avanzate_fileTrace_proprietaCifrate:

Cifratura delle Proprietà 
--------------------------------------

Come descritto nella sezione :ref:`avanzate_fileTrace_proprieta` è possibile cifrare le informazioni di una proprietà tramite la sintassi:

- '*format.encryptedProperty.<posizione>.<nomeProprietà>=<valoreProprietàDaCifrare>*'

La modalità con cui verranno cifrati i valori della proprietà deve essere indicata tramite un'altra riga definita tramite la seguente sintassi: 

  '*format.encrypt.<posizione>.<nomeProprietà>=<encryptionMode>*'

La modalità '*<encryptionMode>*' deve corrispondere ad una tra quelle definite all'interno del file di configurazione dei topic. Di seguito un esempio di definizione di due modalità:

   ::

      # encryption modes
      encrypt.encMode1.mode=java
      encrypt.encMode1.keystore.type=symm
      encrypt.encMode1.key.path=/tmp/symmetric.secretkey
      encrypt.encMode1.key.algorithm=AES
      encrypt.encMode1.algorithm=AES/CBC/PKCS5Padding
      encrypt.encMode1.encoding=base64

      encrypt.encMode2.mode=jose
      encrypt.encMode2.keystore.type=public
      encrypt.encMode2.key.path=/tmp/publicKey.pem
      encrypt.encMode2.key.id=myKEY
      encrypt.encMode2.key.algorithm=RSA-OAEP-256
      encrypt.encMode2.algorithm=A256GCM
      encrypt.encMode2.include.cert=false
      encrypt.encMode2.include.public.key=true
      encrypt.encMode2.include.key.id=true
      encrypt.encMode2.include.cert.sha1=false
      encrypt.encMode2.include.cert.sha256=false

Ogni modalità viene descritta da un insieme di direttive definite tramite la sintassi:

   - '*encrypt.<encryptionModeName>.<direttiva>*'

Di seguito vengono fornite tutte le direttive supportate:

- *mode* [required]: indica il tipo di token cifrato prodotto:

     - *jose*: viene prodotto un token JSON Web Encryption (JWE) conforme al RFC 7516;
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

- *ksm* [optional]: consente di riferire un KSM di 'unwrap', tramite il proprio identificativo, definito nel file *<directory-lavoro>/byok.properties*; maggiori dettagli vengono forniti nella sezione :ref:`byokInstallKsm`;

- *ksm.param.<paramName>* [optional]: come descritto nella sezione :ref:`byokInstallKsmParametri` ogni KSM può richiedere dei parametri di input che possono essere forniti tramite la direttiva '*ksm.param.<paramName>*'.

**Rappresentazione dei dati cifrati con mode=java**

Come descritto in precedenza indicando la modalità 'java' nella direttiva 'mode' viene utilizzata la classe Cipher fornita dal package javax.crypto per cifrare i dati che poi verranno serializzati su file di log a seconda della direttiva '*encoding*' fornita: base64 o hex.

In funzione del tipo di chiave (simmetrica o asimmetrica) e della direttiva key.wrap la rappresentazione dei dati cifrati conterrà più parti che devono essere considerate per poter effettuare l'operazione inversa di decifratura:

- *chiave simmetrica*:  il dato cifrato è formato da due parti, separate tramite un punto, entrambe codificate in base64 o hex a seconda dell'encoding selezionato; la prima parte rappresenta il Vettore di Inizializzazione (IV) mentre la seconda sono i dati cifrati:

   - <IV>.<DatiCifrati>

- *chiave pubblica asimmetrica con direttiva key.wrap=true*: il dato cifrato è formato da tre parti, separate tramite un punto, entrambe codificate in base64 o hex a seconda dell'encoding selezionato; la prima parte rappresenta la chiave AES generata dinamicamente e cifrata con la chiave pubblica (wrap), la seconda parte il Vettore di Inizializzazione (IV) della cifratura simmetrica e la terza parte sono i dati cifrati con la chiave simmetrica:

    - <WRAP_KEY>.<IV>.<DatiCifrati>

- *chiave pubblica asimmetrica con direttiva key.wrap=false*: è presente solo una parte contenente i dati cifrati con la chiave pubblica asimmetrica:

    - <DatiCifrati>
