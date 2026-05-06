.. _modipa_sicurezza_avanzate_controlliPdnd:

Controlli sugli identificativi PDND
-----------------------------------------

In un'erogazione di una API che adotta un pattern di sicurezza con trust tramite PDND, GovWay attua una serie di controlli — in fase di registrazione tramite console e a runtime durante la validazione dei voucher — sui tre identificativi PDND ``producerId``, ``eServiceId`` e ``descriptorId``. Tali controlli sono personalizzabili tramite le proprietà descritte di seguito, da aggiungere nel file di configurazione locale ``/etc/govway/modipa_local.properties`` (assumendo sia ``/etc/govway`` la directory di configurazione indicata in fase di installazione).


**Controllo di univocità eServiceId e descriptorId**

Le seguenti proprietà consentono di configurare i controlli di univocità effettuati dalla console durante la registrazione di erogazioni con lo stesso eServiceId e/o descriptorId.

``org.openspcoop2.protocol.modipa.pdnd.eServiceId.console.checkUnique=false``

Quando impostata a *true*, la console impedisce la registrazione di più erogazioni con lo stesso eServiceId. Il valore predefinito è *false*, che consente la registrazione di più erogazioni con lo stesso eServiceId.

``org.openspcoop2.protocol.modipa.pdnd.descriptorId.console.checkUnique=true``

Quando impostata a *true*, la console consente la registrazione di più erogazioni con lo stesso eServiceId solo se sono associati descriptorId differenti. Il valore predefinito è *true*.

Qualora si desideri consentire la registrazione di più erogazioni con lo stesso eServiceId e lo stesso descriptorId, è necessario disabilitare questo controllo impostando la proprietà a *false*.

.. note::
   In caso di disabilitazione del controllo, la registrazione di più erogazioni con identico eServiceId e descriptorId sarà consentita esclusivamente se :ref:`Signal-Hub <modipa_signalHub>` non risulta abilitato sull'erogazione.


**Controllo di univocità producerId**

La seguente proprietà consente di configurare il controllo di univocità del producerId (ID Ente) effettuato dalla console durante la registrazione di soggetti.

``org.openspcoop2.protocol.modipa.pdnd.producerId.console.checkUnique=false``

Quando impostata a *true*, la console impedisce la registrazione di più soggetti con lo stesso producerId (ID Ente). Il valore predefinito è *false*, che consente la registrazione di più soggetti con lo stesso producerId.


**Verifica runtime dei valori nel token PDND**

Le seguenti proprietà consentono di configurare se il runtime deve verificare che i valori di producerId, eServiceId e descriptorId presenti nel token PDND corrispondano a quelli configurati sul soggetto erogatore (ID Ente) e sull'erogazione. Per tutte le proprietà il valore predefinito è *true*.

``org.openspcoop2.protocol.modipa.pdnd.producerId.check=true``

``org.openspcoop2.protocol.modipa.pdnd.eServiceId.check=true``

``org.openspcoop2.protocol.modipa.pdnd.descriptorId.check=true``
