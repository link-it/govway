.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_maven:

OWASP Dependency-Check Maven Plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Effettuato il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è possibile avviare manualmente una analisi delle librerie terza parte utilizzando il seguente comando maven nella radice del progetto:

::

    mvn verify

Per saltare la fase di compilazione, packaging e di test è possibile utilizzare il comando con i seguenti parametri:

::

    mvn verify -Dcompile=none -Dtestsuite=none -Dpackage=none


Al termine dell'analisi viene prodotto un report nella directory 'dependency-check-result' in differenti formati. La figura :numref:`owasp_maven_report` mostra un esempio di report nel formato HTML.

.. figure:: ../../_figure_console/owasp_maven_report.png
  :scale: 80%
  :name: owasp_maven_report

  OWASP Dependency-Check: html report
