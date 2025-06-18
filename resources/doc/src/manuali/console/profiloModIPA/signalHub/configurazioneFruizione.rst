.. _modipa_signalhub_configurazione_fruizione:

Fruizione del servizio per il deposito dei segnali
----------------------------------------------------

In alternativa alla comunicazione diretta con la PDND, il soggetto erogatore può utilizzare una fruizione messa a disposizione da GovWay tramite un’interfaccia semplificata (descritta di seguito nel paragrafo 'Interfaccia di pubblicazione'), che consente di inviare le informazioni relative al dato oggetto della variazione. GovWay si occuperà di cifrare tali informazioni secondo la configurazione descritta nella sezione :ref:`modipa_signalhub_configurazione_erogazione`, generare l’ID del segnale e depositarlo sulla PDND.

Per fare ciò il prodotto rende disponibile una fruizione build-in ':numref:`SignalHubFuizione`' (chiamata ``api-pdnd-push-signals``), erogata dal soggetto PDND e fruita dal soggetto di default definito durante l’installazione. Tale fruizione sarà presente automaticamente se, durante l’installazione di GovWay, viene scelto il profilo ModI tra quelli abilitati. La fruizione deve essere finalizzata negli aspetti descritti nel paragrafo 'Configurazione della fruizione'.

.. figure:: ../../_figure_console/SignalHubFruizione.png
    :scale: 90%
    :align: center
    :name: SignalHubFuizione

    Fruizione built-in per la pubblicazione dei segnali


**Interfaccia di pubblicazione**

Per invocare la fruizione built-in è sufficiente utilizzare la base url di invocazione con il suffisso 'signals' tramite un http GET:
- http://localhost:8080/govway/rest/out/<NOME_SOGGETTO>/PDND/api-pdnd-push-signals/v1/signals

La pubblicazione di una variazione di dato richiede che siano fornite le seguenti informazioni tramite query parameter o http header:

- govway_signal_object_id o GovWay-Signal-ObjectId : l'identificativo in chiaro che verrà pseudoanonimizzato da GovWay;
- govway_signal_object_type o GovWay-Signal-ObjectType: campo libero per GovWay; rappresenta il tipo di oggetto a cui fa riferimento il segnale;
- govway_signal_type o GovWay-Signal-Type: deve essere utilizzato un valore tra CREATE, UPDATE o DELETE;
- govway_signal_service_id o GovWay-Signal-ServiceId: service id della PDND configurato nella maschera del servizio descritto nella sezione :ref:`modipa_signalhub_configurazione_erogazione`

Il servizio per cui si intende pubblicare una variazione di dato può essere riferito in una modalità alternativa al service id tramite i seguenti due parametri:

- govway_signal_service o GovWay-Signal-Service: nome dell'erogazione su GovWay
- govway_signal_service_version o GovWay-Signal-Service-Version: versione dell'erogazione su GovWay

È anche possibile personalizzare la modalità di integrazione con la fruizione built-in attuando una personalizzazione nella scheda di modifica del profilo di interoperabilità relativa alla fruizione come mostrata in figura ':numref:`SignalHubFuizioneConfigurazione`'.

.. figure:: ../../_figure_console/SignalHubFruizioneConfigurazione.png
    :scale: 90%
    :align: center
    :name: SignalHubFuizioneConfigurazione

    Personalizzazione della fruizione built-in

I vari parametri possono:

 - seguire la configurazione di default secondo i nomi dei parametri e degli header http descritti precedentemente;
 - essere inseriti come parte della richiesta: header HTTP, parametri della query, estratti dal contenuto JSON (tramite jsonPath) etc...

Per personalizzare la posizione dei parametri, è possibile consultare tutte le wildcard disponibili tramite il pulsante di help presente accanto all’input del parametro ridefinito.

**Configurazione della fruizione**

La fruizione built-in ':numref:`SignalHubFuizione`' (chiamata ``api-pdnd-push-signals``) deve essere finalizzata negli aspetti descritti di seguito.

- *Endpoint di esposizione delle API della PDND*: nella sezione 'connettore' deve essere indicata la corretta url di esposizione delle API PDND (figura :numref:`fruizioneAPIPDNDpassiPreliminariConnettoreSignalHub`):

	- ambiente di collaudo: https://api.att.signalhub.interop.pagopa.it/1.0/push
	- ambiente di produzione: https://api.signalhub.interop.pagopa.it/1.0/push

	.. note::
	
		Le url indicate potrebbero variare; si raccomanda di ottenere sempre dalla PDND le url aggiornate.

  .. figure:: ../../_figure_console/SignalHubFruizione_connettore.png
    :scale: 70%
    :name: fruizioneAPIPDNDpassiPreliminariConnettoreSignalHub

    Fruizione della API di pubblicazione del segnale: connettore

