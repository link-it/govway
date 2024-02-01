.. _mon_reportistica_config_csv:

Formato CSV/XLS del Report Configurazione API
-----------------------------------------------

L'esportazione dei dettagli relativi ad una erogazione o fruizione produce una riga che contiene le principali informazioni sulla configurazione.
Le righe prodotte per la singola erogazione o fruizione possono essere più di una nei seguenti casi:

- *attivazione di gruppi di azioni o risorse*: viene prodotta una riga per ogni gruppo il cui nome viene riportato nella colonna 'Gruppo'
- *connettore multiplo*: viene prodotta una riga per ogni connettore definito nella lista dei connettori multipli. Il nome del connettore viene riportato nella colonna 'Connettore Multiplo'

Di seguito il nome dell'header e il formato del valore inserito in ogni colonna. La descrizione viene raggruppa per tipologia di informazioni

- 'Profilo Interoperabilità': indica il profilo a cui la configurazione appartiene (es. 'API Gateway' o 'ModI').
- 'Tipo API': assume il valore 'rest' o 'soap' a seconda della tecnologia dell'API.
- 'API': identificatore dell'API nel formato '<nomeAPI> v<versioneAPI>'; per il profilo SPCoop viene indicato anche il soggetto referente '<nomeAPI> v<versioneAPI> (<soggettoReferente>)'.
- 'PortType': valorizzato solo su api 'soap' viene riportato il nome del Port-Type implementato.
- 'Fruitore' [solo in una fruizione]: identificativo del soggetto a cui appartiene la fruizione.
- 'Erogatore': identificativo del soggetto che eroga l'API.
- 'Implementazione API': identificativo dell'erogazione o della fruizione.
- 'Azione/Risorsa': vengono elencata tutte le azioni/risorse dell'API separate con un ritorno a capo. Su api 'rest' ogni risorsa viene indicata con il formato 'HttpMethod Path'.
- 'Gruppo': contiene l'eventuale identificativo associato al gruppo di azioni o risorse. In caso non siano stati attivati gruppi sull'erogazione o fruzione, il nome riportato corrisponde al gruppo di default 'Predefinito'.
- 'Stato': indica se l'erogazione o la fruizione risulta sospesa (valore 'disabilitato') o regolarmente attiva (valore 'abilitato').
- 'URL di Invocazione': endpoint utilizzabile per invocare l'API.
- 'Modalità Identificazione Azione': indica la funzionalità di identificazione dell'azione associata all'API. In caso la riga si riferisca ad un gruppo differente dal predefinito sarà presente il valore 'delegatedBy'.
- 'Pattern': eventuale espressione regolare, xpath o jsonPath utilizzata dalla modalità di identificazione dell'azione indicata.
- 'Force Interface Based': indicazione se debba essere utilizzata la modalità alternativa di riconoscimento basata sull'interfaccia di servizio (wsdl, openApi...) se l'identificazione indicata nella colonna 'Modalità Identificazione Azione' non ha permesso di individuare una azione o risorsa. I valori presenti saranno 'abilitato' o 'disabilitato'.
- 'CORS': viene indicato il valore 'default' se la configurazione relativa al CORS non è stata ridefinita. Altrimenti verranno riportati i valori 'abilitato' o 'disabilitato' a seconda della modalità di ridefinizione.
- 'Token (Stato)': indicazione se il Controllo degli Accessi si attende un token (valore 'abilitato') o meno (valore 'disabilitato').
- 'Token (Opzionale)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se il token è atteso obbligatoriamente (valore 'disabilitato') o meno (valore 'abilitato').
- 'Token (Policy)': identificativo della Token Policy di validazione configurata sul Controllo degli Accessi.
- 'Token (Validazione JWT)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se il token ricevuto sarà validato come JWT Token tramite un trustStore (valore 'abilitato') o meno (valore 'disabilitato').
- 'Token (Introspection)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se il token ricevuto sarà validato attraverso l'invocazione del servizio di Introspection configurato nella policy (valore 'abilitato') o meno (valore 'disabilitato').
- 'Token (UserInfo)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se il token ricevuto sarà utilizzato per recuperare informazioni sull'utente attraverso l'invocazione del servizio di UserInfo configurato nella policy (valore 'abilitato') o meno (valore 'disabilitato').
- 'Token (Token Forward)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se le informazioni del token ricevuto debbano essere propagate all'implementazione dell'API di backend (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Token Issuer)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se all'interno del token è atteso obbligatoriamente l'informazione sull'Issuer (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Token ClientID)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se all'interno del token è atteso obbligatoriamente l'informazione sul ClientID (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Token Subject)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se all'interno del token è atteso obbligatoriamente l'informazione sul Subject (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Token Username)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se all'interno del token è atteso obbligatoriamente l'informazione sull'Username (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Token eMail)': nel caso sia abilitata l'autenticazione tramite token, viene riportata l'indicazione se all'interno del token è atteso obbligatoriamente l'informazione sull'eMail (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autenticazione (Stato)': autenticazione di trasporto configurata nel Controllo degli Accessi.
- 'Autenticazione (Opzionale)': nel caso sia abilitata l'autenticazione trasporto, viene riportata l'indicazione se una credenziale è attesa obbligatoriamente (valore 'disabilitato') o meno (valore 'abilitato').
- 'Autenticazione (proprieta)': eventuali proprietà aggiuntive attivate sull'autenticazione di trasporto configurata nel Controllo degli Accessi.
- 'AttributeAuthority (attributi)': per ogni AttributeAuthority configurato viene riportato il nome e la lista degli attributi da richiedere nel formato '<nomeAA>=<attributo1>,<attributo2>'.
- 'Autorizzazione (Stato)': autorizzazione configurata nel Controllo degli Accessi.
- 'Autorizzazione (proprieta)': eventuali proprietà aggiuntive attivate sull'autorizzazione configurata nel Controllo degli Accessi.
- 'Autorizzazione (Richiedenti Autorizzati)': indicazione se risulta abilitata l'autorizzazione puntuale per richiedenti (valore 'abilitato') o meno (valore 'disabilitato').
- 'Soggetti Autorizzati' [solo in una erogazione]: elenco dei soggetti autorizzati separati con un ritorno a capo.
- 'Applicativi Autorizzati': elenco degli applicativi autorizzati separati con un ritorno a capo. Nel caso di erogazione per ogni applicativo viene fornito anche il suffisso ' soggetto:<tipoSoggettoProprietario>/<nomeSoggettoProprietario>'.
- 'Autorizzazione (Ruoli)': indicazione se risulta abilitata l'autorizzazione per ruoli (valore 'abilitato') o meno (valore 'disabilitato').
- 'Ruoli Richiesti': nel caso di autorizzazione per ruoli, viene riportata l'indicazione se i ruoli richiesti siano 'almeno uno' o 'tutti'.
- 'Ruoli': nel caso di autorizzazione per ruoli viene riportato l'elenco dei ruoli configurati separati con un ritorno a capo. Il formato di ogni ruolo è il seguente '<identificativoRuolo> (fonte: <qualisiasi/interna/esterna>)'.
- 'Autorizzazione (Scope)': indicazione se risulta abilitata l'autorizzazione per scope (valore 'abilitato') o meno (valore 'disabilitato').
- 'Scope': nel caso di autorizzazione per scope viene riportato l'elenco degli scope configurati separati con un ritorno a capo.
- 'Autorizzazione (Token Claims)': eventuali controlli di autorizzazione basati sui claim del token.
- 'Autorizzazione Contenuti (Stato)': indicazione se risulta abilitata l'autorizzazione per contenuti (valore 'abilitato') o meno (valore 'disabilitato').
- 'Autorizzazione Contenuti (proprieta)': vengono riportati i criteri di autorizzazione per contenuti impostati.
- 'RateLimiting': elenco delle policy di rate limiting attive separate con un ritorno a capo. Ogni policy viene riportata con il seguente formato: '<Alias> <abilitato/disabilitato/warningOnly> <TipoRisorsa> <ValoreSoglia>[ <ValoreSogliaRisposta>]'. Il valore soglia di risposta è presente solo per policy di tipo 'DimensioneMassimaMessaggio'.
- 'Validazione (Stato)': indicazione se risulta abilitata la funzionalità di validazione dei contenuti applicativi (valore 'abilitato') o meno (valore 'disabilitato').
- 'Validazione (Tipo)': se abilitata la funzionalità di validazione dei contenuti viene riportato il tipo di validazione. 
- 'Validazione (Accetta MTOM)': indicazione se è abilitata la gestione dei messaggi MTOM durante la funzionalità di validazione dei contenuti (valore 'abilitato') o meno (valore 'disabilitato').
- 'Caching Risposta': viene indicato il valore 'default' se la configurazione non è stata ridefinita. Altrimenti verranno riportati i valori 'abilitato' o 'disabilitato' a seconda della modalità di ridefinizione.
- 'Sicurezza Messaggio (Stato)': indicazione se risulta abilitata la funzionalità di sicurezza dei messaggi (valore 'abilitato') o meno (valore 'disabilitato').
- 'Schema Sicurezza (Richiesta)': tipo di sicurezza applicata al messaggio di richiesta.
- 'Schema Sicurezza (Risposta)': tipo di sicurezza applicata al messaggio di risposta.
- 'MTOM (Richiesta)': tipo di gestione MTOM applicata al messaggio di richiesta.
- 'MTOM (Risposta)': tipo di gestione MTOM applicata al messaggio di risposta.
- 'Trasformazioni': elenco delle trasformazioni configurate, separate con un ritorno a capo, nel formato '<nomeTrasformazione> <abilitato/disabilitato>'.
- 'Correlazione Applicativa (Richiesta)': indicazione se risulta abilitata la funzionalità di correlazione applicativa sulla richiesta (valore 'abilitato') o meno (valore 'disabilitato').
- 'Correlazione Applicativa (Risposta)': indicazione se risulta abilitata la funzionalità di correlazione applicativa sulla risposta (valore 'abilitato') o meno (valore 'disabilitato').
- 'Registrazione Messaggi': viene indicato il valore 'default' se la configurazione non è stata ridefinita. Altrimenti vengono riportati i dettagli relativi alla registrazione delle 4 tipologie di messaggio (richiesta-ingresso/richiesta-uscita/risposta-ingresso/risposta-uscita), separate con un ritorno a capo, nel formato '<tipologia> header:<abilitato/disabilitato> payload:<abilitato/disabilitato>'.
- 'Proprietà': proprietà di configurazione associate all'erogazione o alla fruizione.
- 'Metadati': vengono riportati gli eventuali metadati di integrazione configurati.
- 'Handlers': viene indicato il valore 'default' se la configurazione non è stata ridefinita. Altrimenti vengono elencati i tipi di handler separati con la virgola associati ad ogni flusso. Ogni flusso viene separato con il ritorno a capo.
- 'Configurazioni Profilo Interoperabilità': nel caso di profilo di interoperabilità 'ModI' vengono riportate eventuali configurazioni relative alla sicurezza messaggio.
- 'MessageBox' [solo in una erogazione]: indicazione se la funzionalità di MessageBox tramite IntegrationManager è abilitata (valore 'abilitato') o meno (valore 'disabilitato').
- 'Sbustamento SOAP' [solo in una erogazione]: indicazione se la funzionalità di sbustamento SOAP è abilitata (valore 'abilitato') o meno (valore 'disabilitato').
- 'Sbustamento Protocollo' [solo in una erogazione]: indicazione se la funzionalità di sbustamento delle informazioni relative al profilo di interoperabilità è abilitata (valore 'abilitato') o meno (valore 'disabilitato').
- 'Connettore (Tipo)': tipo del connettore configurato.
- 'Connettore (Endpoint)': indirizzo relativo all'implementazione dell'API di backend.
- 'Connettore (Debug)': indicazione se è attivo il debug sul connettore (valore 'true') o meno (valore 'false');
- 'Connettore (Username)': username configurato per una autenticazione http basic sul connettore.
- 'Connettore (Proxy Endpoint)': eventuale proxy http configurato sul connettore.
- 'Connettore (Proxy Username)': eventuale username utilizzato per l'autenticazione http basic sul proxy attivato sul connettore.
- 'Connettore (SSL Type)': versione TLS (es. TLSv1.2) utilizzata su connettore di tipo 'https'.
- 'Connettore (Hostname Verifier)': indicazione se è attivo (valore 'true') o meno (valore 'false') l'hostname verifier su connettore di tipo 'https'.
- 'Connettore (KeyStore)': viene indicato il tipo di keystore configurato su connettore di tipo 'https'.
- 'Connettore (TrustStore)': viene indicato il tipo di truststore configurato su connettore di tipo 'https'.
- 'Connettore (KeyStore Location)': viene indicata il path del keystore configurato su connettore di tipo 'https'.
- 'Connettore (TrustStore Location)': viene indicata il path del truststore configurato su connettore di tipo 'https'. Se è stata configurata l'opzione di accettare qualsiasi certificato viene indicata 'Trust all certificates'.
- 'Connettore (Client Certificate)': se non attiva una configurazione keystore viene riportato il valore 'false'. Altrimenti se è definito un alias per la chiave da utilizzare viene riportato il valore dell'alias configurato altrimenti viene indicato il valore 'true'.
- 'Connettore (Altre Configurazioni)': vengono riportate le configurazioni dei connettori differenti da http.
- 'Porta Delegata' [solo in una fruizione]: nome interno della fruizione.
- 'Porta Applicativa' [solo in una erogazione]: nome interno dell'erogazione.
- 'Connettore Multiplo' [solo in una erogazione]: in caso di configurazione con connettore multiplo, viene riportato il nome del connettore che interessa la riga.
- 'Applicativo Server' [solo in una erogazione]: se nel connettore è stato attivato un applicativo server, viene riportato il nome dell'applicativo.

