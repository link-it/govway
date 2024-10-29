# LDAP

In questo package sono implementati i test per il client ldap

## Client

Il package client implementa un client ldap di sola lettura.
Il package comprende un interafaccia di un client, un implementazione di tale interfaccia usando il framework di spring.
Una classe query che definisce la tipologia di query da effettuare.
Una flasse filtro che serve a definire un filtro di ricerca.
Una classe che implementa una factory per generare un implementazione concreta del client.
 
## Test

Per testare tale package viene utilizzata la testsuite di spring che permette di istanziare un embedded server in java e 
di caricare all'interno vari dati da un file in formato ldif.
Dopo che il server viene istanziato vari test possono essere effettutati per controllare il corretto funzionamento del client.

### Testare Query

Il test delle query viene effettuato inviando al server ldap diverse richieste con filtri e attributi diversi e ci si aspetta
che il risultato sia equivalente ai filtri applicati ad una copia delle risorse locale.

### Testare il caricamento di un file binario (un file CRL)

Un altro test effettuato serve a controllare la ricezione corretta di file binari, in questo caso nel server e' caricato
un file crl in formato binario, questo test lo richiede tramite una semplice query e poi controlla che l'issuer sia
quello effettivamente impostato nella crl