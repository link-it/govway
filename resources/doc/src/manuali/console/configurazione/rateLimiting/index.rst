.. _configurazioneRateLimiting_descrizione:

Rate Limiting
~~~~~~~~~~~~~

Questa sezione descrive come creare e attivare le policy di controllo del
traffico:

-  *Registro Policy*: Consente di accedere al Registro delle Policy per
   visualizzare, modificare e creare le policy di controllo istanziabili
   per la configurazione del rate limiting. Tra parentesi viene
   visualizzato il numero di policy attualmente presenti nel registro.
   Questa funzionalità è descritta nella sezione :ref:`registroPolicy`.

-  *Policy Globali*: Consente di accedere al Registro delle Policy
   Attivate in ambito globale, cioè operative sul traffico complessivo
   che transita sul gateway. A queste policy si aggiungono quelle
   eventualmente definite localmente nella configurazione specifica di
   ciascuna erogazione/fruizione.
   Questa funzionalità è descritta nella sezione :ref:`trafficoPolicy`.

.. toctree::
        :maxdepth: 2

        registro
	policy
	statistiche
