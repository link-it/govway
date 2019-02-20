Errori Generati dal Gateway
---------------------------

La gestione dei casi di errore nelle comunicazioni mediate da un Gateway
devono tenere conto di ulteriori situazioni che possono presentarsi
rispetto alla situazione di dialogo diretto tra gli applicativi. Oltre
agli errori conosciuti dagli applicativi, e quindi previsti nei
descrittori del servizio, gli applicativi client possono ricevere
ulteriori errori generati dal gateway.

Govway genera differenti errori a seconda se l'erogazione o la fruizione
riguarda una API di tipologia SOAP (sezione :ref:`soapFault`) o REST (sezione :ref:`rfc7807`).

Per entrambe le tipologie, all'interno dell'errore generato, viene
ritornato al client un codice di errore che rappresenta una
classificazione interna a GovWay dell'errore avvenuto. Nella sezione :ref:`codiciErrore`
vengono riportate due tabelle che descrivono cosa rappresentano tali
codici.

.. toctree::
        :maxdepth: 2
        
        rfc7807
	soapFault
	codiciErrore
