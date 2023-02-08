.. _modipa_sicurezza_avanzate:

Funzionalità Avanzate
-----------------------


La gestione dei pattern di sicurezza messaggio possono essere personalizzati su diversi aspetti:

- :ref:`modipa_sicurezza_avanzate_applicabilita`: è possibile attivare la sicurezza messaggio puntualmente solamente sulla richiesta o sulla risposta di una operazione. Per API REST è possibile anche definire dei criteri di applicabilità della sicurezza messaggio in base a Content-Type o codici di risposta HTTP.

- :ref:`modipa_sicurezza_avanzate_header`: può essere selezionato l'header http utilizzato per veicolare il token JWT su API REST.

- :ref:`modipa_sicurezza_avanzate_claims`: possono essere configurati ulteriori claims da aggiungere nel payload del JWT su API REST.

- :ref:`modipa_sicurezza_avanzate_header_soap`: possono essere configurati ulteriori header soap da aggiungere agli elementi inclusi nella firma su API SOAP.

- :ref:`modipa_sicurezza_avanzate_sbustamento`: è possibile configurare GovWay al fine di non eliminare il token di sicurezza dai messaggi dopo averli validati.

- :ref:`modipa_sicurezza_avanzate_fruizione_keystore`: è possibile attivare un differente scenario di fruizione rispetto a quello di default che prevede l'associazione del keystore di firma all'applicativo mittente.

- :ref:`modipa_sicurezza_avanzate_fruizione_pdnd`:  è possibile configurare i parametri (Keystore, KID e clientId), richiesti da una negoziazione PDND, all'interno di fruizione.


.. toctree::
        :maxdepth: 2

	applicabilita
	header/index
	claims
	header_soap
	sbustamento
	fruizione_keystore
	fruizione_pdnd

