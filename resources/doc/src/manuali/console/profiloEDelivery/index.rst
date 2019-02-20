.. _profiloEdev:

======================
Profilo 'eDelivery'
======================

In questa modalità operativa la govwayConsole consente di produrre
configurazioni di scenari di interoperabilità che si basano sullo
standard europeo eDelivery. Per rendere il trattamento dei messaggi
conforme a tale standard, GovWay si interfaccia ad una installazione del
software Domibus
(https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/Domibus).

Il processo di configurazione rimane strutturalmente analogo a quanto
già descritto per la modalità API Gateway. Sono però presenti proprietà
specifiche del contesto eDelivery i cui valori devono essere foriniti
affinché il dialogo con l'access point Domibus possa essere realizzato
correttamente.

Nel seguito andiamo a descrivere i passi di configurazione evidenziando,
per differenza con il caso API Gateway, gli elementi di eDelivery che
dovranno essere gestiti. Al termine della configurazione è necessario
procedere con l'export dei dati in formato *PMODE*. Il file prodotto è
quello necessario per permettere la configurazione dell'access point
Domibus.

.. toctree::
        :maxdepth: 2

        passiPreliminari
	erogazioni
	fruizioni
	pmode
