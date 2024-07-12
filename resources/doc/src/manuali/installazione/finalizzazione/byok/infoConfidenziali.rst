.. _byokInstallSecurityGovWay:

Cifratura delle Informazioni Confidenziali
-------------------------------------------------------

L'attivazione di un *Security Engine* (:ref:`byokInstallSecurityEngine`) comporta che venga utilizzato da parte di GovWay per la cifratura e la decifratura delle informazioni confidenziali salvate su database (descritte nella sezione :ref:`console_informazioni_confidenziali_info`) e per le :ref:`console_informazioni_confidenziali_proprieta`.

Per attivare la cifratura deve essere abilitata la proprietà *govway.security* nel file *<directory-lavoro>/byok.properties* valorizzandola con l'identificativo di un (:ref:`byokInstallSecurityEngine`).

Di seguito un esempio in cui viene attivata la cifratura tramite il Security Engine 'gw-pbkdf2':

::

    # Security engine di default utilizzato da GovWay
    govway.security=gw-pbkdf2
