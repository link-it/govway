.. _errori_400_CorrelationInformationNotFound:

CorrelationInformationNotFound
------------------------------

L'errore indica che nella richiesta non è stato possibile per GovWay estrarre il *Riferimento ID Richiesta* da utilizzare per effettuare una correlazione asincrona tra operazioni differenti.

La correlazione è attivabile su una API tramite la funzionalità descritta nella sezione :ref:`correlazioneTransazioniDifferenti`.

.. note::
      In mancanza di un *Riferimento ID Richiesta*, GovWay per default non solleva alcun errore. È possibile forzare la generazione dell'errore intervenendo sul file di proprietà esterno /etc/govway/trasparente_local.properties aggiungendo le seguenti proprietà

	::

		# Fruizioni
		org.openspcoop2.protocol.trasparente.pd.riferimentoIdRichiesta.required=true
		# Erogazioni
		org.openspcoop2.protocol.trasparente.pa.riferimentoIdRichiesta.required=true


L'errore viene anche sollevato se GovWay non rileva il riferimento alla richiesta nelle collaborazioni asincrone del Profilo di Interoperabilità SPCoop. Per maggiori dettagli si rimanda alla sezione :ref:`profiliAsincroni`.



