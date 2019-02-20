.. _profiloSPCoop_configurazione:

Configurazione di un servizio SPCoop
------------------------------------

Il primo passaggio per la configurazione di un servizio SPCoop è quello
di creare il relativo Accordo di Servizio. Questi viene creato
registrando una nuova API (sezione *Registro > API*). Come illustrato
nelle figura seguente, la particolarità di questa configurazione,
rispetto a quanto descritto in precedenza, risiede nella presenza del
campo *Soggetto referente*, nel quale deve essere selezionato uno dei
soggetti precedentemente registrati.

   .. figure:: ../_figure_console/AggiungiAccordoSPCoop.png
    :scale: 100%
    :align: center
    :name: accordoSPCoop

    Creazione Accordo di Servizio SPCoop

Se non viene fornito un WSDL, relativo all'accordo di servizio, è
necessario definire manualmente l'interfaccia del servizio, analogamente
a quanto descritto in sezione :ref:`confManuale`. In questo caso, l'aggiunta del servizio,
comprende i profili di collaborazione asincroni oltre alle
caratteristiche aggiuntive specifiche del protocollo SPCoop (vedi sezione :ref:`profiliEgov`). La
figura seguente mostra i dettagli di questo caso.

   .. figure:: ../_figure_console/AggiungiPortTypeSPCoop.png
    :scale: 100%
    :align: center
    :name: portTypeSPCoop

    Aggiunta Servizio SPCoop

La registrazione di una nuova erogazione o fruizione, presenta le
seguenti differenze rispetto a quanto descritto per la modalità API
Gateway:

-  È presente il campo *Tipo* relativamente al servizio

-  È presente il campo *Versione Protocollo* per selezionare la versione
   della specifica SPCoop adottata.

   .. figure:: ../_figure_console/ErogazioneSPCoop.png
    :scale: 100%
    :align: center
    :name: erogazioneSPCoop

    Creazione erogazione SPCoop
