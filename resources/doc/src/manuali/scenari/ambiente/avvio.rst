.. _scenari_ambiente_avvio:

Avvio Ambiente
---------------

Dopo aver scompattato l'`archivio <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_, indicato nei prerequisiti, sarà possibile avviare un ambiente tramite docker compose preinizializzato per gli scenari descritti nel manuale. Di seguito vengono forniti tutti i passaggi da effettuare per ottenere un ambiente funzionante:

- *Archivio*: scompattare l'`archivio <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_ nella cartella di destinazione scelta per ospitare l'ambiente di esecuzione degli scenari.

- *Hostname*: l'ambiente è configurato per utilizzare l'hostname 'govway.localdomain'. Configurare una risoluzione dell'hostname ad esempio registrando nel file  /etc/hosts l'entry:

   ::

        127.0.0.1       govway.localdomain

- *Ambiente Docker*: avviare l'ambiente docker compose utilizzando lo script '*starttest.sh*' presente all'interno della cartella di destinazione dell'ambiente (:numref:`docker_avvio_fig`).

   .. figure:: ../_figure_scenari/scenari_docker_avvio.png
    :scale: 80%
    :align: center
    :name: docker_avvio_fig

    Schermata di avvio "docker-compose up"

I componenti avviati sono i seguenti:

    * gateway: l'istanza di Govway

    * PGSQL16: il database Postgres

    * keycloak: l'authorization server

    * nginx: il server web

.. note::

	Lo script '*starttest.sh*' si occupa di inizializzare due variabili di ambiente prima di avviare l'ambiente tramite il comando '*docker-compose up*':

	- SERVER_FQDN: definisce l'hostname dell'ambiente (negli esempi govway.localdomain)
	- LOCAL_DATA: directory contenente gli storage locali utilizzate dalle immagini docker avviate dal compose (l'archvio fornisce già la directory ./data)

Dopo aver avviato l'ambiente è possibile verificare l'accesso alle seguenti console:

- *GovWay - Console di Gestione*: permette di visualizzare le configurazioni realizzate su Govway (:numref:`accesso_console_fig`).

   ::

        endpoint: https://govway.localdomain/govwayConsole/
	username: amministratore
	password: 123456

   .. figure:: ../_figure_scenari/govwayConsole_login.png
    :scale: 80%
    :align: center
    :name: accesso_console_fig

    Accesso alla console di gestione

- *GovWay - Console di Monitoraggio*: permette di consultare le transazioni gestite da Govway (:numref:`accesso_monitoraggio_fig`).

   ::

        endpoint: https://govway.localdomain/govwayMonitor/
	username: operatore
	password: 123456

   .. figure:: ../_figure_scenari/govwayMonitor_login.png
    :scale: 80%
    :align: center
    :name: accesso_monitoraggio_fig

    Accesso alla console di monitoraggio

- *Keycloak - Authorization Server*: permette di consultare le configurazioni realizzate sull'Authorization Server Keycloak (:numref:`accesso_keycloak_fig`).

   ::

        endpoint: https://govway.localdomain/auth/
	username: admin
	password: admin

   .. figure:: ../_figure_scenari/keycloak_login.png
    :scale: 80%
    :align: center
    :name: accesso_keycloak_fig

    Accesso alla console dell'authorization server
