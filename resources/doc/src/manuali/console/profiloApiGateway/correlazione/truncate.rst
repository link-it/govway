.. _correlazione_truncate:

Lunghezza massima dell'identificativo di correlazione applicativa
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

L'identificativo applicativo estratto deve possedere una lunghezza non superiore ai 255 caratteri.

Nel caso l'identificativo di correlazione superi la massima lunghezza consentita il comportamento di default di GovWay varia a seconda del criterio di gestione dell'identificazione fallita configurato nella regola di correlazione:

- nel caso di gestione di tipo 'blocca' la transazione termina con errore e nel diagnostico viene informato l'utente che è stata superata la massima lunghezza consentita;

- nel caso di gestione di tipo 'accetta' la transazione termina con successo e non viene salvata alcun id di correlazione applicativa.

È possibile modificare i comportamenti di default precedentemente indicati abilitando il troncamento dell'identificativo estratto al fine di portare la sua lunghezza alla massima dimensione consentita.
Per abilitare il troncamento è possibile registrare una delle seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione (i valori associabili alle proprietà sono 'true' o 'false'):

- *correlation.request.truncate* o *correlation.response.truncate* : consentono di abilitare il troncamento rispettivamente per la richiesta o per la risposta;

- *correlation.truncate*: consente di abilitare il troncamento sia per la richiesta che per la risposta.

Sono inoltre disponibili altre proprietà che consentono una abilitazione a grana più fine sulla singola modalità di gestione:

- *correlation.request.blockIdentificationFailed.truncate* o *correlation.response.blockIdentificationFailed.truncate* : consentono di abilitare il troncamento, rispettivamente per la richiesta o per la risposta, solamente per la gestione di tipo 'blocca';

- *correlation.request.acceptIdentificationFailed.truncate* o *correlation.response.acceptIdentificationFailed.truncate* : consentono di abilitare il troncamento, rispettivamente per la richiesta o per la risposta, solamente per la gestione di tipo 'accetta';

- *correlation.blockIdentificationFailed.truncate* : consente di abilitare il troncamento, sia per la richiesta che per la risposta, solamente per la gestione di tipo 'blocca';

- *correlation.acceptIdentificationFailed.truncate* : consente di abilitare il troncamento, sia per la richiesta che per la risposta, solamente per la gestione di tipo 'accetta'.
