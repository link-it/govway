.. _sicurezzaLivelloMessaggioXMLSignature:

XML Signature
~~~~~~~~~~~~~

Lo schema gestisce la firma del documento XML: in spedizione è disponibile come *XML Signature*, in ricezione come *XML Verifier Signature*.

**Ricezione (Receiver): XML Verifier Signature**

Viene verificata la firma presente nel documento XML ricevuto (:numref:`xmlReceiverSignatureFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioXMLReceiverSignature.png
    :scale: 70%
    :align: center
    :name: xmlReceiverSignatureFig

    Ricezione: XML Verifier Signature

- *TrustStore*: truststore contenente il certificato di verifica, indicato tramite *Alias Certificato* (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Spedizione (Sender): XML Signature**

Il documento XML viene firmato (:numref:`xmlSenderSignatureFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioXMLSenderSignature.png
    :scale: 70%
    :align: center
    :name: xmlSenderSignatureFig

    Spedizione: XML Signature con Key Info

- *Signature Algorithm* (default: RSA-SHA256), *Digest Algorithm* (default: SHA256) e *Canonicalization Algorithm* (default: Exclusive XML Canonicalization 1.0 senza commenti): algoritmi utilizzati per la firma;
- *Key Info*: inserisce nella firma l'elemento KeyInfo con le informazioni sulla chiave di verifica; il *Tipo* indica se inserire il certificato (*x509*) o la sola chiave pubblica (*RSA*) corrispondenti all'*Alias Certificato* indicato;
- *KeyStore*: keystore contenente la chiave privata di firma (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
