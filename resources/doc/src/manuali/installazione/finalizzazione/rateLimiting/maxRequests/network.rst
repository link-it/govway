.. _maxRequests_network:

Numero Massimo Connessioni 'Keep-Alive' per Destinazione
---------------------------------------------------------

GovWay consente di configurare il numero massimo di connessioni HTTP mantenute aperte (in presenza di 'keep-alive') per stessa destinazione. Nell'installazione di
default tale limite è fissato a 200 connessione per stessa destinazione.

Per modificare la configurazione sul numero di connessioni accedere alla voce *'Configurazione - Proprietà di Sistema'*
del menù e modificare il valore associato alla proprietà '*http.maxConnections*'.

    .. figure:: ../../../_figure_installazione/govwayConsole_httpKeeyAliveMaxConnections.png
        :scale: 100%
        :align: center
	:name: inst_httpKeeyAliveMaxConnectionsFig

        Numero Massimo di Connessioni 'Keep-Alive' per Destinazione


.. note::

	Effettuata la modifica dei files è necessario un riavvio dell'Application Server per rendere operative le modifiche.

