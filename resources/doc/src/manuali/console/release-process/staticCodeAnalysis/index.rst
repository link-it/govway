.. _releaseProcessGovWay_staticCodeAnalysis:

Static Code Analysis
~~~~~~~~~~~~~~~~~~~~~~~

Identifica possibili vulnerabilità all'interno del codice sorgente tramite il tool `SpotBugs <https://spotbugs.github.io/>`_.

`SpotBugs <https://spotbugs.github.io/>`_ è un tool che analizza il codice per trovare possibili problemi cercando pattern riconducibili a bug difficili da individuare con i test dinamici (:ref:`releaseProcessGovWay_dynamicAnalysis`). 

Utilizzare uno strumento che ispezioni i sorgenti ogni volta che qualcosa viene modificato impedisce al codice di peggiorare lentamente senza che nessuno se ne accorga fino a quando non è troppo tardi. Per tale motivo ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ viene effettuata automaticamente una verifica dei sorgenti nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. Maggiori dettagli vengono forniti nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_ci`.

Il tool viene utilizzato fin dalle fasi di sviluppo dai programmatori tramite il `plugin per Eclipse <https://spotbugs.readthedocs.io/en/latest/eclipse.html>`_ come descritto nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_eclipse`.

Effettuato il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è possibile anche avviare manualmente una analisi statica come descritto nella sezione :ref:`releaseProcessGovWay_staticCodeAnalysis_maven`.

.. note::
      I problemi di sicurezza relativi alle librerie terza parte utilizzate sono trattati separatamente con il lavoro di analisi descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis` e di conseguenza il codice sorgente di tali librerie è escluso dall'analisi del codice statico.


Di seguito vengono forniti i criteri di esecuzione dell'analisi statica tramite il tool 'SpotBugs':

- efforts: viene utilizzato il livello 'max' che consente di avere la massima precisione per trovare più bug (vedi sezione `efforts <https://spotbugs.readthedocs.io/en/stable/effort.html>`_);

- confidence: viene utilizzato il livello 'low' per non filtrare alcun bug (vedi parametro `confidence <https://spotbugs.readthedocs.io/en/stable/ant.html>`_);

- rank: vengono verificati tutti i livelli di bug da 1 a 4 (quelli considerati 'spaventosi'), quelli da 5 a 9 (gravi), da 10 a 14 (preoccupanti) e il livello 15 dei bug di basso profilo.


.. toctree::
        :maxdepth: 2
        
	ci_jenkins
	eclipse
	maven
	
