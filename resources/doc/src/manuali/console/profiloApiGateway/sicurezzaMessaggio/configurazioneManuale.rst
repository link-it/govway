.. _sicurezzaLivelloMessaggioManuale:

Configurazione Manuale
^^^^^^^^^^^^^^^^^^^^^^

Selezionando come schema di sicurezza *Configurazione Manuale* la sicurezza del messaggio viene definita tramite un elenco di proprietà (nome e valore) passate direttamente al motore di sicurezza, senza l'ausilio di una maschera. Questa modalità consente di utilizzare opzioni non previste dagli schemi descritti nelle sezioni precedenti.

Le proprietà corrispondono a quelle che le maschere degli schemi valorizzano. I nomi delle proprietà gestite da GovWay sono definiti nella classe `SecurityConstants <https://github.com/link-it/govway/blob/master/core/src/org/openspcoop2/security/message/constants/SecurityConstants.java>`_.

**API SOAP (WS-Security)**

Oltre alle proprietà di GovWay sono utilizzabili quelle definite dalla libreria Apache WSS4J (`WSS4J Configuration <https://ws.apache.org/wss4j/config.html>`_); ad esempio:

- *action*: azioni da eseguire, separate da spazio (es. 'Timestamp Signature');
- *signatureParts* / *encryptionParts*: elementi da firmare o cifrare in spedizione, oppure attesi in ricezione, nel formato descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityParti`;
- *actor*, *mustUnderstand*, *timeToLive*, *futureTimeToLive* e le altre proprietà descritte negli schemi WS-Security.

**API REST (JOSE e XML)**

Le proprietà utilizzabili sono quelle definite nella classe indicata in precedenza (es. *signatureMode*, *signatureDetached*, *joseUseHeaders*, *encryptionMode*, *detachSecurityInfo*).
