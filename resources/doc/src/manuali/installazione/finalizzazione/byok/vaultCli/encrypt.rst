.. _byokInstallToolVaultCliEncDec:

Encrypt / Decrypt
-------------------------------------------------------

Il tool *govway-vault-cli* consente la cifratura o decifratura di chiavi, riferendosi a un security engine o un KSM definito nel file *<directory-lavoro>/byok.properties*.

I comandi da utilizzare sono *encrypt.sh* per la cifratura e *decrypt.sh* per la decifratura.

Se invocato senza parametri, il tool visualizza nell'errore restituito gli argomenti attesi che dovranno essere forniti con l'invocazione:

::

    encrypt -system_in|-file_in=text|path -system_out|-file_out=path [-sec|-ksm=id]
    
Gli argomenti prevedono:

- il contenuto da elaborare, fornito tramite una delle due seguenti modalità:

    - *-system_in=TEXT*: consente di fornire il testo da elaborare direttamente a linea di comando;
    - *-file_in=PATH*: consente di indicare il path assoluto contenente il contenuto da elaborare;
    
- dove serializzare il contenuto elaborato (cifrato o decifrato):

    - *-system_out*: il contenuto viene visualizzato come output dell'esecuzione del tool a linea di comando;
    - *-file_out=PATH*: consente di indicare il path assoluto dove verrà salvato il contenuto elaborato;
    
- il riferimento a un security engine o un KSM definito nel file *<directory-lavoro>/byok.properties*:

    - *-sec=id*: identificativo di un :ref:`byokInstallSecurityEngine`;
    - *-ksm=id*: identificativo di un :ref:`byokInstallKsm`.
    
Di seguito un esempio che assume l'esistenza di un security engine 'gw-pbkdf2' configurato per attuare una cifratura attraverso la derivazione di una chiave conforme al comando 'openssl aes-256-cbc -pbkdf2 -k encryptionPassword -a'.

::

    [govway-vault-cli]$ cat Prova.txt
    !!Hello World!!
    
    [govway-vault-cli]$ ./encrypt.sh -file_in=Prova.txt -file_out=Prova.txt.enc -sec=gw-pbkdf2
    Encrypted content in 'Prova.txt.enc'

    [govway-vault-cli]$ cat Prova.txt.enc
    ==gw-pbkdf2==.U2FsdGVkX1/0Qvn5Uzg8yb7xezxhSks2+buxg+/6al+Ltk+Kzo6iuP5BSiiatjFB

    [govway-vault-cli]$ ./decrypt.sh -file_in=Prova.txt.enc -file_out=Prova.txt.decoded -sec=gw-pbkdf2
    Decrypted content in 'Prova.txt.decoded'

    [govway-vault-cli]$ cat Prova.txt.decoded
    !!Hello World!!

    

