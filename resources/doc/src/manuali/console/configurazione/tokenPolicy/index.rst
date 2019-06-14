.. _tokenPolicy:

Token Policy
------------

Per poter definire politiche di controllo degli accessi basate sui
Bearer Token o per poterne spedire uno verso l'endpoint associato ad un connettore
è necessario creare delle Token Policy da riferire nelle
configurazioni degli specifici servizi. La gestione delle Token Policy
si effettua andando alla sezione *Configurazione > Token Policy* della
govwayConsole. Per creare una nuova policy si utilizza il pulsante
*Aggiungi*. Il form di creazione appare inizialmente come quello
illustrato in :numref:`tokenPolicyFig`.


   .. figure:: ../../_figure_console/TokenPolicy-nuova.png
    :scale: 100%
    :align: center
    :name: newTokenPolicyFig

    Creazione di una Token Policy

Inizialmente si inseriscono i dati identificativi:

-  *Nome*: nome univoco da assegnare alla policy

-  *Tipo*: determina il tipo di policy:
	
	- *Validazione*: definisce una policy utilizzabile per validare Bearer Token nel Controllo degli Accessi (:ref:`apiGwGestioneToken`)
	- *Negoziazione*: definisce i criteri per la negoziazione di un Bearer Token poi utilizzato sui connettori nei quali sarà associata la policy (:ref:`avanzate_connettori_tokenPolicy`)

-  *Descrizione*: testo di descrizione generale della policy

I parametri richiesti differiscono a seconda del tipo selezionato.
Le sezioni successive dettagliano i due tipi supportati.

.. toctree::
        :maxdepth: 3

	tokenNegoziazione
	tokenValidazione
