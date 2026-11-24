.. _sicurezzaLivelloMessaggioJOSESignature:

JWS Signature
~~~~~~~~~~~~~

Lo schema gestisce la firma del payload JSON tramite JSON Web Signature: in spedizione è disponibile come *JWS Signature*, in ricezione come *JWS Verifier Signature*. Per i campi comuni agli schemi JOSE si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioJOSEComuni`.

**Ricezione (Receiver): JWS Verifier Signature**

Viene verificata la firma del token ricevuto; il messaggio inoltrato contiene il payload originale (:numref:`joseReceiverSignatureFig` e :numref:`joseReceiverSignatureHeadersFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSEReceiverSignature.png
    :scale: 70%
    :align: center
    :name: joseReceiverSignatureFig

    Ricezione: JWS Verifier Signature con TrustStore

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSEReceiverSignatureHeaders.png
    :scale: 70%
    :align: center
    :name: joseReceiverSignatureHeadersFig

    Ricezione: JWS Verifier Signature con certificato ricavato dall'header x5c

- *Tipo*: serializzazione attesa (*Compact* o *JSON*);
- *Use Headers*: il certificato o la chiave pubblica di verifica vengono ricavati dall'header del token (vedi :ref:`sicurezzaLivelloMessaggioJOSEComuni`); in modalità *Compact* si indica l'algoritmo di firma atteso (*Signature Algorithm*), oppure *Definito nel token (alg)* per accettare quello indicato nell'header del token;
- *Detached*: il payload è il contenuto del messaggio, mentre il token, privo del payload, viene letto dall'header HTTP o dal parametro della URL indicato (*Tipo*: *Header HTTP* o *URL Query Parameter*); per la serializzazione JSON, *Base64 Encoded* (default: abilitato) indica che il token è codificato in base64;
- *TrustStore* (senza *Use Headers*): truststore contenente il certificato o la chiave di verifica:

  - per i tipi JKS e PKCS12 si indica l'*Alias Certificato*, per il tipo JWK Set il *Kid Certificato*;
  - per il tipo JCEKS (firma HMAC con chiave segreta) si indicano *Alias Chiave Segreta*, *Password Chiave Segreta* e l'algoritmo di firma (default: HS256);
  - per gli altri tipi, in modalità *Compact*, si indica l'algoritmo di firma atteso (*Signature Algorithm*).

**Spedizione (Sender): JWS Signature**

Il payload JSON viene firmato e il messaggio inviato contiene il token JWS (:numref:`joseSenderSignatureFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioJOSESenderSignature.png
    :scale: 70%
    :align: center
    :name: joseSenderSignatureFig

    Spedizione: JWS Signature con header kid e x5c

- *Tipo*: serializzazione del token (*Compact* o *JSON*);
- *Signature Algorithm* (default: RS256): algoritmo di firma; gli algoritmi HMAC (HS256, HS384, HS512) utilizzano una chiave segreta, gli altri (es. RS256, PS256, ES256) una chiave privata;
- *Detached*: il payload rimane nel messaggio e il token, privo del payload, viene inserito nell'header HTTP o nel parametro della URL indicato (*Tipo*: *Header HTTP* o *Parametro URL*); per la serializzazione JSON, *Base64 Encoded* (default: abilitato) indica se il token deve essere codificato in base64;
- *Payload Encoding* (serializzazione JSON non detached, default: abilitato): il payload viene codificato in base64url nel token; disabilitandolo il payload viene inserito senza codifica (RFC 7797);
- *Include Headers*: informazioni da inserire nell'header del token (vedi :ref:`sicurezzaLivelloMessaggioJOSEComuni`);
- *KeyStore*: keystore contenente la chiave di firma; per gli algoritmi HMAC un keystore JCEKS o JWK Set con la chiave segreta, per gli altri un keystore JKS, PKCS12, JWK Set o *Key Pair* (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).
