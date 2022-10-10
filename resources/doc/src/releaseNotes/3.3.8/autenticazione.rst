Miglioramenti alla funzionalità di Autenticazione
--------------------------------------------------

Per l'autenticazione https è adesso possibile:

- associare un truststore per verificare i certificati client ricevuti;

- definire delle CRL per la verifica delle revoche.

Le stesse funzionalità sono state rese disponibili anche per il gestore delle credenziali utilizzabile per l'autenticazione tramite certificati client ottenuti tramite header HTTP.
In quest'ultima modalità è stata aggiunta la possibilità di decodificare i certificati ricevuti in entrambe le modalità supportate, prima provando la decodifica 'urlEncoded' ed in caso di fallimento la decodifica 'base64'.
