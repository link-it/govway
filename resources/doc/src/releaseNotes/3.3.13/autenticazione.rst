Miglioramenti alla funzionalità di Autenticazione
--------------------------------------------------

Il gestore delle credenziali, utilizzabile per l'autenticazione dei certificati client ottenuti tramite header HTTP, supporta adesso anche la decodifica HEX.
È stata inoltre aggiunta la possibilità di decodificare i certificati ricevuti in qualsiasi modalità supportata, prima provando la decodifica 'urlEncoded', in caso di fallimento la decodifica 'base64' e infine la decodifica 'hex'.
