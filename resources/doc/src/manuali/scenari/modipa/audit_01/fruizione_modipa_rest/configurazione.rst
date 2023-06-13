.. _scenari_fruizione_rest_modipa_audit_01_configurazione:

Configurazione
--------------

.. note::

  Per operare con la govwayConsole in modo conforme a quanto previsto dalla specifica del Modello di Interoperabilità si deve attivare, nella testata dell'interfaccia, il Profilo di Interoperabilità "ModI". Si suggerisce inoltre di selezionare il soggetto 'Ente' per visualizzare solamente le configurazioni di interesse allo scenario e nascondere le configurazioni "di servizio" necessarie ad implementare la controparte.

  .. figure:: ../../../_figure_scenari/modipa_profilo.png
   :scale: 80%
   :align: center
   :name: modipa_profilo_f_audit_01_fig

   Profilo ModI della govwayConsole

La configurazione dello scenario è del tutto analogo a quello descritto nello scenario :ref:`scenari_fruizione_rest_modipa_auth_pdnd_configurazione` con la sola eccezione del pattern di audit aggiuntivo utilizzato in questo scenario: "AUDIT_REST_01".

**Registrazione API**

Viene registrata l'API "PetStoreAuditPDND" con il relativo descrittore OpenAPI 3. Vengono selezionati i pattern "ID_AUTH_CHANNEL_01" (sicurezza canale) e "ID_AUTH_REST_01" (sicurezza messaggio) nella sezione "ModI" indicando nel campo "Generazione Token" il valore "Authorization PDND". Viene infine abilitata l'opzione 'Informazioni Audit' e selezionato il pattern "AUDIT_REST_01" e lo schema dei dati "Linee Guida ModI" (:numref:`modipa_profili_api_audit_01_fr_fig`). Per ulteriori dettagli sullo schema dei dati di un token di audit si rimanda alle sezioni :ref:`modipa_infoUtente_audit01_schema` e :ref:`modipa_infoUtente_audit01_schema_custom`.

.. figure:: ../../../_figure_scenari/modipa_profili_api_audit01.png
 :scale: 80%
 :align: center
 :name: modipa_profili_api_audit_01_fr_fig

 Configurazione Pattern ModI "AUDIT_REST_01" sulla API REST


**Fruizione**

Nella fruizione "PetStoreAuditPDND", relativa all'API precedentemente inserita, vanno indicati i dati specifici nella sezione "ModI Richiesta" (:numref:`modipa_fruizione_richiesta_integrity_02_fig`) necessari a generare il token 'Agid-JWT-TrackingEvidence'. In particolare è possibile specificare l'audience atteso dall'erogatore e il tempo di validità del token.

.. figure:: ../../../_figure_scenari/modipa_fruizione_richiesta_audit_01.png
 :scale: 80%
 :align: center
 :name: modipa_fruizione_richiesta_audit_01_fig

 Configurazione richiesta della fruizione


