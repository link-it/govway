.. _multiTenant:

Multi-Tenant
------------

I processi di configurazione e monitoraggio attuabili tramite le console
sono ottimizzati nell'ottica di gestire sempre un unico dominio
rappresentato da un soggetto interno il cui nome è stato fornito durante
l'esecuzione dell'installer (:numref:`interop_fig`). In tal senso, le
fruizioni e le erogazioni si intendono sempre in soggettiva riguardo un
singolo soggetto interno amministrato dall'utente in sessione.

La funzionalità Multi-Tenant è un'opzione che consente di estendere
l'ambito delle configurazioni prodotte dalla govwayConsole a più di un
soggetto interno al dominio. Tale opzione si attiva accedendo alla voce
*'Configurazione - Generale'* del menù, sezione *'Multi-Tenant'*.

    .. figure:: ../_figure_installazione/govwayConsole_multiTenant.png
        :scale: 100%
        :align: center
	:name: inst_multitenantFig

        Abilitazione Multi-Tenant

Una volta abilitato accedere alla voce *'Soggetti'* del menù e
selezionare il pulsante *'Aggiungi'* per registrare un nuovo soggetto
interno (nuovo dominio).

    .. figure:: ../_figure_installazione/govwayConsole_multiTenant_soggetto.png
        :scale: 100%
        :align: center
	:name: inst_multitenantSoggettoFig

        Registrazione nuovo Soggetto
    
Terminata la registrazione del nuovo soggetto sia nella console di
gestione (*govwayConsole*) che nella console di monitoraggio
(*govwayMonitor*) prima di procedere con qualsiasi operazione è adesso
possibile selezionare il soggetto per cui si intende gestire il dominio
attraverso l'apposito menù situato in alto a destra nell'intestazione
delle console.

    .. figure:: ../_figure_installazione/govwayConsole_multiTenant_selezione_soggetto.png
        :scale: 100%
        :align: center
	:name: inst_multitenantSelezioneSoggettoFig

        Selezione del Soggetto
