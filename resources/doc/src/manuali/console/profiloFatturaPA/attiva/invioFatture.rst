.. _profiloFatturaPA_attiva_invioFatture:

Invio della fattura
~~~~~~~~~~~~~~~~~~~

Per l'invio della fattura l'applicativo mittente deve utilizzare:

-  Una URL così composta:

   ::

       http://<host-govway>/govway/sdi/out/xml2soap/<SoggettoSDI>/CentroServiziFatturaPA/SdIRiceviFile/v1?Versione=<VersioneFatturaPA>&TipoFile=<TipoFile>&IdPaese=<IdPaese>&IdCodice=<IdCodice>

   dove:

   -  *host-govway*: è l'hostname con cui è raggiungibile l'istanza di
      Govway.

   -  *SoggettoSDI*: il soggetto interno al dominio come configurato
      durante l'esecuzione del govlet di fatturazione passiva.

   -  *Versione*: versione della fattura che si sta inviando: FPA12
      (Fattura 1.2 per Pubbliche Amministrazione), FPR12 (Fattura 1.2
      per Privati), SDI11 e SDI10 (Fattura per Pubblica amministrazione
      versione 1.1. e 1.0).

   -  *TipoFile*: tipo di fattura: XML (Fattura firmata XADES), P7M
      (Fattura firmata CADES) o ZIP (archivio di fatture).

   -  *IdPaese e IdCodice*: dati del trasmittente della fattura.

-  L'invocazione deve essere corredata dalle credenziali che sono state
   indicate durante la configurazione tramite il relativo govlet.

-  A seconda del tipo di fattura deve essere utilizzato il corretto
   header http *Content-Type*:

   -  XML: è possibile utilizzare *text/xml* o *application/xml*

   -  P7M: *application/pkcs7-mime*

   -  XML: *application/zip*

Un esempio di invio di una fattura viene fornito tramite il seguente
comando curl:

    **Soggetto Interno al Dominio**

    In questo esempio si suppone che il nome del soggetto (riferito
    precedentemente come *SoggettoSDI*) fornito durante la fase di
    installazione di GovWay sia *Ente*.

::

    curl -X POST -basic --user SdIRiceviFile:123456 \
    --data-binary @IT01234567890_11111.xml.p7m \
    -H "Content-Type: application/pkcs7-mime" \
    "http://127.0.0.1:8080/govway/sdi/out/xml2soap/Ente/CentroServiziFatturaPA/SdIRiceviFile/v1?Versione=SDI10&TipoFile=P7M&IdPaese=IT&IdCodice=01629370097"

.. note::
	La generazione di un nome di file univoco da associare alla fattura viene gestita da GovWay.
	
	È possibile disabilitare tale gestione disabilitando la proprietà 'org.openspcoop2.protocol.sdi.fatturazioneAttiva.nomeFile.gestione' 
	nel file '/etc/govway/sdi_local.properties'.
	Se viene disabilitata la funzionalità (attiva per default), la gestione dei nomi dei file (correttezza sintattica, univocità, ...) è demandata 		  
	all'Applicativo Client che deve obbligatoriamente fornire il nome del file da associare alla fattura attraverso uno dei seguenti modi:

	- query parameter 'NomeFile'
	- header http 'SDI-NomeFile'
	- header http 'GovWay-SDI-NomeFile'
