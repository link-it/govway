.. _releaseProcessGovWay_thirdPartyDynamicAnalysis:

Third Party Dependency Analysis
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ogni libreria terza parte utilizzata da GovWay viene sottoposta a verifica di possibile presenza di vulnerabilità di sicurezza note tramite due tool complementari:

- `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_: utilizza il matching CPE per associare le librerie alle vulnerabilità note. La sua configurazione può essere consultata nel file `mvn/dependencies/pom.xml <https://github.com/link-it/govway/blob/3.4.x/mvn/dependencies/pom.xml>`_.

- `OSV-Scanner <https://google.github.io/osv-scanner/>`_ (Google): utilizza il matching per coordinate Maven (groupId:artifactId:version), più preciso del CPE per l'ecosistema Java. La sua configurazione può essere consultata nel file `mvn/dependencies/pom.xml <https://github.com/link-it/govway/blob/3.4.x/mvn/dependencies/pom.xml>`_ e nello script `mvn/dependencies/osv/osv-scanner.sh <https://github.com/link-it/govway/blob/3.4.x/mvn/dependencies/osv/osv-scanner.sh>`_.

Il duplice controllo è necessario per massimizzare la copertura: i due tool utilizzano database e metodi di matching differenti, e ciascuno rileva vulnerabilità che l'altro potrebbe non individuare.

**OWASP Dependency-Check** è configurato per utilizzare le seguenti basi dati di vulnerabilità note:

- `National Vulnerability Database <https://nvd.nist.gov/>`_;
- `RetireJS <https://retirejs.github.io/retire.js/>`_;
- `CISA Known Exploited Vulnerabilities Catalog <https://www.cisa.gov/known-exploited-vulnerabilities-catalog>`_;
- `Github Advisory Database (via NPM Audit API) <https://github.com/advisories/>`_.

**OSV-Scanner** utilizza il database `OSV <https://osv.dev/>`_ che aggrega vulnerabilità da più fonti ecosystem-native:

- `GitHub Advisory Database <https://github.com/advisories/>`_;
- Maven Advisory;
- e altre fonti aggregate dalla piattaforma OSV.

L'analisi viene effettuata in automatico ad ogni commit sul `branch 3.4.x dei sorgenti del progetto <https://github.com/link-it/govway/tree/3.4.x/>`_, come descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_ci`.

La verifica può essere attivata anche manualmente, effettuando il checkout dei `sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_ come descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_maven`.

Nel caso in cui il processo di verifica, descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_ci`, rilevasse una vulnerabilità, viene avviata una gestione della vulnerabilità come descritto in :ref:`vulnerabilityManagement`.

Altrimenti, se a valle dell'analisi della vulnerabilità rilevata, si riscontrasse un falso positivo (:ref:`vulnerabilityManagement_skip_registry`), questa verrebbe registrata come tale nella configurazione di entrambi i tool, in modo che successive verifiche non ne segnalino più la presenza. Maggiori dettagli sulla modalità di registrazione dei falsi positivi vengono forniti nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip`.

.. note::
   Per evitare che il progetto erediti possibili vulnerabilità da software terze parti non utilizzati, tutte e sole le librerie terza parte utilizzate nel progetto govway sono definite puntualmente nei file `mvn/dependencies/*/pom.xml <https://github.com/link-it/govway/tree/3.4.x/mvn/dependencies>`_.

   Per ognuna di tali librerie, maven è configurato per il download puntuale del solo archivio jar interessato, escludendo esplicitamente il download ricorsivo degli archivi jar indicati come dipendenze, utilizzando l'elemento `'exclusions' <https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html>`_, come mostrato di seguito:

::

	<dependency>
		<groupId>...</groupId>
		<artifactId>....</artifactId>
		<version>....</version>
		<exclusions>
		        <exclusion>
		                <groupId>*</groupId>
		                <artifactId>*</artifactId>
		        </exclusion>
		</exclusions>
	</dependency>


.. toctree::
        :maxdepth: 2
        
	ci_jenkins
	maven
	skip
