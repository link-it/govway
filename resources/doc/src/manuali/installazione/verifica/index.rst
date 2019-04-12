.. _inst_verifica:

===========================
Verifica dell'Installazione
===========================

Appena conclusa la fase di dispiegamento si può procedere con l'avvio
dell'application server, quindi:

#. Verificare che la *govwayConsole*, l'applicazione web per la gestione
   di GovWay, sia accessibile tramite browser all'indirizzo:
   *http://<hostname-pdd>/govwayConsole*. In caso di corretto
   funzionamento verrà visualizzata la schermata seguente:

   .. figure:: ../_figure_installazione/govwayConsole_login.png
    :scale: 100%
    :align: center

    Verifica Installazione: govwayConsole

#. Accedere alla govwayConsole utilizzando le credenziali fornite
   durante l'esecuzione dell'installer.

#. Verificare che la *govwayMonitor*, l'applicazione web per il
   monitoraggio di GovWay, sia accessibile tramite browser
   all'indirizzo: *http://<hostname-pdd>/govwayMonitor*. In caso di
   corretto funzionamento verrà visualizzata la schermata seguente:

   .. figure:: ../_figure_installazione/govwayMonitor_login.png
    :scale: 100%
    :align: center

    Verifica Installazione: govwayMonitor
   
#. Accedere alla govwayMonitor utilizzando le credenziali fornite
   durante l'esecuzione dell'installer.

#. Se durante l'esecuzione dell'Installer è stato indicato di generare il servizio che consente la configurazione tramite API REST, 
   in caso di corretto funzionamento sarà possibile scaricare l'interfaccia OpenAPI v3.
   L'interfaccia nel formato yaml sarà disponibile all'indirizzo:

   -  *http://<hostname-pdd>/govwayAPIConfig/openapi.yaml* 

   L'interfaccia nel formato json sarà disponibile all'indirizzo:

   -  *http://<hostname-pdd>/govwayAPIConfig/openapi.json*

#. Se durante l'esecuzione dell'Installer è stato indicato di generare il servizio che consente il monitoraggio tramite API REST, 
   in caso di corretto funzionamento sarà possibile scaricare l'interfaccia OpenAPI v3.
   L'interfaccia nel formato yaml sarà disponibile all'indirizzo:

   -  *http://<hostname-pdd>/govwayAPIMonitor/openapi.yaml* 

   L'interfaccia nel formato json sarà disponibile all'indirizzo:

   -  *http://<hostname-pdd>/govwayAPIMonitor/openapi.json*

