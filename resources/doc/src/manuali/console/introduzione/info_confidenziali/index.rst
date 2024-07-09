.. _console_informazioni_confidenziali:

Informazioni confidenziali
---------------------------

Nella base dati di GovWay sono presenti diverse informazioni confidenziali, descritte nel seguito di questa sezione suddividendole per categorie di appartenenza.

- :ref:`console_informazioni_confidenziali_password` che verranno utilizzate dalle applicazioni client per invocare le API esposte su GovWay o dagli utenti per collegarsi alle console di gestione e monitoraggio;

- :ref:`console_informazioni_confidenziali_info`; password per accedere a keystore e chiavi private utilizzate per instaurare connessioni HTTPS o per attuare sicurezza di messaggio, credenziali HTTP Basic o API key inviate a un backend, o password per l'accesso a un proxy HTTP;

- :ref:`console_informazioni_confidenziali_proprieta` che possono contenere informazioni confidenziali e quindi necessitano a loro volta di una cifratura;

- :ref:`console_informazioni_confidenziali_keystore` riferiti dalle configurazioni utilizzate per instaurare connessioni HTTPS o per attuare sicurezza di messaggio.

.. toctree::
        :maxdepth: 2

        password
	info
	proprieta
	keystore

