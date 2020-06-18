Miglioramenti alle Console
--------------------------

Sono stati apportati i seguenti miglioramenti:

-  *Elemento in Lista Ricercabile*: aggiunta la possibilità di selezionare un elemento tra quelli di una lista scrivendo il criterio di ricerca all'interno della lista stessa.

-  *Highlight*: aggiunto il supporto dell'highlight della keyword di ricerca effettuata sia all'interno delle liste che nei campi con funzionalità di 'autocomplete'.


Relativamente alla sola console di gestione:

- *Risorse REST con qualsiasi path*: aggiunta la possibilità di registrare manualmente risorse in API REST con HttpMethod definito e Path Qualsiasi.

- *Modifica Risorsa*: è stata aggiunta la possibilità di accedere al dettaglio di una risorsa, dall'elenco presente in una API, cliccando anche sul metodo http.

- *Modifica Nome Applicativo*: aggiunta la possibilità di modificare il nome di un applicativo precedentemente registrato.

- *Modifica API di una Erogazione/Fruizione*: aggiunta la possibilità di modificare l'API implementata in una erogazione o fruizione.

- *Sospensione API*: la sospensione di una API (erogazione/fruizione), o la successiva riattivazione, non necessita più un'operazione di reset delle cache per l'effettiva applicazione sul runtime di GovWay. L'operazione effettuata tramite console è immediatamente applicata sul runtime.


Relativamente alla sola console di monitoraggio

- *Ricerca per ID Transazione*: è adesso il primo criterio di ricerca per identificativo

- *Pie Chart*: nei report a torta, sulla destra di ogni voce presente nella legenda viene adesso riportato anche il numero di record oltra la %.

- *Distribuzione Temporale*:  aggiunta la possibilità di personalizzare il numero di label da visualizzare sull'asse X nei report con distribuzione temporale.

- *Nuovi esiti associati alle Transazioni*:

	- 'Errore ModI PA': gli errori generati durante la validazione 'ModI PA' vengono adesso classificati con un esito dedicato;
	- 'Richiesta o Risposta già Elaborata': i nuovi esiti sono associati a transazioni riguadanti richieste o risposte duplicate.

- *Escludi Richieste Scartate*: il comportamento di default utilizzato nella sezione Transazioni e Statistiche è indicabile tramite la proprietà 'transazioni.escludiRichiesteScartate.defaultValue' nel file di configurazione esterno '/etc/govway/monitor_local.properties'.

- *Volume di Traffico Iniziale*: il grafico che fornisce il volume di traffico complessivo, disponibile una volta collegati alla console, è adesso configurabile per ritornare il volume complessivo di tutti i Profili di Interoperabilità abilitati sul gateway.

