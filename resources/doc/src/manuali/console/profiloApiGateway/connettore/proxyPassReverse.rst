.. _proxyPassReverse:

ProxyPassReverse per Header HTTP Location e Set-Cookie
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La funzionalità di 'ProxyPassReverse' modifica la URL presente negli header HTTP 'Location' e 'Content-Location' sulle risposte di reindirizzamento HTTP e il path presente negli header HTTP 'Set-Cookie' sostituendo il backend server (se presente come url assoluta) e il context path con l'indirizzo di esposizione dell'API Su GovWay.

.. note::
   L'indirizzo di esposizione di una API su GovWay è configurabile e personalizzabile come descritto nella sezione :ref:`configGenerale_urlInvocazione`.

La funzionalità, attiva per default su API di tipo REST, è personalizzabile registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione:

- *connettori.proxyPassReverse.enabled*: (default: true) consente di abilitare o disabilitare la funzionalità di proxy pass reverse. I valori associabili alle proprietà sono 'true' o 'false'.

- *connettori.proxyPassReverse.headers*: (default: Location,Content-Location) consente di indicare i nomi degli header HTTP della risposta su cui verrà attuata la trasformazione della url.

- *connettori.proxyPassReverse.setCookie.headers*: (default: Set-Cookie) consente di indicare i nomi degli header HTTP della risposta su cui viene atteso un cookie in cui verrà attuata la trasformazione del path.


