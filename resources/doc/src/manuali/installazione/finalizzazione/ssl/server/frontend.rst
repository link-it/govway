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

#. Se il frontend inserisce in un header http il DN del Subject e/o dell'Issuer relativo ai certificati client autenticati, deve essere indicato il nome di tali header tramite la seguente configurazione:

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

#. Se il frontend inserisce in un header http il principal dell'identità relativa al chiamante, deve essere indicato il nome di tale header tramite la seguente configurazione:

   ::

      # L'identità del chiamante può essere fornita dal WebServer anche come informazione 'principal' tramite il seguente header:
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.principal=#PRINCIPAL_HEADER-NAME#
                              
   inserendo al posto di #PRINCIPAL_HEADER-NAME# il nome dell'header http utilizzato dal frontend. 

#. Le credenziali, raccolte negli header precedentemente dichiarati, verranno utilizzate da GovWay per attuare i processi di autenticazione abilitati su ogni erogazione. La presenza obbligatoria o meno di credenziali veicolate tramite header http può essere abilitata tramite la seguente proprietà:

   ::

      # - none: le richieste in arrivo possono non presentare alcun header che veicola credenziali.
      # - atLeastOne: le richieste in arrivo devono presentare almeno un header che veicola credenziali.
      # - ssl/principal: le richieste in arrivo devono presentare gli header richiesti dalla modalità scelta, che è di fatto l'unica modalità di autenticazione poi configurabile sulle erogazioni.
      # Con la modalità 'none' o 'atLeastOne' è possibile usare il gestore davanti a erogazioni con tipi di autenticazione differenti, 
      # delegando quindi alla singola erogazione il controllo che le credenziali attese siano effettivamente presenti.
      org.openspcoop2.pdd.services.pa.gestoreCredenziali.modalita=none/atLeastOne/ssl/principal

#. È possibile abilitare l'autenticazione del frontend in modo da accettare gli header http contenenti le credenziali solamente da un frontend autenticato tramite la seguente configurazione:

   ::

      # Modalità di autenticazione da parte di GovWay del webServer (none/ssl/basic/principal)
      org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale=none
      # Credenziali attese da GovWay (a seconda della modalità di autenticazione indicata) che identificano il webServer
      #org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.username=Username
      #org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.password=Password
      #org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.ssl.subject=Subject
      #org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.principal=Principal

Ogni parametro di configurazione descritto nei precedenti punti è personalizzabile in funzione del profilo di interoperabilità e del soggetto associato ad ogni dominio gestito. Di seguito vengono definite le varie modalità di ridefinizione nell'ordine dalla più generica alla più specifica, agendo dopo il prefisso 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.' e prima del nome della proprietà:

- *org.openspcoop2.pdd.services.pa.gestoreCredenziali.<profilo>.PROPRIETA*

  consente di restringere la configurazione ad un determinato Profilo di Interoperabilità; '<profilo>' può assumere i valori 'trasparente' (Profilo API Gateway), 'modipa' (Profilo ModI), 'spcoop' (Profilo SPCoop), 'as4' (Profilo eDelivery), 'sdi' (Profilo Fatturazione Elettronica). Esempio:

   ::

      org.openspcoop2.pdd.services.pa.gestoreCredenziali.spcoop.nome=WebServerAutenticazioneSPCoop

- *org.openspcoop2.pdd.services.pa.gestoreCredenziali.<nomeSoggetto>.PROPRIETA*

  la configurazione indicata verrà utilizzata solamente per il soggetto interno indicato in '<nomeSoggetto>'. Esempio:

   ::

      org.openspcoop2.pdd.services.pa.gestoreCredenziali.EnteDominioInternoEsempio.nome=WebServerAutenticazioneSPCoop

- *org.openspcoop2.pdd.services.pa.gestoreCredenziali.<profilo>-<nomeSoggetto>.PROPRIETA*

  configurazione che consente di indicare il profilo di interoperabilità a cui appartiene il soggetto indicato, visto che un soggetto con lo stesso nome può essere registrato su profili differenti.  Esempio:

   ::

      org.openspcoop2.pdd.services.pa.gestoreCredenziali.spcoop-EnteDominioInternoEsempio.nome=WebServerAutenticazioneSPCoop

- *org.openspcoop2.pdd.services.pa.gestoreCredenziali.<tipoSoggetto>-<nomeSoggetto>.PROPRIETA*
 
  rispetto alle precedenti due proprietà è possibile indicare per il soggetto interno, indicato in '<nomeSoggetto>', anche il tipo (tipoSoggetto>. Questa opzione è utile nei profili di interoperabilità dove ai soggetti è possibile associare più tipi, come ad es. in SPCoop dove sono utilizzabili i tipi 'spc', 'aoo', 'test'. Esempio:

   ::

      org.openspcoop2.pdd.services.pa.gestoreCredenziali.aoo-EnteDominioInternoEsempio.nome=WebServerAutenticazioneSPCoop

- *org.openspcoop2.pdd.services.pa.gestoreCredenziali.<profilo>-<tipoSoggetto>-<nomeSoggetto>.PROPRIETA*

  rappresenta la configurazione più specifica possibile dove viene combinato sia il profilo di interoperabilità che il tipo e il nome del soggetto interno. Esempio:

   ::

      org.openspcoop2.pdd.services.pa.gestoreCredenziali.spcoop-aoo-EnteDominioInternoEsempio.nome=WebServerAutenticazioneSPCoop
