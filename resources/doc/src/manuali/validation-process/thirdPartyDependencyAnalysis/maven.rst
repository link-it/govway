.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_maven:

Verifica manuale tramite Maven
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::
   OSV-Scanner richiede l'installazione del binario `osv-scanner <https://github.com/google/osv-scanner/releases>`_ e la specifica del suo path tramite il parametro ``-Dosv.scanner.home``.

Effettuato il checkout dei `dei sorgenti del progetto GovWay <https://github.com/link-it/govway/>`_, è possibile avviare manualmente una analisi delle librerie terza parte utilizzando il seguente comando maven nella radice del progetto:

::

    mvn verify -Dosv.scanner.home=/path/to/osv-scanner

Per saltare la fase di compilazione, packaging e di test è possibile utilizzare il comando con i seguenti parametri:

::

    mvn verify -Dosv.scanner.home=/path/to/osv-scanner -Dcompile=none -Dtestsuite=none -Dpackage=none

Per eseguire solo OWASP Dependency-Check (senza OSV-Scanner):

::

    mvn verify -Dcompile=none -Dtestsuite=none -Dpackage=none -Dosv=none

Per eseguire solo OSV-Scanner (senza OWASP Dependency-Check):

::

    mvn verify -Dosv.scanner.home=/path/to/osv-scanner -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none


**OWASP Dependency-Check**

Al termine dell'analisi viene prodotto un report nella directory 'dependency-check-result' in differenti formati. La figura :numref:`owasp_maven_report` mostra un esempio di report nel formato HTML.

.. figure:: ../_figure_console/owasp_maven_report.png
  :scale: 80%
  :name: owasp_maven_report

  OWASP Dependency-Check: html report

**OSV-Scanner**

Al termine dell'analisi viene prodotto un report nella directory 'osv-scanner-result' in differenti formati. La figura :numref:`osv_scanner_maven_report` mostra un esempio di report nel formato HTML.

.. figure:: ../_figure_console/osv_scanner_maven_report.png
  :scale: 50%
  :name: osv_scanner_maven_report

  OSV-Scanner: html report
