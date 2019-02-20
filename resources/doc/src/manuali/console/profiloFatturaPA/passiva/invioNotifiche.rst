.. _profiloFatturaPA_passiva_invioNotifiche:

Invio della Notifica di Esito Committente
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per l'invio della Notifica di Esito Committente l'applicativo deve
utilizzare:

-  Una URL così composta:

   ::

       http://<host-govway>/govway/sdi/out/xml2soap/<SoggettoSDI>/CentroServiziFatturaPA/SdIRiceviNotifica/v1?NomeFile=<NomeFileFattura>&IdentificativoSdI=<identificativoSDI>

   dove:

   -  *host-govway*: è l'hostname con cui è raggiungibile l'istanza di
      Govway.

   -  *SoggettoSDI*: il soggetto interno destinatario delle fatture,
      come configurato durante l'esecuzione del govlet di fatturazione
      passiva.

   -  *NomeFileFattura*: è il nome del file che contiene la fattura cui
      fa riferimento la notifica EC.

   -  *identificativoSDI*: è l'identificativo SDI che fa riferimento al
      lotto della fattura ricevuta.

-  L'invocazione deve essere corredata dalle credenziali che sono state
   indicate durante la configurazione tramite il relativo govlet.

-  Utilizzare l'header http *Content-Type* valorizzato con *text/xml* o
   *application/xml*

Un esempio di invio di una fattura viene fornito tramite il seguente
comando curl:

    **Soggetto Interno al Dominio**

    In questo esempio si suppone che il nome del soggetto (riferito
    precedentemente come *SoggettoSDI*) fornito durante la fase di
    installazione di GovWay sia *Ente*.

::

    curl -X POST -basic --user SdIRiceviNotifica:123456 \
    --data-binary @IT01234567890_11111_EC_001.xml \
    -H "Content-Type: application/xml" \
    "http://127.0.0.1:8080/govway/sdi/out/xml2soap/Ente/CentroServiziFatturaPA/SdIRiceviNotifica/v1?NomeFile=IT01234567890_11111.xml&IdentificativoSdI=345"
