.. _releaseProcessGovWay_staticCodeAnalysis_src:

Sorgenti soggetti a controllo qualità
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nei `sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_ sono presenti sia i moduli utilizzati dagli archivi binari di GovWay che i componenti che realizzano i test dinamici (:ref:`releaseProcessGovWay_dynamicAnalysis`); quest'ultimi non sono soggetti a controllo qualità. Di seguito viene riportato per ogni modulo soggetto a controllo qualità la posizione all'interno del progetto.

- *utilità di base*; libreria di utility comune utilizzata da tutti gli altri componenti:

	- `tools/utils <https://github.com/link-it/govway/tree/master/tools/utils>`_ (archivio: openspcoop2_utils-<version>.jar)
	- `tools/generic_project <https://github.com/link-it/govway/tree/master/tools/generic_project>`_ (archivio: openspcoop2_generic-project-<version>.jar)

- *runtime gateway*; contiene i moduli che definiscono il runtime di govway:

	- `core/src/org/openspcoop2/message <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/message>`_ (archivio: openspcoop2_message-<version>.jar)
	- `core/src/org/openspcoop2/core <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/core>`_ (archivio: openspcoop2_core-<version>.jar)
	- `core/src/org/openspcoop2/protocol <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/protocol>`_ (archivio: openspcoop2_protocol-api-<version>.jar e openspcoop2_protocol-<version>.jar)
	- `core/src/org/openspcoop2/monitor <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/monitor>`_ (archivio: openspcoop2_monitor-api-<version>.jar e openspcoop2_monitor-<version>.jar)
	- `core/src/org/openspcoop2/security <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/security>`_ (archivio: openspcoop2_security-<version>.jar)
	- `core/src/org/openspcoop2/pdd <https://github.com/link-it/govway/tree/master/core/src/org/openspcoop2/pdd>`_ (archivio: openspcoop2_pdd-<version>.jar)

- *profili di interoperabilità*; ogni profilo viene realizzato come un plugin che consente di personalizzare il comportamento del runtime:

	- 'API Gateway'; `protocolli/trasparente <https://github.com/link-it/govway/tree/master/protocolli/trasparente>`_ (archivio: openspcoop2_trasparente-protocol-<version>.jar)
	- 'ModI'; `protocolli/modipa <https://github.com/link-it/govway/tree/master/protocolli/modipa>`_ (archivio: openspcoop2_modipa-protocol-<version>.jar)
	- 'SPCoop'; `protocolli/spcoop <https://github.com/link-it/govway/tree/master/protocolli/spcoop>`_ (archivio: openspcoop2_spcoop-protocol-<version>.jar)
	- 'eDelivery'; `protocolli/as4 <https://github.com/link-it/govway/tree/master/protocolli/as4>`_ (archivio: openspcoop2_as4-protocol-<version>.jar)
	- 'Fatturazione Elettronica'; `protocolli/sdi <https://github.com/link-it/govway/tree/master/protocolli/sdi>`_ (archivio: openspcoop2_sdi-protocol-<version>.jar)

- *console web*; di seguito vengono descritti tutti i moduli che definiscono le console di gestione e di monitoraggio:

	- console di gestione 'govwayConsole':

		- `tools/web_interfaces/lib/control_station <https://github.com/link-it/govway/tree/master/tools/web_interfaces/control_station>`_ (archivio: openspcoop2_web-govwayConsole-<version>.jar)

	- console di monitoraggio 'govwayMonitor':

		- `tools/web_interfaces/lib/monitor/src/src_core <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/src/src_core>`_ (archivio: openspcoop2_web-govwayMonitor-core-<version>.jar)
		- `tools/web_interfaces/lib/monitor/src/src_transazioni <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/src/src_transazioni>`_ (archivio: openspcoop2_web-govwayMonitor-transazioni-<version>.jar)
		- `tools/web_interfaces/lib/monitor/src/src_statistiche <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/src/src_stat>`_ (archivio: openspcoop2_web-govwayMonitor-statistiche-<version>.jar)
		- `tools/web_interfaces/lib/monitor/src/src_eventi <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/src/src_eventi>`_ (archivio: openspcoop2_web-govwayMonitor-eventi-<version>.jar)
		- `tools/web_interfaces/lib/monitor/src/src_allarmi <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/src/src_allarmi>`_ (archivio: openspcoop2_web-govwayMonitor-allarmi-<version>.jar)
		- 'Pagine JSF'; `tools/web_interfaces/monitor/deploy/pages <https://github.com/link-it/govway/tree/master/tools/web_interfaces/monitor/deploy/pages>`_

	- librerie comuni:

		- 'Audit'; `tools/web_interfaces/lib/audit <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/audit>`_ (archivio: openspcoop2_web-lib-audit-<version>.jar)
		- 'Utenze'; `tools/web_interfaces/lib/users <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/users>`_ (archivio: openspcoop2_web-lib-users-<version>.jar)
		- 'Code'; `tools/web_interfaces/lib/queue <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/queue>`_ (archivio: openspcoop2_web-lib-queue-<version>.jar)
		- 'Widget'; `tools/web_interfaces/lib/mvc <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/mvc>`_ (archivio: openspcoop2_web-lib-mvc-<version>.jar)
		- 'Loader'; `tools/web_interfaces/loader <https://github.com/link-it/govway/tree/master/tools/web_interfaces/loader>`_ (archivio: openspcoop2_web-loaderConsole-<version>.jar)
		- 'Javascript'; `tools/web_interfaces/lib/js <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/js>`_
		- 'Pagine JSP'; `tools/web_interfaces/lib/jsplib <https://github.com/link-it/govway/tree/master/tools/web_interfaces/lib/jsplib>`_

- *api*; le api di configurazione e monitoraggio:

	- api di configurazione 'govwayAPIConfig'; `tools/rs/config/server <https://github.com/link-it/govway/tree/master/tools/rs/config/server>`_ (archivio: openspcoop2_rs-config-server-<version>.jar)
	- api di configurazione 'govwayAPIMonitor'; `tools/rs/monitor/server <https://github.com/link-it/govway/tree/master/tools/rs/monitor/server>`_ (archivio: openspcoop2_rs-monitor-server-<version>.jar)

- *batch*; di seguito vengono descritti i batch utilizzabili su GovWay:

	- batch di generazione delle statistiche; `tools/batch/statistiche <https://github.com/link-it/govway/tree/master/tools/batch/statistiche>`_ (archivio: openspcoop2_batch-statistiche-<version>.jar)
	- batch per la gestione del repository di runtime; `tools/batch/runtime-repository <https://github.com/link-it/govway/tree/master/tools/batch/runtime-repository>`_ (archivio: openspcoop2_batch-runtime-repository-<version>.jar)

.. toctree::
        :maxdepth: 2
        
	eclipse
