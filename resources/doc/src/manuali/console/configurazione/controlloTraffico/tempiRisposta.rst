.. _console_tempiRisposta:

Tempi Risposta
~~~~~~~~~~~~~~

In questa sezione vengono indicati i valori limite di default riguardo i
tempi di risposta dei servizi con cui il gateway interagisce durante
l'elaborazione delle richieste. Nel caso delle erogazioni, si tratta dei
tempi di risposta dei servizi interni, successivamente ad una richiesta
di erogazione dall'esterno. Nel caso delle fruizioni, si tratta dei
tempi di risposta dei servizi esterni, successivamente ad una richiesta
di fruizione da parte di un client interno al dominio. I tempi
configurabili sono:

-  *Connection Timeout (ms)*: Intervallo di tempo atteso, sulle
   comunicazioni in uscita, prima di sollevare l'errore Connection
   Timeout (scadenza del tempo di attesa per stabilire una connessione).

-  *Read Timeout (ms)*: Intervallo di tempo atteso, dopo aver stabilito
   una connessione in uscita, prima di sollevare l'errore di Read
   Timeout (scadenza del tempo di attesa per ricevere il payload
   dall'interlocutore).

-  *Tempo Medio di Risposta (ms)*: Valore di soglia del tempo medio di
   risposta al fine di valutare la situazione di *Degrado
   Prestazionale*, condizione per l'applicabilità di eventuali politiche
   restrittive come documentate più avanti.
