@ignore
Feature: Genera transazioni con esiti appartenenti a tutti i gruppi, dotate delle informazioni sul mittente

# Feature di servizio, non contiene test.
#
# Il fixture di base (prepare_tests) produce transazioni completate con successo e una richiesta priva
# di credenziali, che termina con esito 16 'Autenticazione Fallita'. Su quest'ultima pero' non e'
# disponibile alcuna informazione sul mittente - niente applicativo, niente identificativo autenticato,
# niente token - percio' nelle distribuzioni che raggruppano per quelle dimensioni non comparirebbe, e
# le verifiche sul filtro dell'esito non avrebbero nulla da confrontare.
#
# Qui vengono prodotte quattro transazioni, una per ciascun gruppo di esito, tutte autenticate tranne
# la prima (che per definizione non puo' esserlo), e a tutte vengono associate le credenziali del token
# gia' registrate da prepare_tests, con la stessa tecnica usata la' per le transazioni riuscite:
#
#   esito 16  Autenticazione Fallita   -> gruppo 'richieste scartate'
#   esito  2  Fault Applicativo        -> gruppo 'fault', e anche 'errori di consegna'
#   esito 29  Risposta HTTP 4xx        -> gruppi 'fallite' ed 'errori di consegna'
#   esito 10  Errore di Connessione    -> gruppi 'fallite' ed 'errori di consegna'
#
# Viene utilizzato il riferimento 'client_id_application', cioe' il clientId nella forma strutturata
# che riporta anche l'applicativo registrato, cosi' le transazioni sono visibili sia nelle
# distribuzioni per token info (per tutti i claim, comprese le informazioni PDND associate a quel
# riferimento) sia in quella per applicativo identificato tramite token.
#
# Il fault applicativo e la risposta 4xx si ottengono dai parametri 'problem' e 'returnCode' della
# servlet di test, senza toccare la configurazione. L'errore di connessione richiede invece un backend
# irraggiungibile, e per ottenerlo NON viene deviato un connettore esistente: il gateway mantiene in
# cache la configurazione gia' utilizzata e una modifica non sarebbe visibile al runtime, mentre in
# questo ambiente il servizio di reset della cache non e' disponibile
# (org.openspcoop2.pdd.check.readJMXResources.enabled=false). Viene invece creato un gruppo nuovo, su
# una risorsa non utilizzata dalle altre chiamate, con il connettore gia' puntato ad una porta chiusa:
# un mapping mai invocato prima non e' in cache e viene letto dal database, cosi' come avviene in
# prepare_tests, che crea gruppi e li invoca immediatamente.
#
# Deve essere invocata prima di lock_db, in modo che il campionamento statistico successivo includa
# tutte le transazioni prodotte.

Scenario:

* def applicativoAuth = call basic ({ username: setup.applicativo.credenziali.username, password: setup.applicativo.credenziali.password })
* def erogazionePath = 'erogazioni/' + setup.erogazione_petstore.api_nome + '/' + setup.erogazione_petstore.api_versione

# Associa alla transazione appena prodotta le credenziali del token registrate da prepare_tests
* def associaInformazioniToken =
"""
function(idTransazione) {
    var sql = "UPDATE transazioni SET token_username = '" + setup.id_credenziale.username
        + "', token_issuer = '" + setup.id_credenziale.issuer
        + "', token_client_id = '" + setup.id_credenziale.client_id_application
        + "', token_subject = '" + setup.id_credenziale.subject
        + "', token_mail = '" + setup.id_credenziale.email
        + "' WHERE id = '" + idTransazione + "'";
    karate.log('Associo le informazioni sul token alla transazione ' + idTransazione);
    setup.db.update(sql);
}
"""


# --- esito 16: Autenticazione Fallita (gruppo richieste scartate) ---

Given url setup.url_invocazione
And path 'pet'
And headers ({ 'X-Forwarded-For': '127.0.0.2' })
And request setup.pet_update
When method put
Then status 401
* call pause(1000)
* eval associaInformazioniToken(responseHeaders['GovWay-Transaction-ID'][0])


# --- esito 2: Fault Applicativo (gruppo fault) ---
# La servlet di test risponde con un problem detail quando riceve il parametro 'problem'

Given url setup.url_invocazione
And path 'pet'
And param problem = 'true'
And header Authorization = applicativoAuth
And headers ({ 'X-Forwarded-For': '127.0.0.2' })
And request setup.pet_update
When method put
Then assert responseStatus >= 400
* call pause(1000)
* eval associaInformazioniToken(responseHeaders['GovWay-Transaction-ID'][0])


# --- esito 29: Risposta HTTP 4xx (gruppi fallite ed errori di consegna) ---
# La servlet di test ritorna il codice indicato nel parametro 'returnCode'

Given url setup.url_invocazione
And path 'pet'
And param returnCode = '404'
And header Authorization = applicativoAuth
And headers ({ 'X-Forwarded-For': '127.0.0.2' })
And request setup.pet_update
When method put
Then assert responseStatus >= 400 && responseStatus < 500
* call pause(1000)
* eval associaInformazioniToken(responseHeaders['GovWay-Transaction-ID'][0])


# --- esito 10: Errore di Connessione (gruppi fallite ed errori di consegna) ---

# Individua una risorsa dell'API non utilizzata dalle altre chiamate, da dedicare al gruppo con il
# connettore irraggiungibile: '/store/inventory' non ha parametri ed e' invocabile in GET
* def filtroRisorsa = function(r){ return r.path == '/store/inventory' }

Given url configUrl
And path 'api', setup.api_petstore.nome, setup.api_petstore.versione, 'risorse'
And header Authorization = govwayConfAuth
When method get
Then status 200
* def risorsaDedicata = karate.filter(response.items, filtroRisorsa)[0]
* print 'Risorsa dedicata al gruppo con connettore irraggiungibile:', risorsaDedicata.nome

# Il gruppo viene creato con una propria configurazione, autenticata come le altre chiamate
* def gruppoErrore =
"""
({
    nome: 'GruppoErroreConnessione',
    azioni: [ risorsaDedicata.nome ],
    configurazione: {
        modalita: 'nuova',
        autenticazione: {
            tipo: 'http-basic',
            opzionale: false
        }
    }
})
"""

Given url configUrl
And path erogazionePath, 'gruppi'
And params ({ soggetto: soggettoDefault })
And header Authorization = govwayConfAuth
And request gruppoErrore
When method post
Then status 201

# Connettore del nuovo gruppo su una porta chiusa. Il gruppo non e' ancora mai stato invocato,
# quindi alla prima richiesta la configurazione viene letta dal database, connettore compreso
Given url configUrl
And path erogazionePath, 'connettore'
And params ({ soggetto: soggettoDefault, gruppo: gruppoErrore.nome })
And header Authorization = govwayConfAuth
When method get
Then status 200
* def connettoreIrraggiungibile = ({ connettore: karate.merge(response.connettore, { endpoint: 'http://127.0.0.1:808/TestService/echo' }) })

Given url configUrl
And path erogazionePath, 'connettore'
And params ({ soggetto: soggettoDefault, gruppo: gruppoErrore.nome })
And header Authorization = govwayConfAuth
And request connettoreIrraggiungibile
When method put
Then status 204

Given url setup.url_invocazione
And path 'store', 'inventory'
And header Authorization = applicativoAuth
And headers ({ 'X-Forwarded-For': '127.0.0.2' })
When method get
Then assert responseStatus >= 500
* call pause(1000)
* eval associaInformazioniToken(responseHeaders['GovWay-Transaction-ID'][0])
