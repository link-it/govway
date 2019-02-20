.. _console_multitenant:

Multi-Tenant
------------

I processi di configurazione, descritti in questo manuale, sono
ottimizzati nell'ottica di mantenere sempre sottinteso il soggetto
interno al dominio. In tal senso, le fruizioni e le erogazioni si
intendono sempre in soggettiva riguardo un singolo soggetto interno
amministrato dall'utente in sessione.

*Multi-tenant* è un'opzione che consente di estendere l'ambito delle
configurazioni prodotte dalla govwayConsole a più di un soggetto interno
al dominio. Tale opzione si attiva nella configurazione generale (sezione :ref:`configGenerale`).

Per gestire la compresenza di più soggetti interni al dominio, per la
configurazione di erogazioni e fruizioni, è possibile scegliere quali
soggetti interni rendere ammissibili (:numref:`multitenantFig`):

-  *Fruizioni (Soggetto Erogatore)*

   -  *Tutti*: indica che tutti i soggetti interni, censiti nel registro
      di GovWay, sono selezionabili come soggetto erogatore, in una
      fruizione.

   -  *Escludi Soggetto Fruitore*: indica che tutti i soggetti interni,
      tranne il soggetto fruitore, sono selezionabili come soggetto
      erogatore, in una fruizione.

   -  *Solo Soggetti Esterni*: indica che il soggetto erogatore di una
      fruizione deve essere un soggetto esterno.

-  *Erogazioni (Soggetti Fruitori)*

   -  *Tutti*: indica che tutti i soggetti interni, censiti nel registro
      di GovWay, sono selezionabili come soggetti fruitori, in una
      erogazione.

   -  *Escludi Soggetto Erogatore*: indica che tutti i soggetti interni,
      tranne il soggetto erogatore, sono selezionabili come soggetti
      fruitori, in una erogazione.

   -  *Solo Soggetti Esterni*: indica che i soggetti fruitori di una
      erogazione devono essere soggetti esterni.

   .. figure:: ../_figure_console/Multi-Tenant-Config.png
    :scale: 100%
    :align: center
    :name: multitenantFig

    Elementi di configurazione della modalità multi-tenant

L'utente che ha l'opzione multi-tenant attiva, visualizza sulla testata
un menu a discesa che consente di selezionare l'utente interno al
dominio sul quale vuole operare (:numref:`multitenantSoggetto`). Se viene selezionato un soggetto
dalla lista, l'operatività sulla console risulterà identica alla
situazione con un unico soggetto interno. Selezionando l'opzione "Tutti"
sarà richiesto nei singoli contesti di specificare il soggetto interno.

   .. figure:: ../_figure_console/Multi-Tenant.png
    :scale: 100%
    :align: center
    :name: multitenantSoggetto

    Selezione del soggetto operativo in modalità multi-tenant

