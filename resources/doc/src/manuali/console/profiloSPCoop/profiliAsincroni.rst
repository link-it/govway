.. _profiliAsincroni:

Profili Asincroni
-----------------

Profilo di Collaborazione Asincrono Simmetrico
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La registrazione di un profilo asincrono simmetrico prevede che vengano
correlati tra di loro due azioni di due servizi differenti presenti
all'interno del solito accordo di servizio parte comune (API). Di
seguito un esempio di tale configurazione.

   .. figure:: ../_figure_console/asCorrelazione.png
    :scale: 100%
    :align: center
    :name: asCorrelazione

    Correlazione Asincrona Simmetrica

Ruolo Fruitore
^^^^^^^^^^^^^^

Per poter fruire di un servizio con il profilo asincrono simmetrico la
registrazione dell'applicativo fruitore deve prevedere, oltre alle
normali configurazioni, la definizione di un connettore attraverso il
quale la PdD consegnerà la risposta asincrona. Per definire tale
connettore utilizzare la sezione 'Risposta Asincrona' presente
nell'elenco degli applicativi relativamente al servizio desiderato.

Ruolo Erogatore
^^^^^^^^^^^^^^^

Per poter erogare un servizio con il profilo asincrono simmetrico non
sono richieste particolari configurazioni. Dovrà essere erogato il
servizio relativo alla richiesta e fruito il servizio su cui inviare la
risposta.

Profilo di Collaborazione Asincrono Asimmetrico
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La registrazione di un profilo asincrono asimmetrico prevede che vengano
correlati tra di loro due azioni, normalmente di uno stesso servizio,
presenti all'interno dell'accordo di servizio parte comune (API). Di
seguito un esempio di tale configurazione.

   .. figure:: ../_figure_console/aaCorrelazione.png
    :scale: 100%
    :align: center
    :name: aaCorrelazione

    Correlazione Asincrona Asimmetrica

Ruolo Fruitore
^^^^^^^^^^^^^^

Per poter fruire un servizio con il profilo asincrono asimmetrico non
sono richieste particolari configurazioni. Dovrà essere fruito il
servizio su cui inviare la richiesta e richiedere l'esito della
risposta.

Ruolo Erogatore
^^^^^^^^^^^^^^^

Per poter erogare un servizio con il profilo asincrono asimmetrico la
registrazione del servizio applicativo erogatore deve prevedere, oltre
alle normali configurazioni, la definizione di un connettore attraverso
il quale la PdD consegnerà il messaggio contenente la richiesta dello
stato dell'operazione (seconda fase del profilo asincrono asimmetrico).
Per definire tale connettore utilizzare la sezione 'Risposta Asincrona'
presente nell'elenco dei servizi applicativi relativamente al servizio
desiderato.
