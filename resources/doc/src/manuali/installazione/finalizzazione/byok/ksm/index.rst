.. _byokInstallKsm:

Key Management Service
-------------------------------------------------------

In questa sezione viene descritta la sintassi da utilizzare per definire i KMS funzionali ad operazioni di cifratura (wrap) o di decifratura (unwrap) di un'informazione sensibile o di una chiave/keystore.

GovWay consente di definire molteplici policy KSM agendo sul file *<directory-lavoro>/byok.properties*.

La configurazione di ogni KSM è rappresentata dall'insieme di proprietà che presentano lo stesso prefisso 'ksm.<idKsm>'. L'identificativo <idKsm> serve univocamente ad aggregare tali proprietà.

Un KSM viene identificato univocamente da GovWay tramite la proprietà 'ksm.<idKsm>.type'; il suo valore deve essere univoco rispetto ai valori forniti negli altri KSM per la medesima proprietà.

Ogni KSM verrà indirizzato tramite il suo identificativo su molteplici aspetti:

- l'associazione ad un security engine, come descritto nella sezione :ref:`byokInstallSecurityEngine`;

- la decodifica dei valori indicati nel file *<directory-lavoro>/govway.secrets.properties*, come descritto nella sezione :ref:`govwaySecretsMap`;

- la cifratura o decifratura di chiavi e informazioni gestite tramite il tool 'govway-vault-cli' descritto nella sezione :ref:`byokInstallToolVaultCli`;

- la decodifica di chiavi e keystore cifrati, riferiti nei connettori HTTPS e nelle funzionalità di message security, attraverso la selezione di un KSM tra quelli resi disponibili nella scelta della 'BYOK Policy' nella console di gestione (govwayConsole) come mostrato nella figura :numref:`byokPolicyConnettoreEsempio`.

Quando la selezione di un KSM avviene tramite una console di gestione, non viene visualizzato all'utente l'identificativo ma bensì una label che deve essere definita nella proprietà 'ksm.<idKsm>.label'; di conseguenza anche questo valore deve essere univoco rispetto a quello fornito per gli altri KSM.

Di seguito un esempio di KSM basato sulla cifratura di un'informazione tramite la derivazione di una chiave conforme al comando 'openssl aes-256-cbc -pbkdf2 -k encryptionPassword -a':

::

    # Esempio di KSM Wrap per una cifratura basata su chiave derivata da una password configurata all'interno del security engine 'openssl-pbkdf2'
    ksm.govway-openssl-pbkdf2-wrap.label=OpenSSL 'pbkdf2' Wrap Example
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


Di seguito vengono descritte tutte le opzioni di configurazione utilizzabili nella registrazione di un KSM all'interno del file *<directory-lavoro>/byok.properties*:

- ksm.<idKsm>.type: identificativo univoco;

- ksm.<idKsm>.label: etichetta associata al ksm e visualizzata nelle maschere di configurazione (deve essere a sua volta univoca);

- ksm.<idKsm>.mode: [wrap|unwrap] indica se il KSM è utilizzabile per effettuare la cifratura (wrap) o la decifratura (unwrap);

- ksm.<idKsm>.encryptionMode: [optional; default:remote] una operazione di wrap/unwrap può essere realizzata invocando un ksm remoto (via http) o cifrando/decifrando tramite keystore locali.
	
Nelle sezioni seguenti verranno fornite le opzioni di configurazione specifica di una operazione di wrap/unwrap a seconda della modalità indicata nella proprietà 'ksm.<idKsm>.encryptionMode':

- 'local' (:ref:`byokInstallKsmLocale`): utilizzato per implementare un approccio 'HYOK' le operazioni di cifratura e decifratura avvengono utilizzando una master key presente in un keystore locale (risiedente su filesystem) o all'interno di un HSM;

- 'remote' (:ref:`byokInstallKsmRemoto`): utilizzato per un approccio 'BYOK' dove la master key è depositata su un servizio remoto (es. in cloud).

In entrambe le configurazioni (locale o remota) sarà possibile riferire dei parametri descritti nella sezione :ref:`byokInstallKsmParametri`

.. toctree::
        :maxdepth: 2

	locale
	remoto
	parametri
