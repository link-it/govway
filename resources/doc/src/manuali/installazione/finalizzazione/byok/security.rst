.. _byokInstallSecurityEngine:

Security Engine
-------------------------------------------------------

Nella sezione :ref:`byokInstallKms` è stata documentata la sintassi necessaria a registrare KMS che consentono la cifratura di un'informazione confidenziale (wrap) o la decifratura (unwrap).

L'assocazione di un KMS per l'operazione 'wrap' e un altro per l'operazione 'unwrap' consente di definire un *Security Engine*.

Di seguito un esempio in cui viene valorizzato il parametro richiesto da entrambi i KMS riferiti:

::

    # Esempio di cifratura basata su chiave derivata da una password attesa come variabile di sistema o della JVM
    security.gw-pbkdf2.kms.wrap=openssl-pbkdf2-wrap
    security.gw-pbkdf2.kms.unwrap=openssl-pbkdf2-unwrap
    security.gw-pbkdf2.kms.param.encryptionPassword=esempio
    
La configurazione di ogni *Security Engine* è rappresentata dall'insieme di proprietà che presentano lo stesso prefisso 'security.<idSecurityEngine>'. L'identificativo <idSecurityEngine>, a differenza della configurazione descritta nella sezione :ref:`byokInstallKms`, serve sia ad aggregare tali proprietà che ad identificarlo univocamente.
    
Di seguito vengono descritte tutte le opzioni di configurazione utilizzabili nella registrazione di un *Security Engine* all'interno del file *<directory-lavoro>/byok.properties*:

- *security.<idSecurityEngine>.kms.wrap* [required]: identificativo del KMS da utilizzare per l'operazione di cifratura (wrap);
- *security.<idSecurityEngine>.kms.unwrap* [required]: identificativo del KMS da utilizzare per l'operazione di decifratura (unwrap);
- *security.<idSecurityEngine>.kms.param.<paramName>* [optional]: come descritto nella sezione :ref:`byokInstallKmsParametri` ogni KMS può richiedere dei parametri di input che possono essere forniti all'interno della definizione del *Security Engine* tramite la direttiva '*kms.param.<paramName>*'.
