.. _govwayConfigMapConfig:

Configurazione delle variabili 
---------------------------------

All'interno del file *<directory-lavoro>/govway.map.properties* è possibile razionalizzare una serie di variabili Java tramite la seguente sintassi:

::

    # Consente di definire proprietà java tramite la sintassi:
    java.<nome>=<valore>

È possibile verificare le variabili Java, sia quelle definite nel file *govway.map.properties* che quelle presenti originariamente nel sistema, tramite due modalità:

- tutte le variabili Java vengono registrate da GovWay nel file di log *<directory-log>/govway_configurazioneSistema.log*;
- lo stesso file è inoltre generabile dinamicamente accedendo alla sezione '*Strumenti > Runtime*' (:ref:`strumenti_runtime`), tramite la voce *Download*.

I valori delle variabili potrebbero contenere informazioni confidenziali (es. password) che non devono finire in chiaro all'interno dei log sopra indicati. Per attivarne l'offuscamento è possibile utilizzare i seguenti costrutti:

::

    # Per offuscare variabili java usare la sintassi:
    obfuscated.java.keys=<nome1>,<nome2>,...,<nomeN>

Vengono supportate diverse modalità di offuscamento, attivabili assegnando uno dei seguenti valori alla proprietà 'obfuscated.mode':

- digest (default): viene calcolato il digest del valore rispetto all'algoritmo indicato nella proprietà 'obfuscated.digest' (default: SHA-256);
- static: viene utilizzato staticamente il valore indicato nella proprietà 'obfuscated.static' (default: ******);
- none: non viene attuato alcun offuscamento.

Di seguito, la sintassi da utilizzare nel file *govway.map.properties*:

::

    # Modalità utilizzata per offuscare
    obfuscated.mode=digest
    #obfuscated.digest=SHA-256
    #obfuscated.static=******




