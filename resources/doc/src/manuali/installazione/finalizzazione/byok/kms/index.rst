.. _byokInstallKms:

Key Management Service
-------------------------------------------------------

In questa sezione viene descritta la sintassi da utilizzare per definire i KMS funzionali ad operazioni di cifratura (wrap) o di decifratura (unwrap) di un'informazione sensibile o di una chiave/keystore.

GovWay consente di definire molteplici policy KMS agendo sul file *<directory-lavoro>/byok.properties*.

La configurazione di ogni KMS è rappresentata dall'insieme di proprietà che presentano lo stesso prefisso 'kms.<idKms>'. L'identificativo <idKms> serve univocamente ad aggregare tali proprietà.

.. note::

           Nelle versioni precedenti alla 3.3.16.p1 veniva utilizzata la keyword 'ksm' al posto di 'kms'. Anche se ancora supportata, l’utilizzo di 'ksm' è deprecato e sarà rimosso nelle versioni future. Si raccomanda pertanto di aggiornare i file di configurazione utilizzando la nuova keyword 'kms'.

Un KMS viene identificato univocamente da GovWay tramite la proprietà 'kms.<idKms>.type'; il suo valore deve essere univoco rispetto ai valori forniti negli altri KMS per la medesima proprietà.

Ogni KMS verrà indirizzato tramite il suo identificativo su molteplici aspetti:

- l'associazione ad un security engine, come descritto nella sezione :ref:`byokInstallSecurityEngine`;

- la decodifica dei valori indicati nel file *<directory-lavoro>/govway.secrets.properties*, come descritto nella sezione :ref:`govwaySecretsMap`;

- la cifratura o decifratura di chiavi e informazioni gestite tramite il tool 'govway-vault-cli' descritto nella sezione :ref:`byokInstallToolVaultCli`;

- la decodifica di chiavi e keystore cifrati, riferiti nei connettori HTTPS e nelle funzionalità di message security, attraverso la selezione di un KMS tra quelli resi disponibili nella scelta della 'BYOK Policy' nella console di gestione (govwayConsole) come mostrato nella figura :numref:`byokPolicyConnettoreEsempio`.

Quando la selezione di un KMS avviene tramite una console di gestione, non viene visualizzato all'utente l'identificativo ma bensì una label che deve essere definita nella proprietà 'kms.<idKms>.label'; di conseguenza anche questo valore deve essere univoco rispetto a quello fornito per gli altri KMS.

Di seguito un esempio di KMS basato sulla cifratura di un'informazione tramite la derivazione di una chiave conforme al comando 'openssl aes-256-cbc -pbkdf2 -k encryptionPassword -a':

::

    # Esempio di KMS Wrap per una cifratura basata su chiave derivata da una password configurata all'interno del security engine 'openssl-pbkdf2'
    kms.govway-openssl-pbkdf2-wrap.label=OpenSSL 'pbkdf2' Wrap Example
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


Di seguito vengono descritte tutte le opzioni di configurazione utilizzabili nella registrazione di un KMS all'interno del file *<directory-lavoro>/byok.properties*:

- kms.<idKms>.type: identificativo univoco;

- kms.<idKms>.label: etichetta associata al kms e visualizzata nelle maschere di configurazione (deve essere a sua volta univoca);

- kms.<idKms>.mode: [wrap|unwrap] indica se il KMS è utilizzabile per effettuare la cifratura (wrap) o la decifratura (unwrap);

- kms.<idKms>.encryptionMode: [optional; default:remote] una operazione di wrap/unwrap può essere realizzata invocando un kms remoto (via http) o cifrando/decifrando tramite keystore locali.
	
Nelle sezioni seguenti verranno fornite le opzioni di configurazione specifica di una operazione di wrap/unwrap a seconda della modalità indicata nella proprietà 'kms.<idKms>.encryptionMode':

- 'local' (:ref:`byokInstallKmsLocale`): utilizzato per implementare un approccio 'HYOK' le operazioni di cifratura e decifratura avvengono utilizzando una master key presente in un keystore locale (risiedente su filesystem) o all'interno di un HSM;

- 'remote' (:ref:`byokInstallKmsRemoto`): utilizzato per un approccio 'BYOK' dove la master key è depositata su un servizio remoto (es. in cloud).

In entrambe le configurazioni (locale o remota) sarà possibile riferire dei parametri descritti nella sezione :ref:`byokInstallKmsParametri`

.. toctree::
        :maxdepth: 2

	locale
	remoto
	parametri
