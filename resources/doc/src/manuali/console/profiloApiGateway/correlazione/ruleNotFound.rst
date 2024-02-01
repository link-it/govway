.. _correlazione_ruleNotFound:

Nessuna regola di correlazione applicativa applicabile
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Una richiesta non applicabile a nessuna regola di correlazione applicativa (vedi configurazione '*Elemento*' descritto in :ref:`correlazione`), fino alla versione 3.3.13.p1, terminava con l'errore "Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti è presente nel body". Dalla versione successiva è stato modificato il default in modo da accettare la richiesta.

È possibile ripristinare il precedente comportamento registrando una delle seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione (i valori associabili alle proprietà sono 'true' o 'false'):

- *correlation.request.ruleNotFound.abortTransaction*: (default:false) consente di terminazione con errore la transazione, in caso la richiesta non sia applicabile a nessuna regola di correlazione applicativa;

- *correlation.response.ruleNotFound.abortTransaction*: (default:false) consente di terminazione con errore, in caso la risposta non sia applicabile a nessuna regola di correlazione applicativa.
