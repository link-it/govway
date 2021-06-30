.. _3.3.4.2_bug:

Bug Fix 3.3.4.p2
----------------

Sono stati risolti i seguenti bug:

- gli scope presenti in un access token con formato JWT non venivano identificati se la Token Policy associata per la validazione utilizzata il parser 'OpenID Connect - ID Token' comportando un errore di autorizzazione 'AuthorizationMissingScope';

- in caso di installazione Multi-Tenant, gli applicativi interni 'ModI' di un Tenant non venivano identificati sull'erogazione di un altro Tenant, facendo fallire eventuali autorizzazioni puntuali configurate nel controllo degli accessi;

- corretta un'anomalia presente nel connettore https, dove se veniva impostata una configurazione errata (es. path di un keystore inesistente), veniva segnalato un errore generico 'no SSLSocketFactory specified' invece della motivazione puntuale;

- con l'introduzione della funzionalità di ottimizzazione delle connessioni, la consegna con connettore multipli 'Più Destinatari' su API Soap con profilo 'oneway' falliva con un errore simile al seguente: Riscontrato errore durante la gestione del messaggio [EJBUtils.sendToConsegnaContenutiApplicativi(RichiestaApplicativa)]: GESTORE_MESSAGGI, Errore di aggiornamento proprietario Messaggio INBOX/xxxx: null


Per la console di gestione sono stati risolti i seguenti bug:

- la console non consentiva l'aggiornamento delle chiavi private registrate in modalità 'Archivio' (caricate su database), relativamente alla sezione 'sicurezza messaggio' degli applicativi e delle erogazioni nel profilo ModI;

- risolta anomalia presente nella gestione degli allegati delle API e delle Erogazioni e Fruizioni:

	- il caricamento di allegati xml e xsd falliva segnalando erroneamente un contenuto scorretto;

	- il caricamento di allegati di altro tipo veniva completato con successo sulla console anche se poi i dati salvati risultavano corrotti;

- nella scheda di dettaglio di una erogaziono o fruizione, sull'informazione riportata per il connettore è stata aggiunta l'indicazione sull'eventuale policy di negoziazione token configurata;

- i connettori multipli, definiti su di un'erogazione, non venivano cancellati dalla base dati in seguito all'eliminazione dell'erogazione causando il fallimento di una eventuale nuova creazione della medesima erogazione appena eliminata.


Per la console di monitoraggio sono stati risolti i seguenti bug:

- risolto bug che visualizzava erroneamente la checkbox di selezione delle consegne avvenute con connettore multiplo;

- nei csv esportati tramite la funzionalità di reportistica delle configurazioni non venivano riportati i soggetti e gli applicativi autorizzati. Inoltre in presenza di connettori multipli non veniva riportato il nome del connettore.

