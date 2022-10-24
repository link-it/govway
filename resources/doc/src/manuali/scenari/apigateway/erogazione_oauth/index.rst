.. _scenari_erogazione_oauth:

Erogazione OAuth
================

**Obiettivo**

Esporre un servizio accessibile tramite protocollo OAuth2 (Authorization Code).

**Sintesi**

Assumendo che sia stata effettuata la configurazione di un'erogazione ad accesso pubblico (vedi scenario :ref:`scenari_erogazione_pubblica`), verifichiamo in questo scenario come impostare il sistema di controllo degli accessi affinché il servizio richieda un token di sicurezza, come previsto dal protocollo OAuth2. In particolare la limitazione dell'accesso sarà configurata solo per le operazioni di scrittura, lasciando libero accesso per le letture.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../../_figure_scenari/ErogazioneOAuth.png
    :scale: 60%
    :align: center
    :name: erogazione_oauth_fig

    Erogazione OAuth

I passi previsti sono i seguenti:

1. Il client entra in possesso del token, previa autenticazione e consenso dell'utente richiedente.
2. Il client utilizza il token per l'invio della richiesta.
3. Govway valida il token ricevuto e verifica i criteri di controllo degli accessi.
4. Se la validazione è superata, Govway inoltra la richiesta al servizio erogatore.

.. toctree::
    :maxdepth: 2

    esecuzione
    configurazione
