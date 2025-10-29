Revisione delle url di invocazione di una erogazione o fruizione
----------------------------------------------------------------

Sono state adottate le seguenti revisioni nelle url di invocazione di
una erogazione e fruizione nel profilo 'API Gateway' al fine di
semplificarle ed adeguarle agli standard di mercato.

-  *erogazione*: non è più obbligatorio specificare il protocollo
   *'api'* ed il canale di inbound *'in'*. La versione indicata nel path
   presenta inoltre il prefisso 'v'.

   -  *precedente*: http://host/govway/\ **api/in/**\ Ente/API/1

   -  *nuova*: http://host/govway/Ente/API/\ **v**\ 1

-  *fruizione*: sono state adottate le medesime revisioni
   dell'erogazione fatta eccezione per il canale di outbound *'out'* che
   rimane obbligatorio.

   -  *precedente*:
      http://host/govway/\ **api/**\ out/Ente/EnteEsterno/API/1

   -  *nuova*: http://host/govway/out/Ente/EnteEsterno/API/\ **v**\ 1
