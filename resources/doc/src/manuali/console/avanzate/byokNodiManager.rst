.. _configAvanzataByokSecurityGovWayRemoto:

Cifratura delle Informazioni Confidenziali solamente su nodi run
-------------------------------------------------------------------

In architetture dove risiede una suddivisione fisica tra le istanze run e le istanze manager contenenti le console di gestione, la cifratura delle informazioni confidenziali può avvenire attraverso 2 modalità:

- la modalità standard, descritta nella sezione :ref:`byokInstallSecurityGovWay`, in cui viene attivato un unico *Security Engine* che prevede la condivisione della chiave master da parte di tutti i nodi run e manager;
- una differente modalità in cui la chiave master risiede solamente sui nodi run e i nodi manager richiedono l'operazione di wrap/unwrap ai nodi run.

La seconda modalità richiede la configurazione del file *<directory-lavoro>/govway.nodirun.properties* come descritto nella sezione :ref:`cluster-console`.

La configurazione dei KSM utilizzati dai nodi manager potranno riferire speciali costanti:

- ${govway-runtime:endpoint-wrap}: consente di riferire l'endpoint esposto dai nodi run che implementa l'operazione di wrap;
- ${govway-runtime:endpoint-unwrap}: consente di riferire l'endpoint esposto dai nodi run che implementa l'operazione di unwrap;
- ${govway-runtime:username} e ${govway-runtime:password}: consente di riferire la credenziale http-basic configurata in un nodo run e descritta nella sezione :ref:`cluster-console`.

L'attivazione del *Security Engine* dovrà avvenire definendo 2 proprietà differenti nel file *<directory-lavoro>/byok.properties*:

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
    security.gw-pbkdf2.ksm.wrap=openssl-pbkdf2-wrap
    security.gw-pbkdf2.ksm.unwrap=openssl-pbkdf2-unwrap
    security.gw-pbkdf2.ksm.param.encryptionPassword=${envj:read(GOVWAY_ENCRYPTION_PASSWORD)}
    #
    # 'gw-remote' usato dai nodi manager
    security.gw-remote.ksm.wrap=govway-remote-wrap
    security.gw-remote.ksm.unwrap=govway-remote-unwrap
    security.gw-remote.ksm.param.endpointWrap=${govway-runtime:endpoint-wrap}
    security.gw-remote.ksm.param.endpointUnwrap=${govway-runtime:endpoint-unwrap}
    security.gw-remote.ksm.param.username=${govway-runtime:username}
    security.gw-remote.ksm.param.password=${govway-runtime:password}
    # =======================================================================================
    
    # =======================================================================================
    # KSM 'gw-pbkdf2' usato dai nodi run
    #
    # WRAP
    ksm.govway-openssl-pbkdf2-wrap.label=OpenSSL 'pbkdf2' Wrap
    ksm.govway-openssl-pbkdf2-wrap.type=openssl-pbkdf2-wrap
    ksm.govway-openssl-pbkdf2-wrap.mode=wrap
    ksm.govway-openssl-pbkdf2-wrap.encryptionMode=local
    ksm.govway-openssl-pbkdf2-wrap.input.param1.name=encryptionPassword
    ksm.govway-openssl-pbkdf2-wrap.input.param1.label=Encryption Password
    ksm.govway-openssl-pbkdf2-wrap.local.impl=openssl
    ksm.govway-openssl-pbkdf2-wrap.local.keystore.type=pass
    ksm.govway-openssl-pbkdf2-wrap.local.password=${ksm:encryptionPassword}
    ksm.govway-openssl-pbkdf2-wrap.local.password.type=openssl-pbkdf2-aes-256-cbc
    ksm.govway-openssl-pbkdf2-wrap.local.encoding=base64
    #
    # UNWRAP
    ksm.govway-openssl-pbkdf2-unwrap.label=OpenSSL 'pbkdf2'
    ksm.govway-openssl-pbkdf2-unwrap.type=openssl-pbkdf2-unwrap
    ksm.govway-openssl-pbkdf2-unwrap.mode=unwrap
    ksm.govway-openssl-pbkdf2-unwrap.encryptionMode=local
    ksm.govway-openssl-pbkdf2-unwrap.input.param1.name=encryptionPassword
    ksm.govway-openssl-pbkdf2-unwrap.input.param1.label=Encryption Password
    ksm.govway-openssl-pbkdf2-unwrap.local.impl=openssl
    ksm.govway-openssl-pbkdf2-unwrap.local.keystore.type=pass
    ksm.govway-openssl-pbkdf2-unwrap.local.password=${ksm:encryptionPassword}
    ksm.govway-openssl-pbkdf2-unwrap.local.password.type=openssl-pbkdf2-aes-256-cbc
    ksm.govway-openssl-pbkdf2-unwrap.local.encoding=base64
    # =======================================================================================
        
    # =======================================================================================
    # KSM 'gw-remote' usato dai nodi manager
    #
    # WRAP
    ksm.govway-remote-wrap.label=GovWay Remote Wrap
    ksm.govway-remote-wrap.type=govway-remote-wrap
    ksm.govway-remote-wrap.mode=wrap
    ksm.govway-remote-wrap.encryptionMode=remote
    ksm.govway-remote-wrap.input.param1.name=endpointWrap
    ksm.govway-remote-wrap.input.param1.label=Endpoint Wrap Key
    ksm.govway-remote-wrap.input.param2.name=username
    ksm.govway-remote-wrap.input.param2.label=Username
    ksm.govway-remote-wrap.input.param3.name=password
    ksm.govway-remote-wrap.input.param3.label=Password
    ksm.govway-remote-wrap.http.endpoint=${ksm:endpointWrap}&paramValue=${ksm-base64-urlencoded-key}
    ksm.govway-remote-wrap.http.method=GET
    ksm.govway-remote-wrap.http.username=${ksm:username}
    ksm.govway-remote-wrap.http.password=${ksm:password}
    #
    # UNWRAP
    ksm.govway-remote-unwrap.label=GovWay Remote Unwrap
    ksm.govway-remote-unwrap.type=govway-remote-unwrap
    ksm.govway-remote-unwrap.mode=unwrap
    ksm.govway-remote-unwrap.encryptionMode=remote
    ksm.govway-remote-unwrap.input.param1.name=endpointUnwrap
    ksm.govway-remote-unwrap.input.param1.label=Endpoint Unwrap Key
    ksm.govway-remote-unwrap.input.param2.name=username
    ksm.govway-remote-unwrap.input.param2.label=Username
    ksm.govway-remote-unwrap.input.param3.name=password
    ksm.govway-remote-unwrap.input.param3.label=Password
    ksm.govway-remote-unwrap.http.endpoint=${ksm:endpointUnwrap}&paramValue=${ksm-urlencoded-key}
    ksm.govway-remote-unwrap.http.method=GET
    ksm.govway-remote-unwrap.http.username=${ksm:username}
    ksm.govway-remote-unwrap.http.password=${ksm:password}
    ksm.govway-remote-unwrap.http.response.base64Encoded=true
    # =======================================================================================
