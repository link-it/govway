Nuova Gestione degli Errori generati da GovWay
----------------------------------------------

Sono state completamente riviste le informazioni di errore ritornate al client, in seguito ad anomalie rilevate da GovWay sulla richiesta o sulla risposta.

Oltre agli errori già previsti nei descrittori del servizio, gli applicativi client possono pertanto ricevere due tipi di errori generati direttamente da GovWay:

- *Errori Client*: sono identificabili da un codice http 4xx su API REST o da un fault code 'Client' su API SOAP. Indicano che GovWay ha rilevato problemi nella richiesta effettuata dal client (es. errore autenticazione, autorizzazione, validazione contenuti...).

- *Errori Server*: sono identificabili dai codici http 502, 503 e 504 per le API REST o da un fault code 'Server' generato dal Gateway e restituito con codice http 500 per le API SOAP.

La codifica degli errori prodotta dal Gateway permette alle applicazioni client di discriminare tra errori causati da una richiesta errata, per i quali è quindi necessario intervenire sull'applicazione client prima di effettuare nuovi invii, ed errori dovuti allo stato dei servizi invocati, per i quali è invece possibile continuare ad effettuare la richiesta. 

Per ciascun errore GovWay riporta le seguenti informazioni:

- Un codice http su API REST o un fault code su API SOAP.
- Un codice di errore, indicato nell'header http 'GovWay-Transaction-ErrorType', che riporta l'errore rilevato dal gateway (es. AuthenticationRequired, TokenExpired, InvalidRequestContent ...). 
- Un identificativo di transazione, indicato nell'header http 'GovWay-Transaction-ID', che identifica la transazione in errore, utile principalmente per indagini diagnostiche.
- Un payload http, contenente maggiori dettagli sull'errore, opportunamente codificato per API REST (Problem Details - RFC 7807) o SOAP (Fault).

Nella configurazione di default di GovWay, gli errori restituiti ai client non contengono dettagli che possano causare disclosure di informazioni relative al dominio interno. In alcuni casi, per facilitare il supporto alla risoluzione di problemi, è comunque possibile abilitare la generazione di codici più specifici di errore ritornati al client nell'header http 'GovWay-Transaction-ErrorStatus'.
