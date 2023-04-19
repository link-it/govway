Miglioramenti alla funzionalità di Negoziazione Token
-----------------------------------------------------

In una token policy di negoziazione è adesso possibile personalizzare i seguenti parametri della chiamata verso l'authorization server:

	- metodo http;
	- eventuale content-type e payload;
	- aggiunta di header http;
	- definire credenziali http-basic, http-bearer e l'invio di un certificato tls client;
	- personalizzazione del parsing della risposta.

Inoltre anche nelle modalità di negoziazione standard già esistenti è stata aggiunta la possibilità di aggiungere header http personalizzati nella richiesta.

Infine è stato migliorato il tooltip visualizzato sul connettore di una erogazione in modo da visualizzare l'eventuale token policy associata.


