Miglioramenti della funzionalità di estrazione dei contenuti JSON
-----------------------------------------------------------------

L'estrazione dei contenuti da messaggi JSON, utilizzata nelle
funzionalità di Correlazione Applicativa, Rate Limiting,
Trasformazioni, Identificazione dell'azione etc. era possibile
attraverso la definizione di espressioni JSONPath.

Essendo allo stato attuale, XPath più espressivo di JSONPath, è stata
introdotta la possibilità di utilizzare espressioni XPath su di una
rappresentazione xml dell'oggetto json in transito.
