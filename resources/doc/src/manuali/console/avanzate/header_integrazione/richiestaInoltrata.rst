.. _headerIntegrazione_richiestaInoltrata:

Scambio di informazioni nella richiesta inoltrata dal gateway al server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le informazioni fornite dal gateway all'applicativo erogatore, sia per
quanto concerne fruizioni che per erogazioni, sono riassunte nella :numref:`headerGw2SrvTab`.

.. table:: Scambio di informazioni nella richiesta inoltrata dal gateway al server
   :widths: auto
   :name: headerGw2SrvTab

   =========================================  ==============================================
   Nome Header Trasporto                      Descrizione                                                                       
   =========================================  ==============================================
    GovWay-Message-ID                         Identificativo del messaggio assegnato da GovWay
    GovWay-Relates-To                         Identificativo del messaggio riferito
    GovWay-Conversation-ID                    Identificativo della conversazione
    GovWay-Transaction-ID                     Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

Inoltre, solamente per quanto concerne le erogazioni, all'applicativo
interno al dominio vengono inoltrate ulteriori meta-informazioni
riguardanti la transazione gestita sul gateway descritte nella :numref:`headerGw2SrvErogTab`.

.. table:: Scambio di informazioni nella richiesta inoltrata dal gateway al server per una Erogazione
   :widths: auto
   :name: headerGw2SrvErogTab

   =========================================  ==============================================================================================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================================================================================
   GovWay-Sender-Type                         Codice che identifica il tipo del mittente
   GovWay-Sender                              Identificativo del mittente
   GovWay-Provider-Type                       Codice che identifica il tipo del destinatario
   GovWay-Provider                            Identificativo del destinatario
   GovWay-Service-Type                        Codice che identifica il tipo del servizio
   GovWay-Service                             Identificativo del servizio
   GovWay-Service-Version                     Progressivo di versione del servizio
   GovWay-Action                              Identificativo dell'azione
   GovWay-Application-Message-ID              Identificativo del messaggio assegnato dall'applicativo
   GovWay-Application                         Identificativo dell'applicativo
   GovWay-Token-Sender-Type                   Codice che identifica il tipo del dominio mittente dell'applicativo identificato tramite autenticazione token
   GovWay-Token-Sender                        Identificativo del dominio mittente dell'applicativo identificato tramite autenticazione token
   GovWay-Token-Application                   Identificativo dell'applicativo identificato tramite autenticazione token
   =========================================  ==============================================================================================================
