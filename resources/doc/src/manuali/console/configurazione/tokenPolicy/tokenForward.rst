.. _tokenPolicy_tokenForward:

Token Forward
~~~~~~~~~~~~~

Azione di elaborazione che consiste nell'inoltro del token ricevuto al
destinatario. Una volta attivata questa opzione, devono essere indicate
le seguenti informazioni:

-  *Originale*: opzione che consente di inoltrare il token originale al
   destinatario. Attivando questo flag è necessario specificare la
   modalità di inoltro a scelta tra le seguenti opzioni:

   -  *Come è stato ricevuto*: Il token viene inoltrato al destinatario
      utilizzando lo stesso metodo con cui è stato ricevuto dal gateway.

   -  *RFC 6750 - Bearer Token Usage (Authorization Request Header
      Field)*: Il token viene inoltrato al destinatario utilizzando
      l'header Authorization presente nella richiesta HTTP.

   -  *RFC 6750 - Bearer Token Usage (URI Query Parameter)*: Il token
      viene inoltrato al destinatario tramite parametro access\_token
      della Query String.

   -  *Header HTTP*: Il token viene inoltrato al destinatario
      utilizzando un header HTTP il cui nome deve essere specificato nel
      campo seguente.

   -  *Parametro URL*: Il token viene inoltrato al destinatario
      utilizzando un parametro della Query String il cui nome deve
      essere specificato nel campo seguente.

-  *Informazioni Raccolte*: opzione disponibile quando è stata abilitata
   una delle azioni di validazione del token (introspection, user info o
   validazione JWT), consente di veicolare i dati ottenuti dal servizio
   di validazione, al destinatario. Una volta attivato il flag è
   necessario specificare la modalità di inoltro dei dati selezionando
   una tra le opzioni seguenti:

   -  *GovWay Headers*: I dati raccolti dal token vengono inseriti nei
      seguenti header HTTP:

      ::

          GovWay-Token-Issuer
          GovWay-Token-Subject
          GovWay-Token-Username
          GovWay-Token-Audience
          GovWay-Token-ClientId
          GovWay-Token-IssuedAt
          GovWay-Token-Expire
          GovWay-Token-NotToBeUsedBefore
          GovWay-Token-Scopes
          GovWay-Token-FullName
          GovWay-Token-FirstName
          GovWay-Token-MiddleName
          GovWay-Token-FamilyName
          GovWay-Token-EMail
	  GovWay-Token-PurposeId
	  GovWay-Token-Jti

   -  *GovWay JSON*: I dati raccolti dal token vengono inseriti in un
      oggetto JSON, il cui JsonSchema è il seguente:

      ::

          {
	    "required" : [ "id" ],
	    "properties": {
		"id": {"type": "string"},
		"issuer": {"type": "string"},
		"subject": {"type": "string"},
		"username": {"type": "string"},
		"audience": {
		   "type": "array",
		   "items": {"type": "string"}
		},
		"clientId": {"type": "string"},
		"iat": {
		   "type": "string",
		   "format": "date-time"
		},
		"expire": {
		   "type": "string",
		   "format": "date-time"
		},
		"nbf": {
		   "type": "string",
		   "format": "date-time"
		},
		"roles": {
		   "type": "array",
		   "items": {"type": "string"}
		},
		"scope": {
		   "type": "array",
		   "items": {"type": "string"}
		},
		"userInfo": {
		   "type": "object",
		   "properties": {
		       "fullName": {"type": "string"},
		       "firstName": {"type": "string"},
		       "middleName": {"type": "string"},
		       "familyName": {"type": "string"},
		       "email": {"type": "string"}
		   },
		   "additionalProperties": false
		},
		"jti": {"type": "string"},
		"purposeId": {"type": "string"},
		"claims": {
		    "type": "array",
		    "items": {
		        "name": {"type": "string"},
		        "value": {"type": "string"}
		    },
		   "additionalProperties": false
		},
		"processTime": {
		   "type": "string",
		   "format": "date-time"
		}
	    },
	    "additionalProperties": false
	}

      Il JSON risultante viene inserito nell'Header HTTP *GovWay-Token*.

   -  *GovWay JWS*: I dati raccolti dal token vengono inseriti in un
      oggetto JSON, come descritto al punto precedente. In questo caso
      il token JSON viene inserito successivamente in un JWT e quindi
      firmato. Il JWS risultante viene inserito nell'Header HTTP
      *GovWay-JWT*.

   -  *JSON*: Le informazioni ottenute dai servizi di introspection,
      userinfo o il json estratto dal token jwt dopo la validazione,
      vengono inseriti negli header http o nelle proprietà della url indicati.

      .. note::
         Le informazioni sono esattamente quelle recuperate dai servizi
         originali (o presenti nel token originale nel caso di
         validazione jwt).

   -  *JWS/JWE*: Uguale alla modalità JSON con la differenza che negli
      header http, o nelle proprietà della url, vengono inseriti dei JWT
      firmati (caso JWS) o cifrati (caso JWE) contenenti al loro interno
      il JSON.
