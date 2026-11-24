.. _sicurezzaLivelloMessaggioXMLEncrypt:

XML Encrypt / Decrypt
~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce la cifratura del documento XML: in spedizione è disponibile come *XML Encrypt*, in ricezione come *XML Decrypt*.

Sono supportate le seguenti modalità di gestione della chiave di cifratura:

- *chiave asimmetrica* (default, *Symmetric Key* disabilitato): il documento viene cifrato con una chiave generata per il messaggio, a sua volta cifrata con la chiave pubblica del destinatario;
- *chiave simmetrica con wrapping* (*Symmetric Key* e *Wrapped Key* abilitati): il documento viene cifrato con una chiave generata per il messaggio, a sua volta cifrata con una chiave segreta condivisa;
- *chiave simmetrica diretta* (*Symmetric Key* abilitato e *Wrapped Key* disabilitato): il documento viene cifrato direttamente con la chiave segreta condivisa.

**Ricezione (Receiver): XML Decrypt**

Il documento XML ricevuto viene decifrato (:numref:`xmlReceiverDecryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioXMLReceiverDecrypt.png
    :scale: 70%
    :align: center
    :name: xmlReceiverDecryptFig

    Ricezione: XML Decrypt con chiave asimmetrica

- *Symmetric Key* e *Wrapped Key*: modalità di gestione della chiave utilizzata dal mittente, come descritto in precedenza;
- *KeyStore*: con chiave asimmetrica, keystore contenente la chiave privata di decifratura; con chiave simmetrica, keystore JCEKS contenente la chiave segreta condivisa (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Spedizione (Sender): XML Encrypt**

Il documento XML viene cifrato (:numref:`xmlSenderEncryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioXMLSenderEncrypt.png
    :scale: 70%
    :align: center
    :name: xmlSenderEncryptFig

    Spedizione: XML Encrypt con chiave asimmetrica

- *Symmetric Key* e *Wrapped Key*: modalità di gestione della chiave, come descritto in precedenza;
- *Key Algorithm* (default: AES): algoritmo della chiave generata per il messaggio;
- *Key Wrap Algorithm*: algoritmo con cui viene cifrata la chiave generata; con chiave asimmetrica il default è RSA v1.5, con chiave simmetrica AES-256;
- *Encryption Algorithm* (default: AES-256), *Digest Algorithm* (default: SHA256) e *Canonicalization Algorithm* (default: Inclusive XML Canonicalization 1.0 con commenti): algoritmi utilizzati per la cifratura;
- *TrustStore* (chiave asimmetrica): truststore contenente il certificato del destinatario, indicato tramite *Alias Certificato*;
- *KeyStore* (chiave simmetrica): keystore JCEKS contenente la chiave segreta condivisa (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
