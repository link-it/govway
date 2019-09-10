.. _deploy_upd_properties:

Aggiornamento dei file di properties
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nella sottodirectory *cfg* si trovano i template dei file di properties
esterni. Questi file, durante l'installazione del prodotto, sono già
stati copiati nella directory di lavoro di GovWay. Tali file di
properties hanno lo scopo di fornire all'utente dei file
pre-confezionati, con proprietà commentate, da utilizzare rapidamente
secondo quanto descritto nei manuali d'utilizzo del prodotto, per
modificare eventuali configurazioni built-in. Non è quindi
indispensabile che tali file vengano riportati sull'installazione
precedente e soprattutto occorre fare attenzione a non sovrascrivere
eventualmente i precedenti, se erano stati modificati rispetto al
template iniziale (generato dall'installer).

.. note::

	Nella sottodirectory *cfg/utilities/diff* vengono riportate solamente le modifiche attuate sui file, rispetto alla versione precedente, nel formalismo "diff" (estensione .diff) o il file intero (estensione .properties) se si tratta di un file che non esisteva nella precedente versione
