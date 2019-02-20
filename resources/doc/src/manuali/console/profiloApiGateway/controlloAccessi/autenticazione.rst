.. _apiGwAutenticazione:

Autenticazione
^^^^^^^^^^^^^^

In questa sezione è possibile configurare il meccanismo di
autenticazione richiesto per l'accesso al servizio. Come mostrato in :numref:`autenticazione`,
si possono specificare:

-  Il tipo di autenticazione, per il quale si procede come già descritto
   per l'attività di creazione dell'erogazione nella sezione :ref:`erogazione`.

-  Se è stata attivata, al passo precedente, la gestione del token sarà
   possibile aggiungere ulteriori criteri di autenticazione basati sul
   contenuto del token ricevuto. In tal caso è possibile autenticare la
   richiesta sulla base delle seguenti metainformazioni presenti nel
   token: Issuer, ClientId, Subject, Username, Email.

i criteri di autenticazione possono essere attuati sia a livello del
trasporto che del token (se abilitata la gestione del token al passo
precedente).

   .. figure:: ../../_figure_console/Autenticazione.png
    :scale: 100%
    :align: center
    :name: autenticazione

    Configurazione dell’autenticazione del servizio
