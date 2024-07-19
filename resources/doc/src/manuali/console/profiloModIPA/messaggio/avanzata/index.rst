.. _modipa_sicurezza_avanzate:

Funzionalità Avanzate
-----------------------


La gestione dei pattern di sicurezza messaggio possono essere personalizzati su diversi aspetti:

- :ref:`modipa_sicurezza_avanzate_azioni`: è possibile modificare i pattern di sicurezza messaggio applicati puntualmente solamente su una operazione.

- :ref:`modipa_sicurezza_avanzate_applicabilita`: è possibile attivare la sicurezza messaggio puntualmente solamente sulla richiesta o sulla risposta di una operazione. Per API REST è possibile anche definire dei criteri di applicabilità della sicurezza messaggio in base a Content-Type o codici di risposta HTTP.

- :ref:`modipa_sicurezza_avanzate_header`: può essere selezionato l'header http utilizzato per veicolare il token JWT su API REST.

- :ref:`modipa_sicurezza_avanzate_custom_signature`: può essere attivato un token 'JWT-Signature' personalizzato per API REST.

- :ref:`modipa_sicurezza_avanzate_claims`: possono essere configurati ulteriori claims da aggiungere nel payload del JWT su API REST.

- :ref:`modipa_sicurezza_avanzate_header_soap`: possono essere configurati ulteriori header soap da aggiungere agli elementi inclusi nella firma su API SOAP.

- :ref:`modipa_sicurezza_avanzate_sbustamento`: è possibile configurare GovWay al fine di non eliminare il token di sicurezza dai messaggi dopo averli validati.

- :ref:`modipa_sicurezza_avanzate_fruizione_keystore_scenari`: è possibile attivare differenti scenari di fruizione rispetto a quello di default che prevede l'associazione del keystore di firma sull'applicativo mittente.

- :ref:`modipa_sicurezza_avanzate_fruizione_purposeId_scenari`: l'identificativo univoco della finalità, ottenuto dalla PDND, per cui si intende fruire di un servizio è configurabile in diverse modalità a seconda dello scenario che si desidera supportare.

- :ref:`modipa_sicurezza_avanzate_intermediario`: è possibile indicare un soggetto come intermediario. Questo consente di autorizzare una richiesta proveniente da un soggetto identificato sul canale (l'intermediario), che risulta differente dal soggetto a cui appartiene l'applicativo identificato tramite token di sicurezza.

- :ref:`modipa_sicurezza_avanzate_pdndFailed`: è possibile modificare il comportamento di default per far fallire la transazione in caso il recupero delle informazioni sul client o sull'organizzazione tramite :ref:`modipa_passiPreliminari_api_pdnd` fallisca.

- :ref:`modipa_sicurezza_avanzate_pdndRateLimiting`: le politiche di rate limiting consentono di conteggiare le richieste o filtrare anche rispetto alle informazioni prelevate dalla PDND.


.. toctree::
        :maxdepth: 2

	azioni
	applicabilita
	header/index
        custom_signature
	claims
	header_soap
	sbustamento
	fruizione_scenari/index
	purposeId
	intermediario
	pdndFailed
	pdndRateLimiting

