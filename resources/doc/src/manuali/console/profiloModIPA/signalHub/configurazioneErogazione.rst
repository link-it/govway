.. _modipa_signalhub_configurazione_erogazione:

Erogazione del servizio per il recupero delle informazioni crittografiche: funzione di hashing e seme
---------------------------------------------------------------------------------------------------------

GovWay espone un servizio che consente ai fruitori di recuperare le informazioni crittografiche necessarie per la pseudoanonimizzazione degli identificativi relativi ai dati oggetto di variazione e pubblicati tramite Signal-Hub.

Come descritto nella sezione :ref:`modipa_signalhub_console` una volta abilitato il supporto a Signal-Hub sull'erogazione è possibile configurare i parametri crittografici tramite i seguenti campi (':numref:`SignalHubErogazione2`):

- Risorsa: va selezionata la risorsa che esporrà le informazioni di informazioni crittografiche utilizzate per la pseudoanonimizzazione tramite l'interfaccia descritta di seguito in questa sezione;
- Algoritmo: algoritmo utilizzato per generare l’hash dell’identificativo del dato oggetto di variazione;
- Dimensione Seme: la dimensione del seme che concorre alla generazione dell'hash
- Giorni Rotazione Seme: indicazione in giorni dopo i quali il seme verrà variato e una notifica di variazione di seme verrà inviata ai fruitori.

.. figure:: ../../_figure_console/SignalHubErogazione.png
    :scale: 90%
    :align: center
    :name: SignalHubErogazione2

    Schermata di configurazione del servizio Signal-Hub su un'erogazione ModI

La seconda parte della configurazione consente di specificare puntualmente gli applicativi o i ruoli che tali applicativi debbano possedere per essere autorizzati alla pubblicazione dei segnali tramite la fruizione descritta nella sezione :ref:`modipa_signalhub_configurazione_fruizione` per il servizio configurato. Gli applicativi selezionabili saranno esclusivamente quelli già presenti nella lista di applicaivi autorizzati come richiedenti nella fruizione built-in ``api-pdnd-push-signals`` descritta nella sezione :ref:`modipa_signalhub_configurazione_fruizione`.

**Integrazione delle informazioni crittografiche nell’OpenAPI registrato sulla PDND**

L’OpenAPI pubblicato sulla Piattaforma Digitale Nazionale Dati (PDND) deve includere, oltre alle consuete operazioni applicative previste dal servizio, un’ulteriore operazione dedicata al recupero delle informazioni crittografiche.

Le sezioni seguenti illustrano nel dettaglio l’operazione esposta da GovWay, sia per API di tipo REST che SOAP, al fine di fornire ai referenti degli e-Service tutte le indicazioni necessarie per estendere correttamente l’interfaccia applicativa con l’operazione richiesta dal Signal Hub.

.. toctree::
    :maxdepth: 2

    configurazioneErogazioneREST
    configurazioneErogazioneSOAP
