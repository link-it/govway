.. _sicurezzaLivelloMessaggioJOSE:

JOSE (API REST JSON)
^^^^^^^^^^^^^^^^^^^^

Gli schemi JOSE (JSON Object Signing and Encryption) sono disponibili per le API REST con messaggi JSON e consentono di firmare (JWS, JSON Web Signature) o cifrare (JWE, JSON Web Encryption) il payload del messaggio. Come per gli schemi WS-Security, si distingue tra il ruolo *Receiver* (verifica o decifratura del messaggio ricevuto) e *Sender* (firma o cifratura del messaggio inviato).

.. _sicurezzaLivelloMessaggioJOSEComuni:

Campi comuni
""""""""""""

- *Tipo* (default: *Compact*): serializzazione del token JOSE; *Compact* produce la rappresentazione compatta (le parti del token separate da '.'), *JSON* la rappresentazione JSON;
- *Ripulisci Messaggio dalle Informazioni Sicurezza* (solo ricezione, default: abilitato): dopo la verifica o la decifratura il messaggio inoltrato contiene il solo payload originale.

**Header JOSE in spedizione**

Abilitando *Include Headers* è possibile indicare quali informazioni inserire nell'header del token:

- *Key Id (kid)*: identificativo della chiave utilizzata;
- *X.509 Certificate*: il certificato viene incluso (*x5c*) oppure referenziato tramite una URL (*x5u*), indicata nel campo *URL*;
- *Digest X.509 Certificate*: impronta del certificato, calcolata con SHA-1 (*x5t*) o SHA-256 (*x5t#S256*);
- *Public Key* (keystore di tipo JWK Set): la chiave pubblica viene inclusa come JSON Web Key (*jwk*) oppure referenziata tramite la URL di un JWK Set (*jku*);
- *Content Type (cty)*, *Type (typ)* e *Critical Headers (crit)*: tipo del contenuto, tipo del token ed elenco, separato da ',', degli header che il destinatario deve obbligatoriamente comprendere;
- *Custom Headers*: da 1 a 10 header aggiuntivi, ciascuno con *Nome* e *Valore*.

**Header JOSE in ricezione**

Abilitando *Use Headers* la chiave o il certificato da utilizzare vengono ricavati dalle informazioni presenti nell'header del token ricevuto, invece che da un keystore o truststore prefissato. Si indicano gli header accettati (*x5c*, *x5u*, *x5t*, *x5t#256*, *jwk*, *jku*) e, in base alla selezione, compaiono le sezioni con cui:

- validare i certificati X.509 ricevuti (truststore, con eventuali verifiche di revoca tramite OCSP o CRL);
- accedere in HTTPS alle URL indicate negli header *x5u* e *jku* (truststore del canale HTTPS).

I campi relativi a keystore e truststore sono descritti nella sezione :ref:`sicurezzaLivelloMessaggioKeystore`.

Schemi di sicurezza
"""""""""""""""""""

.. toctree::
        :maxdepth: 2

        signature
        encrypt
        payloadEnrichment

È inoltre disponibile lo schema *Configurazione Manuale*, descritto nella sezione :ref:`sicurezzaLivelloMessaggioManuale`.
