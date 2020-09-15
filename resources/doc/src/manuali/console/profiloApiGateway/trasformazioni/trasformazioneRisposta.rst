Regole di Trasformazione della Risposta
***************************************

Analogamente a quanto visto per la richiesta è possibile utilizzare il link "Risposte", nell'area "Regole di Trasformazione", per procedere con l'impostazione di regole per trasformare le risposte. A differenza del caso della richiesta, dove si può definire un unico meccanismo di trasformazione, in questo caso è possibile definire diverse regole di trasformazione basate sulla casistica delle risposte che si può presentare.

Quando si aggiunge una nuova regola di trasformazione della risposta si procede inserendo le seguenti informazioni (:numref:`trasf_Risposta`):

- Nome: nome assegnato alla regola di trasformazione
- Codice Risposta: Come criterio di applicabilità della regola, è possibile indicare il codice di risposta con le seguenti opzioni:

    - Qualsiasi: qualunque codice di risposta ottenuto
    - Singolo: si inserisce unp specifico codice di risposta per il quale è applicabile la regola
    - Intervallo: si inseriscono gli estremi dell'intervallo di codici di risposta per il quale è applicabile la regola

- Content-Type: criterio di corrispondenza con uno dei content-type indicati
- Pattern: espressione XPath o JsonPath da confrontare con il contenuto della risposta per un eventuale match

   .. figure:: ../../_figure_console/TrasformazioniRegolaRisposta.png
    :scale: 80%
    :align: center
    :name: trasf_Risposta

    Creazione regola di trasformazione della risposta

Le operazioni di trasformazione sulla risposta sono attuabili in maniera del tutto analoga a quanto già descritto per la richiesta. Diversamente dal caso della richiesta, al posto delle modifiche sui parametri della URL (non presenti nella risposta) è possibile modificare il Codice Risposta restituito.

.. note::
    Se sulla richiesta si è scelto di attuare la conversione da SOAP a REST, o viceversa, la trasformazione complementare risulterà disponibile anche nella configurazione della risposta.
