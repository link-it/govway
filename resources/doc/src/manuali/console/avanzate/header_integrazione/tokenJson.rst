.. _integrazioneTokenJson:

Scambio di informazioni tramite un token JSON
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Oltre alle modalità di interscambio di informazioni standard, descritte nelle precedenti sezioni, il client può fornire altre informazioni al gateway tramite un json il cui formato non è prestabilito da GovWay ma può essere definito in maniera arbitraria dal client.

Nella configurazione di default, GovWay si attende il json all'interno dell'header http 'GovWay-Integration' codificato in base64. La presenza dell'header http non è obbligatoria ma se presente il json viene acceduto e le informazioni presenti al suo interno vengono rese disponibili tramite la keyword 'integration' come informazione dinamica descritta nella sezione :ref:`valoriDinamici`. Le informazioni possono essere poi utilizzate nelle varie funzionalità del gateway nelle quali è possibile utilizzare i :ref:`valoriDinamici` come ad esempio nella generazione di token di sicurezza ModI descritti nella sezione :ref:`modipa_sicurezza_avanzate_claims`.

È possibile configurare una modalità di scambio differente registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *integrationInfo.enabled* : consente di disabilitare la lettura del json di integrazione. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *integrationInfo.type* : consente di indicare dove risiede il json di integrazione. I valori associabili alla proprietà sono 'http_header' (default) o 'query_parameter'.

- *integrationInfo.name* : nome dell'header http o del parametro della url dove risiede il json di integrazione (default: 'GovWay-Integration').

- *integrationInfo.encode* : tipo di codifica utilizzata per trasmettere il json di integrazione. I valori associabili alla proprietà sono:

	- 'base64' (default)
	- 'hex'
	- 'jwt': json atteso come payload del jwt
	- 'plain': nessuna codifica

- *integrationInfo.required* : indica se l'header http o il parametro della url deve essere obbligatoriamente presente nella richiesta. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è disabilitato.

Inoltre anche tra le informazioni restituite nella risposta dal server a GovWay può essere presente l'header http 'GovWay-Integration'. Come per la richiesta la sua presenza non è obbligatoria e le informazioni presenti al suo interno vengono rese disponibili tramite la keyword 'integrationResponse' come informazione dinamica descritta nella sezione :ref:`valoriDinamici`.

Anche per la risposta è possibile configurare una modalità di scambio differente registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *responseIntegrationInfo.enabled* : consente di disabilitare la lettura del json di integrazione della risposta. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *responseIntegrationInfo.name* : nome dell'header http dove risiede il json di integrazione (default: 'GovWay-Integration').

- *responseIntegrationInfo.encode* : tipo di codifica utilizzata per trasmettere il json di integrazione. I valori associabili sono gli stessi utilizzabili sulla richiesta: 'base64', 'hex', 'jwt' e 'plain'.

- *responseIntegrationInfo.required* : indica se l'header http della risposta deve essere obbligatoriamente presente. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è disabilitato.
