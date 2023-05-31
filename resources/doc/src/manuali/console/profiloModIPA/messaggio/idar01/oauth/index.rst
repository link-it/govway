.. _modipa_oauth:

ID_AUTH_REST_01 tramite un Authorization Server OAuth differente dalla PDND
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il token di autenticazione ID_AUTH_REST_01 descritto nella sezione ':ref:`modipa_idar01`' viene generato e firmato dall'applicativo mittente. 

Un token con analoga struttura descritto nella sezione ':ref:`modipa_pdnd`' viene invece rilasciato dalla Piattaforma Digitale Nazionale Dati (PDND). 

Un token di autenticazione che rispetti la struttura prevista dal pattern 'ID_AUTH_REST_01', descritto nella sezione ':ref:`modipa_pdnd`', può essere rilasciato in maniera simile anche da un authorization server differente dalla PDND. GovWay consente di implementare questo scenario con configurazioni molto simili a quelle già descritte nelle sezioni ':ref:`modipa_pdnd_fruizione`' e ':ref:`modipa_pdnd_erogazione`'.

Di seguito vengono forniti i dettagli di configurazione necessari ad utilizzare un authorization server negli scenari di fruizione o erogazione di un servizio.

.. toctree::
   :maxdepth: 2

    Passi per la configurazione di una fruizione <oauth_fruizione>
    Passi per la configurazione di una erogazione <oauth_erogazione>



























