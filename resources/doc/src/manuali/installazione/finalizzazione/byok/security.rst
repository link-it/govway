.. _byokInstallSecurityEngine:

Security Engine
-------------------------------------------------------

Nella sezione :ref:`byokInstallKsm` è stata documentata la sintassi necessaria a registrare KSM che consentono la cifratura di un'informazione confidenziale (wrap) o la decifratura (unwrap).

L'assocazione di un KSM per l'operazione 'wrap' e un altro per l'operazione 'unwrap' consente di definire un *Security Engine*.

Di seguito un esempio in cui viene valorizzato il parametro richiesto da entrambi i KSM riferiti:

::

    # Esempio di cifratura basata su chiave derivata da una password attesa come variabile di sistema o della JVM
    security.gw-pbkdf2.ksm.wrap=openssl-pbkdf2-wrap
    security.gw-pbkdf2.ksm.unwrap=openssl-pbkdf2-unwrap
    security.gw-pbkdf2.ksm.param.encryptionPassword=esempio
    
La configurazione di ogni *Security Engine* è rappresentata dall'insieme di proprietà che presentano lo stesso prefisso 'security.<idSecurityEngine>'. L'identificativo <idSecurityEngine>, a differenza della configurazione descritta nella sezione :ref:`byokInstallKsm`, serve sia ad aggregare tali proprietà che ad identificarlo univocamente.
    
Di seguito vengono descritte tutte le opzioni di configurazione utilizzabili nella registrazione di un *Security Engine* all'interno del file *<directory-lavoro>/byok.properties*:

- *security.<idSecurityEngine>.ksm.wrap* [required]: identificativo del KSM da utilizzare per l'operazione di cifratura (wrap);
- *security.<idSecurityEngine>.ksm.unwrap* [required]: identificativo del KSM da utilizzare per l'operazione di decifratura (unwrap);
- *security.<idSecurityEngine>.ksm.param.<paramName>* [optional]: come descritto nella sezione :ref:`byokInstallKsmParametri` ogni KSM può richiedere dei parametri di input che possono essere forniti all'interno della definizione del *Security Engine* tramite la direttiva '*ksm.param.<paramName>*'.
