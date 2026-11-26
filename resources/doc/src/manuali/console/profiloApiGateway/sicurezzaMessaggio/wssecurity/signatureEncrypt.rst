.. _sicurezzaLivelloMessaggioWSSecuritySignatureEncrypt:

WS-Security Signature Encrypt / Signature Decrypt
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Lo schema combina in un'unica configurazione la firma e la cifratura XML del messaggio: in spedizione è disponibile come *WS-Security Signature Encrypt*, in ricezione come *WS-Security Signature Decrypt*. I campi delle sezioni *Signature* ed *Encrypt*/*Decryption* corrispondono a quelli degli schemi :ref:`sicurezzaLivelloMessaggioWSSecuritySignature` e :ref:`sicurezzaLivelloMessaggioWSSecurityEncrypt`; di seguito vengono descritti i campi specifici della combinazione. Per i campi presenti nella sezione *Informazioni Generali* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

**Ordine delle operazioni**

Il campo *Ordine* indica la sequenza con cui il mittente applica le due operazioni:

- *Cifratura >> Firma* (default): gli elementi vengono prima cifrati e poi firmati; la firma copre gli elementi cifrati e nell'header WS-Security precede gli elementi di cifratura, consentendo a chi riceve di verificarla prima di decifrare;
- *Firma >> Cifratura*: gli elementi vengono prima firmati e poi cifrati; la firma copre il contenuto in chiaro e chi riceve deve decifrare il messaggio prima di poterne verificare la firma.

.. note::

	L'ordine *Cifratura >> Firma*, insieme all'opzione *Decifra solo Firmati* in ricezione, garantisce che un messaggio venga decifrato solo dopo la verifica della firma. Con l'ordine *Firma >> Cifratura*, o in assenza dell'opzione, un messaggio costruito ad arte viene decifrato prima di qualsiasi verifica: con gli algoritmi di cifratura in modalità CBC un mittente malevolo potrebbe dedurre, dall'esito della decifratura di un testo cifrato alterato, informazioni sul contenuto cifrato (attacchi di tipo 'padding oracle').

**Ricezione (Receiver): WS-Security Signature Decrypt**

Viene verificata la firma e vengono decifrati gli elementi cifrati presenti nel messaggio ricevuto (:numref:`wssReceiverSignatureDecryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverSignatureDecrypt.png
    :scale: 70%
    :align: center
    :name: wssReceiverSignatureDecryptFig

    Ricezione: WS-Security Signature Decrypt

- *Ordine* (default: *Cifratura >> Firma*): ordine delle operazioni effettuate dal mittente. In ricezione le azioni vengono verificate in qualsiasi ordine: l'ordine *Cifratura >> Firma* viene imposto solo abilitando l'opzione *Decifra solo Firmati*;
- *Decifra solo Firmati* (presente solo con l'ordine *Cifratura >> Firma*; default: abilitato): vengono decifrati solo gli elementi cifrati coperti da una firma già verificata. Un messaggio con elementi cifrati non firmati, o in cui la firma non precede gli elementi di cifratura nell'header WS-Security, viene rifiutato prima della decifratura con un errore che non dipende dall'esito della decifratura. L'opzione richiede che il mittente includa nella firma tutti gli elementi cifrati, allegati compresi;
- *Timestamp*: richiede anche la presenza di un elemento Timestamp (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`);
- *RSA15 Key Wrap Algorithm* (default: disabilitato): consente di accettare chiavi simmetriche cifrate con l'algoritmo RSA v1.5;
- *Elementi firmati attesi* ed *Elementi cifrati attesi* (default: *Qualsiasi*): elementi che devono risultare firmati e cifrati (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityPartiAttese`);
- *Algoritmi accettati* (default: *Qualsiasi*): algoritmi di firma e di digest accettati (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityAlgoritmiAccettati`);
- *TrustStore* (sezione *Signature*): truststore con i certificati utilizzati per validare il certificato di firma;
- *KeyStore* (sezione *Decryption*): keystore contenente la chiave privata con cui decifrare la chiave simmetrica utilizzata dal mittente (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Spedizione (Sender): WS-Security Signature Encrypt**

Vengono firmati e cifrati gli elementi indicati del messaggio, nell'ordine indicato (:numref:`wssSenderSignatureEncryptFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSignatureEncrypt.png
    :scale: 70%
    :align: center
    :name: wssSenderSignatureEncryptFig

    Spedizione: WS-Security Signature Encrypt con Timestamp

- *Ordine* (default: *Cifratura >> Firma*): ordine con cui vengono applicate la cifratura e la firma;
- *Timestamp*: aggiunge un elemento Timestamp (vedi :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`);
- sezione *Signature*: elementi da firmare, algoritmi, *Key Identifier* e *KeyStore* con la chiave privata di firma, come nello schema :ref:`sicurezzaLivelloMessaggioWSSecuritySignature`. Con l'ordine *Cifratura >> Firma*, affinché il ricevente possa decifrare solo elementi firmati, gli elementi da firmare devono comprendere quelli cifrati (es. *Body Attachments* se vengono cifrati Body e allegati);
- sezione *Encrypt*: elementi da cifrare, algoritmi, *Key Identifier* e *TrustStore* con il certificato del destinatario, come nello schema :ref:`sicurezzaLivelloMessaggioWSSecurityEncrypt`.
