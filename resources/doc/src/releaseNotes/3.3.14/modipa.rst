Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti alla gestione dei pattern 'AUDIT_REST_01' e 'AUDIT_REST_02':

- aggiunta la possibilità di definire dei criteri di validazioni sui claim attesi all'interno del token di audit; i criteri associabili ad ogni specifico claim sono:

	- una lista di valori ammessi;
	- una validazione tramite espressione regolare;
	- indicazione della lunghezza minima e/o massima di caratteri;

- nella definizione delle informazioni personalizzate da includere nel token di AUDIT è adesso possibile indicare per ogni singolo claim se l'informazione veicolata sia riutilizzabile o meno su differenti chiamate; l'intero token di audit verrà salvato in cache e riutilizzato su differenti chiamate solo se tutti i claim inseriti all'interno del token risultano configurati come riutilizzabili;

- attivando una configurazione opzionale per il token di audit, l'impostazione veniva ignorata e il token veniva obbligatoriamente richiesto; l'anomalia è stata risolta.


Sono stati apportati i seguenti miglioramenti alla funzionalità di integrazione con la PDND:

- è stato rivisto il concetto di richiedente di una richiesta di servizio al fine di considerare anche il nome dell'organizzazione recuperata tramite le API PDND, in modo da visualizzarla al posto del clientId durante la consultazione dello storico delle transazioni;

- sono stati introdotti i seguenti miglioramenti alla console e alle API di monitoraggio per utilizzare i dati individuati tramite le API PDND:

	- nello storico delle transazioni è adesso possibile effettuare una ricerca per nome dell'organizzazione individuata;
	- i dati dei report statistici possono essere filtrati per nome dell'organizzazione;
	- è possibile adesso ottenere una distribuzione per clientId contenente anche le informazioni recuperate tramite le API PDND (nome organizzazione, external-id, categoria);

- tramite la console di gestione è adesso possibile verificare o eliminare i dati presenti nella cache locale contenente le chiavi pubbliche (JWK) e le informazioni sui client raccolte tramite le API PDND;

- nella configurazione che consente l'invocazione delle API PDND è adesso possibile:

	- produrre header o parametri della url personalizzati da inoltrare verso la fruizione delle API;
	- disattivare l'invio di credenziali basic;
	- personalizzare le chiamate per tenant in una installazione multi-tenant.


Sono infine stati apportati i seguenti miglioramenti:

- aggiunto supporto per uno scenario di fruizione ModI in cui sia necessario utilizzare il materiale crittografico definito nella token policy per firmare i token di AUDIT e di INTEGRITY;

- la validazione dei token 'ModI' non supportava token contenenti claim 'aud' definiti come stringhe di array; è stato aggiunto il supporto in modo da rispettare entrambe le modalità (array of case-sensitive strings or single case-sensitive string) indicate nel RFC 'https://datatracker.ietf.org/doc/html/rfc7519.html#section-4.1.3';

- la produzione e la validazione dell'header di integrità 'Custom-JWT-Signature' è adesso attivabile anche per metodi senza payload.
