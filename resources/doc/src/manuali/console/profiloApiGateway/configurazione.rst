.. _configSpecifica:

Configurazione dell'API
------------------------

I passi di configurazione fin qui descritti, per la registrazione di
erogazioni e fruizioni, consentono di ottenere uno stato delle entità
del registro pronto all'utilizzo in numerose situazioni.

Cliccando sulla voce *Erogazioni* o *Fruizioni* nell'intestazione dell'elenco è possibile consultarne i dettagli selezionando l'API attivata di interesse. 

La pagina di dettaglio consente di accedere ai principali elementi di configurazione :

- *Nome*: in assenza di configurazioni specifiche per risorsa/azione, accanto al nome dell'erogazione o della fruizione è presente un'icona che permette di disattivare/riattivare l'API come descritto nella sezione.
- *URL Invocazione*: se la console viene utilizzata in modalità avanzata, accedendo alla modifica della URL di Invocazione è possibile configurare la modalità di identificazione dell'azione come descritto.
- *Connettore*: endpoint del servizio acceduto dal gateway, cui verranno consegnate le richieste pervenute. In questa è presente sia l'icona a matita per aggiornare il valore del connettore che un'icona che consente di testare la raggiungibilità del servizio tramite il connettore fornito. Maggiori dettagli vengono forniti.
- *Gestione CORS*: stato abilitazione della funzione CORS. L'icona a matita consente di modificare l'impostazione corrente come descritto nella sezione.

.. figure:: ../_figure_console/paginaDettaglioErogazione.png
    :scale: 70%
    :align: center
    :name: paginaDettaglioErogazione

    Dettaglio di una erogazione

Tramite il pulsante *Configura* è inoltre possibile aggiungere ulteriori elementi di
configurazione attraverso le ulteriori funzionalità messe a
disposizione da GovWay.

.. figure:: ../_figure_console/ConfigurazioneSpecifica.png
    :scale: 70%
    :align: center
    :name: configurazioneSpecifica

    Configurazione di una erogazione


Accanto a ciascuna delle voci in elenco è presente un'icona che in base al colore assume i seguenti significati:
    - **Grigio**: funzionalità non attiva
    - **Rosso**: funzionalità attivata ma configurata in maniera incompleta o errata, quindi non funzionante
    - **Giallo**: funzionalità attivata in modalità opzionale o "non bloccante" e quindi in sola notifica
    - **Verde**: funzionalità attiva

Le funzionalità specifiche possono essere configurate in maniera differenziata per gruppi di risorse/azioni relative alla API erogata/fruita. Una nuova configurazione specifica può essere creata tramite il pulsante *Crea Nuova*. Il passaggio tra una configurazione e l'altra sarà possibile tramite i tab che risulteranno visibili nell'interfaccia. Questa funzionalità è descritta in dettaglio.

Le sezioni successive descrivono in dettaglio le configurazioni sopraelencate e i relativi contesti di utilizzo.
Tranne dove esplicitamente dichiarato, gli schemi di configurazione
descritti in seguito possono essere attuati sia sulle erogazioni che
sulle fruizioni.
