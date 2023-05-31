.. _modipa_idar04:

[INTEGRITY_REST_02] Integrità del payload delle request REST in PDND
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questo pattern di sicurezza consente di estendere il pattern "ID_AUTH_REST_01" aggiungendo un meccanismo che garantisce l'integrità del payload del messaggio.

Il pattern prevede l'indicazione all'interno del token di un identificativo della chiave pubblica (kid) associata alla chiave privata utilizzata dal client per firmare il token. Tale identificativo kid viene generato dalla PDND quando viene registrato il materiale crittografico (chiave pubblica) ed e recuperabile dall'erogatore tramite le API messe a disposizione dalla PDND stessa.

Di seguito vengono forniti i dettagli di configurazione aggiuntivi, rispetto ai passi descritti nella sezione ':ref:`modipa_pdnd`', per gli scenari di fruizione o erogazione di un servizio.

.. toctree::
   :maxdepth: 2

    Passi per la configurazione di una fruizione <pdnd_fruizione>
    Passi per la configurazione di una erogazione <pdnd_erogazione>
