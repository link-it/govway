.. _3.3.16.2_bug:

Bug Fix 3.3.16.p2
------------------

Adeguato il profilo di interoperabilità 'ModI' alle nuove linee guida PDND indicate nell'issue 'https://github.com/pagopa/pdnd-interop-frontend/issues/1215':

- rivista la generazione dell’asserzione per la richiesta di voucher PDND, assicurando che includa esclusivamente i claim previsti dalla specifica;

- introdotta la possibilità di configurare l’ID Ente per i soggetti con profilo ModI; in fase di validazione del voucher viene ora verificata la corrispondenza tra il claim producerId e l’ID Ente dell'erogatore configurato.

Sono stati inoltre corrette le seguenti anomalie:

- aggiunti controlli espliciti sulla presenza dei claim 'iat', 'exp' e 'nbf' durante la validazione dei voucher PDND;

- corretto un malfunzionamento che impediva lo scorrimento di oltre 20 elementi nella cache PDND consultabile tramite console di gestione 'govwayConsole'.


Infine è stato risolto un bug nelle API di configurazione che non consentiva la creazione di un API con interfaccia 'OpenApi3.0'.

