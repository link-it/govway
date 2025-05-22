Miglioramenti all'integrazione con la PDND
--------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.16.p2'

È Stato adeguato il profilo di interoperabilità 'ModI' alle nuove linee guida PDND indicate nell'issue 'https://github.com/pagopa/pdnd-interop-frontend/issues/1215':

- rivista la generazione dell’asserzione per la richiesta di voucher PDND, assicurando che includa esclusivamente i claim previsti dalla specifica;

- introdotta la possibilità di configurare l’ID Ente per i soggetti con profilo ModI; in fase di validazione del voucher viene ora verificata la corrispondenza tra il claim producerId e l’ID Ente dell'erogatore configurato;

- introdotta la possibilità di indicare 'eserviceId' e/o 'descriptorId' nella configurazione dell'erogazione; in fase di validazione del voucher viene ora verificata la corrispondenza tra il claim eserviceId e/o il claim descriptorId con i corrispettivi valori configurati;

- infine sono aggiunti controlli espliciti sulla presenza dei claim 'iat', 'exp' e 'nbf' durante la validazione dei voucher PDND.
