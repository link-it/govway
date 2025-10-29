Miglioramenti alla funzionalità 'Header di Integrazione'
--------------------------------------------------------

.. note::

   Nuova Funzionalità introdotta nella versione '3.3.9.p2'

Oltre alle informazioni standard previste dagli header di integrazione di GovWay, i client applicativi possono adesso fornire informazioni custom al gateway tramite un json il cui formato può essere arbitrariamente definito dal client.

Il json può essere inviato nell'header http 'GovWay-Integration' codificato in base64.

La presenza dell’header http non è obbligatoria ma se presente le informazioni contenute vengono rese disponibili per l'uso nelle varie funzionalità del gateway, come ad esempio la correlazione applicativa o l'utilizzo di claim custom nella generazione di token di sicurezza ModI, o nella generazione di asserzioni JWT per la negoziazione di token OAuth.
