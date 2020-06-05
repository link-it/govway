.. _errori_401_ProxyAuthenticationRequired:

ProxyAuthenticationRequired
---------------------------

La richiesta non possiede le credenziali necessarie per poter autenticare il frontend su cui è stata effettuata l'autenticazione degli applicativi chiamanti.

*Scenario in cui si presenta l'errore*

Nel caso in cui la terminazione ssl viene gestita su un frontend http (Apache httpd, IIS, etc) GovWay necessita di ricevere le credenziali per attuare il processo di autenticazione descritto nella sezione :ref:`apiGwAutenticazione`.
Nel caso di utilizzo di una integrazione 'mod_jk' tra frontend e application server, GovWay riceve i certificati gestiti sul frontend http in maniera trasparente.
Negli altri casi invece deve essere configurato opportunamente il frontend http per inoltrare i certificati client o il DN attraverso header HTTP a GovWay. 

Nell'ambito di tale configurazione è possibile abilitare l'autenticazione del frontend in modo da accettare gli header http contenenti le credenziali solamente da un frontend autenticato.

Maggiori dettagli sulla funzionalità sono descritti nella sezione :ref:`install_ssl_server_frontend` della Guida di Installazione.



