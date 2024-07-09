.. _govwaySecretsMapConfig:

Configurazione delle variabili cifrate
---------------------------------------

All'interno del file *<directory-lavoro>/govway.secrets.properties* è possibile razionalizzare una serie di variabili Java tramite la seguente sintassi:

::

    # Consente di definire proprietà java tramite la sintassi:
    java.<nome>=<valore>

Il valore fornito dovrà essere decifrabile tramite uno dei security engine o KMS di 'unwrap' disponibili all'interno della configurazione *<directory-lavoro>/byok.properties*, descritti rispettivamente nelle sezioni  :ref:`byokInstallSecurityEngine` e :ref:`byokInstallKsm`. 

La modalità di decifratura di default è definibile tramite la proprietà 'unwrap.default.mode', che può assumere i seguenti valori:

- security: viene utilizzato il security engine riferito dall'identificativo riportato nella proprietà 'unwrap.default.id'. Se la proprietà 'unwrap.default.mode' non è definita, viene utilizzato il security engine di default descritto nella sezione :ref:`byokInstallSecurityGovWay` utilizzato per cifrare le informazioni confidenziali.

- ksm: viene utilizzato il Key Management Service riferito dall'identificativo riportato nella proprietà 'unwrap.default.id'.

::

    # Indica la modalità di default utilizzata per decifrare i valori forniti rispetto alle configurazioni presenti nel file 'byok.properties'.
    # security: viene utilizzato il security engine indicato nella proprietà 'unwrap.default.id' (per default viene utilizzato il security engine caricato da GovWay)
    # ksm: viene utilizzato il key management service definito tramite la proprietà 'unwrap.default.id'
    unwrap.default.mode=
    unwrap.default.id=

È possibile verificare che le variabili Java siano state caricate tramite due modalità:

- tutte le variabili java vengono registrate da GovWay nel file di log *<directory-log>/govway_configurazioneSistema.log*;
- lo stesso file è inoltre generabile dinamicamente accedendo alla sezione '*Strumenti > Runtime*' (:ref:`strumenti_runtime`), tramite la voce *Download*.

I valori delle variabili, poiché contengono secrets, non vengono registrati nei log sopra indicati ma viene attuato un offuscamento definito dal valore della proprietà 'obfuscated.mode':

- digest (default): viene calcolato il digest del valore rispetto all'algoritmo indicato nella proprietà 'obfuscated.digest' (default: SHA-256) 
- static: viene utilizzato staticamente il valore indicato nella proprietà 'obfuscated.static' (default: ******)
- none: non viene attuato alcun offuscamento

Di seguito, la sintassi da utilizzare nel file *govway.secrets.properties*:

::

    # Modalità utilizzata per offuscare
    obfuscated.mode=digest
    #obfuscated.digest=SHA-256
    #obfuscated.static=******

È inoltre possibile definire una modalità di decifratura differente da quella di default specificando la modalità da utilizzare per la singola variabile tramite il prefisso 'java.security.' o il prefisso 'java.ksm.', a seconda che si voglia utilizzare rispettivamente un security engine o un KSM per la decodifica.

Di seguito un esempio di utilizzo di un security engine differente:

::

    java.<nomeVariabile>=<valoreCifratoTramiteSecurityEngineIdX>
    java.security.<nomeVariabile>=<identificativoSecurityEngineIdX>
    
Di seguito un esempio di utilizzo di un ksm:

::

    java.<nomeVariabile>=<valoreCifratoTramiteKSMIdX>
    java.ksm.<nomeVariabile>=<identificativoKSMIdX>

Come descritto nella sezione :ref:`byokInstallKsm` ogni KSM può richiedere dei parametri di input. Tali parametri possono essere forniti nel file *govway.secrets.properties* tramite la seguente sintassi:

::

    # Per un ksm è possibile configurare i parametri richiesti tramite la seguente sintassi:
    ksm.<identificativoKSM>.param.<nomeParametro>=<valoreParametro>

È infine possibile definire all'interno del file *govway.secrets.properties* delle variabili i cui valori non sono cifrati, registrandole con la seguente modalità:

::

    java.<nomeVariabile>=<valoreInChiaro>
    java.wrapped.<nomeVariabile>=false

