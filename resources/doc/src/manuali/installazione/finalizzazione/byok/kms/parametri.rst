.. _byokInstallKmsParametri:

Parametri di un KMS
-------------------------------------------------------

Nelle sezioni :ref:`byokInstallKmsLocale` e :ref:`byokInstallKmsRemoto` è stata descritta la sintassi da utilizzare per attuare operazioni di cifratura/decifratura utilizzando rispettivamente una master key presente in un keystore locale o remoto. 

Tutte le configurazioni descritte nelle precedenti sezioni possono riferire dei parametri risolti a runtime durante l'utilizzo del KMS tramite la sintassi '${kms:<nomeparametro>}'. I parametri riferiti dovranno essere definiti in un kms attraverso le seguenti proprietà:

- kms.<idKms>.input.<idParam>.name: [optional] identificativo univoco del parametro
- kms.<idKms>.input.<idParam>.label: [optional] definisce l'etichetta associata al parametro di configurazione

Tutti i parametri dovranno essere valorizzati ogni volta che viene riferito un KMS come mostrato ad esempio nella sezione :ref:`byokInstallSecurityEngine`, durante l'associazione ad un security engine tramite le proprietà 'security.<identificativo>.kms.param.<nomeParametro>', o nella sezione :ref:`govwaySecretsMapConfig`, attraverso le proprietà 'kms.<identificativoKMS>.param.<nomeParametro>'.

Oltre ai parametri esistono le seguenti variabili speciali che possono essere utilizzate all'interno delle configurazioni di un KMS per riferire l'informazione confidenziale stessa per la quale si sta operando la cifratura o la decifratura:

- ${kms-key}: bytes dell'informazione confidenziale;
- ${kms-urlencoded-key}: bytes dell'informazione confidenziale codificata per essere utilizzata in una url;
- ${kms-base64-key}: bytes dell'informazione confidenziale codificata in base64;
- ${kms-hex-key}: bytes dell'informazione confidenziale codificata in una rappresentazione esadecimale;
- ${kms-base64-urlencoded-key}: bytes dell'informazione confidenziale codificata in base64 e successivamente codificata per poter essere utilizzata in una url;
- ${kms-hex-urlencoded-key}: bytes dell'informazione confidenziale codificata in una rappresentazione esadecimale e successivamente codificata per poter essere utilizzata in una url.

Di seguito un esempio di KMS che definisce dei parametri e li utilizza per definire la modalità di invocazione del kms remoto.

::

    kms.govway-example.label=GovWay Example
    kms.govway-example.type=govway-example
    kms.govway-example.mode=unwrap
    kms.govway-example.encryptionMode=remote
    kms.govway-example.input.param1.name=endpointUnwrap
    kms.govway-example.input.param1.label=Endpoint Unwrap Key
    kms.govway-example.input.param2.name=username
    kms.govway-example.input.param2.label=Username
    kms.govway-example.input.param3.name=password
    kms.govway-example.input.param3.label=Password
    kms.govway-example.http.endpoint=${kms:endpointUnwrap}&paramValue=${kms-urlencoded-key}
    kms.govway-example.http.method=GET
    kms.govway-example.http.username=${kms:username}
    kms.govway-example.http.password=${kms:password}

