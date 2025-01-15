.. _modipa_sicurezza_avanzate_pdndConfAvanzata_tipoTokenPolicy:

Suddivisione tra Token Policy PDND e Token Policy OAuth generiche
------------------------------------------------------------------

Tutte le token policy di negoziazione definite con la modalità descritta nella sezione :ref:`tokenNegoziazionePolicy_pdnd` vengono considerate token policy dedicate alla negoziazione di token con la PDND.

Per quanto riguarda invece le token policy di validazione, quelle dedicate alla validazione di token PDND sono tutte quelle indicate nelle proprietà '*org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.<identificativoRepositoryPDND>.tokenPolicy*' configurate nel file "/etc/govway/modipa_local.properties" come descritto precedentemente per i repository utilizzati per il recupero delle chavi pubbliche.
