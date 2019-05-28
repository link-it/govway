.. _trasformazione:

Trasformazione
**************

Nel contesto della configurazione specifica di una erogazione o di una fruizione si può accedere alla funzionalità "Trasformazioni" per inserire una lista di definizioni che applicano trasformazioni ai flussi in entrata e/o uscita. Le trasformazioni create hanno la struttura di una lista ordinata e a ciascun elemento della lista è associato un insieme di criteri di applicabilità. La logica del gateway è quella di analizzare le trasformazioni nell'ordine della lista, selezionando la prima di esse i cui criteri di applicabilità sono tutti soddisfatti.

Tramite il pulsante *Aggiungi* è possibile inserire una nuova trasformazione (:numref:`trasf_CreaRegola`).

   .. figure:: ../../_figure_console/TrasformazioniCreaRegola.png
    :scale: 100%
    :align: center
    :name: trasf_CreaRegola

    Nuova Trasformazione

La creazione di una trasformazione richiede che vengano inseriti i seguenti dati:

- Nome: identificativo che rappresenta il nome assegnato alla trasformazione
- Applicabilità: sono i campi che vanno a comporre il criterio di applicabilità della trasformazione:

    - Risorse/Azioni: le operazioni sulle quali è applicabile la trasformazione.
    - Content-Type: i content-type sui quali è applicabile la trasformazione.
    - Pattern: il pattern inserito viene confrontato con il messaggio di richiesta del flusso di comunicazione al fine di verificare l'eventuale match. Il pattern può essere espresso nella sintassi "XPath", nel caso di messaggi XML, o JSONPath, nel caso di messaggi JSON.

Le trasformazioni create sono visualizzate nella forma di elenco ordinato (:numref:`trasf_ListaRegole`). L'icona iniziale di ciascun elemento consente di modificarne la posizione.

   .. figure:: ../../_figure_console/TrasformazioniListaRegole.png
    :scale: 100%
    :align: center
    :name: trasf_ListaRegole

    Lista regole di trasformazione

Ciascuna regola elencata visualizza i dati che sono stati forniti come criterio di applicabilità. A quelli inseriti in fase di creazione si aggiungono i Soggetti e gli Applicativi, che possono essere forniti accedendo i rispettivi collegamenti. I soggetti/applicativi associati ad una regola saranno confrontati con l'identità del soggetto/applicativo mittente di ciascuna richiesta.

Accedendo il dettaglio di una regola di trasformazione vengono presentate le due sezioni:

- Trasformazione: per aggiornare il nome o i criteri di applicabilità.
- Regole di Trasformazione: per aggiornare le regole di trasformazione attuate sulla richiesta e sulla risposta.

.. toctree::
   :maxdepth: 2

   trasformazioneRichiesta
   trasformazioneRisposta

