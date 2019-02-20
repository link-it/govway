Revisione dei formati di errore generati dal Gateway
----------------------------------------------------

I formati dei messaggi di errore generati dal Gateway sono ora conformi
a quanto previsto dall'RFC 7807 e dalle specifiche AGID "MI 2018". Sono
stati inoltre uniformati i messaggi di errore ritornati nelle erogazioni
e nelle fruizioni.

Per le API di tipologia REST viene generato un oggetto *Problem Details*
come definito nella specifica *RFC 7807*
(https://tools.ietf.org/html/rfc7807). Le casistiche di errore
supportate sono le seguenti:

-  *401*: rientrano in questa castistica gli errori avenuti durante le
   fasi di autenticazione degli applicativi e di verifica del token
   OAuth

-  *403*: identifica un'autorizzazione fallita

-  *404*: richiesta una erogazione o fruizione inesistente

-  *400*: l'errore occorso è imputabile ai dati forniti dal client (es.
   messaggio non valido in caso di validazione attiva)

-  *429*: identifica una violazione della politica di Rate Limiting

-  *503*: rientrano in questa casistica gli errori causati da una
   irraggiungibilità dell'applicativo indirizzato dal Gateway o una
   temporanea sospensione della erogazione/fruzione

-  *500*: qualsiasi altro errore

Nell'elemento *detail* è presente il dettaglio dell'errore mentre
nell'elemento *govway\_status* una codifica in GovWay di tale errore.

Per le API di tipologia SOAP, sia in erogazione che in fruizione, viene
generato un SOAPFault contenente un actor valorizzato con
*http://govway.org/integration*. Nell'elemento *fault string* è presente
il dettaglio dell'errore mentre nell'elemento *fault code* una codifica
in GovWay di tale errore.
