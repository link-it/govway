.. _3.2.1.p1:

Versione 3.2.1.p1
=================

Correzione Bug Critico su Profilo 'Fatturazione Elettronica'
------------------------------------------------------------

L'identificativo SDI per FatturaPA viene definito come xsd:integer con totalDigits=12. In GovWay è stato erroneamente utilizzato il tipo java Integer, non potendo gestire correttamente i messaggi SDI (fatture ed esiti) con identificativo oltre il numero limite di 2147483647.
Il problema è stato risolto adeguando la gestione del tipo dell'Identificativo SDI da Integer a String allineandosi così con quanto previsto nella FatturaB2B nella quale l'identificativo SDI viene definito come xsd:string con lunghezza da 1 a 36.
