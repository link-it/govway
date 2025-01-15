.. _modipa_sicurezza_avanzate_pdndConfAvanzata_multiTenant:

Utilizzo di client 'api interop' in ambiente Multi-Tenant
----------------------------------------------------------------------

La fruizione delle :ref:`modipa_passiPreliminari_api_pdnd` richiedono un `client di tipo 'api interop' <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/client-e-materiale-crittografico>`_  per poter essere consultate; la sua registrazione deve essere effettuata sulla PDND dagli amministratori dell'Ente e il materiale crittografico (le chiavi) associate al client devono essere registrati tramite la console di gestione (govwayConsole) tra i dati della fruizione built-in 'api-pdnd' come descritto nella sezione :ref:`modipa_passiPreliminari_api_pdnd`.

In presenza di una configurazione Multi-Tenant può essere utilizzato un `client di tipo 'api interop' <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/client-e-materiale-crittografico>`_ differente per ogni ente. Per attivare questa configurazione deve essere scelto dove mantenere il materiale crittografico dedicato ad ogni `client 'api interop' <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/client-e-materiale-crittografico>`_ come descritto nella sezione :ref:`modipa_sicurezza_avanzate_fruizione_keystore_scenari`.

.. note::
	Con l'attivazione della configurazione Multi-Tenant la consultazione delle risorse '*GET /keys/{kid}*', '*GET /clients/{clientId}*' e '*GET /organizations/{organizationId}*' avverrà tramite l'utilizzo di un materiale crittografico dedicato al tenant mentre la consultazione degli eventi, utilizzata per conoscere eventuali revoche o aggiornamenti di chiavi pubbliche, verrà comunque effettuata utilizzando il tenant di default dell'installazione. Per dedicare un materiale crittografico anche a questo tipo di consultazione è altrimenti necessario registrare un nuovo repository remoto dedicato al tenant seguendo le indicazioni fornite nella sezione :ref:`modipa_sicurezza_avanzate_pdndConfAvanzata_api`.

La configurazione consigliata è quella di mantenere l'utilizzo della token policy 'api-pdnd' fornita built-in e di definire una nuova fruizione 'api-pdnd' per ogni Tenant. Di seguito gli step richiesti dalla configurazione consigliata:

- Registrare una nuova fruizione delle api-pdnd per ogni tenant sulla falsa riga della fruizione built-in; in ogni fruizione indicare il materiale crittografico del `client di tipo 'api interop' <https://docs.pagopa.it/interoperabilita-1/manuale-operativo/client-e-materiale-crittografico>`_ associato al tenant.

- Editare il file "/etc/govway/modipa_local.properties" per attivare la configurazione Multi-Tenant:

   - aggiungere la seguente proprietà valorizzata con i nomi dei soggetti (tenant), separati da virgola, che devono interagire con le :ref:`modipa_passiPreliminari_api_pdnd`
   
     ::

        org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant=TENANT-1,...,TENANT-N

   - per ogni tenant aggiungere un'ulteriore proprietà valorizzata con la url della fruizione delle api-pdnd specifica per il tenant:

     ::

       org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.baseUrl.TENANT-1=http://127.0.0.1:8080/govway/rest/out/TENANT-1/PDND/api-pdnd/v1/keys
       ...
       org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.baseUrl.TENANT-N=http://127.0.0.1:8080/govway/rest/out/TENANT-N/PDND/api-pdnd/v1/keys
   
Per ogni tenant oltre alla base-url è possibile personalizzare i seguenti aspetti relativi all'invocazione della fruizione:

- credenziali 'http-basic' utilizzate per invocare la fruizione;

  ::

      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.username.TENANT=username   
      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.password.TENANT=password
    
- header HTTP aggiuntivi inviata alla fruizione;

  ::

      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.header.TENANT.NOME_HEADER_1=VALORE_HEADER_1
      ...
      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.header.TENANT.NOME_HEADER_N=VALORE_HEADER_N
    
- parametri della url aggiunti alla base url utilizzata per invocare la fruizione;

  ::

      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.queryParameter.TENANT.NOME_PARAMETRO_1=VALORE_PARAMETRO_1
      ...
      org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.http.queryParameter.TENANT.NOME_PARAMETRO_N=VALORE_PARAMETRO_N
    
- ridefinizione della base url dinamica; invece di fornire una url per ogni tenant è possibile definire una serie di proprietà che consentono di sostituire un pezzo della url di default dinamicamente rispetto al tenant:

     ::

       org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.baseUrl.defaultString=/rest/out/Soggetto/
       org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.baseUrl.placeholder=@TENANT@
       org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.pdnd.multiTenant.baseUrl.tenantString=/rest/out/@TENANT@/

