.. _configvVlidazioneOpzioniAvanzate:

Opzioni Avanzate
-----------------------------------------

È possibile selezionare la libreria client utilizzata per validare il token (Discovery, Introspection, UserInfo) registrando la :ref:`configProprieta` '*connettori.token.validate.httplibrary*' sull'erogazione o sulla fruizione:

     - 'org.apache.hc.client5': (default) viene utilizzato come client http la libreria `Apache HttpClient 5 <https://hc.apache.org/httpcomponents-client-5.5.x/index.html>`_;
     - 'java.net.HttpURLConnection': viene utilizzata come client http la precedente libreria utilizzata nelle versioni 3.3.x di GovWay.

