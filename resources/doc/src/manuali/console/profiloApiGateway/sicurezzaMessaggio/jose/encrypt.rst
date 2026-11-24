.. _sicurezzaLivelloMessaggioJOSEEncrypt:

JWE Encrypt / Decrypt
~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce la cifratura del payload JSON tramite JSON Web Encryption: in spedizione è disponibile come *JWE Encrypt*, in ricezione come *JWE Decrypt*. Il contenuto viene cifrato con una chiave simmetrica (*Content Algorithm*), a sua volta cifrata per il destinatario (*Key Algorithm*). Per i campi comuni agli schemi JOSE si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioJOSEComuni`.

**Ricezione (Receiver): JWE Decrypt**

Il token ricevuto viene decifrato e il messaggio inoltrato contiene il payload originale (:numref:`joseReceiverDecryptFig` e :numref:`joseReceiverDecryptHeadersFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSEReceiverDecrypt.png
    :scale: 70%
    :align: center
    :name: joseReceiverDecryptFig

    Ricezione: JWE Decrypt con KeyStore

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSEReceiverDecryptHeaders.png
    :scale: 70%
    :align: center
    :name: joseReceiverDecryptHeadersFig

    Ricezione: JWE Decrypt con chiave individuata tramite l'header x5c

- *Tipo*: serializzazione attesa (*Compact* o *JSON*);
- *Key Algorithm* e *Content Algorithm* (senza *Use Headers*): algoritmi di cifratura attesi per la chiave e per il contenuto;
- *KeyStore* (senza *Use Headers*): keystore contenente la chiave di decifratura; per i tipi JKS e PKCS12 si indicano *Alias Chiave Privata* e *Password Chiave Privata*, per il tipo JWK Set il *Kid Chiave Privata*, per il tipo JCEKS *Alias Chiave Segreta* e *Password Chiave Segreta* (vedi :ref:`sicurezzaLivelloMessaggioKeystore`);
- *Use Headers*: la chiave di decifratura viene individuata a partire dalle informazioni presenti nell'header del token; in base agli header accettati compaiono:

  - *Certificati JWK (jwk/jku) - KeyStore*: JWK Set contenente le chiavi private corrispondenti alle chiavi pubbliche indicate nel token;
  - *Certificati X.509 (x5c/x5u) - KeyStore*: keystore contenente le chiavi private corrispondenti ai certificati indicati nel token, con l'elenco (da 1 a 10) delle coppie *Alias* e *Password* delle chiavi utilizzabili;
  - *Validazione Certificati X.509 (x5c/x5u) - TrustStore* e *Configurazione HTTPS (jku/x5u) - TrustStore*: come descritto nella sezione :ref:`sicurezzaLivelloMessaggioJOSEComuni`.

**Spedizione (Sender): JWE Encrypt**

Il payload JSON viene cifrato e il messaggio inviato contiene il token JWE (:numref:`joseSenderEncryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSESenderEncrypt.png
    :scale: 70%
    :align: center
    :name: joseSenderEncryptFig

    Spedizione: JWE Encrypt

- *Tipo*: serializzazione del token (*Compact* o *JSON*);
- *Key Algorithm* (default: RSA-OAEP-256): algoritmo con cui viene cifrata la chiave di contenuto;
- *Content Algorithm* (default: A256GCM): algoritmo con cui viene cifrato il contenuto;
- *Compressione (Deflate)*: il contenuto viene compresso prima della cifratura;
- *Include Headers*: informazioni da inserire nell'header del token (vedi :ref:`sicurezzaLivelloMessaggioJOSEComuni`);
- *KeyStore*: keystore contenente il certificato o la chiave pubblica del destinatario, indicati tramite *Alias Certificato* (o il kid per il tipo JWK Set); con il tipo JCEKS viene utilizzata una chiave segreta, indicata tramite *Alias Chiave Segreta* e *Password Chiave Segreta* (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
