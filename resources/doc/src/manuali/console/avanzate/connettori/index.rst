.. _avanzate_connettori:

Connettori
----------

I connettori rappresentano le entità di configurazione che consentono a
GovWay di indirizzare le comunicazioni verso gli attori dei flussi di
erogazione/fruizione gestiti. Nel nostro contesto possiamo distinguere
due tipologie di comunicazioni:

-  *GovWay ---> Applicativo Esterno*, nel caso di fruizioni

-  *GovWay ---> Applicativo Interno*, nel caso di erogazioni

I connettori di GovWay permettono di configurare differenti aspetti
della comunicazione http:

-  *Autenticazione http*: tale funzionalità permette di impostare delle
   credenziali http basic (username e password).

-  *Autenticazione https*: se l'utente lo desidera può personalizzare
   tutti gli aspetti che riguardano una comunicazione sicura su https.

-  *Proxy*: è possibile configurare un proxy http che media la
   comunicazione.

-  *Ridefinisci Tempi Risposta*: permette di ridefinire i tempi di
   risposta che sono stati configurati a livello generale, nell'ambito
   del controllo del traffico (vedi sezione :ref:`console_tempiRisposta`).

Attivando la *modalità avanzata* dell'interfaccia saranno inoltre
disponibili le seguenti opzioni:

-  *Data Transfer Mode*: tramite questa configurazione è possibile
   indicare se la comunicazione deve avvenire in modalità
   transfer-encoding-chunked (streaming) o content length fisso.

-  *Redirect*: tramite questa configurazione è possibile indicare se un
   eventuale redirect ritornato dal server contattato deve essere
   seguito o meno.

-  *Debug*: è possibile abiltare un log verboso di tutta la
   comunicazione.

La govwayConsole, tramite l'interfaccia in modalità *avanzata*, consente
anche di configurare le comunicazioni attraverso connettori non basati
sul protocollo HTTP (o HTTPS). GovWay offre built-in i seguenti
ulteriori connettori:

-  *JMS*: connettore basato sul protocollo JMS

-  *File*: connettore che permette di serializzare il messaggio di
   richiesta su FileSystem ed opzionalmente generare una risposta.

-  *Null*: connettore per test. Si comporta come un servizio Oneway
   ricevendo richieste senza rispondere

-  *NullEcho*: connettore per test. Si comporta come un servizio
   Sincrono rispondendo con un messaggio identico alla richiesta

Nel seguito vengono descritte alcune funzionalità specifiche dei
connettori HTTP e HTTPS. Inoltre viene fornita una descrizione del
connettore built-in JMS.

.. toctree::
        :maxdepth: 2
        
        http
        https
        proxy
	httpOpzioniAvanzate
	debug
	jms
	file