- *Token Policy di negoziazione del voucher*: nella precedente sezione 'connettore' si è potuto vedere come sia stata associata al connettore una Token Policy di Negoziazione del tipo descritto nella sezione ':ref:`tokenNegoziazionePolicy_jwt`'. La token policy 'api-pdnd' riferita (figura :numref:`fruizioneAPIPDNDpassiPreliminariTokenPolicySignalHub`) deve essere finalizzata nei seguenti aspetti:

	- Url: deve essere indicato l'endpoint di negoziazione del voucher esposto dalla PDND:

		- ambiente di collaudo: https://auth.uat.interop.pagopa.it/token.oauth2
		- ambiente di produzione: https://auth.interop.pagopa.it/token.oauth2

	        .. note::
	
		      Le url indicate potrebbero variare; si raccomanda di ottenere sempre dalla PDND le url aggiornate come indicato nella sezione `Richiesta di un voucher spendibile presso le API di Interoperabilità <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/utilizzare-i-voucher#richiesta-di-un-voucher-spendibile-presso-le-api-di-interoperabilita>`_ dove viene indicato che l'URL dell'endpoint cambia in funzione dell'ambiente e sarà chiaramente visibile sull'interfaccia all'interno del back office.

	- Audience: deve essere indicato il corretto valore atteso dal servizio della PDND, valore che cambia in funzione dell'ambiente:

		- ambiente di collaudo: auth.uat.interop.pagopa.it/client-assertion
		- ambiente di produzione: auth.interop.pagopa.it/client-assertion

	        .. note::
	
		      I valori indicati potrebbero variare; si raccomanda di ottenere sempre dalla PDND i valori aggiornati.

  .. figure:: ../../_figure_console/fruizioneAPI_PDND_tokenPolicy.png
    :scale: 70%
    :name: fruizioneAPIPDNDpassiPreliminariTokenPolicySignalHub

    Fruizione della API di pubblicazione del segnale: token policy

- *Materiale crittografico e dati della PDND*: nella sezione 'ModI' devono essere configurati tutti i parametri relativi al materiale crittografico e ai dati identificativi ottenuti dalla PDND in seguito alla registrazione del client di tipo 'api interop' (figura :numref:`fruizioneAPIPDNDpassiPreliminariModISignalHub`):


	- Key Id (kid) del Certificato: identificativo kid della chiave pubblica;
	- Identificativo: clientId associato alla chiave pubblica;
	- Chiave Privata e Chiave Pubblica: indica il path su file system rispettivamente delle chiavi private e pubbliche in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8);
	- Password Chiave Privata: se la chiave privata è cifrata deve essere indicata la password.

	.. note::
	
		Tramite il campo 'Tipo' è possibile utilizzare un tipo di archivio differente dalla coppia di chiavi pubblica e privata come un keystore 'PKCS12', 'JKS' o un archivio json 'JWK'.

  .. figure:: ../../_figure_console/fruizioneAPI_PDND_signalHub.png
    :scale: 70%
    :name: fruizioneAPIPDNDpassiPreliminariModISignalHub

    Fruizione della API di pubblicazione del segnale: profilo 'ModI'

- *Controllo degli Accessi*: si può notare come la fruizione riporta uno "stato rosso" che evidenzia una configurazione incompleta nella parte relativa al *Controllo degli Accessi*. Procedere con la configurazione del :ref:`apiGwControlloAccessi` al fine di registrare almeno un applicativo autorizzato ad invocare la fruizione. Da notare come l'autorizzazione presente 'signal-Hub' attuerà un'ulteriore processo di autorizzazione verificando che l'applicativo identificato sia presente o possieda il ruolo indicato nella configurazione 'Signal-Hub' del servizio per cui si intende depositare un segnale.




**Multi Tenant**

Nel caso di un contesto multi-tenant sarà necessario creare una fruizione per ciascun soggetto multi-tenant interno. Ogni fruizione dovrà possedere come soggetto erogatore il soggetto built-in PDND e come fruitore il soggetto che eroga l’e-service specifico.
