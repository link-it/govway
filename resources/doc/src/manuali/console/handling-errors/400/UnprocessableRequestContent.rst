.. _errori_400_UnprocessableRequestContent:

UnprocessableRequestContent
---------------------------

GovWay ha rilevato un payload differente da quello indicato nell'header http 'Content-Type'.

L'errore viene restituito anche quando la richiesta contiene un Envelope SOAP con una struttura non conforme (es. più elementi Body o Header, Fault non unico figlio del Body); maggiori dettagli sono presenti nella sezione :ref:`configAvanzataStrutturaEnvelopeSoap`.
