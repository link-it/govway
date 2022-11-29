Miglioramenti alla funzionalità 'Header di Integrazione'
--------------------------------------------------------

.. note::

   La funzionalità è stata introdotta nella versione '3.3.9.p2'

Oltre alle modalità di interscambio di informazioni standard il client può adesso fornire altre informazioni al gateway tramite un json il cui formato non è prestabilito da GovWay ma può essere definito in maniera arbitraria dal client.

Il json può essere inviato nell'header http 'GovWay-Integration' codificato in base64.

La presenza dell’header http non è obbligatoria ma se presente le sue informazioni vengono rese disponibili tramite la keyword "integration" utilizzabile nelle varie funzionalità del gateway come ad esempio la correlazione applicativa o l'utilizzo di claim custom nella generazione di token di sicurezza ModI o nella generazione di asserzioni JWT per la negoziazione di token OAuth.

Inoltre per quanto concerne i claim aggiuntivi che possono essere aggiunti all'interno del payload dei JWT generati da GovWay è adesso possibile:

- aggiungere il claim solamente se la risoluzione dinamica del valore viene effettuata con successo utilizzando la forma opzionale "?{..}";

- definire tipi primitivi json (boolean,int,long,float,double) effettuando un cast nella forma "cast(<valore> as <tipoPrimitivo>)";

- convertire una lista json di tipi primitivi in lista di stringhe effettuando un cast nella forma "cast(<valore> as string array)".
