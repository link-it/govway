.. _errori_400_SoapMustUnderstandUnknown:

SoapMustUnderstandUnknown
-------------------------

GovWay ha rilevato una richiesta verso una API SOAP che contiene un SOAP Header con attributo 'mustUnderstand' e senza un actor/role definito che risulta sconosciuto a GovWay.

.. note::
      L'errore viene generato solamente se GovWay è stato configurato per riconoscere e trattare solamente alcuni SOAP Header specifici. La configurazione di default di govway è di far passare tutti i SOAP Header; per modificarla agire sul file di proprietà esterno /etc/govway/govway_local.properties aggiungendo le seguenti proprietà

	::

		# Possibili valori: true/false
		org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders=false

		# Sintassi per filtri specifici: 
		# org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.LOCAL_NAME=NAMESPACE_URI
		# Se si deve definire più header con stesso local name e differente namespace si può utilizzare la seguente sintassi:
		# org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.LOCAL_NAME!NUMERO_PROGRESSIVO=NAMESPACE_URI
		# Esempio per Bypass per WS-Security:
		#org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.Security=http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd
		# Esempio per Bypass per WS-Reliability
		#org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.Sequence=http://schemas.xmlsoap.org/ws/2005/02/rm
