.. _tracciamentoErogazione:

Tracciamento
~~~~~~~~~~~~

Il tracciamento è la funzionalità del gateway che comporta la
registrazione dei dati relativi alle comunicazioni in transito
riguardanti i servizi erogati e fruiti. Nella logica del gateway, tutte
le informazioni che riguardano una singola interlocuzione, a partire
dalla richiesta pervenuta fino alla conclusione con l'invio
dell'eventuale risposta, sono riconducibili ad un'unica entità
denominata *Transazione*.

Una transazione registrata dal gateway ha la seguente struttura:

-  *Dati di Identificazione Generale*. Sono le informazioni che
   identificano la comunicazione specifica in termini dei soggetti
   coinvolti e del servizio richiesto: Soggetto Erogatore, Soggetto
   Fruitore, Servizio, Azione, Esito, ...

-  *Dati della Richiesta*. Sono le informazioni di dettaglio relative
   alla richiesta: Identificativo del Messaggio, Timestamp di ingresso,
   Timestamp di uscita, dimensioni del messaggio, ...

-  *Dati della Risposta*. Sono le medesime informazioni già citate al
   punto precedente, ma relative alla comunicazione di risposta.

-  *Traccia Richiesta*. La traccia emessa dal gateway con i dettagli
   relativi alla richiesta.

-  *Traccia Risposta*. La traccia emessa dal gateway con i dettagli
   relativi alla risposta.

-  *Messaggi Diagnostici*. La sequenza dei messaggi diagnostici,
   ordinati cronlogicamente, emessi dal gateway nel corso
   dell'elaborazione dell'intera transazione.

-  *Fault di Ingresso*. Viene registrato come Fault di Ingresso
   l'eventuale messaggio di errore ricevuto dal gateway durante
   l'invocazione di un servizio (interno o esterno al dominio gestito).

-  *Fault di Uscita*. Viene registrato come Fault di Uscita l'eventuale
   messaggio di errore inoltrato dal gateway al mittente della richiesta
   (interno o esterno al dominio gestito), dopo aver ricevuto un fault
   dal servizio invocato.

-  *Parametri e Misurazioni*. Sono i parametri e le misurazioni che
   riguardano la transazione, come ad esempio: l'identificativo della
   transazione, le url invocate, i tempi di latenza, ...

In questa sezione è possibile personalizzare la configurazione di
default del tracciamento definita in accordo a quanto descritto in sezione :ref:`tracciamento`. Le
personalizzazioni inserite in questo contesto avranno validità per le
sole comunicazioni riguardanti la specifica erogazione/fruizione (:numref:`tracciamentoErogazioneFig`).

   .. figure:: ../_figure_console/TracciamentoServizio.png
    :scale: 100%
    :align: center
    :name: tracciamentoErogazioneFig

    Tracciamento per la singola erogazione/fruizione

Le sezioni presenti nella pagina sono:

-  *Transazioni Registrate*: l'utente ha l'opzione per mantenere il
   default definito nella sezione di configurazione generale (sezione :ref:`tracciamento`) oppure
   ridefinirlo.

-  *Messaggi Diagnostici*: l'utente ha l'opzione per mantenere il
   default definito nella sezione di configurazione generale (sezione :ref:`tracciamento`) oppure
   ridefinire il criterio per la sola memorizzazione su Database.

-  *Correlazione Applicativa*: consente di impostare delle regole per
   estrarre dai messaggi in transito, codici, riferimenti, o altri
   contenuti al fine di arricchire i dati tracciamento generati dal
   gateway (sezione :ref:`correlazione`).
