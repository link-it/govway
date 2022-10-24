.. _scenari_erogazione_soap_modipa_auth_pdnd_configurazione:

Configurazione
--------------

.. note::

  Per operare con la govwayConsole in modo conforme a quanto previsto dalla specifica del Modello di Interoperabilità si deve attivare, nella testata dell'interfaccia, il Profilo di Interoperabilità 'ModI'. Si suggerisce inoltre di selezionare il soggetto 'Ente' per visualizzare solamente le configurazioni di interesse allo scenario e nascondere le configurazioni "di servizio" necessarie ad implementare la controparte.

  .. figure:: ../../../_figure_scenari/modipa_profilo.png
   :scale: 80%
   :align: center
   :name: modipa_profilo_soap_pdnd_fig

   Profilo ModI della govwayConsole

Il processo di configurazione per questo scenario è del tutto analogo a quello descritto per lo scenario :ref:`scenari_erogazione_rest_modipa_auth_pdnd_esecuzione`. Nel seguito viene riporta solamente la differenza relativa alla registrazione dell'API. 

**Registrazione API**

Viene registrata l'API "CreditCardVerificationAuthPDND" con il relativo descrittore WSDL. Viene selezionato il solo pattern "ID_AUTH_CHANNEL_01" (sicurezza canale) mentre non deve essere selezionato alcun pattern di sicurezza messaggio nella sezione "ModI" poichè la gestione del token avverrà tramite validazione di un token OAuth attivato sull'erogazione (:numref:`modipa_profili_api_soap_pdnd_fig`).

.. figure:: ../../../_figure_scenari/modipa_profili_api_soap_pdnd.png
 :scale: 80%
 :align: center
 :name: modipa_profili_api_soap_pdnd_fig

 Configurazione Pattern ModI con "ID_AUTH_CHANNEL_01" senza sicurezza messaggio
