.. _avanzate_connettori_status:

Connettore Status
~~~~~~~~~~~~~~~~~

Il connettore permette di conoscere lo stato di un servizio, seguendo lo standard presente nel seguente
documento dell' :download:`AGID <>`:

   -  **4.2.11 [RAC_REST_NAME_011]** Esporre lo stato del servizio

       - L’API **DEVE** esporre lo stato del servizio al path `/status` e ritornare un oggetto con media-type `application/problem+json` (RFC 7807). Se il servizio funziona correttamente, l’API ritorna HTTP status 200 OK altrimenti 500, sempre con un problem details al suo interno.

   -  **5.1.4 [RAC_SOAP_004]** Esporre lo stato del servizio

       - L’API **DEVE** includere un metodo `echo` per restituire lo stato della stessa.

Oltre alle funzioni descritte nel documento sopra menzionato, GovWay propone altri formati di risposta configurabili da console.
Per controllare il corretto funzionamento del servizio, sono possibili varie configurazioni. Nel caso base (ovvero senza alcuna verifica abilitata), il connettore restituirà sempre un risultato positivo.


   .. figure:: ../../_figure_console/ConnettoreSTATUS.jpg
    :scale: 100%
    :align: center
    :name: ConnettoreSTATUSFig

    Dati di configurazione di un connettore Status

Facendo riferimento alla schermata raffigurata in :numref:`ConnettoreSTATUSFig`, descriviamo il significato dei parametri:

   -  **Risposta**: Questo campo è modificabile solo nel caso di servizi di tipo REST; nel caso di servizi SOAP sarà utilizzabile solo il caso **ModI**. Nel caso di servizi REST, è possibile scegliere tra i valori `ModI` e `Personalizzato`: nel primo caso, il connettore restituirà il formato descritto nel documento dell'AGID; nel secondo caso, invece, sarà possibile scegliere nella seconda selezione tra diversi valori:

      -  *vuoto*: nel caso di risposta positiva, il connettore restituirà un messaggio vuoto senza alcun content type
      -  *text*: nel caso di risposta positiva, il connettore restituirà un messaggio di tipo `text/plain` con un messaggi di corretta esecuzione
      - *json*: nel caso di risposta positiva, il connettore restituirà un messaggio con formato `application/json`
      - *xml*: nel caso di risposta positiva, il connettore restituirà un messaggio con formato `application/xml`

   -  **Verifica connettività**: consente di abilitare la verifica connettività, in cui il connettore effettua test sui connettori HTTP(S) per verificare se gli endpoint configurati accettano connessioni.

   -  **Verifica statistica**: consente di abilitare la verifica statistica, in cui il connettore utilizza le statistiche di GovWay per verificare se almeno una transazione diretta verso il servizio è andata a buon fine nell'intervallo temporale impostato negli input sottostanti.

   -  **Frequenza**: Consente di impostare l'unità di misura dell'intervallo temperale

   -  **Intervallo Osservazione**: Consente di impostare la durata dell'intervallo temporale in cui controllare le statistiche prodotte da GovWay

   -  **Cache lifetime (Opzionale)**: In questo input è possibile impostare una durata, in secondi, della permanenza in cache delle statistiche controllate dal connettore.
