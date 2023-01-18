Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2022-46364: aggiornate librerie 'org.apache.cxf:cxf-\*' alla versione 3.5.5 (e dipendenza org.ow2.asm:asm alla versione 9.4);

- CVE-2022-41915: aggiornate librerie 'io.netty:netty-\*' alla versione 4.1.86.Final.

Sono stati risolti i seguenti bug:

- le operazioni riguardanti richieste definite tramite WSDL con stile rpc, non venivano riconosciute dal processo di lettura delle informazioni SOAP in streaming;

- aggiunta proprietà 'validation.rpc.rootElementUnqualified.accept', configurabile nell'erogazione o nella fruizione, che consente di indicare se devono essere accettate o meno richieste RPC il cui root-element non appartiene ad alcun namespace in modo da poter disattivare il comportamento di default del prodotto che consente di accettare le richieste al fine di essere compatibile con framework soap datati;

- un utilizzo di configurazioni che prevedono keystore PKCS12 o CRL in formato PEM su GovWay dispiegato nell'application server JBoss EAP 7.3 (aggiornato all'ultimo patch level) provocava il seguente errore: "java.lang.NoClassDefFoundError: org/bouncycastle/util/encoders/Base64 at org.bouncycastle.jcajce.provider.asymmetric.x509.PEMUtil.readPEMObject()";

- La risoluzione dinamica di una risorsa che riferiva un metodo con un parametro contenente un punto non funzionava.  Ad esempio supponendo di avere nella richiesta un header con nome 'Header3.1', l'espressione '${transportContext:headerFirstValue(Header3.1)}' falliva con il seguente errore: "... resolution failed: method [org.openspcoop2.protocol.engine.URLProtocolContextImpl.getHeaderFirstValue(Header3()] not found ...".

- risolto problema di caching delle richieste su API SOAP, in alcune condizioni limite di errore, dove avveniva una inviduazione errata dell'azione;

- il timer che verifica la disponibilità delle risorse (connessione verso i database di runtime, tracciamento, configurazioni) è adesso configurabile per iterare il controllo x volte prima di segnalare l'anomalia (default: 5 iterazioni, una ogni 500ms). L'iterazione nel controllo serve ad evitare che una singola anomalia (es. di rete) possa bloccare tutta la gestione delle richieste fino al prossimo controllo che per default avviene dopo 30 secondi.

Per la console di gestione sono stati risolti i seguenti bug:

- la selezione di un numero di Entries da visualizzare differente dal default (20) provocava uno stato di attesa infinito della console causato dall'errore: "Uncaught ReferenceError: selectedIndex is not defined";

- la verifica CSRF falliva erroneamente dopo un controllo dei riferimenti di un oggetto in 2 scenari d'uso differenti:

	- entrando nel dettaglio un soggetto (o applicativo o ruolo o scope), controllando i riferimenti dell'oggetto e successivamneto provandolo a salvare;

	- nelle liste controllando i riferimenti di un oggetto e successivamente provandolo ad eliminare;

- era erroneamente concesso modificare la token policy associata ad un applicativo anche se quest'ultimo risultava censito puntualmente nel controllo degli accessi di una erogazione o fruizione;

- la modifica delle credenziali di un applicativo di dominio esterno, per il profilo di interoperabilità 'ModI', non funzionava nel caso di credenziali di tipo 'Authorization PDND' o 'Authorization OAuth' nei seguenti casi:

	- modifica dell valore dell'identificativo;

	- aggiunta o eliminazione di un certificato X.509 all'autorizzazione per gestire l'integrità;

- sono stati corretti i seguenti problemi relativi all'importazione di una configurazione:

	-  se l'archivio importato conteneva 2 applicativi con profilo di interoperabilità 'ModI' di dominio esterno, uno definito con credenziale 'Authorization ModI' tramite un certificato x.509 ed uno definito con credenziale 'Authorization PDND + Integrity' contenente lo stesso certificato x.509, il caricamento falliva segnalando erroneamente la duplicazione dell'associazione del certificato x.509 ai due applicativi, mentre doveva essere permesso poichè uno dei due viene riconosciuto tramite l'identificativo della token policy e non tramite il certificato (poi utilizzato per la verifica dell'integrità);

	- dopo aver esportato una API, la successiva importazione dell’archivio in un’installazione in cui il soggetto di default indicato nell'installer differiva, provocava un fallimento dell'import poichè veniva erroneamente richiesta la presenza del soggetto originale da cui era stato fatto l'export.


