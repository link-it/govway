.. _modipa_sicurezza_avanzate_pdndConfAvanzata_api_recuperoChiave:

Recupero chiave pubblica tramite kid
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Un token JWT, che contiene nell'header il riferimento (kid) alla chiave pubblica utilizzata per firmare il token, può essere validato attraverso la chiave pubblica recuperata invocando la risorsa '*GET /keys/{kid}*'. Questa modalità si attiva configurando il tipo di truststore indicato nella proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores' registrabile nel file "/etc/govway/modipa_local.properties".

La configurazione di default prevede la registrazione di un solo repository remoto con identificativo di configurazione 'pdnd', associato alla token policy di validazione built-in 'PDND' descritta nella sezione :ref:`modipa_passiPreliminari_trustStore_pdnd`.

È possibile procedere alla registrazione di ulteriori repository indicandoli all'interno della proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores' separando ogni identificatore con la virgola.
Per ogni identificatore di repository dovranno essere definite le proprietà descritte nell'elenco puntato '*Fruizione dell'API PDND da parte di GovWay*' presente nella sezione :ref:`modipa_passiPreliminari_api_pdnd`, utilizzando come prefisso 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.<identificativoRepositoryPDND>.' che contiene nella parte finale l'identificatore del repository. Oltre alle proprietà descritte nella sezione indicate dovranno essere definite anche le seguenti proprietà:

- *name* (obbligatorio): nome del repository;
- *label* (obbligatorio): label associato al repository visualizzato nella console di gestione;
- *tokenPolicy* (obbligatorio): identificativo della token policy di validazione associata al repository.
