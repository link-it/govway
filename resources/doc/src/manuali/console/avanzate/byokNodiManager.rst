.. _configAvanzataByokSecurityGovWayRemoto:

Cifratura delle Informazioni Confidenziali
---------------------------------------------------------------------

In architetture dove risiede una suddivisione fisica tra le istanze run e le istanze manager contenenti le console di gestione, la cifratura delle informazioni confidenziali può avvenire attraverso due modalità:

- la modalità standard, descritta nella sezione :ref:`byokInstallSecurityGovWay`, in cui viene attivato un unico *Security Engine* che prevede la condivisione della chiave master da parte di tutti i nodi run e manager;
- una differente modalità in cui la chiave master risiede solamente sui nodi run e i nodi manager richiedono l'operazione di wrap/unwrap ai nodi run.

Di seguito vengono fornite le indicazioni necessarie ad attivare la seconda modalità.

.. note::
    La seconda modalità può essere utilizzata per scenari di test o in ambienti progettati per far sì che le funzioni di wrap/unwrap esposte dai nodi run siano sufficientemente protette da criteri di sicurezza elevati, accessibili solamente dai nodi manager e dove il livello di sicurezza di accesso a questi ultimi è equivalente a quello dei corrispettivi nodi run.

La seconda modalità richiede la configurazione del file *<directory-lavoro>/govway.nodirun.properties* come descritto nella sezione :ref:`cluster-console`.

Devono inoltre essere abilitate le funzioni di wrap e unwrap (per default disabilitate) attraverso la creazione delle seguenti due proprietà sul file *<directory-lavoro>/govway_local.properties*:

 ::

    org.openspcoop2.pdd.byok.jmx.wrap.enabled=true
    org.openspcoop2.pdd.byok.jmx.unwrap.enabled=true

Tanto premesso la configurazione dei KMS utilizzati dai nodi manager potranno riferire speciali costanti:

- ${govway-runtime:endpoint-wrap}: consente di riferire l'endpoint esposto dai nodi run che implementa l'operazione di wrap;
- ${govway-runtime:endpoint-unwrap}: consente di riferire l'endpoint esposto dai nodi run che implementa l'operazione di unwrap;
- ${govway-runtime:username} e ${govway-runtime:password}: consente di riferire la credenziale http-basic configurata in un nodo run e descritta nella sezione :ref:`cluster-console`.

L'attivazione del *Security Engine* dovrà avvenire definendo due proprietà differenti nel file *<directory-lavoro>/byok.properties*:

- *govway.security*: identificativo del (:ref:`byokInstallSecurityEngine`) che devono utilizzare i nodi manager;
- *govway.security.runtime*: identificativo del (:ref:`byokInstallSecurityEngine`) utilizzato sui nodi run.

Di seguito un esempio in cui viene attivata la cifratura tramite il Security Engine 'gw-pbkdf2' sui nodi run e l'invocazione remota da parte dei nodi manager:

