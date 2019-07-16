Espressioni XPath su messaggi JSON
----------------------------------------------------------

In diverse funzionalità (:ref:`correlazione`, :ref:`rateLimiting_attivazioneNuovaPolicy`, :ref:`identificazioneAzione` ) è stata documentato la possibilità di utilizzare espressioni jsonPath o XPath per estrarre contenuti dai messaggi JSON o XML in transito sul Gateway.

L'estrazione dei contenuti da messaggi JSON si basa su espressioni JSONPath che allo stato attuale non hanno la stessa "potenza" delle espressioni XPath. 
Ad esempio:

- non è possibile ottenere il nome di un claim, come invece in XPath è possibile ottenere il local-name di un elemento tramite la funzione 'local-name'
- non si dispongono delle complesse funzioni per le elaborazioni sulle stringhe (ad es. in xpath è disponibile la funzione 'substring-before')
- ...

Per ovviare a tali limitazioni GovWay fornisce la possibilità di utilizzare espressioni XPath su messaggi JSON attraverso la seguente sintassi:

.. code-block:: none

   xpath [namespace(prefix1:uri1, ... ,prefixN:uriN) ] <espressioneXPathStandard>

Nel caso il gateway rilevi una espressione che inizi con il prefisso 'xpath ' da applicare su un messaggio JSON, effettua una trasformazione del messaggio in una rappresentazione xml. 
Ad esempio per il messaggio JSON:

.. code-block:: json

	{
		"prova":"test1",
		"prova2":23
	}

Per estrarre il valore del field 'prova' è possibile utilizzare le seguenti espressioni, la prima jsonPath e le successive xpath:

- $.prova
- xpath //prova/text()
- xpath /json2xml/prova/text()

La espressioni xpath sono utilizzabili poichè il messaggio JSON viene convertito nel seguente messaggio xml (inserito all'interno dell'elemento radice 'json2xml'):

.. code-block:: xml

	<json2xml>
		<prova>test1</prova>
		<prova2>23</prova2>
	</json2xml>

Mentre nell'esempio precedente sono sufficienti le funzionalità offerte dal jsonPath per estrarre il valore del field 'prova', ricorrere all'utilizzo di xPath è necessario se ad esempio vogliamo ottenere il nome di un field. Nell'esempio seguente l'espressione fornita consente di estrarre il nome dell'ultimo field presente nella struttura json 'prova2'. Tale risultato è ottenibile solamente utilizzando l'espressione xPath:

.. code-block:: none

   xpath local-name(/json2xml/*[last()])

In alcuni contesti i servizi REST non vengono implementati a partire da interfacce progettate ad hoc (OpenAPI, Swagger ...) ma sono frutto di una trasformazioni di esistenti servizi SOAP. In questi scenari, i servizi REST veicolano messaggi JSON ottenuti attraverso la trasformazione dei relativi messaggi XML utilizzati su SOAP. Per poter utilizzare espressioni xPath devono essere affrontate le problematiche di risoluzione dei prefissi e dei namespace. In questi contesti i messaggi JSON presenteranno field che possiedono nel nome il carattere ':' ereditato dalla rappresentazione xml. Di seguito un esempio di messaggio json ottenuto da una trasformazione di un messaggio xml equivalente:

.. code-block:: json

	{
	    "m:NomeAzioneTestRequest":  {
		"bodyWithNS" : "true",
		"xmlns:m" : "http://testNamespace",
		"prodotto" : {
		    "codice" : "26",
		    "altro:codice3" : "34",
		    "xmlns:altro" : "http://testNamespaceAltro"
		}
	    }
	}

Supponendo di voler estrarre il nome del field 'NomeAzioneTestRequest' e da questo eliminare anche il suffisso 'Request' è possibile utilizzare la seguente espressione xPath:

.. code-block:: none

   xpath namespace(m:http://testNamespace, altro:http://altro) substring-before(local-name(//json2xml/*),\"Request\")

Si può notare come tra il prefisso 'xpath ' e l'espressione xpath vera e propria (substring-before(...)) siano stati definiti i namespace che coinvolgono i field presenti nella struttura json che avevano il carattere ':'. 

La struttura xml, ottenuta dalla conversione del messaggio json, su cui viene applicata l'espressione xpath è la seguente:

.. code-block:: xml

	<json2xml xmlns:m="http://testNamespace"  xmlns:altro="http://altro"  xmlns:___xmlns="http://govway.org/utils/json2xml/xmlns">
		<m:NomeAzioneTestRequest>
			<bodyWithNS>true</bodyWithNS>
			<___xmlns:m>http://testNamespace</___xmlns:m>
			<prodotto>
				<codice>26</codice>
				<altro:codice3>34</altro:codice3>
				<___xmlns:altro>http://testNamespaceAltro</___xmlns:altro>
			</prodotto>
		</m:NomeAzioneTestRequest>
	</json2xml>

.. note::
	Il prefisso 'xmlns:' viene gestito automaticamente da GovWay, il quale gli associa un namespace di default 'http://govway.org/utils/json2xml/xmlns'. Tale namespace è possibile ridefinirlo aggiungendo all'elenco dei namespace anche un mapping per 'xmlns'.

