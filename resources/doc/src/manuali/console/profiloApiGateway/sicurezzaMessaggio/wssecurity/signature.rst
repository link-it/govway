.. _sicurezzaLivelloMessaggioWSSecuritySignature:

WS-Security Signature
~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce la firma XML degli elementi del messaggio. Per i campi presenti nella sezione *Informazioni Generali* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

**Ricezione (Receiver)**

Viene verificata la firma presente nel messaggio ricevuto (:numref:`wssReceiverSignatureFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverSignature.png
    :scale: 70%
    :align: center
    :name: wssReceiverSignatureFig

    Ricezione: WS-Security Signature con Timestamp

- *Timestamp*: richiede anche la presenza di un elemento Timestamp nell'header WS-Security; il campo *Future Time to Live (seconds)* è descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`;
- *Elementi firmati attesi* (default: *Qualsiasi*): elementi che devono risultare firmati (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityPartiAttese`);
- *TrustStore*: truststore con i certificati utilizzati per validare il certificato di firma, comprensivo delle eventuali verifiche di revoca tramite OCSP o CRL (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Spedizione (Sender)**

Vengono firmati gli elementi indicati del messaggio (:numref:`wssSenderSignatureFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSignature.png
    :scale: 70%
    :align: center
    :name: wssSenderSignatureFig

    Spedizione: WS-Security Signature con Timestamp

- *Timestamp*: aggiunge un elemento Timestamp all'header WS-Security; il campo *Time to Live (seconds)* è descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`;
- *Elementi da firmare* (default: *Body*, oppure *Timestamp Body* se abilitato il Timestamp): con *Configurazione Manuale* si indicano gli *Elementi Obbligatori*, che devono essere presenti nel messaggio, e gli *Elementi Opzionali*, firmati solo se presenti (formato descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityParti`);
- *Signature Algorithm* (default: RSA-SHA256), *Digest Algorithm* (default: SHA256) e *Canonicalization Algorithm* (default: Exclusive XML Canonicalization 1.0 senza commenti, richiesto da WS-I Basic Profile): algoritmi utilizzati per la firma;
- *Key Identifier - Tipo* (default: *Binary Security Token*): modalità con cui il certificato di firma viene referenziato nel messaggio:

  - *Binary Security Token*: il certificato viene incluso nell'header WS-Security; con *Certificato Singolo* (default: abilitato) viene incluso il solo certificato di firma, altrimenti l'intera catena;
  - *Issuer-Serial Security Token Reference*, *X509 Key Identifier*, *Thumbprint Key Identifier*, *SKI Key Identifier*: il certificato viene referenziato tramite, rispettivamente, emittente e numero seriale, certificato codificato, impronta SHA-1 o Subject Key Identifier; per le modalità Issuer-Serial, Thumbprint e SKI l'opzione *Include Signature Token* consente di includere anche il certificato;
  - *Public Key Value*: viene inclusa la sola chiave pubblica;

- *KeyStore*: keystore contenente la chiave privata di firma (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
