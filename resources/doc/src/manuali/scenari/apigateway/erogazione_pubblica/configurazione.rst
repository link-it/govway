.. _scenari_erogazione_pubblica_configurazione:

Configurazione
--------------

In questa sezione vengono mostrate le parti di interesse relative alla configurazione con accesso pubblico.

Si assume che sia stata configurata una API 'PetStore' con il descrittore OpenAPI 3 (scaricabile al seguente `indirizzo <https://raw.githubusercontent.com/link-it/govway/master/resources/openapi/3.0/openapi.yaml>`_).

Per registrare una erogazione dell'API 'PetStore' pubblicamente accessibile si deve cliccare sul pulsante "Aggiungi" all'interno della sezione "Erogazione" (:numref:`erogazione_pubblica_new_fig`):

1. Selezionare l'API "PetStore v1" nel riquadro delle Informazioni Generali.

2. Selezionare l'accesso API "pubblico" nel riquadro Controllo dei Accessi.

3. Verificare che il campo "Endpoint", nel riquadro Connettore, sia stato correttamente inizializzato sulla base del valore di default presente nel descritto della API.

   .. note:: **Verifica del certificato server**
       
       Poichè il servizio PetStore è disponibile solamente in https, modificare il prefisso dell'endpoint fornito.
       Inoltre per validare il certificato ritornato dal server 'petstore.swagger.io' deve essere effettuata una opportuna configurazione del trustStore tls come descritto nella sezione :ref:`avanzate_connettori_https`.
       Poichè non è obiettivo di questo scenario si suggerisce di disabilitare la validazione del certificato server se si rilevano problematiche di trust del certificato server.

   .. figure:: ../../_figure_scenari/ErogazionePubblica_new.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_new_fig

    Creazione di un'erogazione ad accesso pubblico

4. Salvare la configurazione dell'erogazione.

5. Nel dettaglio della configurazione dell'erogazione è possibile vedere come non vi sia abilitato alcun controllo nella voce 'Controllo Accessi'. 

   .. note:: 
       Esaminando l'erogazione preconfigurata si può notare come le risorse siano state suddivise in due gruppi in cui varia proprio il controllo degli accessi, e la risorsa invocata (GET /pet/findByStatus) rientra nel gruppo 'Predefinito' dove il controllo degli accessi risulta disabilitato. L'altro gruppo verrà descritto nello scenario :ref:`scenari_erogazione_oauth`.

   .. figure:: ../../_figure_scenari/ErogazionePubblica_configDetail.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_detail_fig

    Configurazione dell'erogazione
