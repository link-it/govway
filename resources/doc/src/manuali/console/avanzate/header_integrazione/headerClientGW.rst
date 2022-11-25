.. _headerClientGW:

Scambio di informazioni nella richiesta del client verso il gateway
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le informazioni che possono essere fornite dal client al gateway sono
riassunte nella tabella :numref:`headerClient2GwTab` e riguardano le modalità *Trasporto* e *Url
Based* attive di default.

.. table:: Scambio di informazioni nella richiesta del client verso il gateway
   :widths: 30 30 40
   :name: headerClient2GwTab

   =============================   =========================   ==========================================
   Nome Header Trasporto           Nome Url Property           Descrizione
   =============================   =========================   ==========================================
   GovWay-Action                   govway\_action              Identificativo dell'azione invocata. Tale informazione deve essere fornita dal client se il servizio è stato configurato in modalità di identificazione dell'azione *input-based*. (Sezione :ref:`identificazioneAzione`)
   GovWay-Relates-To               govway\_relates\_to         Identificativo di un precedente messaggio a cui la richiesta in essere si riferisce. (Sezione :ref:`correlazioneTransazioniDifferenti`)                                                                                                 
   GovWay-Conversation-ID          govway\_conversation\_id    Identificativo di una conversazione a cui la richiesta in essere si riferisce (Sezione :ref:`correlazioneTransazioniDifferenti`)                                                                                                       
   =============================   =========================   ==========================================

Esiste inoltre la possibilità per il client di fornire informazioni di integrazione tramite un json, come descritto nella sezione :ref:`integrazioneTokenJson`.
