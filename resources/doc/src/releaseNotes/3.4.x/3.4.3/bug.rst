Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terze parti:

- CVE-2026-44417, CVE-2026-44618, CVE-2026-44930: aggiornata libreria 'org.apache.cxf:\*' alla versione 4.2.1

- CVE-2026-42402, CVE-2026-42403, CVE-2026-42404: aggiornata libreria 'org.apache.neethi:neethi' alla versione 3.2.2

- CVE-2026-0636, CVE-2026-5588: aggiornata libreria 'org.bouncycastle:\*' alla versione 1.84

- GHSA-h8r8-wccr-v5f2, GHSA-cjmm-f4jc-qw8r, GHSA-cj63-jhhr-wcxv: aggiornata libreria 'org.webjars:swagger-ui' alla versione 5.32.2

- CVE-2026-34478, CVE-2026-34480, CVE-2026-34479, CVE-2026-34477: aggiornata libreria 'org.apache.logging.log4j:log4j-core' alla versione 2.25.4

- CVE-2026-34481: aggiornata libreria 'org.apache.logging.log4j:log4j-layout-template-json' alla versione 2.25.4

- CVE-2026-22732:

  - aggiornata libreria 'org.springframework.security:\*' alla versione 7.0.5
  - aggiornata libreria 'org.springframework:\*' alla versione 7.0.7

- CVE-2026-42579, CVE-2026-42583, CVE-2026-33870, CVE-2026-33871, CVE-2026-47691, CVE-2026-45674, CVE-2026-44249, CVE-2026-45416, CVE-2026-45673: aggiornata libreria 'io.netty:\*' alla versione 4.2.15.Final

È stato inoltre integrato OSV-Scanner (Google) come strumento complementare a OWASP Dependency-Check per la scansione delle vulnerabilità nelle dipendenze, colmando la lacuna di copertura dovuta alla disabilitazione di Sonatype OSS Index e al backlog di arricchimento dell'NVD. È stata aggiunta anche la generazione dell'SBOM (Software Bill of Materials) nei formati CycloneDX e SPDX.

Sono stati risolti i seguenti bug per la componente runtime del gateway:

- Risolto un problema nel parsing degli URL LDAP privi di porta esplicita nei CRL Distribution Point, che causava il fallimento della validazione CRL con errore "Unsupported authority". Nell'ambito dello stesso intervento è stata corretta anche l'anomalia nel recupero delle CRL via LDAP, in cui gli attributi restituiti con l'opzione ;binary non venivano riconosciuti correttamente.

- Risolta un'anomalia per cui, con tracciamento 'FileTrace' abilitato in modalità asimmetrica (solo header oppure solo payload) e registrazione messaggi disabilitata, venivano comunque inserite righe prive di contenuto nella tabella 'dump_messaggi'.

- Corretta la mancata registrazione degli eventi del Controllo del Traffico con identificativo di configurazione superiore a 255 caratteri: ampliata la colonna 'notifiche_eventi.id_configurazione', rimossa dall'indice 'INDEX_EVENTI' e aggiunto un troncamento di sicurezza.

Per la console di gestione sono stati risolti i seguenti bug:

- Risolte alcune anomalie relative al caricamento delle API tramite interfaccia OpenAPI:

  - Risolta un'anomalia che impediva il caricamento di interfacce OpenAPI 3 contenenti schemi con proprietà il cui nome inizia con $ (ad esempio $ref, $id, $schema), tipiche nella modellazione di documenti JSON Schema.

  - Corretta la risoluzione degli alias YAML nelle specifiche OpenAPI, che falliva al superamento del limite di default di SnakeYAML (maxAliasesForCollections=50): il valore è stato portato a 500 ed è stato migliorato il messaggio di errore restituito in caso di fallimento. Aggiunta inoltre una protezione contro gli attacchi "billion laughs" mediante un controllo sulla dimensione del JSON risultante dalla risoluzione degli alias.

- Risolta un'anomalia per cui la funzionalità "Copia" presente accanto al valore del Connettore (e ad altri campi delle viste di riepilogo) restituiva negli appunti un valore alterato rispetto a quello configurato (caratteri come _, \*, [, ], (, ) rimossi, entità HTML decodificate, spazi normalizzati, contenuto troncato a 250 caratteri).

- Risolta un'anomalia nella maschera di editing di applicativi e soggetti configurati con credenziali http-basic: il salvataggio della configurazione, in assenza di modifiche al campo password, causava la cancellazione della password precedentemente impostata.

- Corretto un problema nella configurazione delle trasformazioni (HTTP Headers e Query Parameters) per cui la selezione delle operazioni "update" e "delete" dal menu "Operazione" non veniva recepita correttamente. La stessa anomalia interessava il filtro per HTTP Method nelle liste delle risorse, dove la selezione del metodo "DELETE" non veniva applicata.