::

    # =======================================================================================================================
    govway.security=gw-remote
    govway.security.runtime=gw-pbkdf2
    # =======================================================================================================================

    # =======================================================================================
    # Security Engine
    #
    # 'gw-pbkdf2' usato dai nodi run
    security.gw-pbkdf2.kms.wrap=openssl-pbkdf2-wrap
    security.gw-pbkdf2.kms.unwrap=openssl-pbkdf2-unwrap
    security.gw-pbkdf2.kms.param.encryptionPassword=${envj:read(GOVWAY_ENCRYPTION_PASSWORD)}
    #
    # 'gw-remote' usato dai nodi manager
    security.gw-remote.kms.wrap=govway-remote-wrap
    security.gw-remote.kms.unwrap=govway-remote-unwrap
    security.gw-remote.kms.param.endpointWrap=${govway-runtime:endpoint-wrap}
    security.gw-remote.kms.param.endpointUnwrap=${govway-runtime:endpoint-unwrap}
    security.gw-remote.kms.param.username=${govway-runtime:username}
    security.gw-remote.kms.param.password=${govway-runtime:password}
    # =======================================================================================
    
    # =======================================================================================
    # KMS 'gw-pbkdf2' usato dai nodi run
    #
    # WRAP
    kms.govway-openssl-pbkdf2-wrap.label=OpenSSL 'pbkdf2' Wrap
    kms.govway-openssl-pbkdf2-wrap.type=openssl-pbkdf2-wrap
    kms.govway-openssl-pbkdf2-wrap.mode=wrap
    kms.govway-openssl-pbkdf2-wrap.encryptionMode=local
    kms.govway-openssl-pbkdf2-wrap.input.param1.name=encryptionPassword
    kms.govway-openssl-pbkdf2-wrap.input.param1.label=Encryption Password
    kms.govway-openssl-pbkdf2-wrap.local.impl=openssl
    kms.govway-openssl-pbkdf2-wrap.local.keystore.type=pass
    kms.govway-openssl-pbkdf2-wrap.local.password=${kms:encryptionPassword}
    kms.govway-openssl-pbkdf2-wrap.local.password.type=openssl-pbkdf2-aes-256-cbc
    kms.govway-openssl-pbkdf2-wrap.local.encoding=base64
    #
    # UNWRAP
    kms.govway-openssl-pbkdf2-unwrap.label=OpenSSL 'pbkdf2'
    kms.govway-openssl-pbkdf2-unwrap.type=openssl-pbkdf2-unwrap
    kms.govway-openssl-pbkdf2-unwrap.mode=unwrap
    kms.govway-openssl-pbkdf2-unwrap.encryptionMode=local
    kms.govway-openssl-pbkdf2-unwrap.input.param1.name=encryptionPassword
    kms.govway-openssl-pbkdf2-unwrap.input.param1.label=Encryption Password
    kms.govway-openssl-pbkdf2-unwrap.local.impl=openssl
    kms.govway-openssl-pbkdf2-unwrap.local.keystore.type=pass
    kms.govway-openssl-pbkdf2-unwrap.local.password=${kms:encryptionPassword}
    kms.govway-openssl-pbkdf2-unwrap.local.password.type=openssl-pbkdf2-aes-256-cbc
    kms.govway-openssl-pbkdf2-unwrap.local.encoding=base64
    # =======================================================================================
        
    # =======================================================================================
    # KMS 'gw-remote' usato dai nodi manager
    #
    # WRAP
    kms.govway-remote-wrap.label=GovWay Remote Wrap
    kms.govway-remote-wrap.type=govway-remote-wrap
    kms.govway-remote-wrap.mode=wrap
    kms.govway-remote-wrap.encryptionMode=remote
    kms.govway-remote-wrap.input.param1.name=endpointWrap
    kms.govway-remote-wrap.input.param1.label=Endpoint Wrap Key
    kms.govway-remote-wrap.input.param2.name=username
    kms.govway-remote-wrap.input.param2.label=Username
    kms.govway-remote-wrap.input.param3.name=password
    kms.govway-remote-wrap.input.param3.label=Password
    kms.govway-remote-wrap.http.endpoint=${kms:endpointWrap}&paramValue=${kms-base64-urlencoded-key}
    kms.govway-remote-wrap.http.method=GET
    kms.govway-remote-wrap.http.username=${kms:username}
    kms.govway-remote-wrap.http.password=${kms:password}
    #
    # UNWRAP
    kms.govway-remote-unwrap.label=GovWay Remote Unwrap
    kms.govway-remote-unwrap.type=govway-remote-unwrap
    kms.govway-remote-unwrap.mode=unwrap
    kms.govway-remote-unwrap.encryptionMode=remote
    kms.govway-remote-unwrap.input.param1.name=endpointUnwrap
    kms.govway-remote-unwrap.input.param1.label=Endpoint Unwrap Key
    kms.govway-remote-unwrap.input.param2.name=username
    kms.govway-remote-unwrap.input.param2.label=Username
    kms.govway-remote-unwrap.input.param3.name=password
    kms.govway-remote-unwrap.input.param3.label=Password
    kms.govway-remote-unwrap.http.endpoint=${kms:endpointUnwrap}&paramValue=${kms-urlencoded-key}
    kms.govway-remote-unwrap.http.method=GET
    kms.govway-remote-unwrap.http.username=${kms:username}
    kms.govway-remote-unwrap.http.password=${kms:password}
    kms.govway-remote-unwrap.http.response.base64Encoded=true
    # =======================================================================================
