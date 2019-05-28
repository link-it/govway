.. _configSpecifica:

Configurazione Specifica
------------------------

I passi di configurazione fin qui descritti, per la registrazione di
erogazioni e fruizioni, consentono di ottenere uno stato delle entità
del registro pronto all'utilizzo in numerose situazioni.

Nei casi in cui si abbia l'esigenza di aggiungere ulteriori elementi di
configurazione, sfruttando le ulteriori funzionalità messe a
disposizione da GovWay, si procede con le ulteriori configurazioni,
disponibili a partire dall'erogazione o fruizione già creata in
precedenza accedendo tramite il link *Configura* presente
nel dettaglio dell'erogazione/fruizione. Si accede quindi alla sezione di configurazione specifica (:numref:`configurazioneSpecifica`).

   .. figure:: ../_figure_console/ConfigurazioneSpecifica.png
    :scale: 100%
    :align: center
    :name: configurazioneSpecifica

    Configurazione Specifica di una erogazione

Le voci di configurazione che possono essere accedute sono:

- Controllo Accessi
- Rate Limiting
- Validazione
- Caching Risposta
- Sicurezza Messaggio
- MTOM (solo SOAP)
- Trasformazioni
- Tracciamento
- Registrazione Messaggi

Accanto a ciascuna delle voci in elenco è presente un'icona che in base al colore assume i seguenti significati:
    - **Grigio**: funzionalità non attiva
    - **Rosso**: funzionalità attivata ma configurata in maniera incompleta o errata, quindi non funzionante
    - **Giallo**: funzionalità attivata in modalità opzionale o "non bloccante" e quindi in sola notifica
    - **Verde**: funzionalità attiva

Le funzionalità specifiche possono essere configurate in maniera differenziata per gruppi di risorse/azioni relative alla API erogata/fruita. Una nuova configurazione specifica può essere creata tramite il pulsante *Crea Nuova*. Il passaggio tra una configurazione e l'altra sarà possibile tramite i tab che risulteranno visibili nell'interfaccia. Questa funzionalità è descritta in dettaglio nella sezione :ref:`configSpecificaRisorsa`.

Le sezioni successive descrivono in dettaglio le configurazioni sopraelencate e i relativi contesti di utilizzo.
Tranne dove esplicitamente dichiarato, gli schemi di configurazione
descritti in seguito possono essere attuati sia sulle erogazioni che
sulle fruizioni.
