.. _byokInstallToolVaultCliUpdate:

Aggiornamento delle Informazioni confidenziali
-------------------------------------------------------

Il tool *govway-vault-cli* può essere utilizzato sulla base dati esistente sia per cifrare le informazioni confidenziali precedentemente salvate in chiaro, sia per aggiornarle utilizzando una differente master key.

.. note::
      Si consiglia di effettuare un backup della base dati prima di procedere con l'aggiornamento delle informazioni confidenziali.

Il comando *update.sh*, se invocato senza parametri, visualizza nell'errore restituito gli argomenti attesi che dovranno essere forniti con l'invocazione:

::

    update -sec_in|-plain_in[=id] -sec_out|-plain_out)[=id] [-report=path]

Gli argomenti prevedono:

- indicazione sull'attuale cifratura utilizzata:

    - *-plain_in*: indica che le informazioni confidenziali presenti nella base dati risultano attualmente in chiaro;
    - *-sec_in=id*: identificativo del :ref:`byokInstallSecurityEngine` con cui sono state cifrate le informazioni presenti nella base dati;

- indicazione sulla nuova cifratura che si intende apportare:

    - *-sec_out=id*: identificativo del :ref:`byokInstallSecurityEngine` con cui si intende cifrare le informazioni presenti nella base dati;
    - *-plain_out*: indica che le informazioni confidenziali presenti nella base dati saranno salvate in chiaro;

- *-report=PATH* [optional]: argomento opzionale che consente di definire un path su file system dove verranno salvate tutte le informazioni modificate dall'operazione di aggiornamento.
