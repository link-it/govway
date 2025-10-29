Miglioramenti alla funzionalità di Tracciamento
------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- anche per le richieste contenenti credenziali non valide, token scaduti o non autorizzati vengono adesso registrate:

	- le informazioni sui claim principali presenti nel token (clientId, subject/issuer, username, eMail); 
	- le informazioni recuperate tramite le API PDND (es. nome e categoria dell'organizzazione);
	- l'identificativo autenticato a livello trasporto (principal);

- sono stati aggiunti nuovi esiti per le transazioni:

	- 'Read Timeout': risposta non ricevuta entro il timeout specificato;
	- 'Request Read Timeout': richiesta non ricevuta entro il timeout specificato;
	- 'Connection Timeout': connessione non stabilita entro il timeout specificato;
	- 'Negoziazione Token Fallita': indica degli errori emersi durante la negoziazione del token;

- le classi di appartenenza degli esiti sono state riviste al fine di includere il nuovo esito 'Request Read Timeout' e l'esito 'Connessione Client Interrotta' in una nuova classe 'Errore Client Indisponibile';

- i nuovi esiti relativi a timeout concorrono alla generazione di eventi che consentono all'operatore di indivuare l'occorrenza di errori di timeout senza dove effettuare ricerche puntuali nello storico delle transazioni;

- le informazioni raccolte tramite le API PDND sono state aggiunte alla base dati di tracciamento in modo da consentirne l'estrazione tramite viste personalizzate;

- nella funzionalità 'fileTrace' è adesso possibile accedere alle seguenti informazioni ModI:

	- informazioni del token ModI di audit 'Agid-JWT-TrackingEvidence';
	- informazioni recuperate tramite le API PDND.
