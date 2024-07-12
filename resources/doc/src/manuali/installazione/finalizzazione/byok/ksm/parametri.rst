.. _byokInstallKsmParametri:

Parametri di un KSM
-------------------------------------------------------

Nelle sezioni :ref:`byokInstallKsmLocale` e :ref:`byokInstallKsmRemoto` è stata descritta la sintassi da utilizzare per attuare operazioni di cifratura/decifratura utilizzando rispettivamente una master key presente in un keystore locale o remoto. 

Tutte le configurazioni descritte nelle precedenti sezioni possono riferire dei parametri risolti a runtime durante l'utilizzo del KSM tramite la sintassi '${ksm:<nomeparametro>}'. I parametri riferiti dovranno essere definiti in un ksm attraverso le seguenti proprietà:

- ksm.<idKsm>.input.<idParam>.name: [optional] identificativo univoco del parametro
- ksm.<idKsm>.input.<idParam>.label: [optional] definisce l'etichetta associata al parametro di configurazione

Tutti i parametri dovranno essere valorizzati ogni volta che viene riferito un KSM come mostrato ad esempio nella sezione :ref:`byokInstallSecurityEngine`, durante l'associazione ad un security engine tramite le proprietà 'security.<identificativo>.ksm.param.<nomeParametro>', o nella sezione :ref:`govwaySecretsMapConfig`, attraverso le proprietà 'ksm.<identificativoKSM>.param.<nomeParametro>'.

Oltre ai parametri esistono le seguenti variabili speciali che possono essere utilizzate all'interno delle configurazioni di un KSM per riferire l'informazione confidenziale stessa per la quale si sta operando la cifratura o la decifratura:

- ${ksm-key}: bytes dell'informazione confidenziale;
- ${ksm-urlencoded-key}: bytes dell'informazione confidenziale codificata per essere utilizzata in una url;
- ${ksm-base64-key}: bytes dell'informazione confidenziale codificata in base64;
- ${ksm-hex-key}: bytes dell'informazione confidenziale codificata in una rappresentazione esadecimale;
- ${ksm-base64-urlencoded-key}: bytes dell'informazione confidenziale codificata in base64 e successivamente codificata per poter essere utilizzata in una url;
- ${ksm-hex-urlencoded-key}: bytes dell'informazione confidenziale codificata in una rappresentazione esadecimale e successivamente codificata per poter essere utilizzata in una url.

Di seguito un esempio di KSM che definisce dei parametri e li utilizza per definire la modalità di invocazione del ksm remoto.

::

    ksm.govway-example.label=GovWay Example
    ksm.govway-example.type=govway-example
    ksm.govway-example.mode=unwrap
    ksm.govway-example.encryptionMode=remote
    ksm.govway-example.input.param1.name=endpointUnwrap
    ksm.govway-example.input.param1.label=Endpoint Unwrap Key
    ksm.govway-example.input.param2.name=username
    ksm.govway-example.input.param2.label=Username
    ksm.govway-example.input.param3.name=password
    ksm.govway-example.input.param3.label=Password
    ksm.govway-example.http.endpoint=${ksm:endpointUnwrap}&paramValue=${ksm-urlencoded-key}
    ksm.govway-example.http.method=GET
    ksm.govway-example.http.username=${ksm:username}
    ksm.govway-example.http.password=${ksm:password}

