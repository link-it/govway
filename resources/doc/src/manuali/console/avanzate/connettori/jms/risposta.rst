.. _avanzate_connettori_jms_risposta:

Risposta restituita al client
*****************************

Il connettore JMS pubblica il messaggio sulla risorsa queue/topic indicata senza ricevere alcuna risposta dall'applicativo destinatario. Completata con successo la pubblicazione, GovWay genera la risposta da restituire al client in funzione del tipo di API:

- *API REST*: viene restituita una risposta senza contenuto con codice HTTP 204. Il codice può essere personalizzato su una singola erogazione o fruizione di API attraverso la definizione della seguente :ref:`configProprieta`:

  - *connettori.jms.response.rest.returnCode* (default: 204): codice HTTP della risposta; sono ammessi solamente codici 2xx. Un valore non valido viene segnalato come errore di configurazione e il messaggio non viene pubblicato.

- *API SOAP*: per le azioni con un profilo di collaborazione diverso da oneway (es. sincrono) viene restituito un SOAP Envelope con Body vuoto e codice HTTP 200. Il contenuto del SOAP Body può essere configurato su una singola erogazione o fruizione di API attraverso la definizione della seguente :ref:`configProprieta`:

  - *connettori.jms.response.soap.operationWrapper* (true/false default:false): se abilitata, il SOAP Body della risposta contiene un elemento vuoto con il nome del primo elemento del SOAP Body della richiesta e il suffisso 'Response', definito nel medesimo namespace, come previsto dalle convenzioni degli stili 'RPC' e 'document/literal wrapped' (es. per una richiesta contenente l'elemento 'ns:Notifica' la risposta conterrà l'elemento vuoto 'ns:NotificaResponse').

  Per le azioni con profilo di collaborazione oneway non viene restituito alcun contenuto al client.

Una risposta con un contenuto differente (es. un esito applicativo di avvenuta pubblicazione) può essere prodotta configurando una :ref:`trasformazioniRisposta`.
