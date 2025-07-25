.. _releaseProcessGovWay_staticCodeAnalysis_spotbugs_ci:

SpotBugs Warnings Jenkins Plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ad ogni commit sul `branch 3.4.x dei sorgenti del progetto <https://github.com/link-it/govway/tree/3.4.x/>`_ viene effettuata automaticamente una verifica dei sorgenti nell'ambiente di `Continuous Integration Jenkins di GovWay <https://jenkins.link.it/govway4/job/GovWay/>`_.

L'analisi produce un `report di dettaglio <https://jenkins.link.it/govway4/job/GovWay/lastCompletedBuild/spotbugs/>`_ sulle vulnerabilità trovate. Per ogni vulnerabilità identificata vengono forniti maggiori dettagli come la severità, la categoria (es. Security), il tipo (codice del pattern che identifica il bug), il package e la classe dove è stato rilevato (es. :numref:`spotbugs_vulnerability_details`). 

.. figure:: ../../_figure_console/spotbugs_vulnerability_details.png
  :scale: 60%
  :name: spotbugs_vulnerability_details

  SpotBugs: dettaglio di una vulnerabilità

Nella `homepage dell'ambiente CI Jenkins di GovWay <https://jenkins.link.it/govway4/job/GovWay/>`_ è anche disponibile un report che visualizza il trend delle vulnerabilità rispetto ai commit effettuati nel tempo (es. :numref:`spotbugs_vulnerability_trend`).

.. figure:: ../../_figure_console/spotbugs_vulnerability_trend.png
  :scale: 80%
  :name: spotbugs_vulnerability_trend

  SpotBugs Warnings Trend

Sono inoltre disponibili `report di dettaglio in vari formati <https://jenkins.link.it/govway4-testsuite/static_analysis/>`_ (:numref:`spotbugs_maven_report_elenco_ci`). 

.. figure:: ../../_figure_console/spotbugs_maven_report_elenco.png
  :scale: 80%
  :name: spotbugs_maven_report_elenco_ci

  SpotBugs: report in vari formati

La figura :numref:`spotbugs_maven_report_ci` mostra un esempio di report nel formato HTML.

.. figure:: ../../_figure_console/spotbugs_maven_report.png
  :scale: 60%
  :name: spotbugs_maven_report_ci

  SpotBugs: html report
