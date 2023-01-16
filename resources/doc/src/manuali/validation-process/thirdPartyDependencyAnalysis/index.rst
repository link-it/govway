.. _releaseProcessGovWay_thirdPartyDynamicAnalysis:

Third Party Dependency Analysis
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ogni libreria terza parte utilizzata da GovWay viene sottoposta a verifica di possibile presenza di vulnerabilità di sicurezza note tramite il tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_. la cui configurazione può essere consultata nel file `mvn/dependencies/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/pom.xml>`_.

Il tool è configurato per utilizzare le seguenti base dati di vulnerabilità note:

- `National Vulnerability Database <https://nvd.nist.gov/>`_;
- `NPM Public Advisories <https://www.npmjs.com/advisories>`_;
- `RetireJS <https://retirejs.github.io/retire.js/>`_;
- `Sonatype OSS Index <https://ossindex.sonatype.org/>`_;
- `CISA Known Exploited Vulnerabilities Catalog <https://www.cisa.gov/known-exploited-vulnerabilities-catalog>`_.

L'analisi viene effettuata in automatico ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_, come descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_ci`.

La verifica può essere attivata anche manualmente, effettuando il checkout dei `sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_ come descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_maven`.

Nel caso in cui il processo di verifica, descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_ci`, rilevasse una vulnerabilità, viene avviata una gestione della vulnerabilità come descritto in :ref:`vulnerabilityManagement`.

Altrimenti, se a valle dell'analisi della vulnerabilità rilevata, si riscontrasse un falso positivo (:ref:`vulnerabilityManagement_skip_registry`), questa verrebbe registrata come tale nella configurazione del tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_, in modo che successive verifiche non ne segnalino più la presenza. Maggiori dettagli sulla modalità di registrazione dei falsi positivi nel tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_ vengono forniti nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip`.

.. note::
   Per evitare che il progetto erediti possibili vulnerabilità da software terze parti non utilizzati, tutte e sole le librerie terza parte utilizzate nel progetto govway sono definite puntualmente nei file `mvn/dependencies/*/pom.xml <https://github.com/link-it/govway/tree/master/mvn/dependencies>`_.

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
