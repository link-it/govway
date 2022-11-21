.. _releaseProcessGovWay_thirdPartyDynamicAnalysis:

Third Party Dependency Analysis
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le librerie incluse nel progetto govway vengono definite nei file `mvn/dependencies/*/pom.xml <https://github.com/link-it/govway/tree/master/mvn/dependencies>`_ del progetto.

Poiché Maven risolve le dipendenze in modo transitivo, e siccome si vuole escludere che vengano inclusi nel progetto librerie terza parte non censite puntualmente, ogni definizione di un jar viene configurata per escludere qualsiasi dipendenza a sua volta richiesta.

Ogni dipendenza di un jar è definita con la seguente struttura dove viene definito l'elemento `'exclusions' <https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html>`_:

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

Ogni libreria terza parte utilizzata viene verifica affinche non sia soggette a vulnerabilità di sicurezza note tramite il tool `OWASP Dependency-Check <https://owasp.org/www-project-dependency-check/>`_ configurato nel file `mvn/dependencies/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/pom.xml>`_. Il tool è configurato per utilizzare le seguenti base dati delle vulnerabilità note:

- `National Vulnerability Database <https://nvd.nist.gov/>`_;
- `NPM Public Advisories <https://www.npmjs.com/advisories>`_;
- `RetireJS <https://retirejs.github.io/retire.js/>`_;
- `Sonatype OSS Index <https://ossindex.sonatype.org/>`_.

L'analisi viene effettuata ad ogni commit sul `master dei sorgenti del progetto <https://github.com/link-it/govway/>`_ dove viene avviata automaticamente una verifica delle librerie nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway/job/GovWay/>`_. Maggiori dettagli vengono forniti nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_ci`.

È attuabile anche una verifica manuale, effettuando il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_ ed avviando una analisi come descritto nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_maven`.

Nella sezione :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip` viene mostrato come vengano gestiti i falsi positivi.

.. toctree::
        :maxdepth: 2
        
	ci_jenkins
	maven
	skip
