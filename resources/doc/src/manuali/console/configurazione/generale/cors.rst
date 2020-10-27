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

   -  *All Allow Origins*: se abilitato, in tutte le risposte viene aggiunto un header http
      'Access-Control-Allow-Origin' valorizzato con '\*'

   -  *Allow Origins*: nel caso non venga abilitato il parametro
      precedente, deve essere indicato una lista di
      origin che vengono impostate nell'header http
      'Access-Control-Allow-Origin' aggiunto in ogni risposta

   -  *All Allow Methods*: se abilitato, in tutte le risposte di una Preflight Request (OPTIONS) viene aggiunto un header http
      'Access-Control-Allow-Methods' valorizzato con i metodi richiesti dall'header 'Access-Control-Request-Method' della richiesta. 
      L'opzione è attivabile solamente se la voce 'All Allow Origins' risulta disabilitata

   -  *Allow Methods*: nel caso non venga abilitato il parametro
      precedente, deve essere indicato una lista di metodi che vengono impostati nell'header http
      'Access-Control-Allow-Methods' di una risposta Preflight Request (OPTIONS)

   -  *All Allow Request Headers*:  se abilitato, in tutte le risposte di una Preflight Request (OPTIONS) viene aggiunto un header http
      'Access-Control-Allow-Headers' valorizzato con gli header http richiesti dall'header 'Access-Control-Request-Headers' della richiesta.
      L'opzione è attivabile solamente se la voce 'All Allow Origins' risulta disabilitata

   -  *Allow Request Headers*: nel caso non venga abilitato il parametro
      precedente, deve essere indicato una lista di header che vengono impostati nell'header http
      'Access-Control-Allow-Headers' di una risposta Preflight Request (OPTIONS)

   -  *Allow Credentials*: se abilitato o disabilitato viene impostato
      relativemente il valore true o false nell'header
      'Access-Control-Allow-Credentials'

   -  *Expose Response Headers*: abilita l'accesso a specifici headers, presenti nella risposta, da parte dei client.

   .. figure:: ../../_figure_console/ConfigurazioneCORS.png
    :scale: 100%
    :align: center
    :name: corsFig

    Maschera di configurazione generale del CORS
