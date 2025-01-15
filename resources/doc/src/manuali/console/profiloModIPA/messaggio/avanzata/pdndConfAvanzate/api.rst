.. _modipa_sicurezza_avanzate_pdndConfAvanzata_api:

Integrazione verso le API PDND
---------------------------------

Di seguito vengono fornite i dettagli delle configurazioni che attivano l'integrazione verso le :ref:`modipa_passiPreliminari_api_pdnd`.

**Recupero chiave pubblica tramite kid**

Un token JWT, che contiene nell'header il riferimento (kid) alla chiave pubblica utilizzata per firmare il token, può essere validato attraverso la chiave pubblica recuperata invocando la risorsa '*GET /keys/{kid}*'. Questa modalità si attiva configurando il tipo di truststore indicato nella proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores' registrabile nel file "/etc/govway/modipa_local.properties".

La configurazione di default prevede la registrazione di un solo repository remoto con identificativo di configurazione 'pdnd', associato alla token policy di validazione built-in 'PDND' descritta nella sezione :ref:`modipa_passiPreliminari_trustStore_pdnd`.

È possibile procedere alla registrazione di ulteriori repository indicandoli all'interno della proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores' separando ogni identificatore con la virgola.
Per ogni identificatore di repository dovranno essere definite le proprietà descritte nell'elenco puntato '*Fruizione dell'API PDND da parte di GovWay*' presente nella sezione :ref:`modipa_passiPreliminari_api_pdnd`, utilizzando come prefisso 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.<identificativoRepositoryPDND>.' che contiene nella parte finale l'identificatore del repository. Oltre alle proprietà descritte nella sezione indicate dovranno essere definite anche le seguenti proprietà:

- *name* (obbligatorio): nome del repository;
- *label* (obbligatorio): label associato al repository visualizzato nella console di gestione;
- *tokenPolicy* (obbligatorio): identificativo della token policy di validazione associata al repository.

**Verifica della presenza di eventi**

Per ogni repository registrato viene verificata la presenza di eventi sulla PDND (*GET /events/keys*), che riportano operazioni di modifica o eliminazione di chiavi pubbliche, se risulta attiva la proprietà '*org.openspcoop2.pdd.gestoreChiaviPDND.enabled*' presente nel file "/etc/govway/govway_local.properties" come descritto nell'elenco puntato '*Pull sulla PDND per ottenere gli eventi relativi alle chiavi*' della sezione :ref:`modipa_passiPreliminari_api_pdnd`.

La configurazione di default prevede la verifica degli eventi per qualsiasi repository definito nel file "/etc/govway/modipa_local.properties" all'interno della proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores'.

È possibile attivare una verifica degli eventi puntuale solamente su alcuni repository modificando il file di configurazione "/etc/govway/govway_local.properties" aggiungendo le seguenti proprietà:

- *org.openspcoop2.pdd.gestoreChiaviPDND.remoteStore.checkAllStores* (boolean, default:true): disabilitare la proprietà (false) per effettuare la verifica puntuale;
- *org.openspcoop2.pdd.gestoreChiaviPDND.remoteStore.name*: indicare i nomi dei repository che si desidera verificare puntualmente separandoli con la virgola. Il nome del repository corrisponde al valore associato alla proprietà '*org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.<identificativoRepositoryPDND>.name*' configurato nel file "/etc/govway/modipa_local.properties").

**Raccolta informazioni sul ClientId mittente**

La raccolta di maggiori informazioni relative all'identificativo client presente nel payload dei token di sicurezza JWT viene effettuata, invocando le risorse *GET /clients/{clientId}* e *GET /organizations/{organizationId}*, se viene abilitata la proprietà '*org.openspcoop2.pdd.gestorePDND.clientInfo.enabled*' presente nel file "/etc/govway/govway_local.properties" come descritto nell'elenco puntato '*Erogazione: maggiori informazioni sul mittente*' della sezione :ref:`modipa_passiPreliminari_api_pdnd`. Per attivare la raccolta delle informazioni sul client, oltre all'abilitazione della proprietà è necessario che la token policy sia associata ad un repository su cui è stata attivata la verifica degli eventi descritta precedentemente.
