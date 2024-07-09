.. _govwayConfigMap:

GovWay Config Map
------------------

All'interno del file *<directory-lavoro>/govway.map.properties* è possibile razionalizzare una serie di variabili Java che potranno essere riferite in qualsiasi file di proprietà di GovWay presente nella *<directory-lavoro>* tramite la sintassi '${nomeVar}'.

Le variabili potranno inoltre essere accedute all'interno delle varie configurazioni di GovWay come descritto, ad esempio, nella sezione :ref:`valoriDinamici`, tramite la sintassi 'java:NAME' o 'envj:NAME'.

La sintassi da utilizzare all'interno del file *<directory-lavoro>/govway.map.properties* viene descritta nella sezione :ref:`govwayConfigMapConfig`.

.. toctree::
        :maxdepth: 2

	config
