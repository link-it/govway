.. _sicurezzaLivelloMessaggioKeystore:

KeyStore e TrustStore
^^^^^^^^^^^^^^^^^^^^^

Le maschere degli schemi di sicurezza che firmano, verificano, cifrano o decifrano un messaggio contengono una sottosezione per indicare il keystore o il truststore da utilizzare. I campi sono comuni a tutti gli schemi.

**KeyStore** (firma e cifratura in spedizione con chiave privata, decifratura in ricezione)

- *Tipo*: formato del keystore (default: JKS); oltre a JKS e PKCS12 sono selezionabili i tipi associati ai dispositivi HSM eventualmente configurati (:ref:`pkcs11`);
- *Path*: percorso del file del keystore sul sistema in cui è installato GovWay;
- *Password*: password di accesso al keystore;
- *Alias Chiave Privata*: alias con cui la chiave privata è registrata nel keystore;
- *Password Chiave Privata*: password della chiave privata;
- *BYOK Policy*: se il keystore riferito è cifrato, indica il KMS di 'unwrap' da utilizzare per la sua decodifica, scelto tra quelli registrati nella configurazione (vedi :ref:`console_informazioni_confidenziali_keystore`); se non indicata, il keystore viene letto così com'è.

Per i keystore di tipo HSM i campi *Path* e *Password* non sono richiesti.

**TrustStore** (verifica della firma in ricezione, cifratura in spedizione con il certificato del destinatario)

- *Tipo*, *Path* e *Password*: come per il keystore;
- *Alias Certificato* (solo cifratura in spedizione): alias con cui è registrato il certificato del destinatario, la cui chiave pubblica viene utilizzata per cifrare;
- *OCSP Policy* (solo verifica della firma): policy OCSP con cui verificare lo stato di revoca del certificato di firma (vedi :ref:`ocsp`); se non indicata non viene effettuata alcuna verifica OCSP;
- *CRL* (solo verifica della firma, se non è indicata una policy OCSP): abilita la verifica dello stato di revoca tramite liste CRL;
- *CRL File(s)*: percorsi dei file CRL, separati da virgola; obbligatorio se è abilitata la verifica tramite CRL, opzionale se è indicata una policy OCSP, che li utilizza quando prevede anche la verifica tramite CRL.

**Tipi aggiuntivi negli schemi JOSE**

Negli schemi JOSE (API REST JSON) sono selezionabili ulteriori tipi:

- *JWK Set*: file contenente un insieme di chiavi in formato JSON Web Key; la chiave da utilizzare viene indicata tramite il suo identificativo nel campo *Kid* e non è richiesta alcuna password;
- *Key Pair* (solo firma in spedizione): coppia di chiavi indicata tramite i percorsi dei file della *Chiave Privata* e della *Chiave Pubblica*, con l'eventuale password della chiave privata;
- *JCEKS*: keystore contenente chiavi segrete, utilizzato con gli algoritmi simmetrici (es. firma HMAC); la chiave viene indicata tramite *Alias Chiave Segreta* e *Password Chiave Segreta*.

Negli schemi XML Encrypt e XML Decrypt il keystore *JCEKS* contiene la chiave segreta utilizzata quando è abilitata l'opzione *Symmetric Key*.

Le password dei keystore e delle chiavi private sono informazioni confidenziali, gestite come descritto nella sezione :ref:`console_informazioni_confidenziali`.
