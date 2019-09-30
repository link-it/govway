.. _tags:

Tags
----

La sezione *Configurazione > Tags* è dedicata alla gestione dei tags che possono essere utilizzati per la classificazione delle API presenti nel registro.

I tags possono essere creati direttamente durante la registrazione di una API, oppure da questa sezione in maniera più sistematica e assegnando loro un tipo, Soap o Rest, che indica l'ambito di utilizzo del tag stesso.

La sezione mostra l'elenco dei tags disponibili (:numref:`tagsList`).

   .. figure:: ../_figure_console/Tags-elenco.png
    :scale: 70%
    :align: center
    :name: tagsList

    Elenco dei tags

L'elenco dei tag può essere filtrato impostando, nella barra dei filtri a comparsa, un pattern per il nome o un tipo.
Oltre ad aggiungere ed eliminare i tag esistenti è possibile esportarli in blocco.

Col pulsante *Aggiungi* si apre il form per creare un nuovo tag (:numref:`tagsNew`).

   .. figure:: ../_figure_console/Tags-new.png
    :scale: 70%
    :align: center
    :name: tagsNew

    Creazione di un tag

Per creare un tag si inseriscono i seguenti dati:

- *Nome*: il nome del tag

- *Descrizione*: descrizione del tag

- *Tipo*: serve per indicare per quali API è possibile utilizzare il tag: SOAP, REST o Qualsiasi.