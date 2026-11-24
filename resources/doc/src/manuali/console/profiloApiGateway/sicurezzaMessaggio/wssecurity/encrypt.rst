.. _sicurezzaLivelloMessaggioWSSecurityEncrypt:

WS-Security Encrypt / Decrypt
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce la cifratura XML degli elementi del messaggio: in spedizione è disponibile come *WS-Security Encrypt*, in ricezione come *WS-Security Decrypt*. Il mittente cifra gli elementi con una chiave simmetrica generata per il messaggio, a sua volta cifrata con la chiave pubblica del destinatario. Per i campi presenti nella sezione *Informazioni Generali* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

**Ricezione (Receiver): WS-Security Decrypt**

Vengono decifrati gli elementi cifrati presenti nel messaggio ricevuto (:numref:`wssReceiverDecryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverDecrypt.png
    :scale: 70%
    :align: center
    :name: wssReceiverDecryptFig

    Ricezione: WS-Security Decrypt

- *Timestamp*: richiede anche la presenza di un elemento Timestamp (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`);
- *RSA15 Key Wrap Algorithm* (default: disabilitato): consente di accettare chiavi simmetriche cifrate con l'algoritmo RSA v1.5, considerato non sicuro e quindi rifiutato per default;
- *Elementi cifrati attesi* (default: *Qualsiasi*): elementi che devono risultare cifrati (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityPartiAttese`);
- *KeyStore*: keystore contenente la chiave privata con cui decifrare la chiave simmetrica utilizzata dal mittente (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Spedizione (Sender): WS-Security Encrypt**

Vengono cifrati gli elementi indicati del messaggio (:numref:`wssSenderEncryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderEncrypt.png
    :scale: 70%
    :align: center
    :name: wssSenderEncryptFig

    Spedizione: WS-Security Encrypt con Timestamp

- *Timestamp*: aggiunge un elemento Timestamp (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`);
- *Elementi da cifrare* (default: *Body*): con *Configurazione Manuale* si indicano gli *Elementi Obbligatori*, che devono essere presenti nel messaggio, e gli *Elementi Opzionali*, cifrati solo se presenti (formato descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityParti`);
- *Key Wrap Algorithm* (default: RSA-OAEP): algoritmo con cui viene cifrata la chiave simmetrica;
- *Encryption Algorithm* (default: AES-256) e *Digest Algorithm* (default: SHA256): algoritmi utilizzati per la cifratura;
- *Key Identifier - Tipo* (default: *Binary Security Token*): modalità con cui viene referenziato il certificato del destinatario, con le stesse opzioni descritte per lo schema :ref:`sicurezzaLivelloMessaggioWSSecuritySignature` (l'opzione si chiama in questo caso *Include Encryption Token*); è disponibile inoltre *EncryptedKeySHA1 Key Identifier*;
- *TrustStore*: truststore contenente il certificato del destinatario, indicato tramite il campo *Alias Certificato* (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