- Risolto un problema di gestione della sessione durante il download che causava l'errore "Controllo validità CSRF non superato" quando si effettuava un'operazione successiva al download di un allegato o di altro contenuto esportabile. La correzione interessa tutte le servlet di download/export (allegati, archivi, tracce, diagnostica, resoconto, configurazione di sistema).

- Corretto un problema per cui la verifica dei certificati dei keystore e truststore della JVM falliva negli ambienti in cui le relative password sono cifrate con meccanismi proprietari dell'application server (es. JBoss Vault). Per gestire correttamente questi scenari è stata introdotta la possibilità di disabilitare tale verifica.

- Corretto un comportamento per cui i criteri di ricerca impostati sulle liste di dettaglio (es. ruoli, proprietà, azioni, gruppi, porte) venivano erroneamente mantenuti al passaggio da un'entità all'altra (soggetto, applicativo, API, erogazione/fruizione): le ricerche delle liste figlie vengono ora azzerate al cambio dell'entità padre.

- Corretta la ricerca per nome nell'elenco dei messaggi (input/output) di un'operazione di un port-type, nel dettaglio di un'API SOAP, che non forniva risultati.

Per il profilo di interoperabilità 'ModI' sono state risolte le seguenti anomalie:

- Corretto un problema nel Controllo Accessi delle erogazioni con profilo PDND/OAuth, in cui il campo "Stato" dell'Autenticazione Token risultava erroneamente modificabile sulle API con risorse definite in versioni precedenti del prodotto.

- Il campo 'client_id' è ora configurabile nei claim custom della richiesta ModI quando il keystore è definito nella fruizione o nella token policy.


Per la console di monitoraggio sono stati risolti i seguenti bug:

- Migliorata la gestione degli errori di validazione nelle ricerche (Transazioni, Eventi, Reportistica, Tracciamento PDND): in caso di campi obbligatori mancanti i risultati non vengono più mostrati, il pannello dei filtri resta aperto e la pagina si posiziona automaticamente sul messaggio di errore.

- Pagina "Informazioni": le azioni di svuotamento cache mostrano ora un messaggio di conferma "Cache svuotata correttamente".

- Corretto un problema per cui, nell'export delle transazioni, le informazioni di integrazione (URL di invocazione, indirizzo client, dati del token, ecc.) non venivano riportate nel manifest quando la transazione falliva prima dell'identificazione dell'erogazione o fruizione invocata.

- Corretto un problema per cui i filtri su Soggetti e API associati a un'utenza operatore venivano applicati in OR, consentendo all'operatore di vedere comunque tutte le API dei soggetti associati. I filtri vengono ora applicati in AND, limitando correttamente la visibilità su Transazioni, Configurazione Generale e Allarmi.

- Corretto un errore nella visualizzazione dei grafici a linee e a barre che generava una NumberFormatException, con conseguente blocco dell'applicazione.

- Risolto un problema di rendering del grafico 'Andamento temporale' che si verificava nella visualizzazione per 'Occupazione Banda' o 'Tempo medio Risposta' quando erano selezionati più tipi contemporaneamente.

- Risolto un errore nella generazione del grafico heatmap dell'Analisi Statistica, che si verificava quando una distribuzione presentava più valori sulla stessa cella (es. distribuzione per token info / client id): tali valori vengono ora aggregati correttamente.

- Corretto il ripristino delle "Ricerche Utente" salvate: l'intervallo temporale viene ora ricalcolato in base al periodo memorizzato (ultima ora, ultime 24 ore ...) e le etichette dei filtri autocomplete (soggetto/servizio/API/azione) vengono nuovamente mostrate al caricamento. Risolta inoltre la mancata visualizzazione del selettore delle ricerche salvate per gli utenti ad accesso globale (senza soggetti associati) quando la ricerca contiene un soggetto, su tutti i moduli (transazioni, statistiche, eventi, allarmi).

- Risolta un'anomalia per cui i messaggi diagnostici personalizzati definiti in 'govway_local.msgDiagnostici.properties' non venivano caricati dalla console di monitoraggio e dalle API di monitoraggio.

Per le API di configurazione sono stati risolti i seguenti bug:

- Risolta un'anomalia per cui gli endpoint di lista (es. GET /erogazioni, GET /api) restituivano profilo 'APIGateway' invece del profilo effettivo (es. SPCoop) quando si utilizzavano le opzioni profilo_qualsiasi=true e soggetto_qualsiasi=true.

- Esposto il flag DPoP (Demonstrating Proof-of-Possession) sulle API ModI, finora disponibile solo da console. Risolta inoltre un'anomalia per cui il flag risultava attivabile anche per API ModI la cui sicurezza messaggio era applicata alla sola risposta, configurazione priva di senso poiché DPoP è una proof presente nella richiesta inviata dal fruitore.
