.. _tokenNegoziazionePolicy_notJwt:

Client Credentials / Resource Owner Password Credentials
---------------------------------------------------------

In entrambe le modalità è necessario definire i parametri di configurazione richiesti dall'authorization server per autenticare GovWay come client autorizzato a negoziare il token. Le modalità supportate sono le seguenti:

-  *Autenticazione Http Basic*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione di tipo HTTP-BASIC. In questo caso dovranno essere forniti Client-ID e Client-Secret nei campi successivi. È inoltre possibile indicare se la coppia di credenziali deve essere codificata nella richiesta 'x-www-form-urlencoded' oppure deve essere inserita in un header HTTP 'Authorization'.

-  *Autenticazione Bearer*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione tramite un bearer token. Il token dovrà essere indicato nel campo successivo fornito.

-  *Autenticazione Https*: flag da attivare nel caso in cui il servizio di negoziazione richieda autenticazione di tipo Https. In questo caso dovranno essere forniti tutti i dati di configurazione nei campi presenti nella sezione 'https'.

Se il tipo di negoziazione selezionato è 'Resource Owner Password Credentials', si dovra inoltre fornire i dati di configurazione specifici dell'autenticazione utente:

-  *Username* e *Password*: Dovranno essere forniti Username e Password dell'utente per cui verrà effettuata la negoziazione del token.
