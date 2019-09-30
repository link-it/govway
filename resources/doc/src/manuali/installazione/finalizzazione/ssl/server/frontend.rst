.. _install_ssl_server_frontend:

Frontend HTTP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nel caso in cui la terminazione ssl viene gestita su un frontend http (Apache httpd, IIS, etc) GovWay necessita di ricevere i certificati client per attuare il processo di autenticazione https.

Nel caso di utilizzo di una integrazione 'mod_jk' tra frontend e application server, GovWay riceve i certificati gestiti sul frontend http in maniera trasparente e non sono richieste ulteriori configurazioni.

Negli altri casi invece deve essere configurato opportunamente il frontend http per inoltrare i certificati client o il DN attraverso header HTTP a GovWay. Si rimanda alla documentazione ufficiale del frontend utilizzato su come attivare tale funzionalità.  Di seguito invece vengono fornite indicazioni su come configurare GovWay per recepire le informazioni dagli header inoltrati dal frontend. 


**Integrazione Frontend - GovWay**

.. note::

   Gli esempi forniti descrivono una configurazione valida per le erogazioni. È sufficiente utilizzare il prefisso 'org.openspcoop2.pdd.services.pd.' invece di 'org.openspcoop2.pdd.services.pa.' per adeguare la configurazione alle fruizioni.


Per abilitare il processamento degli header inoltrati dal frontend è necessario editare il file <directory-lavoro>/govway_local.properties .

#. Abilitare la proprietà 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.enabled'

   ::

      # Mediazione tramite WebServer (Erogazioni)
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.enabled=true
      # Nome del WebServer che media le comunicazioni https con GovWay
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.nome=#FRONTEND-NAME#                          

   inserendo al posto di #FRONTEND-NAME# il nome associato al frontend che verrà utilizzato nella diagnostica di GovWay.

#. Se il frontend inserisce in header http il DN del Subject e/o dell'Issuer relativo ai certificati client autenticati, deve essere indicato il nome di tali header tramite la seguente configurazione:

   ::

      # DN del Subject e dell'Issuer tramite gli header:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.subject=#SUBJECT_HEADER-NAME#
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.issuer=#ISSUER_HEADER-NAME#            
                              
   inserendo al posto di #SUBJECT_HEADER-NAME# il nome dell'header http utilizzato per propagare il DN del Subject (es. 'SSL_CLIENT_S_DN') e al posto di #ISSUER_HEADER-NAME# il nome dell'header http utilizzato per propagare il DN dell'Issuer (es. SSL_CLIENT_I_DN). È possibile anche attuare una configurazione dove viene processato solamente il Subject, lasciando commentata la proprietà relativa all'Isssuer. 

#. Nel caso il frontend inserisce in un header http il certificato x.509 del client autenticato, deve essere indicato il nome di tale header tramite la seguente configurazione:

   ::

      # Certificato tramite l'header:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate=#CLIENT-CERT_HEADER-NAME#
      # Indicazione se l'header valorizzato con il certificato è url encoded:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.url_decode=false
      # Indicazione se l'header valorizzato con il certificato è base64 encoded:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.base64_decode=false
                                      
   inserendo al posto di #CLIENT-CERT_HEADER-NAME# il nome dell'header http utilizzato per propagare il certificato x.509 (es. 'SSL_CLIENT_CERT'). Il certificato inserito nell'header http dal frontend può essere stato codificato in base64 e/o tramite url encoding. È possibile effettuare la decodifica abilitando la proprietà 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.base64_decode' e/o la proprietà org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.url_decode.

#. Se il frontend inserisce in header http il principal dell'identità relativa al chiamante, deve essere indicato il nome di tale header tramite la seguente configurazione:

   ::

      # L'identità del chiamante può essere fornita dal WebServer anche come informazione 'principal' tramite il seguente header:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.principal=#PRINCIPAL_HEADER-NAME#
                              
   inserendo al posto di #PRINCIPAL_HEADER-NAME# il nome dell'header http utilizzato dal frontend. 


