Continuous Integration
---------------------------

-  *Introduzione dell'uso di Jenkins*: ogni commit sul master del progetto (https://github.com/link-it/govway) viene verificato tramite l'esecuzione di oltre 7000 test. Lo stato di ogni commit è verificabile accedendo alla pagina https://jenkins.link.it/govway/job/GovWay/

-  *OWASP Dependency-Check*: tutte le dipendenze relative a jar 3parti vengono adesso verificate per sapere se esistono vulnerabilità conosciute (fase 'verify' di Maven).
