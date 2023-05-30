.. _modipa_passiPreliminari_keystore:

Chiavi di default per la firma dei token ModI
----------------------------------------------

Nelle figure :numref:`FruizioneModIPA` e :numref:`FruizioneModIPA-PDND` della sezione :ref:`modipa_sicurezzaMessaggio` è stato descritto come GovWay utilizzerà la chiave privata associata all'applicativo interno che ha scaturito la richiesta per firmare il token di sicurezza aggiunto al messaggio in uscita dal dominio di gestione o per l'access token di richiesta del voucher verso la PDND. È possibile attuare uno scenario differente in cui la chiave privata viene definita nella fruizione come descritto nella sezione :ref:`modipa_sicurezza_avanzate_fruizione_keystore`; in questo caso è possibile configure l'utilizzo di un keystore di default indicato nella configurazione descritta di seguito. 

Anche per quanto concerne le risposte che GovWay processa in una erogazione, la chiave privata utilizzata per firmare il token di sicurezza aggiunto alla risposta viene preso dalla configurazione di default descritta di seguito.

È sempre possibile ridefinire per ogni singola API il keystore utilizzato in un richiesta di una fruizione o in una risposta di una erogazione.

La coppia di chiavi di default utilizzate per firmare i token ModI possono essere configurate su GovWay tramite le proprietà presenti nel file "/etc/govway/modipa_local.properties" tutte con prefisso 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.':

- *keyStore.path* (obbligatorio):  indica il path su file system di un keyStore contenente la chiave privata;
- *keyStore.tipo* (obbligatorio): indica il tipo di trustStore (JKS);
- *keyStore.password* (obbligatorio): password per accedere al keyStore;
- *key.alias* (obbligatorio): alias della chiave privata all'interno del keyStore;
- *key.password* (obbligatorio): password della chiave privata all'interno del keyStore;

Inoltre se la chiave pubblica presente nel keystore, definito nella proprietà 'keyStore.path', viene utilizzata come materiale crittografico per registrare un client sulla PDND sono utilizzabili le seguenti due ulteriori proprietà:

- *key.clientId* (opzionale): clientId associato dalla PDND alla chiave pubblica;
- *key.kid* (opzionale): identificativo kid con cui la PDND ha registrato la chiave pubblica.


