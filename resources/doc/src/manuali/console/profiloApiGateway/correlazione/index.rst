.. _correlazione:

Correlazione Applicativa
^^^^^^^^^^^^^^^^^^^^^^^^

La funzione di *Correlazione Applicativa* consente al gateway che
elabora il messaggio di richiesta, di estrarre un identificatore
relativo al contenuto applicativo. L'identificatore, se presente,
finisce nei sistemi di tracciamento e diagnostici, a completamento delle
informazioni già presenti. I dati per configurare la correlazione
applicativa consistono in un insieme di regole per l'estrazione di tale
identificatore.

.. note::

	**Lunghezza massima per l'identificativo estratto**

	L'identificativo applicativo estratto deve possedere una lunghezza non superiore ai 255 caratteri. Per maggiori informazioni si rimanda alla sezione :ref:`correlazione_truncate`.

Per accedere alla configurazione della correlazione applicativa, per una
data erogazione/fruizione, si utilizza la sezione "Correlazione
Applicativa" presente nell'ambito della configurazione del tracciamento
di una fruizione/erogazione (sezione :ref:`tracciamentoErogazione`).

Utilizzando il collegamento *Regole*, presente nel riquadro della
Richiesta o Risposta, si accede all'elenco delle regole di correlazione
applicativa presenti. Premere il pulsante *Aggiungi* per aggiungere una
nuova regola (:numref:`correlazioneFig`)

   .. figure:: ../../_figure_console/CorrelazioneApplicativa-new.png
    :scale: 100%
    :align: center
    :name: correlazioneFig

    Creazione di una regola di correlazione applicativa

Per la creazione di una regola di correlazione applicativa si devono
indicare i seguenti dati:

-  *Elemento*: Questo dato serve per capire su quali messaggi è
   applicabile la regola di correlazione applicativa che si sta
   definendo. Lasciando il campo vuoto si intende che la regola si
   applica a tutti i messaggi. In alternativa è possibile indicare:

   -  *Nome Azione o Risorsa*: il nome esatto dell'azione o della
      risorsa su cui verrà applicativa la regola

   - *HttpMethod e Path* (utilizzabile solo su API REST): metodo http e path di una risorsa dell'API; è possibile indicare qualsiasi metodo o qualsiasi path con il carattere speciale '\*'. È inoltre possibile definire solamente la parte iniziale di un path attraverso lo '\*'. Alcuni esempi:

	- 'POST /resource'
	- '\* /resource'
	- 'POST \*'
	- '\* /resource/\*'

   -  *XPath o JSONPath*: Espressione che può rappresentare un XPath o
      JSONPath. Se l'espressione ha un match con il contenuto la regola
      verrà applicata

   -  *LocalName dell'elemento xml*: in caso il messaggio sia un xml
      (soap o rest), è possibile indicare il local name del root element
      xml su cui verrà applicativa la regola

   .. note::

	Se una richiesta non è applicabile a nessuna regola di correlazione applicativa, fino alla versione 3.3.13.p1 la richiesta terminava con l'errore "Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti è presente nel body". Dalla versione successiva è stato modificato il default in modo da accettare la richiesta. È comunque possibile ripristinare il precedente comportamento come descritto nella sezione :ref:`correlazione_ruleNotFound`.

-  *Modalità Identificazione*: rappresenta la modalità di acquisizione
   dell'identificatore applicativo. Può assumere i seguenti valori:

   -  *Url di Invocazione*: il valore viene preso dalla url utilizzata dal
      servizio applicativo per l'invocazione. La regola per l'estrazione
      dalla url viene specificata tramite un'espressione regolare
      inserita nel campo 'Espressione Regolare' (l'espressione deve avere un match con l'intera url).

   -  *Header HTTP*: Il valore viene estratto dall'header di trasporto
      avente il nome indicato nel campo successivo.

   -  *Contenuto*: Il valore viene estratto direttamente dal
      messaggio applicativo. La regola per l'estrazione dal messaggio è
      specificata tramite un'espressione XPath o JSONPath inserita nel
      campo 'Pattern'.

   -  *Header di Integrazione*: il valore viene estratto dall'header di integrazione
      GovWay presente nel valore della proprietà *IDApplicativo*.

   - *Template*: il valore è il risultato dell'istanziazione del template fornito rispetto ai dati della richiesta.

   - *Freemarker Template*: il valore è ottenuto tramite il processamento di un Freemarker Template.

   - *Velocity Template*: il valore è ottenuto tramite il processamento di un Velocity Template.

   -  *Disabilitata*: l'identificatore applicativo non viene estratto.
      Questa opzione è utile quando si vuole disabilitare l'estrazione
      dell'id applicativo solo per specifici messaggi;

-  *Identificazione Fallita*: azione da intraprendere nel caso fallisca
   l'estrazione dell'identificatore applicativo tramite la regola
   specificata. Nel caso sia stato indicato *blocca*, tali richieste non
   verranno accettate con restituzione di un errore al mittente;

   .. note::

	L'estrazione di un identificativo nullo o stringa vuota viene trattato come processo d'identificazione fallito. È possibile modificare questo comportamento seguendo le indicazioni fornite nella sezione :ref:`correlazione_extractedIdentifier`.

-  *Riuso ID*: opzione per abilitare/disabilitare il riuso
   dell'identificatore del messaggio (assegnato dal gateway) nel caso in
   cui vengano inviati messaggi con identificatori applicativi già
   processati in precedenza.

.. toctree::
   :maxdepth: 2

   truncate
   ruleNotFound
   extractedIdentifier


