.. _errori_502_UnprocessableResponseContent:

UnprocessableResponseContent
----------------------------

GovWay ha rilevato un payload differente da quello indicato nell'header http 'Content-Type' della risposta.

L'errore viene generato anche quando la risposta contiene un Envelope SOAP con una struttura non conforme (es. più elementi Body o Header, Fault non unico figlio del Body); maggiori dettagli sono presenti nella sezione :ref:`configAvanzataStrutturaEnvelopeSoap`.
