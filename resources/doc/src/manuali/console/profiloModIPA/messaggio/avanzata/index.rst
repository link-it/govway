.. _modipa_sicurezza_avanzate:

Funzionalità Avanzate
-----------------------


La gestione dei profili di sicurezza messaggio possono essere personalizzati su diversi aspetti:

- :ref:`modipa_sicurezza_avanzate_applicabilita`: è possibile attivare la sicurezza messaggio puntualmente solamente sulla richiesta o sulla risposta di una operazione. Per API REST è possibile anche definire dei criteri di applicabilità della sicurezza messaggio in base a Content-Type o codici di risposta HTTP.

- :ref:`modipa_sicurezza_avanzate_header`: può essere selezionato l'header http utilizzato per veicolare il token JWT su API REST.

- :ref:`modipa_sicurezza_avanzate_sbustamento`: è possibile configurare GovWay al fine di non eliminare il token di sicurezza dai messaggi dopo averli validati.


.. toctree::
        :maxdepth: 2

	applicabilita
	header
	sbustamento

