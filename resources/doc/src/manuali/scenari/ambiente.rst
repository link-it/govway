.. _scenari_ambiente:

Ambiente di esecuzione
======================

Per semplificare la realizzazione e verifica degli scenari d'uso, descritti in questa sezione della documentazione di Govway, è possibile dotarsi dell'ambiente di esecuzione appositamente predisposto.

Prerequisiti
------------
Per l'avvio dell'ambiente di esecuzione degli scenari è necessario disporre del seguente software di base:

- Dotarsi di una installazione `Docker <https://www.docker.com>`_ che gestirà l'intero contesto di esecuzione degli scenari

- Dotarsi dell'applicativo `Postman <https://www.getpostman.com>`_ utilizzato come client per l'invio delle richieste a Govway

L'ambiente di esecuzione è composto da:

- `Ambiente Docker Compose <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_ preinizializzato con gli scenari descritti in queso manuale.
- `Progetto Postman <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari-postman.json>`_ configurato per verificare gli scenari.

.. note::

	Gli scenari configurati sull'ambiente docker devono poter accedere ai seguenti servizi su internet:

	- Petstore: http://petstore.swagger.io/
	- Credit Card Verification: http://ws.cdyne.com/creditcardverify/luhnchecker.asmx

Avvio Ambiente
---------------

Dopo aver scompattato l'`archivio <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_, indicato nei prerequisiti, sarà possibile avviare un ambiente tramite docker compose preinizializzato per gli scenari descritti nel manuale. Di seguito vengono forniti tutti i passaggi da effettuare per ottenere un ambiente funzionante:

- *Archivio*: scompattare l'`archivio <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_ nella cartella di destinazione scelta per ospitare l'ambiente di esecuzione degli scenari.

- *Hostname*: l'ambiente è configurato per utilizzare l'hostname 'govway.localdomain'. Configurare una risoluzione dell'hostname ad esempio registrando nel file  /etc/resolv.conf l'entry:

   ::

        127.0.0.1       govway.localdomain

- *Ambiente Docker*: avviare l'ambiente docker compose utilizzando lo script '*starttest.sh*' presente all'interno della cartella di destinazione dell'ambiente (:numref:`docker_avvio_fig`).

   .. figure:: _figure_scenari/scenari_docker_avvio.png
    :scale: 80%
    :align: center
    :name: docker_avvio_fig

    Schermata di avvio "docker-compose up"

I componenti avviati sono i seguenti:

    * gateway: l'istanza di Govway

    * PGSQL95: il database Postgres

    * keycloak: l'authorization server

    * traefik: il load balancer

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

   .. figure:: _figure_scenari/govwayConsole_login.png
    :scale: 80%
    :align: center
    :name: accesso_console_fig

    Accesso alla console di gestione

- *GovWay - Console di Monitoraggio*: permette di consultare le transazioni gestite da Govway (:numref:`accesso_monitoraggio_fig`).

   ::

        endpoint: https://govway.localdomain/govwayMonitor/
	username: operatore
	password: 123456

   .. figure:: _figure_scenari/govwayMonitor_login.png
    :scale: 80%
    :align: center
    :name: accesso_monitoraggio_fig

    Accesso alla console di monitoraggio

- *Keycloak - Authorization Server*: permette di consultare le configurazioni realizzate sull'Authorization Server Keycloak (:numref:`accesso_keycloak_fig`).

   ::

        endpoint: https://govway.localdomain/auth/
	username: admin
	password: admin

   .. figure:: _figure_scenari/keycloak_login.png
    :scale: 80%
    :align: center
    :name: accesso_keycloak_fig

    Accesso alla console dell'authorization server


Progetto Postman
-----------------

La `collezione Postman <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari-postman.json>`_ comprende tutte le configurazioni utilizzate nei vari scenari presentati (:numref:`postman_indice_fig`). La collection deve essere caricata sul proprio Postman tramite la funzionalità di import.

   .. figure:: _figure_scenari/scenari_postman_indice.png
    :scale: 80%
    :align: center
    :name: postman_indice_fig

    Indice della collection Postman

Una volta effettuato il caricamento della collezione, modificare i parametri della collezione (:numref:`postman_edit_fig`) al fine di indicare nella variabile '*hostname*' (:numref:`postman_hostname_fig`) l'indirizzo ip su cui è stato attivato l'immagine docker compose (per default è presente 127.0.0.1).

   .. figure:: _figure_scenari/postman_edit.png
    :scale: 80%
    :align: center
    :name: postman_edit_fig

    Configurazione Collection Postman

   .. figure:: _figure_scenari/postman_hostname.png
    :scale: 80%
    :align: center
    :name: postman_hostname_fig

    Configurazione Hostname nella Collection Postman

Infine accedere alla configurazione generale di Postman (:numref:`postman_settings_fig`) ed assicurarsi che la voce '*SSL Certificate Verification*' nella maschera '*General*' sia disabilitata (:numref:`postman_ssl_fig`) e che non vi sia impostato un proxy nella maschera '*Proxy*' (:numref:`postman_proxy_fig`).

   .. figure:: _figure_scenari/postman_settings.png
    :scale: 80%
    :align: center
    :name: postman_settings_fig

    Configurazione Generale Postman

   .. figure:: _figure_scenari/postman_ssl.png
    :scale: 80%
    :align: center
    :name: postman_ssl_fig

    Configurazione SSL Postman

   .. figure:: _figure_scenari/postman_proxy.png
    :scale: 80%
    :align: center
    :name: postman_proxy_fig

    Configurazione Proxy Postman
