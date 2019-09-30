.. _scenari_ambiente:

Ambiente di esecuzione
======================

Per semplificare la realizzazione e verifica degli scenari d'uso, descritti in questa sezione della documentazione di Govway, è possibile dotarsi dell'ambiente di esecuzione appositamente predisposto.

Prerequisiti
------------
Per l'avvio dell'ambiente di esecuzione degli scenari è necessario prima compiere i seguenti passi:

- Scaricare l'archivio di installazione al seguente indirizzo `<http://www.govway.org>`_

- Dotarsi di una installazione `Docker <https://www.docker.com>`_ che gestirà l'intero contesto di esecuzione degli scenari

- Dotarsi dell'applicativo `Postman <https://www.getpostman.com>`_ utilizzato come client per l'invio delle richieste a Govway

Avvio
-----

Dopo aver scompattato l'archivio distribuito dovranno essere installate le singole componenti incluse:

- *Archivio di Inizializzazione*: un file zip da scompattare nella cartella di destinazione scelta per ospitare l'ambiente di esecuzione degli scenari.

- *Ambiente Docker*: la configurazione dell'ambiente docker necessaria per il dispiegamento e avvio dei componenti necessari. L'avvio dell'ambiente viene effettuato eseguento il comando 'docker-compose up' nella cartella di destinazione dell'ambiente, dove deve essere presente il file di configurazione docker distribuito 'docker-compose.yml' (:numref:`docker_avvio_fig`).

   .. figure:: _figure_scenari/scenari_docker_avvio.png
    :scale: 80%
    :align: center
    :name: docker_avvio_fig

    Schermata di avvio "docker-compose up"

I componenti avviati sono i seguenti:

    * gateway: l'istanza di Govway

    * PGSQL95: il database Postgres

    * govauth: il service provider SAML (SPID)

    * spid_testenv: IDP SPID di test

    * keycloak: l'authorization server

    * traefik: il load balancer

- *Progetto Postman*: la collection Postman che comprende le request utilizzate nei vari scenari presentati (:numref:`postman_indice_fig`). La collection deve essere caricata sul proprio Postman tramite la funzionalità di import.

   .. figure:: _figure_scenari/scenari_postman_indice.png
    :scale: 80%
    :align: center
    :name: postman_indice_fig

    Indice della collection Postman

Dopo aver avviato l'ambiente docker è possibile verificare l'accesso alla console govwayMonitor, dalla quale si potranno consultare le transazioni gestite da Govway (:numref:`accesso_monitoraggio_fig`).

   .. figure:: _figure_scenari/scenari_accesso_monitoraggio.png
    :scale: 80%
    :align: center
    :name: accesso_monitoraggio_fig

    Accesso alla console di monitoraggio
