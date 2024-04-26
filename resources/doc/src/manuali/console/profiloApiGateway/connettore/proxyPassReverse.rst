.. _proxyPassReverse:

ProxyPassReverse per Header HTTP Location e Set-Cookie
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La funzionalità 'ProxyPassReverse' può essere attivata per gestire due tipologie di header HTTP presenti nelle risposte:

- modificare la URL presente negli header HTTP 'Location' e 'Content-Location' sulle risposte di reindirizzamento HTTP sostituendo il backend server (se presente come url assoluta) e il context path con l'indirizzo di esposizione dell'API Su GovWay;

- modificare gli attributi 'Path' e 'Domain', presenti negli header HTTP 'Set-Cookie' restituiti dal backend server, sostituendo i valori rispettivamente con il context path e con il dominio di esposizione dell'API Su GovWay.

.. note::
   L'indirizzo di esposizione di una API su GovWay è configurabile e personalizzabile come descritto nella sezione :ref:`configGenerale_urlInvocazione`.

La configurazione di default di GovWay effettua la traduzione solamente delle URL presenti negli header HTTP 'Location' e 'Content-Location' su API di tipo REST.

È possibile personalizzare la configurazione registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione:

- *connettori.proxyPassReverse.enabled*: (default: true su API REST, false su API SOAP) consente di abilitare o disabilitare la funzionalità di proxy pass reverse sulle risposte di reindirizzamento HTTP (Location). I valori associabili alle proprietà sono 'true' o 'false'.

- *connettori.proxyPassReverse.headers*: (default: Location,Content-Location) consente di indicare i nomi degli header HTTP della risposta su cui verrà attuata la trasformazione della url.

- *connettori.proxyPassReverse.setCookie.enabled*: (default: false) consente di abilitare la funzionalità di proxy pass reverse sugli header HTTP 'Set-Cookie'. I valori associabili alle proprietà sono 'true' o 'false'. La proprietà abilita la traduzione di entrambi gli attributi 'Path' e 'Domain'. In alternativa è possibile utilizzare le proprietà seguenti per abilitare puntualmente la traduzione solo su uno dei due attributi:

	- *connettori.proxyPassReverse.setCookie.path.enabled*

	- *connettori.proxyPassReverse.setCookie.domain.enabled*

- *connettori.proxyPassReverse.setCookie.headers*: (default: Set-Cookie) consente di indicare i nomi degli header HTTP della risposta su cui viene atteso un cookie in cui verrà attuata la trasformazione del path e/o del domain.


**Configurazione su API SOAP**

Poichè la funzionalità di proxy pass reverse è completamente disabilitata per default su API di tipo SOAP, per attivarla è necessario attuare la registrazione delle :ref:`configProprieta` 'connettori.proxyPassReverse.enabled' e/o 'connettori.proxyPassReverse.setCookie.enabled' (o proprietà specifiche per path/domain) con il valore 'true'.

La sola registrazione delle proprietà non è sufficiente su API SOAP poichè per default gli unici header HTTP della risposta che vengono inoltrati dal backend verso il client sono quelli relativi alle funzionalità CORS (Access-Control-\*). È possibile configurare GovWay per far inoltrare gli header 'Location', 'Content-Location' o 'Set-Cookie' al client in una delle seguenti due modalità:

- *puntuale sull'erogazione/fruizione di API*: creare una regola di trasformazione sugli header http di risposta che consenta l'inoltro verso il client degli header 'Location', 'Content-Location' o 'Set-Cookie' (per ulteriori dettagli sulle trasformazioni far riferimento alla sezione :ref:`trasformazioniRisposta`). Nella figura (:numref:`proxyPassReverse_APISoapSetCookie`) viene fornito un esempio di configurazione.

   .. table:: Trasformazione per attuare proxy pass reverse su API SOAP
      :widths: 35 65 30
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

	


