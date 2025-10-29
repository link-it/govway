Miglioramenti alla funzionalità di generazione dei report statistici
----------------------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- aggiunta configurabilità dei criteri di raggruppamento, tramite proprietà '\*.groupBy.<campo>', per ottimizzare la cardinalità delle aggregazioni;

- migliorato il calcolo della latenza totale nelle aggregazioni statistiche:
	
	- viene ora calcolata anche per le transazioni prive delle date di consegna al backend (data_uscita_richiesta e data_ingresso_risposta), aumentando la copertura e la precisione delle metriche;
	- corretta la formula di calcolo delle latenze medie per escludere dal denominatore le richieste con latenza non disponibile (NULL o -1),  eliminando la sottostima sistematica precedentemente causata da transazioni con esiti di errore e timestamp incompleti.

- il record statistico memorizzato nella base dati relativo all’intervallo corrente viene ora registrato con 'stato_record=3', al fine di distinguerlo dai record riferiti a intervalli passati non più soggetti a modifiche ('stato_record=1').
	
	
