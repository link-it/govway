.. _maxRequests:

Rate Limiting - Numero Complessivo Richieste Simultanee
-------------------------------------------------------

GovWay consente di fissare un numero limite complessivo, indipendente
dalle singole APIs, riguardo alle richieste gestibili simultaneamente
dal gateway, bloccando le richieste in eccesso. Nell'installazione di
default tale limite è fissato a 200 richieste simultanee.

Il limite deve essere allineato rispetto al numero di connessioni
simultanee consentite dal frontend web e/o dall'application server. Ad
esempio su tomcat tale parametro è configurabile all'interno del file
*conf/server.xml* nell'attributo *maxThreads* degli elementi
*connector*.

Per modificare la configurazione sul numero limite di richieste
simultanee accedere alla voce *'Configurazione - Controllo Traffico'*
del menù, sezione *'Limitazione Numero di Richieste Complessive'*.

    .. figure:: ../_figure_installazione/govwayConsole_maxThreads.png
        :scale: 100%
        :align: center
	:name: inst_maxThreadsFig

        Numero Richieste Simultanee
