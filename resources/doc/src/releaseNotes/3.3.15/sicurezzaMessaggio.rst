Miglioramenti alla funzionalità di Sicurezza Messaggio
---------------------------------------------------------

La funzionalità di verifica dei certificati, attuabile tramite la console di gestione, include adesso anche la validazione dei keystore riferiti nella configurazione della sicurezza dei messaggi. 
	
Inoltre, è stata aggiunta la possibilità di disabilitare la 'Compliance BSP 1.1' nella validazione di un messaggio contenente WS-Security Username Token.

Infine sono state introdotte opzioni aggiuntive che consentono di modificare alcuni aspetti relativi alla sicurezza del messaggio attuata tramite la libreria 'wss4j', al fine di renderlo interoperabile con altre librerie più datate:
	
- encoding in base64 dell'attachment prima o dopo aver applicato la sicurezza;
	
- gestione dell'elemento 'InclusiveNamespace' in presenza di lista di prefissi vuota e all'interno dell'elemento 'CanonicalizationMethod';
	
- gestione dell'elemento 'KeyInfo' presente all'interno dell'elemento 'EncryptedData';
	
- aggiunta o meno delle parentesi uncinate ('<' e '>') nei riferimenti agli allegati;
	
- aggiunta dell'header di un attachment all'interno del messaggio cifrato;
	
- aggiunto il supporto per lo scambio di chiavi simmetriche di cifratura usando un'altra chiave simmetrica condivisa, tramite la gestione di keystore di tipo 'jceks'."

