.. _headerIntegrazione:

Header di Integrazione
----------------------

In base alle configurazioni prodotte per i servizi, è previsto in
diverse situazioni che gli applicativi scambino dei dati con il gateway.

Nel caso degli applicativi server lo scopo è quello di ricevere dal
gateway i metadati che riguardano la richiesta gestita.

Per gli applicativi client tale scambio si rende necessario al fine di
fornire al gateway specifici parametri necessari a elaborare la
richiesta.

Per consentire lo scambio di tali informazioni, funzionali
all'integrazione tra applicativi e gateway, sono previste alcune
strutture dati, che indichiamo di seguito con il termine *Header di
Integrazione*, che possono essere trasmesse in differenti modalità:

-  *Trasporto*: le informazioni sono contenute nell'header di trasporto

-  *Url Based*: le informazioni sono incapsulate nella url

-  *SOAP*: le informazioni sono incluse in uno specifico header SOAP
   proprietario di GovWay

-  *WS-Addressing*: le informazioni sono incluse in un header SOAP
   secondo il formato standard WS-Addressing

Nel seguito descriviamo le strutture degli header di integrazione attive
per default con l'installazione del prodotto. Tali strutture variano in
funzione del ruolo dell'applicativo. Per l'applicativo client è
possibile fornire informazioni al gateway tramite le modalità
*Trasporto* e *Url Based*. L'applicativo server, invece, riceve le
informazioni dal gateway solamente tramite la modalità *Trasporto*.

.. toctree::
        :maxdepth: 2

	richiestaInoltrata
	headerRisposta
	headerClientGW
	headers
        tokenJson
