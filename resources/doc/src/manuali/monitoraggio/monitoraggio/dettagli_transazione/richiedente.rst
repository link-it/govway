.. _mon_richiedente:

Richiedente
~~~~~~~~~~~

Sia nell'elenco delle transazioni gestite da GovWay (:ref:`mon_transazioni_lista`) sia nel dettaglio di una transazione (:ref:`mon_dettaglio_transazione`) viene riportata l'informazione sull'identità del richidente che ha effettuato la richiesta. Nel dettaglio della transazione viene inoltre fornito l'indirizzo IP del richiedente.

Entrambie le informazioni vengono calcolate seguendo la logica riportata di seguito.


- IP Richiedente: assume la prima informazione valorizzata, trovata nella richiesta, nel seguente ordine:

	- indirizzo IP presente nella richiesta in uno degli header http appartenente alla classe "Forwarded-For" o "Client-IP";

	- indirizzo IP (socket) del client.

- Richiedente: assume la prima informazione valorizzata, trovata nella richiesta, nel seguente ordine:

	- username presente nel token;

	- identificativo dell'applicativo registrato su GovWay ed identificato tramite il clientId presente nel token;

	- identificativo dell'applicativo registrato su GovWay ed identificato tramite l'autenticazione di trasporto;

	- clientId presente nel token nel caso di client credentials grant type (claims clientId e sub presentano lo stesso valore);

	- tokenSubject[@tokenIssuer]: subject presente nel token; viene aggiunto anche un suffisso @tokenIssuer se è presente anche un issuer nel token;

	- principal: identificativo (credenziali) con cui l'applicativo è stato autenticato sul trasporto; se il tipo di autenticazione risulta essere 'ssl' viene ritornato il valore dell'attributo CN.

   .. note::
         Le informazioni sul richiedente seguono la seguente logica:

         - l'utente descritto in forma umana in un token rappresenta l'informazione più significativa (username);

	 - altrimenti prevale un eventuale applicativo identificato (registrato su GovWay) dando precedenza ad un applicativo token rispetto ad un applicativo di trasporto;

         - infine prevalgono le informazioni di un eventuale token presente rispetto al trasporto; se si tratta di client credentials grant type prevale l'informazione sul clientId altrimenti quella sul subject.
