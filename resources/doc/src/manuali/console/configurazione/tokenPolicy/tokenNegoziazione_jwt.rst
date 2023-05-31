.. _tokenNegoziazionePolicy_jwt:

Signed JWT
----------

Nel caso di modalità di negoziazione basata su uno scambio di un JWT firmato con l'authorization server si dovranno fornire tutti i parametri che andranno a definire il JWT firmato.

Innanzitutto se la modalità prevede una firma tramite chiave asimmetrica devono essere forniti i parametri di accesso ad un keystore contenente la chiave privata da utilizzare per la firma tramite una dei seguenti tipi:

	- 'JKS' o 'PKCS12': viene richiesta l'indicazione del path assoluto del keystore nel campo *File*, la definizione della password per l'accesso al keystore nel campo *Password*, l'alias con cui è riferita la chiave privata nel keystore nel campo *Alias Chiave Privata* e la password della chiave privata nel campo *Password Chiave Privata*;

	- 'JWK Set': deve essere definito il path su filesystem dove risiede l'archivio json nel formato 'JWK Set' e l'identificativo 'kid' (alias) con cui è riferita la chiave privata nel campo *Alias Chiave Privata*;

	- 'Key Pair': deve essere definito il path su filesystem dove risiedono la chiave privata e pubblica in formato PEM o DER (sono supportati sia i formati pkcs1 che pkcs8) e la password della chiave privata se cifrata nel campo *Password Chiave Privata*;

	- Tipi PKCS11: i tipi disponibili sono quelli corrispondenti ai tipi di keystore PKCS11 registrati (':ref:`pkcs11`').

Nella sezione 'JWT Signature' si deve indicare l'algoritmo di firma e l'eventuale chiave segreta nel caso in cui sia prevista una firma tramite chiave simmetrica.

All'interno della sezione 'JWT Header' si possono definire quali parametri dovranno essere presenti nella parte non firmata dell'asserzione JWT:

-  *Key Id (kid)*: indicazione della chiave utilizzata per attuare la firma dell'asserzione JWT, in una delle seguenti modalità:

	- Alias Chiave Privata (solamente in caso di firma con chiave asimmetrica): nel claim 'kid' viene impostato l'alias della chiave privata indicato nella precedente sezione di configurazione;
	
	- Client ID: viene utilizzato il medesimo valore associato al claim 'client_id' inserito nel payload firmato del JWT;

	- Personalizzato: permette di indicare un valore qualsiasi anche formato da parti dinamiche risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

-  *X.509 Certificate* (solamente in caso di firma con chiave asimmetrica): 

	- 'x5c': viene inserito il certificato utilizzato per firmare l'asserzione JWT;

	- 'x5u': viene indicata una url dove reperire il certificato di firma.

-  *Digest X.509 Certificate* (solamente in caso di firma con chiave asimmetrica): consente di indicare il digest del certificato di firma nella modalità SHA1 (x5t) o SHA256 (x5t#S256);

-  *Type*: valore inserito nel claim 'typ';

-  *Content Type*: se abilitato, il claim 'cty' verrà valorizzato con il content-type associato alla richiesta effettuata all'authorization server.

Nella sezione 'JWT Payload' si devono definire i parametri inseriti nella parte firmata dell'asserzione JWT:

-  *Client ID*: identificativo del client censito sull'AuthorizationServer che verrà indicato nel claim 'client_id' dell'asserzione JWT;

-  *Audience*: identifica l'authorization server come destinario dell'asserzione JWT (claim 'aud');

-  *Issuer*: dominio del soggetto firmatario dell'asserzione; se non viene fornito un valore il claim 'iss' verrà valorizzato con il nome del soggetto associato al dominio di gestione della richiesta;

-  *Subject*: il claim 'sub' verrà valorizzato con la medesima informazione inserita nel claim 'client_id' se nel campo Subject non viene fornito alcun valore;

-  *Time to Live*: indica la validità temporale, in secondi, a partire dalla data di creazione dell'asserzione;

-  *Claims*: consente di inserire ulteriori claims nel payload JWT firmato, indicandoli per riga nel formato 'nome=valore' (modalità descritte nella sezione :ref:`avanzate_generazione_claims`).

Tutti i valori definiti nella sezione 'JWT Payload' possono contenere parti dinamiche che verranno risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

Inoltre se non si desidera generare un determinato claim è possibile utilizzare la keyword '${undefined}' come valore del campo.
