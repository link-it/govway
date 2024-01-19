### Introduzione

Gli scripts possono essere utilizzati per effettuare la manutenzione ordinaria delle tabelle di tracciamento.

Lo scenario di manutenzione prevede che lo script venga eseguito ad intervalli regolari (Es. Ogni settimana) per eliminare dalle tabelle dati backuppati in precedenza o che non si ritengono piu' utili.

Si presume quindi che venga definita una politica di manutenzione che stabilisca:
- Numero di giorni da mantenere online (di seguito RETENTION)
- Numero di giorni precedenti la soglia di ritenzione su cui eseguire le cancellazioni (di seguito CLEAN) 

### Descrizione

Eseguito senza modifiche lo script mantiene per default la seguente politica:

RETENTION=30
CLEAN=10

Questo significa che: verranno mantenuti i dati generati nei 30 giorni precedenti la data di esecuzione dello script; vengono eliminati tutti i dati generati 10 giorni prima della soglia di ritenzione.

È possibile modificare la politica di default, editando lo script e modificando i parametri 'clean' e 'retention' definiti in testa allo script stesso.

È consigliabile sempre far verificare lo script SQL modificati dal DBA, in modo da assicurare che siano utilizzabili correttamente nell'installazione oggetto di manutenzione.
