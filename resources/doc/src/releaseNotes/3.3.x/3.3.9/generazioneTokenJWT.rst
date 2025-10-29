Miglioramenti alla modalità di generazione dei token JWT
--------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.9.p2'

Per quanto concerne i claim aggiuntivi che possono essere aggiunti all'interno del payload dei JWT generati da GovWay è adesso possibile:

- aggiungere il claim solamente se la risoluzione dinamica del valore viene effettuata con successo utilizzando la forma opzionale "?{..}";

- definire tipi primitivi json (boolean,int,long,float,double) effettuando un cast nella forma "cast(<valore> as <tipoPrimitivo>)";

- convertire una lista json di tipi primitivi in lista di stringhe effettuando un cast nella forma "cast(<valore> as string array)".
