.. _configSpecificaRisorsa:

Differenziare le configurazioni specifiche per risorsa/azione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le configurazioni specifiche che andiamo a descrivere si possono
differenziare per sottoinsiemi delle azioni/risorse presenti nel
servizio erogato/fruito. Il sistema crea automaticamente una
configurazione unica, valida per tutte le azioni/risorse del servizio.
Per intervenire su tale configurazione, o crearne di nuove, sia accede
al collegamento presente nella colonna *Configurazione*, in
corrispondenza della voce di erogazione/fruizione in elenco. Le
funzionalità di configurazione disponibili per ciascun sottoinsieme di
azioni/risorse sono raggruppabili in:

-  *Controllo Accessi*: per configurare i criteri di autenticazione,
   autorizzazione e gestione token delle richieste.

-  *Validazione*: per configurare i criteri di validazione dei messaggi
   in transito sul gateway.

-  *Sicurezza Messaggio*: per configurare le misure di sicurezza
   applicate a livello del messaggio.

-  *Tracciamento*: per configurare specifiche modalità di estrazione
   dati, dalle comunicazioni in transito, per l'arricchimento della
   traccia prodotta.

-  *MTOM*: per configurare l'utilizzo del protocollo ottimizzato per
   l'invio di attachment tra nodi SOAP.

-  *Registrazione Messaggi*: consente di ridefinire le politiche di
   archiviazione dei payload rispetto a quanto previsto dalla
   configurazione di default (vedi sezione :ref:`tracciamento`).

Per creare un nuovo gruppo di configurazione, dopo aver seguito il
collegamento *visualizza* relativo all'erogazione/fruizione selezionata,
si preme il pulsante *Aggiungi*

   .. figure:: ../_figure_console/Config-new.png
    :scale: 50%
    :align: center
    :name: configNew

    Aggiunta di un gruppo di configurazioni

Compilare il form di creazione della nuova configurazione (:numref:`configNew`):

-  *Azioni:* selezionare dall'elenco le azioni sulle quali si vuole
   abbia effetto la nuova configurazione.

-  *Mode:* effettuare la scelta tra *Eredita Da* e *Nuova*. Scegliendo
   la prima opzione, verrà creata una configurazione clone di quella
   selezionata nell'elemento del form subito successivo
   (Configurazione). Scegliendo la seconda opzione, si procederà alla
   creazione di una nuova configurazione, specificando subito le
   informazioni di Controllo degli Accessi e Connettore.

.. note:: Nota
    Dopo aver creato ulteriori configurazioni, si tenga presente che la
    configurazione di default verrà applicata alle sole azioni per le
    quali non è presente una regola di configurazione specifica.

.. note:: Nota
    È possibile disabilitare un'intera configurazione, senza la
    necessità di eliminarla, utilizzando il collegamento presente nella
    colonna "Abilitato" in corrispondenza dell'elemento di
    configurazione. Un successivo clic farà tornare la configurazione
    nello stato abilitato.
