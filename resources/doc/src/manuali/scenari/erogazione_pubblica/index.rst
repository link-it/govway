.. |br| raw:: html

    <br/>

.. _scenari_erogazione_pubblica:

Erogazione pubblica
===================

Obiettivo
---------
Esporre tramite Govway un servizio con accesso pubblico (forma anonima).

Sintesi
-------
In questo scenario è richiesta l'esposizione tramite gateway di un servizio da erogare, consentendo il libero accesso ai fruitori, che potranno invocare la relativa interfaccia senza presentare alcuna credenziale.

Per illustrare questo scenario, abbiamo scelto il servizio "PetStore", che sarà reso accessibile da Govway tramite l'interfaccia REST in versione OpenAPI 3.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/ErogazionePubblica.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_fig

    Erogazione ad accesso pubblico


Esecuzione
----------
I fruitori del servizio "PetStore" invocano le operazioni disponibili tramite i propri client senza utilizzare alcuna forma di autenticazione.
Avvalendosi eventualmente del progetto Postman a corredo, eseguire *"1. Erogazione Pubblica (findByStatus)"* per verificare l'esecuzione dell'erogazione del servizio PetStore con libero accesso.

   .. figure:: ../_figure_scenari/ErogazionePubblica_postman.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_postman_fig

    Erogazione pubblica, esecuzione da Postman


Configurazione
--------------
Procediamo con la configurazione dell'erogazione del servizio "PetStore", pubblicamente accessibile, assumendo che la relativa API sia stata precedentemente configurata con il proprio descrittore OpenAPI 3 (descrittore scaricabile al seguente indirizzo: https://raw.githubusercontent.com/Mermade/openapi3-examples/master/fail/apimatic-converted-petstore.json).

La configurazione si effettua dalla govwayConsole, nella sezione "Erogazione > Aggiungi" (:numref:`erogazione_pubblica_new_fig`):

1. Selezionare l'API "PetStore v1" nel riquadro delle Informazioni Generali.

2. Selezionare l'accesso API "pubblico" nel riquadro Controllo dei Accessi.

3. Verificare che il campo "Endpoint", nel riquadro Connettore, sia stato correttamente inizializzato sulla base del valore di default presente nel descritto della API.

   .. note:: **Verifica del certificato server**
       |br|
       Poichè il servizio PetStore è disponibile solamente in https, modificare il prefisso dell'endpoint fornito.
       Inoltre per validare il certificato ritornato dal server 'petstore.swagger.io' deve essere effettuata una opportuna configurazione del trustStore tls come descritto nella sezione :ref:`avanzate_connettori_https`.
       Poichè non è obiettivo di questo scenario si suggerisce di disabilitare la validazione del certificato server.

   .. figure:: ../_figure_scenari/ErogazionePubblica_new.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_new_fig

    Creazione di un'erogazione ad accesso pubblico

4. Salvare la configurazione dell'erogazione.

5. Nel dettaglio dell'erogazione appena creata è possibile visualizzare la URL di invocazione che deve essere comunicata ai fruitori affinché possano invocare il servizio (:numref:`erogazione_pubblica_detail_fig`).

   .. figure:: ../_figure_scenari/ErogazionePubblica_detail.png
    :scale: 80%
    :align: center
    :name: erogazione_pubblica_detail_fig

    Dettaglio dell'erogazione
