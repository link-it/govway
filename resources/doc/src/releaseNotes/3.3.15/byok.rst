Nuova funzionalità di cifratura delle informazioni confidenziali
-------------------------------------------------------------------------

È stato agginuto il supporto alla gestione delle informazioni confidenziali salvate su database e delle chiavi/keystore presenti su filesystem attraverso la cifratura/decifratura mediante una master key, utilizzando uno dei seguenti approcci:

- HYOK (Hold Your Own Key): le operazioni di cifratura e decifratura avvengono utilizzando una master key presente in un keystore locale (risiedente su filesystem) o all'interno di un HSM (es. smartcard). 

- BYOK (Bring Your Own Key): la master key viene depositata su un servizio remoto (es. in cloud). In questo caso, le operazioni di wrap-key e unwrap-key delle informazioni confidenziali vengono gestite tramite chiamate API esposte dal servizio remoto.
	
La console di gestione è stata rivista per:

- cifrare i campi contenenti informazioni confidenziali;

- consentire l'indicazione di una modalità 'unwrap' di un keystore cifrato riferito nella configurazione di un connettore https o nella sicurezza messaggio (es. token ModI);

- consentire la registrazione di proprietà definite con un valore cifrato nei seguenti elementi di registro:

   - API erogata o fruita
   - soggetto
   - applicativo
   - configurazione globale

Aggiunto inoltre un supporto per collezionare una serie di variabili Java che potranno essere riferite in qualsiasi file di proprietà di GovWay presente nella 'directory-lavoro' e nelle configurazioni di GovWay (es. all'interno di una trasformazione).

La definizione di una variabile può essere attuata all'interno di due file differenti:

- govway.map.properties: le variabili definite in questo file verranno caricate all'avvio di ogni applicazione GovWay (es. runtime, console, batch ecc.);

- govway.secrets.properties: a differenza del precedente file, i valori delle variabili potranno essere definiti cifrati e GovWay si occuperà di decifrarli prima del loro caricamento nel sistema.
