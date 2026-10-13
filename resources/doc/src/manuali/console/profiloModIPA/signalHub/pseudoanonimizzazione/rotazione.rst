.. _modipa_signalhub_pseudoanonimizzazione_rotazione:

Ciclo di vita e rotazione del seme
-----------------------------------

**Generazione del primo seme**

Il primo seme di un'erogazione viene generato alla prima richiesta verso la risorsa di pseudoanonimizzazione oppure alla pubblicazione del primo segnale ``CREATE``, ``UPDATE`` o ``DELETE``. Alla generazione del primo seme non viene inviato alcun segnale ``SEEDUPDATE``: il seme non sostituisce un seme precedente.

**Durata**

Ogni seme vale per il numero di giorni configurato sull'erogazione ('Giorni Rotazione Seme') al momento della sua generazione. La durata viene memorizzata insieme al seme: la modifica del valore sull'erogazione si applica solamente ai semi generati successivamente, mentre il seme in uso resta valido fino alla sua scadenza originale. Analogamente, una modifica dell'algoritmo o della dimensione del seme si applica a partire dal seme successivo.

**Rotazione**

Scaduto il seme, il primo segnale ``CREATE``, ``UPDATE`` o ``DELETE`` pubblicato causa:

- la generazione di un nuovo seme con algoritmo, dimensione e durata configurati in quel momento sull'erogazione;
- l'invio automatico al Signal Hub PDND del segnale ``SEEDUPDATE``, effettuato tramite la stessa fruizione e con le stesse credenziali del segnale in corso di pubblicazione;
- il calcolo del digest del segnale in corso con il nuovo seme.

Se l'invio del segnale ``SEEDUPDATE`` fallisce, il seme in uso non cambia, il segnale in corso non viene pubblicato e la rotazione viene ritentata alla pubblicazione del segnale successivo.

Dopo la rotazione la risorsa di pseudoanonimizzazione restituisce il nuovo seme. I semi precedenti restano consultabili tramite il signalId del segnale ``SEEDUPDATE`` che li ha introdotti, nei limiti dello storico configurato tramite la proprietà ``org.openspcoop2.protocol.modipa.signalHub.seed.history`` (:ref:`modipa_signalhub_properties`).

**Rotazione forzata**

La rotazione del seme in uso può essere anticipata tramite la console di gestione, nella sezione '*Configurazione > Cache PDND > Semi Signal Hub*' descritta in :ref:`configCachePDNDSemiSignalHub`, dove è anche possibile consultare la data di generazione e di scadenza del seme in uso e lo storico dei semi precedenti.

In presenza di più nodi, ogni nodo mantiene il seme in uso in una cache e lo riverifica sulla base dati con una frequenza massima indicata dalla proprietà ``org.openspcoop2.protocol.modipa.signalHub.seed.cache.refreshSeconds`` (default 300 secondi). Per garantire che nessun nodo continui a utilizzare il seme precedente dopo la rotazione, una rotazione forzata non dichiara scaduto il seme immediatamente ma trascorso tale intervallo: in questo modo tutti i nodi conoscono la nuova scadenza prima che diventi effettiva e la rotazione avviene, come per la scadenza naturale, al primo segnale pubblicato successivamente, con l'invio del segnale ``SEEDUPDATE``. Non è necessario alcun reset delle cache dei nodi.

.. note::
    La cancellazione diretta dei semi sulla base dati è sconsigliata: in assenza di un seme GovWay ne genera uno nuovo come primo seme, senza inviare il segnale ``SEEDUPDATE``, e i consumatori dei segnali non vengono avvisati del cambio.
