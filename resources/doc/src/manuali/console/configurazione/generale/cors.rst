.. _console_cors:

Gestione CORS
~~~~~~~~~~~~~

In GovWay è possibile abilitare la gestione del CORS (*cross-origin HTTP
request (CORS)*) globalmente in modo che sia valido per tutte le APIs.

La configurazione permette di specificare i seguenti parametri:

-  *Stato*: Indicazione se la gestione del CORS è abilitata o meno
   globalmente su GovWay.

-  *Access Control*: tutti i parametri seguenti permettono di
   configurare il CORS. Per il dettaglio su cosa significa ogni singola
   voce si rimanda alla specifica CORS *https://www.w3.org/TR/cors/*.

   -  *All Allow Origins*: se abilitato viene impostato nell'header http
      'Access-Control-Allow-Origin' il valore '\*'

   -  *Allow Origins*: nel caso non venga abilitato il parametro
      precedente, in questo campo è possibile indicare una lista di
      origin che vengono impostate nell'header http
      'Access-Control-Allow-Origin'

   -  *Allow Credentials*: se abilitato o disabilitato viene impostato
      relativemente il valore true o false nell'header
      'Access-Control-Allow-Credentials'

   -  *Allow Methods*: metodi inseriti nell'header http
      'Access-Control-Allow-Methods'

   -  *Allow Request Headers*: nomi di header inseriti nell'header http
      'Access-Control-Allow-Headers'

   -  *Expose Response Headers*: abilita l'accesso a specifici headers, presenti nella risposta, da parte dei client.

   .. figure:: ../../_figure_console/ConfigurazioneCORS.png
    :scale: 100%
    :align: center
    :name: corsFig

    Maschera di configurazione generale del CORS
