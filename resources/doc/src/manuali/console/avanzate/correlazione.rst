.. _correlazioneTransazioniDifferenti:

Correlazione tra transazioni differenti
---------------------------------------

Richiede accesso alla govwayConsole in modalità *avanzata* (sezione :ref:`modalitaAvanzata`).

Come descritto anche nella sezione :ref:`confManuale`, durante la configurazione di un
API di tipo SOAP o REST è possibile specificare i parametri descritti di
seguito rispettivamente in un servizio/azione o in una risorsa.

-  *ID Collaborazione*. Flag per consentire di specificare nelle
   richieste un valore che identifica una conversazione.

-  *Riferimento ID Richiesta*. Flag per consentire di specificare nelle
   richieste un identificativo relativo ad un messaggio precedente.

Tali parametri consentono agli applicativi client di fornire tali
informazioni tramite gli header di integrazione descritti nella sezione :ref:`headerClientGW`

Le informazioni fornite saranno associate alla traccia della transazione
gestita, e quindi utilizzabili in fase di monitoraggio tramite le
modalità di ricerca basate su identificativi descritte nella Guida alla
Console di Monitoraggio.
