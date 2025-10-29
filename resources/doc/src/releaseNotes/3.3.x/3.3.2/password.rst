Miglioramenti alla funzionalità di Autenticazione
-------------------------------------------------

Introdotta la configurabilità del tipo di cifratura delle password utilizzato per:

	- le utenze delle console di gestione e monitoraggio;
	
	- gli applicativi e i soggetti registrati con credenziali 'http-basic'.

È stata adeguata la configurazione di default al fine di utilzzare un algoritmo di cifratura più recente: SHA-512-based Unix crypt ($6$). 

Per garantire la retrocompatibilità con le utenze esistenti, la
verifica delle password viene attuata anche usando il precedente
algoritmo. La verifica in modalità 'backward compatibility' può essere
disattivata una volta migrate tutte le password al nuovo formato di
cifratura.
