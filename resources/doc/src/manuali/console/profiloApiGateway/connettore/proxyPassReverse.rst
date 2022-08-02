.. _proxyPassReverse:

ProxyPassReverse per Header HTTP Location e Set-Cookie
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La funzionalità di 'ProxyPassReverse' modifica la URL presente negli header HTTP 'Location' e 'Content-Location' sulle risposte di reindirizzamento HTTP e il path/domain presente negli header HTTP 'Set-Cookie' sostituendo il backend server (se presente come url assoluta) e il context path con l'indirizzo di esposizione dell'API Su GovWay.

.. note::
   L'indirizzo di esposizione di una API su GovWay è configurabile e personalizzabile come descritto nella sezione :ref:`configGenerale_urlInvocazione`.

La funzionalità è attiva per default solamente su API di tipo REST.

È possibile personalizzare la configurazione registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione:

- *connettori.proxyPassReverse.enabled*: (default: true su API REST, false su API SOAP) consente di abilitare o disabilitare la funzionalità di proxy pass reverse. I valori associabili alle proprietà sono 'true' o 'false'.

- *connettori.proxyPassReverse.headers*: (default: Location,Content-Location) consente di indicare i nomi degli header HTTP della risposta su cui verrà attuata la trasformazione della url.

- *connettori.proxyPassReverse.setCookie.headers*: (default: Set-Cookie) consente di indicare i nomi degli header HTTP della risposta su cui viene atteso un cookie in cui verrà attuata la trasformazione del path.

**Configurazione su API SOAP**

Poichè la funzionalità di proxy pass reverse è disabilitata su API di tipo SOAP è necessario attuare la registrazione della :ref:`configProprieta` 'connettori.proxyPassReverse.enabled' con il valore 'true'.

La sola registrazione della proprietà non è sufficiente su API SOAP poichè per default gli unici header HTTP della risposta che vengono inoltrati dal backend verso il client sono quelli relativi alle funzionalità CORS (Access-Control-\*). È possibile configurare GovWay per far inoltrare gli header 'Location', 'Content-Location' o 'Set-Cookie' al client in una delle seguenti due modalità:

- *puntuale sull'erogazione/fruizione di API*: creare una regola di trasformazione che definisca le seguenti trasformazioni sugli header http di risposta (per ulteriori dettagli sulle trasformazioni far riferimento alla sezione :ref:`trasformazioniRisposta`). Nella figura (:numref:`proxyPassReverse_APISoapSetCookie`) viene fornito un esempio di configurazione di forward degli header HTTP di risposta 'Location', 'Content-Location' e 'Set-Cookie'.

   .. table:: Trasformazione per attuare proxy pass reverse su API SOAP
      :widths: 35 65
      :class: longtable
      :name: proxyPassReverseTrasformazioneAPISOAP

      ==================    ==================================    ==============
      Nome                  Valore                                Operazione    
      ==================    ==================================    ==============
      Location              ${headerResponse:Location}            update        
      Content-Location      ${headerResponse:Content-Location}    update
      Set-Cookie            ${headerResponse:Set-Cookie}          update
      ==================    ==================================    ==============

   .. figure:: ../../_figure_console/ProxyPassReverse_APISoapSetCookie.png
    :scale: 80%
    :align: center
    :name: proxyPassReverse_APISoapSetCookie

    Forward header http di risposta su API SOAP

- *globale per tutte le API SOAP*: editare il file <directory-lavoro>/govway_local.properties aggiungendo la seguente riga:

   ::

      # Header su cui attuare il cookie proxy pass reverse per API SOAP
      org.openspcoop2.pdd.soap.headers.whiteList.response=Access-Control-*,Location,Content-Location,Set-Cookie

	


