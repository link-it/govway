.. _scenari_erogazione_rest_modipa_integrity_02_configurazione:

Configurazione
--------------

.. note::

  Per operare con la govwayConsole in modo conforme a quanto previsto dalla specifica del Modello di Interoperabilità si deve attivare, nella testata dell'interfaccia, il Profilo di Interoperabilità 'ModI'. Si suggerisce inoltre di selezionare il soggetto 'Ente' per visualizzare solamente le configurazioni di interesse allo scenario e nascondere le configurazioni "di servizio" necessarie ad implementare la controparte.

  .. figure:: ../../../_figure_scenari/modipa_profilo.png
   :scale: 80%
   :align: center
   :name: modipa_profilo_integrity_02_fig

   Profilo ModI della govwayConsole


La configurazione dello scenario è del tutto analogo a quello descritto nello scenario :ref:`scenari_erogazione_rest_modipa_auth_pdnd_configurazione` con la sola eccezione del pattern di sicurezza aggiuntivo utilizzato in questo scenario: "INTEGRITY_REST_02".

**Registrazione API**

Viene registrata l'API "PetStoreIntegrity02PDND" con il relativo descrittore OpenAPI 3. Vengono selezionati i pattern "ID_AUTH_CHANNEL_01" (sicurezza canale) e "INTEGRITY_REST_02 con ID_AUTH_REST_01" (sicurezza messaggio) nella sezione "ModI"  indicando nel campo "Generazione Token" il valore "Authorization PDND" (:numref:`modipa_profili_api_integrity02_fig`).

.. figure:: ../../../_figure_scenari/modipa_profili_api_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_profili_api_integrity02_fig

 Configurazione Pattern ModI "INTEGRITY_REST_02 con ID_AUTH_REST_01" sulla API REST


**Applicativo Esterno**

È opzionalmente possibile registrare l'applicativo esterno che corrisponde al fruitore del servizio. Questa scelta può essere fatta in base al tipo di autorizzazione che si è impostata sui fruitori. Vediamo i seguenti casi:

- Se si desidera autorizzare qualsiasi fruitore proveniente dalla PDND, questo passo può anche essere omesso. La validazione del token è sufficiente a stabilire che il fruitore ha ottenuto un voucher dalla PDND valido per il servizio invocato. Questo scenario è quello preconfigurato.
- In alternativa è possibile configurare una autorizzazione puntuale procedendo alla registrazione degli applicativi fornendo i singoli 'client_id' necessari all'identificazione (:numref:`modipa_applicativo_esterno_pdnd_integrity02_fig`). 

.. figure:: ../../../_figure_scenari/modipa_applicativo_pdnd_esterno.png
 :scale: 80%
 :align: center
 :name: modipa_applicativo_esterno_pdnd_integrity02_fig

 Configurazione applicativo esterno (fruitore)


**Erogazione**

Nell'erogazione "PetStoreIntegrity02PDND", relativa all'API precedentemente inserita, vanno indicati i dati specifici nella sezione "ModI Richiesta" (:numref:`modipa_erogazione_richiesta_integrity_pdnd_fig`) necessari per validare le richieste in ingresso relativamente al token 'Agid-JWT-Signature'. Si noti come è stato selezionato un truststore basato sulla PDND al fine di scaricare la chiave pubblica, corrispondente al kid presente nel token, tramite le :ref:`modipa_passiPreliminari_api_pdnd`.

.. figure:: ../../../_figure_scenari/modipa_erogazione_richiesta_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_erogazione_richiesta_integrity02_fig

 Configurazione richiesta dell'erogazione

La sezione "ModI Risposta" si utilizza per indicare i parametri per la produzione del token di sicurezza 'Agid-JWT-Signature' da inserire nel messaggio di risposta (:numref:`modipa_erogazione_risposta_integrity_pdnd_fig`). Si noti come è stato indicato nel campo "Key Id (kid) del Certificato" l'identificativo kid associato alla chiave pubblica registrata sulla PDND.

.. figure:: ../../../_figure_scenari/modipa_erogazione_risposta_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_erogazione_risposta_integrity02_fig

 Configurazione risposta dell'erogazione

