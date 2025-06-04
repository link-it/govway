.. _modipa_tracingPdnd_govway:

Supporto offerto da GovWay
--------------------------

*Panoramica*

GovWay rende completamente trasparente il supporto al tracing PDND per il soggetto erogatore, fornendo un sistema che consente la generazione e pubblicazione dei tracciati verso la PDND.

La raccolta delle informazioni avviene monitorando tutte le transazioni relative alle erogazioni/fruizioni derivanti da API con generazione token di tipo "Authorization PDND".

Tali sistemi di generazione e pubblicazione possono essere implementati tramite:

 - Timer interni a GovWay
 - Programmi batch esterni, eseguibili direttamente dall’utente

*Batch*

Nel caso di installazione avanzata, è possibile installare questi componenti come batch esterni. In tal caso, nella cartella ``batch/`` generata dall’installer, saranno presenti gli script ``generaPdndGenerazioneTracciamento`` e ``generaPdndPubblicazioneTracciamento`` che permettono rispettivamente:

 - La generazione dei dati da inviare alla PDND
 - La pubblicazione, ovvero l’invio effettivo dei file CSV

In questo scenario, è necessario configurare l’ambiente come descritto nella sezione dedicata alla generazione e al report statistico.

*Timers*

Di default, le componenti sono collegate a timer interni di GovWay che consentono l’esecuzione periodica delle funzionalità di generazione e pubblicazione.

È possibile abilitare o disabilitare questi timer dalla console, nella sezione:
``Runtime -> Thread Attivi -> Generazione Tracciamento PDND -> Generazione / Pubblicazione``

*Multi Tenant*

Nel contesto multi-tenant, i servizi di generazione e pubblicazione dei tracciamenti possono operare per ciascun soggetto interno definito.

In caso di personalizzazioni, è possibile accedere alla console nella sezione "Soggetti" per abilitare o disabilitare il supporto al tracciamento per ciascun soggetto specifico. 

Questa operazione può anche essere effettuata direttamente nel file di configurazione delle properties, come descritto nella sezione successiva.
