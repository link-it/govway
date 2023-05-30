.. _modipa_passiPreliminari_trustStore_x509:

Trust tra fruitore ed erogatore tramite certificati X509
---------------------------------------------------------

Per le richieste provenienti da amministrazioni esterne e contenenti token in cui il trust tra fruitore ed erogatore non avviene tramite PDND, GovWay deve validare il certificato presente all'interno del token di sicurezza al fine di verificare che sia rilasciato da una della CA conosciute, che non sia scaduto e che non sia stato eventualmente revocato.
 
Per effettuare la validazione del certificato di firma utilizzato dal mittente, deve essere configurato opportunamente GovWay per quanto riguarda le seguenti proprietà presenti nel file "/etc/govway/modipa_local.properties" (dove si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione) tutte con prefisso 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.':

- *trustStore.path* (obbligatorio):  indica il path su file system di un trustStore contenente le CA conosciute;
- *trustStore.tipo* (obbligatorio): indica il tipo di trustStore (JKS);
- *trustStore.password* (obbligatorio): password per accedere al trustStore;
- *trustStore.crls* (opzionale): permette di indicare un elenco, separato da virgola, di file CRL;
- *trustStore.ocspPolicy* (opzionale): in alternativa alla validazione tramite CRL è possibile associare una policy OCSP indicando uno dei tipi registrati nel file *<directory-lavoro>/ocsp.properties* come proprietà 'ocsp.<idPolicy>.type'; per ulteriori dettagli si rimanda alle sezioni :ref:`ocspInstall` e :ref:`ocspConfig`.

La configurazione sopra indicata rappresenta la configurazione di default che verrà proposta durante la gestione di una erogazione o di una fruizione. È sempre possibile ridefinire per ogni singola API tale configurazione

.. note::

	**TrustStore delle comunicazioni HTTPS**

	Nei pattern di sicurezza per API REST, dove il riferimento al certificato utilizzato viaggia tramite il claim 'x5u', è possibile che GovWay debba effettuare il download del certificato tramite url https che espongono certificati server non validabili tramite le CA note. In tale contesto è possibile configurare un trustStore personalizzato agendo sulle proprietà presenti nel file "/etc/govway/modipa_local.properties" in maniera simile al trustStore dei certificati. Tali proprietà possiedono il prefisso 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.'.


