.. _scenari_erogazione_oauth_configurazione:

Configurazione
--------------

L'erogazione è già stata preconfigurata per prevedere un controllo degli accessi differente tra le risorse che riguardano operazioni di scrittura (POST, PUT, DELETE) e le risorse che riguardano solo letture (GET). 

Di seguito vengono descritti i passi che sono stati effettuati per arrivare alla configurazione esistente partendo dall'erogazione configurata con accesso pubblico. 

I passi di configurazione finalizzati a limitare l'accesso alle sole operazioni di scrittura sono i seguenti:

1. Dal dettaglio dell'erogazione, si procede con la creazione di una nuova configurazione, cui diamo il nome *"Scritture"* (:numref:`erogazione_config_scritture_fig`).

    - Selezionare dall'elenco delle risorse quelle che riguardano operazioni di scrittura (POST, PUT, DELETE)
    - Indicare per la *Modalità* il valore *"Nuova"* e quindi selezionare *"autenticato"* nel campo *Accesso API*

   .. figure:: ../../_figure_scenari/Erogazione_config_scritture.png
    :scale: 80%
    :align: center
    :name: erogazione_config_scritture_fig

    Creazione di una configurazione specifica per le operazioni di scrittura

2. Nella nuova configurazione "Scritture" si va ad aggiornare la sezione *"Controllo Accessi"* effettuando le seguenti azioni (:numref:`erogazione_controlloaccessi_token_fig`):

    - Abilitare l'autenticazione token selezionando la policy *"KeyCloak"* (configurazione preesistente per l'integrazione all'authorization server), lasciando invariate le altre opzioni del medesimo riquadro.
    - Disabilitare le altre funzionalità di controllo degli accessi: Autenticazione Trasporto, Autorizzazione e Autorizzazione Contenuti.

   .. figure:: ../../_figure_scenari/Erogazione_controlloaccessi_token.png
    :scale: 80%
    :align: center
    :name: erogazione_controlloaccessi_token_fig

    Impostazione dell'autenticazione token nel controllo degli accessi

3. Dopo aver salvato la nuova configurazione, verificare il riepilogo delle informazioni, che devono corrispondere a quanto riportato in :numref:`erogazione_token_riepilogo_fig`.

   .. figure:: ../../_figure_scenari/Erogazione_token_riepilogo.png
    :scale: 80%
    :align: center
    :name: erogazione_token_riepilogo_fig

    Riepilogo della configurazione effettuata
