Nuova funzionalità Proxy
-------------------------

In alcune architetture potrebbe essere presente tra GovWay e l'applicativo da contattare un proxy che media le comunicazioni. 

Nelle architetture più comuni si tratta di un proxy http. In questi casi l'indirizzo remoto dell'applicativo viene censito su GovWay, sul connettore dell'API, configurando anche i parametri di accesso al proxy http (hostname, porta, credenziali).

In altri scenari, più complessi, possono essere presenti proxy non http che possiedono funzioni applicative più evolute, quali ad esempio la gestione di una connessione TLS personalizzata per contesti applicativi dove si utilizzano certificati client e/o trustStore differenti. Normalmente in queste situazioni l'endpoint indicato nella configurazione del connettore su GovWay non è l'indirizzo remoto dell'applicativo ma bensì l'indirizzo del proxy applicativo il quale a sua volta si occuperà di inoltrare la richiesta attraverso la consultazione di un suo registro.

Dalla versione 3.3.1 GovWay permette di supportare un'architettura che presenta un Proxy Applicativo 'stateless' che non necessita di consultare un suo registro. L'indirizzo remoto dell'applicativo viene censito su GovWay e viene inoltrato al proxy tramite un header HTTP o un parametro della url.

Una volta abilitata la funzionalità è possibile configurare:

- Endpoint a cui verranno inoltrate le richieste. L'endpoint può contenere parti dinamiche che verranno risolte a runtime dal Gateway.

- Il nome di un header http o il nome di un parametro della url utilizzato per inoltrare al proxy l'indirizzo remoto. È possibile abilitare la condifica in base64 dell'indirizzo remoto.

