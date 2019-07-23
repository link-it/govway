Nuova funzionalità XPATH su JSON
--------------------------------

L'estrazione dei contenuti da messaggi JSON, utilizzato nelle funzionalità di Correlazione Applicativa, Rate Limiting, Trasformazioni, Identificazione dell'azione etc. era attuabile unicamente attraverso la definizione di espressioni JSONPath.

Poichè allo stato attuale quello che consente di fare JSONPath sono un sottoinsieme delle funzionalità offerte da XPath per XML, è stata aggiunta la possibilità di utilizzare espressioni XPath su di una rappresentazione xml dell'oggetto json in transito.
