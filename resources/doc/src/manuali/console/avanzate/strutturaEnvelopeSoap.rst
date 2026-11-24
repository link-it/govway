.. _configAvanzataStrutturaEnvelopeSoap:

Verifica della struttura dell'Envelope SOAP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I parser SOAP non applicano lo schema dell'Envelope in lettura: un messaggio con più elementi *Body* o *Header*, con elementi in ordine errato o con un *Fault* accompagnato da altri contenuti viene accettato, e i componenti che lo elaborano utilizzano tipicamente il primo elemento trovato. Messaggi di questo tipo possono essere sfruttati per attacchi di tipo *XML Signature Wrapping*, in cui la firma WS-Security copre un elemento diverso da quello effettivamente elaborato.

GovWay verifica la struttura dell'Envelope SOAP sia sulle richieste sia sulle risposte, applicando le seguenti regole:

- l'Envelope deve contenere al più un elemento *Header* e un solo elemento *Body*, in quest'ordine;
- non sono ammessi altri elementi o testo come figli dell'Envelope; in SOAP 1.1 lo schema consentirebbe elementi di altro namespace dopo il *Body*, vietati però da WS-I Basic Profile (R1011): per default vengono rifiutati;
- un *Fault* deve essere l'unico figlio del *Body*, e non può comparire fuori dal *Body*.

Un messaggio non conforme viene trattato come un contenuto non processabile:

- *richiesta*: la transazione termina con esito 'Contenuto Richiesta Malformato' e al client viene restituito l'errore :ref:`errori_400_UnprocessableRequestContent` (per le API SOAP sotto forma di SOAP Fault);
- *risposta*: la transazione termina con esito 'Contenuto Risposta Malformato' e viene generato l'errore :ref:`errori_502_UnprocessableResponseContent`, restituito al client, nella configurazione di default, con il codice generico :ref:`errori_502_InvalidResponse` (vedi :ref:`errori_502`).

Il diagnostico emesso indica l'anomalia rilevata (es. *Invalid SOAP Envelope: ...*).

Con la sicurezza messaggio WS-Security abilitata in ricezione viene inoltre verificato che l'elemento protetto, indicato come *Body* nella firma o nella cifratura, sia effettivamente il *Body* del messaggio; in caso contrario la transazione termina con un errore di sicurezza messaggio (*WS-Security protected element is not the SOAP Body*).

**Messaggi gestiti senza costruzione del DOM**

La verifica è completa quando il messaggio viene costruito come DOM, ad esempio per l'applicazione della sicurezza messaggio, della validazione dei contenuti o delle trasformazioni. Quando invece il messaggio viene inoltrato in streaming senza costruire il DOM, GovWay legge solamente la parte iniziale dell'Envelope (fino al primo figlio del *Body*) e la verifica riguarda solo tale parte.

Per verificare anche la parte successiva sono disponibili due controlli opzionali, disabilitati per default, attivabili sull'erogazione o sulla fruizione registrando le seguenti :ref:`configProprieta`:

- *soap.envelopeStructure.reader.fullCheck.enabled* (default: false): la lettura iniziale prosegue fino alla soglia di bufferizzazione dei messaggi SOAP (30KB per default) e tutto quanto letto viene verificato prima dell'inoltro; i messaggi entro la soglia sono quindi verificati interamente, quelli più grandi solo nella parte letta. Il controllo comporta la lettura in memoria fino alla soglia di ogni messaggio. Sulla richiesta la proprietà viene considerata solamente se registrata nella configurazione predefinita dell'erogazione o della fruizione, poiché la lettura avviene prima dell'identificazione del gruppo a cui appartiene l'operazione invocata; sulla risposta viene considerata anche se registrata sul gruppo.

- *soap.envelopeStructure.forward.check.enabled* (default: false): il messaggio viene verificato durante l'inoltro, senza limiti di dimensione, e l'inoltro viene interrotto prima della parte non conforme. Una richiesta viene rifiutata come descritto in precedenza. Una risposta che è già in consegna al client non può invece essere sostituita da un errore: il client riceve una risposta incompleta e la transazione viene registrata con esito 'Contenuto Risposta Malformato'. In entrambi i casi l'eventuale registrazione dei messaggi comprende l'intero messaggio ricevuto. La proprietà viene considerata anche se registrata sul gruppo.

Per i messaggi multipart (SOAP With Attachments, MTOM) i controlli vengono applicati alla parte che contiene l'Envelope, individuata tramite il parametro *start* del Content-Type o, in sua assenza, come prima parte. I controlli si disattivano, senza segnalare errori, quando il contenuto non è interpretabile in modo certo a livello di byte (es. codifica UTF-16, DOCTYPE o entità, parte radice codificata in base64).

**Configurazione globale**

Il comportamento di default è configurabile nel file *govway_local.properties* (si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione) tramite le seguenti proprietà:

   ::

      # Verifica della struttura dell'Envelope SOAP su richiesta e risposta
      org.openspcoop2.pdd.soapMessage.envelopeStructure.validation.request=true
      org.openspcoop2.pdd.soapMessage.envelopeStructure.validation.response=true
      # SOAP 1.1: elementi di altro namespace dopo il Body (vietati da WS-I Basic Profile R1011)
      org.openspcoop2.pdd.soapMessage.envelopeStructure.soap11.allowElementsAfterBody=false
      # Controlli opzionali per i messaggi gestiti senza costruzione del DOM (ridefinibili per singola API)
      org.openspcoop2.pdd.soapMessage.envelopeStructure.reader.fullCheck=false
      org.openspcoop2.pdd.soapMessage.envelopeStructure.forward.check=false

La disabilitazione della verifica (proprietà *validation.request* e *validation.response*) è sconsigliata e va riservata ai casi in cui un backend o un client esistente produca messaggi non conformi che non possono essere corretti.
